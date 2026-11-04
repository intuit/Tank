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
 * A page of a data file's lines, for previewing it.
 *
 * @param offset     the zero-based index of the first line returned
 * @param lines      the lines, without line endings
 * @param totalLines the number of lines in the file
 */
public record DataFilePreview(int id, String name, int offset, List<String> lines, int totalLines) {
}
