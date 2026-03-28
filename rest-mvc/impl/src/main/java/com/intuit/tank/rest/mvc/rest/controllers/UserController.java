/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.dao.GroupDao;
import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.Group;
import com.intuit.tank.project.User;
import com.intuit.tank.vm.common.PasswordEncoder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/v2/users", produces = { MediaType.APPLICATION_JSON_VALUE })
@Tag(name = "Users")
public class UserController {

    private static final Logger LOG = LogManager.getLogger(UserController.class);

    @RequestMapping(method = RequestMethod.GET)
    @Operation(description = "Returns all users", summary = "Get all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all users")
    })
    public ResponseEntity<List<UserTO>> getAllUsers() {
        List<UserTO> users = new UserDao().findAll().stream()
                .map(UserTO::from)
                .collect(Collectors.toList());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @Operation(description = "Returns a user by ID", summary = "Get user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<UserTO> getUser(
            @Parameter(description = "User ID") @PathVariable("id") int id) {
        User user = new UserDao().findById(id);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(UserTO.from(user), HttpStatus.OK);
    }

    @RequestMapping(value = "/groups", method = RequestMethod.GET)
    @Operation(description = "Returns all available groups", summary = "Get all groups")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all groups")
    })
    public ResponseEntity<List<String>> getAllGroups() {
        List<String> groups = new GroupDao().findAll().stream()
                .map(Group::getName)
                .sorted()
                .collect(Collectors.toList());
        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Creates a new user", summary = "Create user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    public ResponseEntity<UserTO> createUser(@RequestBody UserRequest request) {
        if (StringUtils.isBlank(request.getName()) || StringUtils.isBlank(request.getEmail())
                || StringUtils.isBlank(request.getPassword())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        UserDao userDao = new UserDao();
        if (userDao.findByUserName(request.getName()) != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(PasswordEncoder.encodePassword(request.getPassword()));
        applyGroups(user, request.getGroups());
        user = userDao.saveOrUpdate(user);
        LOG.info("Created user: {}", user.getName());
        return new ResponseEntity<>(UserTO.from(user), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @Operation(description = "Updates an existing user", summary = "Update user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<UserTO> updateUser(
            @Parameter(description = "User ID") @PathVariable("id") int id,
            @RequestBody UserRequest request) {
        UserDao userDao = new UserDao();
        User user = userDao.findById(id);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (StringUtils.isNotBlank(request.getEmail())) {
            user.setEmail(request.getEmail());
        }
        if (StringUtils.isNotBlank(request.getPassword())) {
            user.setPassword(PasswordEncoder.encodePassword(request.getPassword()));
        }
        if (request.getGroups() != null) {
            applyGroups(user, request.getGroups());
        }
        user = userDao.saveOrUpdate(user);
        LOG.info("Updated user: {}", user.getName());
        return new ResponseEntity<>(UserTO.from(user), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Operation(description = "Deletes a user", summary = "Delete user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID") @PathVariable("id") int id) {
        UserDao userDao = new UserDao();
        User user = userDao.findById(id);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        userDao.delete(user);
        LOG.info("Deleted user id={}", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/{id}/token", method = RequestMethod.POST)
    @Operation(description = "Generates an API token for a user", summary = "Generate API token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token generated"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<Map<String, String>> generateApiToken(
            @Parameter(description = "User ID") @PathVariable("id") int id) {
        UserDao userDao = new UserDao();
        User user = userDao.findById(id);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        user.generateApiToken();
        user = userDao.saveOrUpdate(user);
        return new ResponseEntity<>(Map.of("apiToken", user.getApiToken()), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/token", method = RequestMethod.DELETE)
    @Operation(description = "Deletes the API token for a user", summary = "Delete API token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Token deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<Void> deleteApiToken(
            @Parameter(description = "User ID") @PathVariable("id") int id) {
        UserDao userDao = new UserDao();
        User user = userDao.findById(id);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        user.deleteApiToken();
        userDao.saveOrUpdate(user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private void applyGroups(User user, List<String> groupNames) {
        user.getGroups().clear();
        if (groupNames == null) return;
        GroupDao groupDao = new GroupDao();
        for (String name : groupNames) {
            user.addGroup(groupDao.getOrCreateGroup(name));
        }
    }

    // ---- Transfer Objects ----

    public static class UserTO {
        public int id;
        public String name;
        public String email;
        public String apiToken;
        public Instant lastLoginTs;
        public List<String> groups;

        public static UserTO from(User u) {
            UserTO to = new UserTO();
            to.id = u.getId();
            to.name = u.getName();
            to.email = u.getEmail();
            to.apiToken = u.getApiToken();
            to.lastLoginTs = u.getLastLoginTs();
            to.groups = u.getGroups().stream().map(Group::getName).sorted().collect(Collectors.toList());
            return to;
        }
    }

    public static class UserRequest {
        private String name;
        private String email;
        private String password;
        private List<String> groups;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public List<String> getGroups() { return groups; }
        public void setGroups(List<String> groups) { this.groups = groups; }
    }
}
