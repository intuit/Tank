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
 * The outcome of deleting several entities at once.
 *
 * @param deleted  the ids that were deleted
 * @param notFound the ids that did not exist
 */
public record BulkDeleteResult(List<Integer> deleted, List<Integer> notFound) {
}
