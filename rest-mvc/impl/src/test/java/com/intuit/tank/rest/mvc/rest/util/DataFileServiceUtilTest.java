package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.dao.DataFileDao;
import com.intuit.tank.datafiles.models.DataFileDescriptor;
import com.intuit.tank.project.DataFile;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class DataFileServiceUtilTest {

    @Test
    void dataFileToDescriptor_convertsAllFields() {
        DataFile df = new DataFile();
        df.setId(5);
        df.setPath("test-data.csv");
        df.setCreator("admin");
        df.setComments("test data");
        df.setCreated(new Date());
        df.setModified(new Date());

        DataFileDescriptor desc = DataFileServiceUtil.dataFileToDescriptor(df);

        assertEquals(5, desc.getId());
        assertEquals("test-data.csv", desc.getName());
        assertEquals("admin", desc.getCreator());
        assertEquals("test data", desc.getComments());
        assertNotNull(desc.getDataUrl());
        assertTrue(desc.getDataUrl().contains("5"));
    }

    @Test
    void descriptorToDataFile_convertsAllFields() {
        DataFileDescriptor desc = DataFileDescriptor.builder()
                .withId(3)
                .withName("myfile.csv")
                .withCreator("user")
                .withComments("desc")
                .withCreated(new Date())
                .withModified(new Date())
                .build();

        DataFileDao dao = mock(DataFileDao.class);
        DataFile df = DataFileServiceUtil.descriptorToDataFile(dao, desc);

        assertEquals(3, df.getId());
        assertEquals("myfile.csv", df.getPath());
        assertEquals("user", df.getCreator());
    }

    @Test
    void descriptorToDataFile_handlesNullId() {
        DataFileDescriptor desc = DataFileDescriptor.builder()
                .withName("noId.csv")
                .build();

        DataFileDao dao = mock(DataFileDao.class);
        DataFile df = DataFileServiceUtil.descriptorToDataFile(dao, desc);

        assertEquals(0, df.getId());
    }
}
