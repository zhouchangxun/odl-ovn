/*
 * Copyright © 2016 www.dtdream.com and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ovn.impl.ovsdb.event;

import org.opendaylight.ovsdb.lib.notation.UUID;

/**
 * Created by hzzhangdongya on 16-6-16.
 */
public class InterfaceOFPortAllocatedEvent extends BaseEvent {
    private final UUID uuid;
    private final String portName;
    private final long ofPort;

    public InterfaceOFPortAllocatedEvent(UUID uuid, String portName, long ofPort) {
        this.uuid = uuid;
        this.portName = portName;
        this.ofPort = ofPort;
    }

    public String getPortName() {
        return portName;
    }

    public long getOfPort() {
        return ofPort;
    }

    public UUID getUuid() {
        return uuid;
    }
}
