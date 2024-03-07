package com.intuit.tank.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.hibernate.Session;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.intuit.tank.project.User;
import com.intuit.tank.test.TestGroups;

import jakarta.persistence.PersistenceException;

public class BaseDaoTest {

    private BaseDao<User> dao;
    private Statistics statistics;

    @BeforeEach
    public void configure() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
        ctx.updateLoggers();
        dao = new UserDao();
        Session session = dao.getEntityManager().unwrap(Session.class);
        statistics = session.getSessionFactory().getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testfindUser() {
        // Arrange
        User entity = DaoTestUtil.createUserData("BaseTestUser0", "TestUser_Password", "BaseTestUser0@intuit.com", Collections.singleton("TestGroup0"));
        User daoEntity = dao.saveOrUpdate(entity);
        statistics.clear();

        // Act & Assert
        User returnEntity = dao.findById(daoEntity.getId());
        assertEquals(0, statistics.getQueryExecutionCount());
        assertEquals(1, statistics.getTransactionCount());
        assertEquals(0, statistics.getQueries().length);
        assertEquals(entity.getName(), returnEntity.getName());
        statistics.clear();

        // Act & Assert
        List<User> users = dao.findAll();
        assertEquals(1, statistics.getQueryExecutionCount());
        assertEquals(1, statistics.getTransactionCount());
        assertEquals(1, statistics.getQueries().length);
        for(User user: users) {
            if(user.getEmail().equals(entity.getEmail())) {
                assertEquals(entity.getName(), user.getName());
                break;
            }
        }
        // cleanup
        dao.delete(entity);
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testfindUsers() {
        // Arrange
        List<User> entities = Stream.of(
                DaoTestUtil.createUserData("BaseTestUser1", "TestUser_Password", "BaseTestUser1@intuit.com", Collections.singleton("TestGroup1")),
                DaoTestUtil.createUserData("BaseTestUser2", "TestUser_Password", "BaseTestUser2@intuit.com", Collections.singleton("TestGroup2")),
                DaoTestUtil.createUserData("BaseTestUser3", "TestUser_Password", "BaseTestUser3@intuit.com", Collections.singleton("TestGroup3")),
                DaoTestUtil.createUserData("BaseTestUser4", "TestUser_Password", "BaseTestUser4@intuit.com", Collections.singleton("TestGroup4"))
                ).collect(Collectors.toList());
        dao.persistCollection(entities);
        statistics.clear();

        // Act
        List<User> returnEntity = dao.findForIds(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

        // Assert
        assertEquals(1, statistics.getQueryExecutionCount());
        assertEquals(1, statistics.getTransactionCount());
        assertEquals(1, statistics.getQueries().length);
        assertEquals(4, returnEntity.size());

        // cleanup
        returnEntity.forEach(user -> dao.delete(user));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testPersistenceException() {
        List<User> entities = Stream.of(
                DaoTestUtil.createUserData("BaseTestUser1", "TestUser_Password", "BaseTestUser1@intuit.com", Collections.singleton("TestGroup10")),
                DaoTestUtil.createUserData("BaseTestUser1", "TestUser_Password", "BaseTestUser1@intuit.com", Collections.singleton("TestGroup11"))
        ).collect(Collectors.toList());

        assertThrows(PersistenceException.class, () -> dao.persistCollection(entities));

        dao.saveOrUpdate(entities.get(0));
        assertThrows(PersistenceException.class, () -> dao.saveOrUpdate(entities.get(1)));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void test_RevisionNumbers() {
        // Arrange
        User entity = DaoTestUtil.createUserData("BaseRevisionTestUser", "TestUser_Password", "BaseTestUser@intuit.com", Collections.singleton("TestRevisionGroup"));
        User daoEntity = dao.saveOrUpdate(entity);
        int revisionNumber = dao.getHeadRevisionNumber(daoEntity.getId());

        assertThrows(IllegalArgumentException.class, () -> dao.findRevision(daoEntity.getId(), 0));

        User user = dao.findRevision(daoEntity.getId(), revisionNumber);
        assert user != null;
        assertEquals(entity, user);

        // Cleanup
        dao.delete(user);
    }
}
