package com.netease.cns.agent.ovsdb;

import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import com.netease.cns.agent.ovsdb.cache.OVSDBCache;
import com.netease.cns.agent.ovsdb.common.Constants;
import org.opendaylight.ovsdb.lib.OvsdbClient;
import org.opendaylight.ovsdb.lib.notation.Mutator;
import org.opendaylight.ovsdb.lib.notation.UUID;
import org.opendaylight.ovsdb.lib.operations.OperationResult;
import org.opendaylight.ovsdb.lib.operations.TransactionBuilder;
import org.opendaylight.ovsdb.lib.schema.DatabaseSchema;
import org.opendaylight.ovsdb.lib.schema.GenericTableSchema;
import org.opendaylight.ovsdb.lib.schema.typed.TyperUtils;
import org.opendaylight.ovsdb.schema.openvswitch.Bridge;
import org.opendaylight.ovsdb.schema.openvswitch.Interface;
import org.opendaylight.ovsdb.schema.openvswitch.OpenVSwitch;
import org.opendaylight.ovsdb.schema.openvswitch.Port;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.opendaylight.ovsdb.lib.operations.Operations.op;

/**
 * Created by hzzhangdongya on 16-6-15.
 */
public class OVSDBBridge {
    private static final Logger LOG = LoggerFactory.getLogger(OVSDBBridge.class);
    private OVSDBManager ovsdbManager;
    private String bridgeName;
    private OVSDBCache cache;

    // TODO:
    // define agent API here.
    // 1. bridge add/delete/query(by-name), should return a temporary object to keep cache internal.
    // 2. bridge port add/delete/query(by-name).
    // transaction should be done transparently, agent does not need to know transaction exsists.
    public OVSDBBridge(OVSDBManager ovsdbManager, String bridgeName) {
        this.ovsdbManager = ovsdbManager;
        this.bridgeName = bridgeName;
        this.cache = ovsdbManager.getCache();
    }

    // TODO: refer to ovs-vsctl and implement necessary condition check for next_cfg
    // and bridge name and .etc.
    private void createBridge() {
        OvsdbClient client = ovsdbManager.getClient();
        DatabaseSchema dbSchema = ovsdbManager.getDBSchema();
        TransactionBuilder tb = client.transactBuilder(dbSchema);

        // Configure bridge with the above port details
        Bridge bridge = TyperUtils.getTypedRowWrapper(dbSchema, Bridge.class);
        bridge.setName(bridgeName);

        tb.add(op.insert(bridge.getSchema()).withId("BridgeToBeAdded").value(bridge.getNameColumn()));

        String interfaceUuid = "InterfaceToBeAdded";
        Interface ovsInterface =
                TyperUtils.getTypedRowWrapper(dbSchema, Interface.class);
        ovsInterface.setType("internal"); // TODO: support others...
        ovsInterface.setName(bridgeName);
        tb.add(op.insert(ovsInterface).withId(interfaceUuid));

        // Add a port with the name of the bridge which can be used as routed port if necessary,
        // this make behavior same as ovs-vsctl.
        String portUuid = "PortToBeAdded";
        Port port = TyperUtils.getTypedRowWrapper(dbSchema, Port.class);
        port.setName(bridgeName);
        port.setInterfaces(Sets.newHashSet(new UUID(interfaceUuid)));
        tb.add(op.insert(port).withId(portUuid));

        bridge.setPorts(Sets.newHashSet(new UUID(portUuid)));
        tb.add(op.mutate(bridge.getSchema())
                .addMutation(bridge.getPortsColumn().getSchema(),
                        Mutator.INSERT, bridge.getPortsColumn().getData())
                .where(bridge.getNameColumn().getSchema().opEqual(bridge.getNameColumn().getData())).build());

        OpenVSwitch openVSwitch = TyperUtils.getTypedRowWrapper(dbSchema, OpenVSwitch.class);
        openVSwitch.setBridges(Sets.newHashSet(new UUID("BridgeToBeAdded")));

        tb.add(op.mutate(openVSwitch.getSchema()).addMutation(openVSwitch.getBridgesColumn().getSchema(),
                Mutator.INSERT, openVSwitch.getBridgesColumn().getData()));

        ListenableFuture<List<OperationResult>> result = tb.execute();
        try {
            List<OperationResult> got = result.get();
            LOG.debug("OVSDB transaction result: {}", got);
        } catch (Exception e) {
            LOG.warn("Transact execution exception: ", e);
        }
    }

    /**
     * Ensure bridge existed, this call will blocking current thread.
     */
    public void ensureExisted() {
        while (!ovsdbManager.isOVSDBConnectionWorking()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                LOG.info("Thread interrupted");
            }
        }

