/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.datafiles;

import com.intuit.tank.dao.DataFileDao;
import com.intuit.tank.dao.PagedQuery;
import com.intuit.tank.dao.PagedResult;
import com.intuit.tank.project.BaseEntity;
import com.intuit.tank.project.OwnableEntity;
import com.intuit.tank.rest.mvc.rest.cloud.MessageEventSender;
import com.intuit.tank.rest.mvc.rest.cloud.ServletInjector;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceBadRequestException;
import com.intuit.tank.rest.mvc.rest.models.BulkDeleteResult;
import com.intuit.tank.rest.mvc.rest.models.DataFileBatchResult;
import com.intuit.tank.rest.mvc.rest.models.DataFilePreview;
import com.intuit.tank.rest.mvc.rest.models.DataFileSummary;
import com.intuit.tank.rest.mvc.rest.models.PageResponse;
import com.intuit.tank.rest.mvc.rest.util.PageRequests;
import com.intuit.tank.rest.mvc.rest.util.UploadedArchive;
import com.intuit.tank.vm.settings.ModificationType;
import com.intuit.tank.vm.settings.ModifiedEntityMessage;
import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import com.intuit.tank.project.DataFile;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceDeleteException;
import com.intuit.tank.rest.mvc.rest.util.DataFileServiceUtil;
import com.intuit.tank.datafiles.models.DataFileDescriptor;
import com.intuit.tank.datafiles.models.DataFileDescriptorContainer;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.storage.FileData;
import com.intuit.tank.storage.FileStorage;
import com.intuit.tank.storage.FileStorageFactory;
import com.intuit.tank.util.DataFileUtil;
import com.intuit.tank.vm.settings.TankConfig;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.io.IOUtils;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceForbiddenAccessException;
import com.intuit.tank.rest.mvc.rest.security.RestAuthorization;
import com.intuit.tank.vm.settings.AccessRight;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

@Service
public class DataFileServiceV2Impl implements DataFileServiceV2 {

    private static final Logger LOGGER = LogManager.getLogger(DataFileServiceV2Impl.class);
    private static final String SERVICE = "datafiles";

    static final int DEFAULT_PREVIEW_LINES = 100;
    static final int MAX_PREVIEW_LINES = 1000;
    static final int MAX_BATCH_FILES = 50;
    static final int MAX_BULK_DELETE = 100;
    /** The file types the web UI accepts as data files. */
    static final String[] DATAFILE_EXTENSIONS = { "csv", "txt", "xml" };

    private static final Map<String, String> SORTABLE_FIELDS = Map.of(
            "id", BaseEntity.PROPERTY_ID,
            "name", "path",
            "owner", OwnableEntity.PROPERTY_CREATOR,
            "created", BaseEntity.PROPERTY_CREATE,
            "modified", BaseEntity.PROPERTY_MODIFIED);

