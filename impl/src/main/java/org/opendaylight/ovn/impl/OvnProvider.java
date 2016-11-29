/*
 * Copyright © 2016 www.dtdream.com and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ovn.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.opendaylight.controller.sal.binding.api.RpcProviderRegistry;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.RpcRegistration;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ovn.rev150105.OvnService;

public class OvnProvider {

    private static final Logger LOG = LoggerFactory.getLogger(OvnProvider.class);
    private final RpcProviderRegistry rpcProviderRegistry;
    private RpcRegistration<OvnService> serviceRegistration;
    private final DataBroker dataBroker;

    //public OvnProvider(final DataBroker dataBroker) {
    //    this.dataBroker = dataBroker;
    //}
    public OvnProvider(final DataBroker dataBroker, RpcProviderRegistry rpcProviderRegistry) {
        this.dataBroker = dataBroker;
        this.rpcProviderRegistry = rpcProviderRegistry;
    }
/*
    @Override
    public void onSessionInitiated(final ProviderContext session) {
        LOG.info("OvnProvider Session Initiating");
        ovnService = session.addRpcImplementation(OvnService.class, new OvnImpl() );
        LOG.info("OvnProvider Session Initiated");
    }
*/
    /**
     * Method called when the blueprint container is created.
     */
    public void init() {
        LOG.info("OvnProvider init start");
		serviceRegistration = rpcProviderRegistry.addRpcImplementation(OvnService.class, new OvnImpl());
        LOG.info("OvnProvider init finished");
    }

    /**
     * Method called when the blueprint container is destroyed.
     */
    public void close()  throws Exception {
        LOG.info("OvnProvider Closed");
        if (serviceRegistration != null) {
			serviceRegistration.close();
            LOG.info("close ovnService");
        }
    }
}
