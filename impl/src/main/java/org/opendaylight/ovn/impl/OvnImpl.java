/*
 * Copyright © 2016 www.dtdream.com and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ovn.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.util.concurrent.Future;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ovn.rev150105.OvnService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ovn.rev150105.HelloWorldInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ovn.rev150105.HelloWorldOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.ovn.rev150105.HelloWorldOutputBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.opendaylight.ovsdb.lib.impl.OvsdbConnectionService;
import org.opendaylight.ovsdb.lib.OvsdbConnection;
import org.opendaylight.ovsdb.lib.OvsdbClient;
import org.opendaylight.ovsdb.lib.OvsdbConnectionInfo;
import org.opendaylight.ovn.impl.ovsdb.OVSDBBridge;
import org.opendaylight.ovn.impl.ovsdb.OVSDBManager;

public class OvnImpl implements OvnService {
	private static final Logger LOG = LoggerFactory.getLogger(OvnImpl.class);
	public static OvsdbConnection ovsdbService = OvsdbConnectionService.getService();
	public OvsdbClient ovsdbClient=null;
	private OVSDBManager ovsdbManager=null;

	public OvnImpl(){
		LOG.info("OvnService init begin");
		InetAddress ovsdbAddr=null;
		try {
			ovsdbAddr = InetAddress.getByName("10.157.0.150");
		}catch(UnknownHostException e){
			LOG.error("address resolve exception");
		}

		ovsdbManager = new OVSDBManager(ovsdbAddr, 6644);
		OVSDBBridge brInt = ovsdbManager.getOVSDBBridge("br-int");
		brInt.ensureExisted();
		LOG.info("Ensure br-int finished");
		LOG.info("OvnService init finished");
	}
	@Override
	public Future<RpcResult<HelloWorldOutput>> helloWorld(HelloWorldInput input) {
		HelloWorldOutputBuilder helloBuilder = new HelloWorldOutputBuilder();
		OvsdbConnectionInfo ovsdbInfo = ovsdbClient.getConnectionInfo();
		helloBuilder.setGreating("Hello " + input.getName()+ovsdbInfo);
		return RpcResultBuilder.success(helloBuilder.build()).buildFuture();
	}
}

