/*
 * Copyright © 2016 dtdream and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ovn.cli.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.opendaylight.ovn.cli.api.OvnCliCommands;

public class OvnCliCommandsImpl implements OvnCliCommands {

    private static final Logger LOG = LoggerFactory.getLogger(OvnCliCommandsImpl.class);
    private final DataBroker dataBroker;

    public OvnCliCommandsImpl(final DataBroker db) {
        this.dataBroker = db;
        LOG.info("OvnCliCommandImpl initialized");
    }

    @Override
    public Object testCommand(Object testArgument) {
        return "This is a test implementation of test-command";
    }
}