package com.intuit.tank.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
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
        User u = DaoTestUtil.createUserData("TestUser1", "TestUser1_Password", "TestUser1@intuit.com", "TestGroup1");
        List<User> entities = new ArrayList<User>();
        entities.add(u);
        dao.persistCollection(entities);
        
        List<User> users = dao.findAll();
        User result = null;
        for(User user: users) {
            if(user.getEmail().equals(u.getEmail())) {
                result = user;
                break;
            }
        }
        assertEquals(u.getName(), result.getName());
    }
}
