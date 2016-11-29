/*
 * Copyright © 2016 www.dtdream.com and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ovn.impl.ovsdb.event;

/**
 * Created by hzzhangdongya on 16-6-15.
 */
public abstract class BaseEvent {
    // TODO: derived event
    // 1. bridge delete event (we may not choose to consider this as operational failure).
    // 2. port add/delete/ofport change event.
}
