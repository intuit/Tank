/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models;

import java.util.List;

/**
 * One page of a listing.
 *
 * @param items the items on this page
 * @param total the number of items across all pages
 * @param page  the zero-based page number
 * @param size  the requested page size
 */
public record PageResponse<T>(List<T> items, long total, int page, int size) {
}
