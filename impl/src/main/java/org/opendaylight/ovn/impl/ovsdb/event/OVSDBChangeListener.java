/*
 * Copyright © 2016 www.dtdream.com and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ovn.impl.ovsdb.event;

/**
 * Created by hzzhangdongya on 16-6-14.
 */
public interface OVSDBChangeListener {
    // TODO:
    // 1. notify ovsdb
    // 2. notify port ofport changes (seems we do not need to handle port add before ofport obtained)
    // 3. notify port removal
    void notify(BaseEvent event);
}
