/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models;

/**
 * An external script to try against a saved script, for {@code POST /v2/scripts/external/test}. Nothing is saved.
 *
 * @param externalScriptId the external script being edited, if it has been saved; its owner may test without
 *                         {@code EDIT_SCRIPT}
 * @param scriptId         the script to run it against
 * @param name             the external script's file name; its extension picks the language
 * @param script           the external script's code
 */
public record ExternalScriptTestRequest(Integer externalScriptId, Integer scriptId, String name, String script) {
}
