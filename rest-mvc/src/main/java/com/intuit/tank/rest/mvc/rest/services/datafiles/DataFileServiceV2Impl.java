/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.datafiles;

import com.intuit.tank.dao.DataFileDao;
import com.intuit.tank.project.DataFile;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceDeleteException;
import com.intuit.tank.rest.mvc.rest.util.DataFileServiceUtil;
import com.intuit.tank.rest.mvc.rest.models.datafiles.DataFileDescriptor;
import com.intuit.tank.rest.mvc.rest.models.datafiles.DataFileDescriptorContainer;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.storage.FileData;
import com.intuit.tank.storage.FileStorage;
import com.intuit.tank.storage.FileStorageFactory;
import com.intuit.tank.util.DataFileUtil;
import com.intuit.tank.vm.settings.TankConfig;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

@Service
public class DataFileServiceV2Impl implements DataFileServiceV2 {

    private static final Logger LOGGER = LogManager.getLogger(DataFileServiceV2Impl.class);

    @Override
    public String ping() {
        return "PONG " + getClass().getInterfaces()[0].getSimpleName();
    }


    @Override
    public DataFileDescriptor getDatafile(Integer datafileId) {
        try {
            DataFileDao dao = new DataFileDao();
            DataFile df = dao.findById(datafileId);
            if (df != null) {
                return DataFileServiceUtil.dataFileToDescriptor(df);
            }
            return null;
        } catch (Exception e) {
            LOGGER.error("Error returning datafile: " + e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("datafiles", "datafile", e);
        }
    }

    @Override
    public DataFileDescriptorContainer getDatafiles() {
        try {
            DataFileDao dao = new DataFileDao();
            List<DataFile> all = dao.findAll();
            List<DataFileDescriptor> result = all.stream().map(DataFileServiceUtil::dataFileToDescriptor).collect(Collectors.toList());
            return new DataFileDescriptorContainer(result);
        } catch (Exception e) {
            LOGGER.error("Error returning all datafiles: " + e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("datafiles", "all datafiles", e);
        }
    }

    @Override
    public StreamingResponseBody getDatafileContent(Integer datafileId, Integer offset, Integer numLines){
        DataFileDao dataFileDao = new DataFileDao();
        DataFile dataFile = dataFileDao.findById(datafileId);
        if (dataFile == null){
            return null;
        }
        offset = offset == null ? 0 : offset;
        numLines = numLines == null ? -1 : numLines;
        return getStreamingOutput(offset, numLines, dataFile);
    }

    private StreamingResponseBody getStreamingOutput(final Integer offset, final Integer numLines, DataFile dataFile) {
        final FileStorage fileStorage = FileStorageFactory.getFileStorage(new TankConfig().getDataFileStorageDir(), false);
        final FileData fd = DataFileUtil.getFileData(dataFile);
        return outputStream -> {
            try (   BufferedReader in = new BufferedReader(new InputStreamReader(fileStorage.readFileData(fd), StandardCharsets.UTF_8));
                    PrintWriter out = new PrintWriter(outputStream) ) {
                int nl = numLines;
                if (!fd.getFileName().toLowerCase().endsWith(".csv")) {
                    nl = -1;
                }
                // Read File Line By Line
                String strLine;
                int lineNum = 0;
                int os = offset < 0 ? 0 : offset;
                while ((strLine = in.readLine()) != null && (nl < 0 || lineNum < (os + numLines))) {
                    if (numLines < 0 || lineNum >= os) {
                        if (os == 0) {
                            out.println(strLine);
                        } else {
                            if (lineNum >= os) {
                                out.println(strLine);
                            }
                        }
                    }
                    lineNum++;
                }
            } catch (IOException e) {
                LOGGER.error("Error returning datafile content: " + e.getMessage(), e);
                throw new GenericServiceResourceNotFoundException("datafiles", "datafile content", e);
            }
        };
    }

    public Map<String, StreamingResponseBody> downloadDatafile(Integer datafileId){
        Map<String, StreamingResponseBody> payload = new HashMap<String, StreamingResponseBody>();
        DataFileDao dataFileDao = new DataFileDao();
        DataFile dataFile = dataFileDao.findById(datafileId);
        if (dataFile == null){
            return null;
        } else {
            StreamingResponseBody streamingOutput = getStreamingOutput(0, -1, dataFile);
            String filename = dataFile.getPath();
            payload.put(filename, streamingOutput);
            return payload;
        }
    }

    @Override
    public Map<String, String> uploadDatafile(Integer datafileId, String contentEncoding, MultipartFile file) throws IOException {
        Map<String, String> payload = new HashMap<>();
        datafileId = datafileId == null ? 0 : datafileId;
        contentEncoding = contentEncoding == null ? "" : contentEncoding;
        InputStream decompressed = StringUtils.equalsIgnoreCase(contentEncoding, "gzip") ?
                new GZIPInputStream(file.getInputStream()) :
                file.getInputStream();
        try {
            DataFileDao dao = new DataFileDao();
            DataFile dataFile = dao.findById(datafileId);
            if (dataFile == null){
                dataFile = new DataFile();
                dataFile.setCreator("System");
                dataFile.setId(0);
            } else {
                payload.put("message", "Datafile with datafile ID " + datafileId + " overwritten with new datafile");
            }

            String newFilename = file.getOriginalFilename().replace(".gz", "");
            dataFile.setPath(newFilename);
            dataFile.setFileName(newFilename);

            dao.storeDataFile(dataFile, decompressed);

            if (datafileId.equals(0)) {
                payload.put("message", "Datafile with new datafile ID " + dataFile.getId() + " has been uploaded");
            } else {
                if (!payload.containsKey("message")) {
                    payload.put("message", "Existing dataFile with dataFile ID " + datafileId + " could not be found, created new datafile " + dataFile.getId());
                }
            }
            payload.put("datafileId", Integer.toString(dataFile.getId()));
        } catch (Exception e) {
            LOGGER.error("Error uploading datafile: " + e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException("datafiles", "new datafile via datafile upload", e);
        } finally {
            try {
                decompressed.close();
            } catch (IOException e) {}
        }
        return payload;
    }


    @Override
    public String deleteDatafile(Integer datafileId){
        try {
            DataFileDao dao = new DataFileDao();
            DataFile dataFile = dao.findById(datafileId);
            if (dataFile == null) {
                LOGGER.warn("Datafile with datafile id " + datafileId + " does not exist");
                return "Datafile with datafile id " + datafileId + " does not exist";
            } else {
                dao.delete(dataFile);
                return "";
            }
        } catch (Exception e) {
            LOGGER.error("Error deleting datafile : " + e, e);
            throw new GenericServiceDeleteException("datafile", "datafile", e);
        }
    }
}
