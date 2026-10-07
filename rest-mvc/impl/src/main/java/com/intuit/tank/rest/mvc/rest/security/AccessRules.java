/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.security;

import com.intuit.tank.project.OwnableEntity;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.settings.AccessRight;
import com.intuit.tank.vm.settings.SecurityConfig;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Predicate;

/**
 * The Tank permission rules, shared by the JSF {@code Security} bean and the REST layer so both
 * enforce the same model:
 * <ul>
 *     <li>members of the {@code admin} group have every right</li>
 *     <li>other users have a right when one of their groups is listed for it in the
 *     {@code <security><restrictions>} section of settings.xml</li>
 *     <li>the creator of an entity is its owner</li>
 * </ul>
 */
public final class AccessRules {

    private AccessRules() {
    }

    public static boolean isAdmin(Predicate<String> isInRole) {
        return isInRole.test(TankConstants.TANK_GROUP_ADMIN);
    }

    public static boolean hasRight(AccessRight right, Predicate<String> isInRole, SecurityConfig config) {
        if (isAdmin(isInRole)) {
            return true;
        }
        if (config == null) {
            return false;
        }
        List<String> associatedGroups = config.getRestrictionMap().get(right.name());
        return associatedGroups != null && associatedGroups.stream().anyMatch(isInRole);
    }

    public static boolean isOwner(String userName, OwnableEntity entity) {
        return entity != null
                && StringUtils.isNotEmpty(entity.getCreator())
                && StringUtils.isNotEmpty(userName)
                && entity.getCreator().equals(userName);
    }
}
