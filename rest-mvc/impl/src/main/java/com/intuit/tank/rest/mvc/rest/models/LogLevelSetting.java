/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models;

/**
 * The log level of one controller node. Each node has its own level.
 *
 * @param level the level name, such as {@code INFO}
 * @param node  the host name of the node that served the request; null in requests
 */
public record LogLevelSetting(String level, String node) {
}
