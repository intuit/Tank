package com.intuit.tank.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.intuit.tank.project.User;
import com.intuit.tank.test.TestGroups;

public class BaseDaoTest {

    private BaseDao<User> dao;

    @BeforeEach
    public void configure() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
        ctx.updateLoggers();
        dao = new UserDao();
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testBaseDao() throws Exception {
        User entity = DaoTestUtil.createUserData("BaseTestUser1", "TestUser1_Password", "BaseTestUser1@intuit.com", Collections.singleton("TestGroup1"));
        List<User> entities = new ArrayList<User>();
        entities.add(entity);
        dao.persistCollection(entities);
        
        List<User> users = dao.findAll();
        for(User user: users) {
            if(user.getEmail().equals(entity.getEmail())) {
                assertEquals(entity.getName(), user.getName());
                break;
            }
        }
        dao.delete(entity);
    }
}
