/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.config;

import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.User;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceUnauthorizedException;
import com.intuit.tank.rest.mvc.rest.models.Option;
import com.intuit.tank.rest.mvc.rest.models.UiOptions;
import com.intuit.tank.script.FailureTypes;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.settings.AgentConfig;
import com.intuit.tank.vm.settings.LocationsConfig;
import com.intuit.tank.vm.settings.LogicStepConfig;
import com.intuit.tank.vm.settings.ProductConfig;
import com.intuit.tank.vm.settings.SelectableItem;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.settings.VmInstanceType;
import com.intuit.tank.vm.settings.VmManagerConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.intuit.tank.rest.mvc.rest.security.SecurityTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConfigServiceV2ImplTest {

    private final ConfigServiceV2Impl service = new ConfigServiceV2Impl();
    private MockedConstruction<TankConfig> tankConfigs;
    private boolean standalone;

    @BeforeEach
    void setUp() {
        useConfig(true, Map.of());
        actAs(user("bob"));
        tankConfigs = Mockito.mockConstruction(TankConfig.class, (mock, context) -> {
            when(mock.getStandalone()).thenAnswer(i -> standalone);
            ProductConfig products = mock(ProductConfig.class);
            when(products.getProducts()).thenReturn(List.of(item("Payroll", "payroll")));
            when(mock.getProductConfig()).thenReturn(products);
            LocationsConfig locations = mock(LocationsConfig.class);
            when(locations.getLocations()).thenReturn(List.of(item("Lab", "lab")));
            when(mock.getLocationsConfig()).thenReturn(locations);
            VmManagerConfig vm = mock(VmManagerConfig.class);
            when(vm.getConfiguredRegions()).thenReturn(List.of(VMRegion.US_WEST_2, VMRegion.US_EAST));
            when(vm.getInstanceTypes()).thenReturn(List.of(VmInstanceType.builder().withName("c5.large")
                    .withUsers(500).withDefault(true).build()));
            when(mock.getVmManagerConfig()).thenReturn(vm);
            AgentConfig agent = mock(AgentConfig.class);
            Map<String, String> clients = new TreeMap<>(Map.of("JDK Client", "com.intuit.JdkClient", "Apache Client", "com.intuit.ApacheClient"));
            when(agent.getTankClientMap()).thenReturn(clients);
            when(agent.getResultsTypeMap()).thenReturn(Map.of());
            when(mock.getAgentConfig()).thenReturn(agent);
            LogicStepConfig logic = mock(LogicStepConfig.class);
            when(logic.getInsertBefore()).thenReturn("before");
            when(logic.getAppendAfter()).thenReturn("after");
            when(mock.getLogicStepConfig()).thenReturn(logic);
        });
    }

    @AfterEach
    void tearDown() {
        tankConfigs.close();
        reset();
    }

    private static SelectableItem item(String display, String value) {
        return new SelectableItem(display, value);
    }

    private static List<String> values(List<Option> options) {
        return options.stream().map(Option::value).collect(Collectors.toList());
    }

    @Test
    void getOptions() {
        UiOptions options = service.getOptions();
        assertEquals(List.of(Option.of("payroll", "Payroll")), options.products());
        assertEquals(List.of(Option.of("lab", "Lab")), options.locations());
        assertEquals(List.of("US_EAST", "US_WEST_2"), values(options.regions()), "configured regions in enum order");
        assertEquals("c5.large", options.vmInstanceTypes().get(0).value());
        assertEquals(500, options.vmInstanceTypes().get(0).usersPerAgent());
        assertTrue(options.vmInstanceTypes().get(0).isDefault());
        assertEquals(List.of("com.intuit.ApacheClient", "com.intuit.JdkClient"), values(options.httpClients()));
        assertEquals("Apache Client", options.httpClients().get(0).label());
        assertEquals(List.of("script", "time"), values(options.terminationPolicies()));
        assertEquals("before", options.logicStep().insertBefore());
        assertEquals("after", options.logicStep().appendAfter());
        assertEquals(FailureTypes.values().length, options.stepOptions().get("failureTypes").size());
        assertFalse(values(options.filterOptions().get("onFailOptions")).contains(FailureTypes.gotoGroupRequest.getValue()));
        assertTrue(options.filterOptions().keySet().containsAll(
                List.of("conditionScopes", "conditionMatches", "actionTypes", "addActionScopes", "removeActionScopes", "replaceActionScopes")));
    }

    @Test
    void getOptions_standaloneOffersOnlyStandaloneRegion() {
        standalone = true;
        assertEquals(List.of("STANDALONE"), values(service.getOptions().regions()));
    }

    @Test
    void getOptions_requiresUser() {
        reset();
        useConfig(false, Map.of());
        assertThrows(GenericServiceUnauthorizedException.class, service::getOptions);
    }

    @Test
    void getUserNames_sortedWithoutDeletedUsers() {
        List<User> users = List.of(User.builder().name("carol").build(), User.builder().name("Alice").build(),
                User.builder().name(ConfigServiceV2Impl.DELETED_USER_PREFIX + "7").build(), User.builder().name("bob").build());
        try (MockedConstruction<UserDao> daos = Mockito.mockConstruction(UserDao.class,
                (mock, context) -> when(mock.findAll()).thenReturn(users))) {
            assertEquals(List.of("Alice", "bob", "carol"), service.getUserNames());
        }
    }
}
