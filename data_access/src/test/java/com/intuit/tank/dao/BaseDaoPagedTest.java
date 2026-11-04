/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.dao;

import com.intuit.tank.project.Project;
import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class BaseDaoPagedTest {

    private final ProjectDao dao = new ProjectDao();
    private final List<Integer> created = new ArrayList<>();
    /** unique marker so other tests' rows don't affect the counts */
    private String tag;

    @BeforeEach
    void createProjects() {
        tag = "paged" + UUID.randomUUID().toString().substring(0, 8);
        save("b-" + tag, "alice", "Payroll");
        save("a-" + tag, "bob", "Payroll");
        save("c-" + tag, "alice", "Tax");
        save("d_" + tag, "alice", "Tax");
    }

    @AfterEach
    void deleteProjects() {
        created.forEach(dao::delete);
    }

    private void save(String name, String creator, String product) {
        Project p = DaoTestUtil.createProject();
        p.setName(name);
        p.setCreator(creator);
        p.setProductName(product);
        created.add(dao.saveOrUpdate(p).getId());
    }

    private static List<String> names(PagedResult<Project> result) {
        return result.items().stream().map(Project::getName).collect(Collectors.toList());
    }

    private PagedQuery search(int page, int size, String sort, boolean ascending, Map<String, Object> equalTo) {
        return new PagedQuery(page, size, sort, ascending, equalTo, tag, List.of(Project.PROPERTY_NAME));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void pagesAreSortedAndCounted() {
        PagedResult<Project> first = dao.findPaged(search(0, 3, Project.PROPERTY_NAME, true, Map.of()));
        assertEquals(4, first.total());
        assertEquals(List.of("a-" + tag, "b-" + tag, "c-" + tag), names(first));

        PagedResult<Project> second = dao.findPaged(search(1, 3, Project.PROPERTY_NAME, true, Map.of()));
        assertEquals(4, second.total());
        assertEquals(List.of("d_" + tag), names(second));

        assertEquals(List.of(), names(dao.findPaged(search(5, 3, Project.PROPERTY_NAME, true, Map.of()))));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void descendingSort() {
        PagedResult<Project> page = dao.findPaged(search(0, 2, Project.PROPERTY_NAME, false, Map.of()));
        assertEquals(List.of("d_" + tag, "c-" + tag), names(page));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void equalityFilters() {
        PagedResult<Project> page = dao.findPaged(search(0, 10, Project.PROPERTY_NAME, true,
                Map.of("creator", "alice", "productName", "Tax")));
        assertEquals(2, page.total());
        assertEquals(List.of("c-" + tag, "d_" + tag), names(page));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void searchIsCaseInsensitiveAndTreatsWildcardsLiterally() {
        PagedQuery upper = new PagedQuery(0, 10, Project.PROPERTY_NAME, true, Map.of(), "B-" + tag.toUpperCase(),
                List.of(Project.PROPERTY_NAME));
        assertEquals(List.of("b-" + tag), names(dao.findPaged(upper)));

        PagedQuery underscore = new PagedQuery(0, 10, Project.PROPERTY_NAME, true, Map.of(), "_" + tag,
                List.of(Project.PROPERTY_NAME));
        assertEquals(List.of("d_" + tag), names(dao.findPaged(underscore)), "_ must not match any character");
    }

    @Test
    public void escapeLike() {
        assertEquals("a!%b!_c!!", BaseDao.escapeLike("a%b_c!"));
    }

    @Test
    public void invalidPageArguments() {
        assertThrows(IllegalArgumentException.class, () -> new PagedQuery(-1, 10, "name", true, null, null, null));
        assertThrows(IllegalArgumentException.class, () -> new PagedQuery(0, 0, "name", true, null, null, null));
    }
}
