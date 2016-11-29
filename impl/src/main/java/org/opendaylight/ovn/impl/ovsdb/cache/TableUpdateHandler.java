/*
 * Copyright © 2016 www.dtdream.com and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ovn.impl.ovsdb.cache;

import org.opendaylight.ovsdb.lib.message.TableUpdates;
import org.opendaylight.ovsdb.lib.schema.DatabaseSchema;

/**
 * Created by hzzhangdongya on 16-6-15.
 */
public abstract class TableUpdateHandler {
    public OVSDBCache cache;

    public TableUpdateHandler(OVSDBCache cache) {
        this.cache = cache;
    }

    public abstract void process(OVSDBCache cache, TableUpdates updates, DatabaseSchema dbSchema);
}
