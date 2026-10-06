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
 * The outcome of uploading several data files or zip archives.
 *
 * @param created the data files created, in upload order
 * @param skipped uploaded files and archive entries that were not data files (wrong extension), by name
 */
public record DataFileBatchResult(List<Created> created, List<String> skipped) {

    public record Created(int id, String name) {
    }
}