    @Autowired
    private ServletContext servletContext;

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
            LOGGER.error("Error returning datafile: {}", e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("datafiles", "datafile", e);
        }
    }

    @Override
    public DataFileDescriptorContainer getDatafiles() {
        try {
            DataFileDao dao = new DataFileDao();
            List<DataFile> all = dao.findAll();
            List<DataFileDescriptor> result = all.stream().map(DataFileServiceUtil::dataFileToDescriptor).collect(Collectors.toList());
            return DataFileDescriptorContainer.builder().withDataFiles(result).build();
        } catch (Exception e) {
            LOGGER.error("Error returning all datafiles: {}", e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("datafiles", "all datafiles", e);
        }
    }

    @Override
    public Map<Integer, String> getAllDatafileNames(){
        try {
            List<DataFile> all = new DataFileDao().findAll();
            return all.stream().collect(Collectors.toMap(DataFile::getId, DataFile::getPath));
        } catch (Exception e) {
            LOGGER.error("Error returning all datafile names: {}", e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("datafiles", "all datafile names", e);
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
                LOGGER.error("Error returning datafile content: {}", e.getMessage(), e);
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
        try (BufferedReader bufferedReader = StringUtils.equalsIgnoreCase(contentEncoding, "gzip") ?
                new BufferedReader(new InputStreamReader(new GZIPInputStream(file.getInputStream()))) :
                new BufferedReader(new InputStreamReader(file.getInputStream()));
                InputStream decompressed = IOUtils.toInputStream(IOUtils.toString(bufferedReader), StandardCharsets.UTF_8)) { // correctly handles compressed datafile content
            DataFileDao dao = new DataFileDao();
            DataFile dataFile = dao.findById(datafileId);
            if (dataFile == null) {
                RestAuthorization.requireRight(AccessRight.CREATE_DATAFILE, "datafiles");
                dataFile = new DataFile();
                dataFile.setCreator(RestAuthorization.currentUserName());
                dataFile.setId(0);
            } else {
                RestAuthorization.requireRightOrOwner(AccessRight.EDIT_DATAFILE, dataFile, "datafiles");
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
        } catch (GenericServiceForbiddenAccessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error uploading datafile: " + e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException("datafiles", "new datafile via datafile upload", e);
        }
        return payload;
    }


    @Override
    public String deleteDatafile(Integer datafileId){
        try {
            DataFileDao dao = new DataFileDao();
            DataFile dataFile = dao.findById(datafileId);
            if (dataFile == null) {
                LOGGER.warn("Datafile with datafile id {} does not exist", datafileId);
                return "Datafile with datafile id " + datafileId + " does not exist";
            } else {
                RestAuthorization.requireRightOrOwner(AccessRight.DELETE_DATAFILE, dataFile, "datafiles");
                dao.delete(dataFile);
                return "";
            }
        } catch (GenericServiceForbiddenAccessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error deleting datafile : {}", e, e);
            throw new GenericServiceDeleteException("datafile", "datafile", e);
        }
    }

    @Override
    public ContentPage readDatafileContent(Integer datafileId, Integer offset, Integer numLines) {
        DataFile dataFile = datafileId != null ? new DataFileDao().findById(datafileId) : null;
        if (dataFile == null) {
            return null;
        }
        int limit = numLines == null || !isCsv(dataFile) ? -1 : numLines;
        Lines lines = readLines(dataFile, offset == null ? 0 : Math.max(0, offset), limit);
        StringBuilder text = new StringBuilder();
        for (String line : lines.page) {
            text.append(line).append(System.lineSeparator());
        }
        return new ContentPage(text.toString(), lines.total);
    }

    @Override
    public PageResponse<DataFileSummary> listDatafiles(Integer page, Integer size, String sort, String owner, String q) {
        RestAuthorization.requireUser(SERVICE);
        Map<String, Object> filters = new HashMap<>();
        filters.put(OwnableEntity.PROPERTY_CREATOR, owner);
        PagedQuery query = PageRequests.toQuery(SERVICE, page, size, sort, SORTABLE_FIELDS, "modified,desc", filters, q,
                List.of("path", "comments"));
        PagedResult<DataFile> result = new DataFileDao().findPaged(query);
        List<DataFileSummary> items = result.items().stream()
                .map(f -> new DataFileSummary(f.getId(), f.getPath(), f.getComments(), f.getCreator(), f.getCreated(),
                        f.getModified()))
                .collect(Collectors.toList());
        return new PageResponse<>(items, result.total(), query.page(), query.size());
    }

    @Override
    public DataFileBatchResult uploadDatafiles(List<MultipartFile> files) {
        String owner = RestAuthorization.requireUser(SERVICE).getName();
        RestAuthorization.requireRight(AccessRight.CREATE_DATAFILE, SERVICE);
        if (files == null || files.isEmpty()) {
            throw new GenericServiceBadRequestException(SERVICE, "files", "at least one file is required");
        }
        if (files.size() > MAX_BATCH_FILES) {
            throw new GenericServiceBadRequestException(SERVICE, "files", "at most " + MAX_BATCH_FILES + " files at a time");
        }
        List<DataFileBatchResult.Created> created = new ArrayList<>();
        List<String> skipped = new ArrayList<>();
        DataFileDao dao = new DataFileDao();
        for (MultipartFile file : files) {
            String uploadName = Objects.requireNonNullElse(file.getOriginalFilename(), "");
            try (UploadedArchive archive = new UploadedArchive(uploadName, file.getInputStream(), DATAFILE_EXTENSIONS)) {
                for (UploadedArchive.UploadedFile entry = archive.next(); entry != null; entry = archive.next()) {
                    DataFile dataFile = new DataFile();
                    dataFile.setPath(entry.name());
                    dataFile.setCreator(owner);
                    dataFile = dao.storeDataFile(dataFile, entry.content());
                    created.add(new DataFileBatchResult.Created(dataFile.getId(), dataFile.getPath()));
                    sendEvent(dataFile, ModificationType.ADD);
                }
                skipped.addAll(archive.skipped());
            } catch (IOException | RuntimeException e) {
                LOGGER.error("Error uploading {} after creating {}: {}", uploadName, created, e.getMessage(), e);
                throw new GenericServiceBadRequestException(SERVICE, "files", "could not read " + uploadName + ": "
                        + e.getMessage() + (created.isEmpty() ? "" : "; data files already created: " + created));
            }
        }
        LOGGER.info("{} uploaded data files {}", owner, created);
        return new DataFileBatchResult(created, skipped);
    }

    @Override
    public DataFilePreview previewDatafile(Integer datafileId, Integer offset, Integer lines) {
        RestAuthorization.requireUser(SERVICE);
        int start = offset == null ? 0 : offset;
        int limit = lines == null ? DEFAULT_PREVIEW_LINES : lines;
        if (start < 0) {
            throw new GenericServiceBadRequestException(SERVICE, "offset", "offset must not be negative");
        }
        if (limit < 1 || limit > MAX_PREVIEW_LINES) {
            throw new GenericServiceBadRequestException(SERVICE, "lines", "lines must be from 1 to " + MAX_PREVIEW_LINES);
        }
        DataFile dataFile = datafileId != null ? new DataFileDao().findById(datafileId) : null;
        if (dataFile == null) {
            throw new GenericServiceResourceNotFoundException(SERVICE, "datafile " + datafileId, null);
        }
        Lines page = readLines(dataFile, start, limit);
        return new DataFilePreview(dataFile.getId(), dataFile.getPath(), start, page.page, page.total);
    }

    @Override
    public BulkDeleteResult deleteDatafiles(List<Integer> datafileIds) {
        RestAuthorization.requireUser(SERVICE);
        if (datafileIds == null || datafileIds.isEmpty()) {
            throw new GenericServiceBadRequestException(SERVICE, "ids", "at least one id is required");
        }
        List<Integer> ids = datafileIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (ids.size() > MAX_BULK_DELETE) {
            throw new GenericServiceBadRequestException(SERVICE, "ids", "at most " + MAX_BULK_DELETE + " ids at a time");
        }
        DataFileDao dao = new DataFileDao();
        List<DataFile> found = new ArrayList<>();
        List<Integer> notFound = new ArrayList<>();
        for (Integer id : ids) {
            DataFile dataFile = dao.findById(id);
            if (dataFile == null) {
                notFound.add(id);
            } else {
                found.add(dataFile);
            }
        }
        // check every data file before deleting any, so a forbidden one leaves all of them in place
        for (DataFile dataFile : found) {
            RestAuthorization.requireRightOrOwner(AccessRight.DELETE_DATAFILE, dataFile, SERVICE);
        }
        List<Integer> deleted = new ArrayList<>();
        for (DataFile dataFile : found) {
            try {
                dao.delete(dataFile.getId());
            } catch (RuntimeException e) {
                LOGGER.error("Error deleting data file {}: {}", dataFile.getId(), e.getMessage(), e);
                throw new GenericServiceDeleteException(SERVICE, "data file " + dataFile.getId()
                        + (deleted.isEmpty() ? "" : " (already deleted: " + deleted + ")"), e);
            }
            deleted.add(dataFile.getId());
            sendEvent(dataFile, ModificationType.DELETE);
        }
        LOGGER.info("{} deleted data files {}", RestAuthorization.currentUserName(), deleted);
        return new BulkDeleteResult(deleted, notFound);
    }

    private record Lines(List<String> page, int total) {
    }

    /**
     * Reads the whole file, keeping the lines from {@code offset} (at most {@code limit} of them, or all when
     * {@code limit} is negative) and counting every line.
     */
    private static Lines readLines(DataFile dataFile, int offset, int limit) {
        FileStorage fileStorage = FileStorageFactory.getFileStorage(new TankConfig().getDataFileStorageDir(), false);
        FileData fd = DataFileUtil.getFileData(dataFile);
        List<String> page = new ArrayList<>();
        int lineNum = 0;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(fileStorage.readFileData(fd), StandardCharsets.UTF_8))) {
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                if (lineNum >= offset && (limit < 0 || page.size() < limit)) {
                    page.add(line);
                }
                lineNum++;
            }
        } catch (IOException | UncheckedIOException e) {
            LOGGER.error("Error reading data file {}: {}", dataFile.getId(), e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException(SERVICE, "datafile content", e);
        }
        return new Lines(page, lineNum);
    }

    private static boolean isCsv(DataFile dataFile) {
        String name = DataFileUtil.getFileData(dataFile).getFileName();
        return name != null && name.toLowerCase().endsWith(".csv");
    }

    /**
     * Tells the web UI's data file cache. The change is already saved, so a failure is logged, not thrown.
     */
    private void sendEvent(DataFile dataFile, ModificationType type) {
        try {
            new ServletInjector<MessageEventSender>().getManagedBean(servletContext, MessageEventSender.class)
                    .sendEvent(new ModifiedEntityMessage(DataFile.class, dataFile.getId(), type));
        } catch (RuntimeException e) {
            LOGGER.warn("Data file {} was changed but the change event could not be sent: {}", dataFile.getId(), e.toString());
        }
    }
}
