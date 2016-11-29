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
 * Port removed. (possible by other application like libvirt)
 */
public class PortRemovedEvent extends BaseEvent {
    private final UUID uuid;
    private final String portName;

    // TODO: may be we later remove uuid, and only expose name to
    // agent, in that case, we should use cache to find uuid of a port.
    public PortRemovedEvent(UUID uuid, String portName) {
        this.uuid = uuid;
        this.portName = portName;
    }

    public String getPortName() {
        return portName;
    }

    public UUID getUuid() {
        return uuid;
    }
}
