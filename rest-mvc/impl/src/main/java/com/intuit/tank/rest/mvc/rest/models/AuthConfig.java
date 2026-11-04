/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models;

import java.util.Date;

/**
 * What the login page needs to know before anyone has signed in.
 *
 * @param ssoEnabled     whether OIDC single sign-on is configured
 * @param standalone     whether the controller runs standalone agents instead of launching cloud instances
 * @param controllerUrl  the configured base URL of this controller
 * @param version        the build version
 * @param buildTimestamp when this build was made, if known
 * @param textBanner     an optional banner to show on every page
 */
public record AuthConfig(boolean ssoEnabled, boolean standalone, String controllerUrl, String version,
                         Date buildTimestamp, String textBanner) {
}