        // If OVSDB connection is working, we can check cache to make sure bridge exists or not
        if (!cache.bridgeExists(this.bridgeName)) {
            createBridge();
        }
    }

    // This interface does not return Future, agent should handle OfPort event asynchronously.
    // TODO: add other parameters.
    public void addPort(String portName) {
        if (!ovsdbManager.isOVSDBConnectionWorking()) {
            LOG.error("Connection to ovsdb-server is not working, failed to add port");
        }

        OvsdbClient client = ovsdbManager.getClient();
        DatabaseSchema dbSchema = ovsdbManager.getDBSchema();
        TransactionBuilder tb = client.transactBuilder(dbSchema);

        String interfaceUuid = "InterfaceToBeAdded";
        Interface ovsInterface =
                TyperUtils.getTypedRowWrapper(dbSchema, Interface.class);
        ovsInterface.setType("internal"); // TODO: support others...
        ovsInterface.setName(portName);
        tb.add(op.insert(ovsInterface).withId(interfaceUuid));

        // Configure port with the above interface details
        String portUuid = "PortToBeAdded";
        Port port = TyperUtils.getTypedRowWrapper(dbSchema, Port.class);
        port.setName(portName);
        port.setInterfaces(Sets.newHashSet(new UUID(interfaceUuid)));
        tb.add(op.insert(port).withId(portUuid));

        // Configure bridge with the above port details
        Bridge bridge = TyperUtils.getTypedRowWrapper(dbSchema, Bridge.class);
        bridge.setName(bridgeName);
        bridge.setPorts(Sets.newHashSet(new UUID(portUuid)));

        tb.add(op.mutate(bridge)
                .addMutation(bridge.getPortsColumn().getSchema(),
                        Mutator.INSERT, bridge.getPortsColumn().getData())
                .where(bridge.getNameColumn().getSchema()
                        .opEqual(bridge.getNameColumn().getData())).build());

        // We do not wait here, we will monitor the port openflow port  to ensure...
        final ListenableFuture<List<OperationResult>> result = tb.execute();
        result.addListener(new Runnable() {
            public void run() {
                try {
                    List<OperationResult> results = result.get();
                    for (OperationResult operationResult : results) {
                        LOG.info("Add port transaction result: " + operationResult.getDetails());
                    }
                } catch (InterruptedException e) {
                    LOG.error("The transaction is interrupted...");
                } catch (ExecutionException e) {
                    LOG.error("Exception in task");
                }
            }
        }, ovsdbManager.getExecutor());
    }

    // This interface does not return Future, agent should handle PortRemoved event asynchronously.
    // TODO: add other parameters.
    public void removePort(UUID uuid) {
        if (!ovsdbManager.isOVSDBConnectionWorking()) {
            LOG.error("Connection to ovsdb-server is not working, failed to remove port");
        }

        OvsdbClient client = ovsdbManager.getClient();
        // TODO: we may initial schema as a singleton and not changed once initialized to prevent
        // these local variables instantiation.
        DatabaseSchema dbSchema = ovsdbManager.getDBSchema();
        TransactionBuilder tb = client.transactBuilder(dbSchema);
        GenericTableSchema portTableSchema = dbSchema.table(Constants.TBL_PORT, GenericTableSchema.class);

        // Configure a port which is only used to get ColumnSchema.
        Port port = TyperUtils.getTypedRowWrapper(dbSchema, Port.class, null);
        tb.add(op.delete(portTableSchema)
                .where(port.getUuidColumn().getSchema().opEqual(uuid)).build());

        // Configure bridge with the above port details
        Bridge bridge = TyperUtils.getTypedRowWrapper(dbSchema, Bridge.class, null);
        tb.add(op.mutate(bridge)
                .addMutation(bridge.getPortsColumn().getSchema(),
                        Mutator.DELETE, Sets.newHashSet(uuid))
                .where(bridge.getNameColumn().getSchema().opEqual(bridgeName)).build());

        // We do not wait here, we will monitor the port openflow port  to ensure...
        final ListenableFuture<List<OperationResult>> result = tb.execute();
        result.addListener(new Runnable() {
            public void run() {
                try {
                    List<OperationResult> results = result.get();
                    for (OperationResult operationResult : results) {
                        LOG.info("Remove port transaction result: " + operationResult.getDetails());
                    }
                } catch (InterruptedException e) {
                    LOG.error("The transaction is interrupted...");
                } catch (ExecutionException e) {
                    LOG.error("Exception in task");
                }
            }
        }, ovsdbManager.getExecutor());
    }
}
