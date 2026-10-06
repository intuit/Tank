/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.dao;

import java.util.List;

/**
 * A page of entities and the number of entities matching the query across all pages.
 */
public record PagedResult<T>(List<T> items, long total) {
}
