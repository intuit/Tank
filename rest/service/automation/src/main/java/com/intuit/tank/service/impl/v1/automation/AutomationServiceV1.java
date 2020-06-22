/*
 * #%L
 * Automation Rest Service
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package com.intuit.tank.service.impl.v1.automation;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletContext;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.intuit.tank.api.model.v1.automation.AutomationJobRegion;
import com.intuit.tank.api.model.v1.automation.AutomationRequest;
import com.intuit.tank.api.model.v1.automation.ApplyFiltersRequest;
import com.intuit.tank.api.model.v1.automation.CreateJobRequest;
import com.intuit.tank.api.model.v1.automation.CreateJobRegion;
import com.intuit.tank.api.service.v1.automation.AutomationService;
import com.intuit.tank.common.ScriptUtil;
import com.intuit.tank.dao.BaseDao;
import com.intuit.tank.dao.DataFileDao;
import com.intuit.tank.dao.FilterDao;
import com.intuit.tank.dao.FilterGroupDao;
import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.dao.JobNotificationDao;
import com.intuit.tank.dao.JobQueueDao;
import com.intuit.tank.dao.JobRegionDao;
import com.intuit.tank.dao.ProjectDao;
import com.intuit.tank.dao.ScriptDao;
import com.intuit.tank.dao.WorkloadDao;
import com.intuit.tank.harness.StopBehavior;
import com.intuit.tank.project.BaseEntity;
import com.intuit.tank.project.DataFile;
import com.intuit.tank.project.EntityVersion;
import com.intuit.tank.project.JobConfiguration;
import com.intuit.tank.project.JobDetailFormatter;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.JobQueue;
import com.intuit.tank.project.JobRegion;
import com.intuit.tank.project.JobValidator;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptFilter;
import com.intuit.tank.project.ScriptFilterGroup;
import com.intuit.tank.project.ScriptGroup;
import com.intuit.tank.project.ScriptGroupStep;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.project.Workload;
import com.intuit.tank.script.processor.ScriptProcessor;
import com.intuit.tank.script.util.ScriptFilterUtil;
import com.intuit.tank.service.impl.v1.cloud.JobController;
import com.intuit.tank.service.util.ResponseUtil;
import com.intuit.tank.service.util.ServletInjector;
import com.intuit.tank.util.TestParamUtil;
import com.intuit.tank.util.TestParameterContainer;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.api.enumerated.ScriptDriver;
import com.intuit.tank.vm.api.enumerated.TerminationPolicy;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.common.util.ReportUtil;
import com.intuit.tank.vm.settings.ModificationType;
import com.intuit.tank.vm.settings.ModifiedEntityMessage;

/**
 * AutomationServiceV1
 * 
 * @author dangleton
 * 
 */
@Path("/v1/automation-service")
public class AutomationServiceV1 implements AutomationService {

	private static final Logger LOG = LogManager.getLogger(AutomationServiceV1.class);

	@Context
	private ServletContext servletContext;

	/**
	 * @inheritDoc
	 */
	@Override
	@Nonnull
	public String ping() {
		return "PONG " + getClass().getSimpleName();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	@Nonnull
	public Response runAutomationJob(@Nonnull FormDataMultiPart formData) {
		AutomationRequest request = null;
		InputStream is = null;
		Map<String, List<FormDataBodyPart>> fields = formData.getFields();
		for (Entry<String, List<FormDataBodyPart>> entry : fields.entrySet()) {
			String formName = entry.getKey();
			LOG.info("Entry name: " + formName);
			for (FormDataBodyPart part : entry.getValue()) {
				MediaType mediaType = part.getMediaType();
				System.out.println("MediaType " + mediaType);
				if (MediaType.APPLICATION_XML_TYPE.equals(mediaType)
						|| MediaType.APPLICATION_JSON_TYPE.equals(mediaType)) {
					if ("automationRequest".equalsIgnoreCase(formName)) {
						request = part.getEntityAs(AutomationRequest.class);
					} else {
						is = part.getValueAs(InputStream.class);
					}
				} else if (MediaType.TEXT_PLAIN_TYPE.equals(mediaType)) {
					String s = part.getEntityAs(String.class);
					System.out.println("Plain Text Value: " + s);
					if ("xmlString".equalsIgnoreCase(formName)) {
						try {
					    	//Source: https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet#Unmarshaller
					    	SAXParserFactory spf = SAXParserFactory.newInstance();
					    	spf.setNamespaceAware(true);
					    	spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
					    	spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
					    	spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
					    	
					    	Source xmlSource = new SAXSource(spf.newSAXParser().getXMLReader(), new InputSource(new StringReader(s)));
					    	
							JAXBContext ctx = JAXBContext.newInstance(AutomationRequest.class.getPackage().getName());
							request = (AutomationRequest) ctx.createUnmarshaller().unmarshal(xmlSource);
						} catch (JAXBException | ParserConfigurationException | SAXException e) {
							throw new RuntimeException(e);
						}
                    }
				} else if (MediaType.APPLICATION_OCTET_STREAM_TYPE.equals(mediaType)) {
					// get the file
					is = part.getValueAs(InputStream.class);
				}
			}
		}
		ResponseBuilder responseBuilder = Response.ok();
		if (request == null) {
			responseBuilder = Response.status(Status.BAD_REQUEST);
			responseBuilder.entity("Requests to run automation jobs must contain an AutomationRequest");
		} else {
			Script script = null;
			if (is != null) {
				try {
					ScriptProcessor scriptProcessor = new ServletInjector<ScriptProcessor>()
							.getManagedBean(servletContext, ScriptProcessor.class);
					List<Integer> filterIds = getFilterList(request);
					script = new Script();
					script.setName(request.getScriptName());
					scriptProcessor.setScript(script);
					script.setProductName(request.getProductName());
					script.setCreator(TankConstants.TANK_USER_SYSTEM);
					List<ScriptStep> scriptSteps = scriptProcessor
							.getScriptSteps(new BufferedReader(new InputStreamReader(is)), getFilters(filterIds));
					List<ScriptStep> newSteps = new ArrayList<>(scriptSteps);
					// script.setScriptSteps(newSteps);
					//
					// script.setScriptSteps(newSteps);
					script = new ScriptDao().saveOrUpdate(script);
					sendMsg(script, ModificationType.ADD);

				} catch (Exception e) {
					LOG.error("Error starting script: " + e, e);
					responseBuilder = Response.status(Status.INTERNAL_SERVER_ERROR);
					responseBuilder.entity("An External Script failed with Exception: " + e.toString());
				} finally {
					IOUtils.closeQuietly(is);
				}
			}
			// now send off to service
			Project p = getOrCreateProject(request);
			JobInstance job = addJobToQueue(p, request, script);
			LOG.info("Automation Job (" + job.getId() + ") requested with values: " + job);
			String jobId = Integer.toString(job.getId());

			JobController controller = new ServletInjector<JobController>().getManagedBean(servletContext,
					JobController.class);
			controller.startJob(jobId);
			responseBuilder = Response.ok();
			// add jobId to response
			responseBuilder.entity(jobId);
		}
		responseBuilder.cacheControl(ResponseUtil.getNoStoreCacheControl());
		return responseBuilder.build();

	}

	/**
	 * @inheritDoc
	 */
	@Override
	@Nonnull
	public Response saveAs(String scriptId, String saveAsName) {
		String newScriptId = "FAILED";
		if (StringUtils.isEmpty(scriptId)) {
			return Response.status(Status.BAD_REQUEST).entity("Failed to recieve a valid scriptId ->" + scriptId + "<-").build();
		}
		if (StringUtils.isEmpty(saveAsName)) {
			return Response.status(Status.BAD_REQUEST).entity("Failed to recieve a valid script name ->" + saveAsName + "<-").build();
		}
		try {
			Script script = new ScriptDao().findById(Integer.parseInt(scriptId));
            Script copyScript = ScriptUtil.copyScript("System", saveAsName, script);
            copyScript = new ScriptDao().saveOrUpdate(copyScript);
            newScriptId = copyScript.getId() + "";
		} catch (NumberFormatException nfe) {
			return Response.status(Status.BAD_REQUEST).entity("Failed to parse a valid scriptId ->" + scriptId + "<-").build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity("Failed to find a valid script for scriptId ->" + scriptId + "<-").build();
		}
		return Response.ok().entity(newScriptId).build();
	}
		
	/**
	 * @inheritDoc
	 */
	@Override
	@Nonnull
	public Response uploadScript(@Nonnull FormDataMultiPart formData) {
		ResponseBuilder responseBuilder = Response.ok();
		FormDataBodyPart scriptId = formData.getField("scriptId");
		FormDataBodyPart newScriptName = formData.getField("scriptName");
		FormDataBodyPart filePart = formData.getField("file");
		try (InputStream is = filePart.getValueAs(InputStream.class)) {
			Script script = null;
			if ("0".equals(scriptId.getValue())) {
				script = new Script();
				script.setName("New");
				script.setCreator("System");
			} else {
				script = new ScriptDao().findById(Integer.parseInt(scriptId.getValue()));
			}
			ScriptProcessor scriptProcessor = new ServletInjector<ScriptProcessor>().getManagedBean(servletContext,
					ScriptProcessor.class);

			scriptProcessor.setScript(script);
			if (StringUtils.isNotEmpty(newScriptName.getValue())) {
				script.setName(newScriptName.getValue());
			}
			List<ScriptStep> scriptSteps = scriptProcessor.getScriptSteps(new BufferedReader(new InputStreamReader(is)),
					new ArrayList<>());
			script = new ScriptDao().saveOrUpdate(script);
			sendMsg(script, ModificationType.UPDATE);
			responseBuilder.entity(Integer.toString(script.getId()));
		} catch (Exception e) {
			LOG.error("Error starting script: " + e, e);
			responseBuilder = Response.status(Status.INTERNAL_SERVER_ERROR);
			responseBuilder.entity("An External Script failed with Exception: " + e.toString());
		}
		return responseBuilder.build();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	@Nonnull
	public Response uploadScript(int scriptId,
								 String scriptName,
								 InputStream fileInputStream,
								 FormDataContentDisposition fileFormDataContentDisposition,
								 String contentEncoding) {
		ResponseBuilder responseBuilder = Response.ok();
		try {
			Script script = null;
			if (0 == scriptId) {
				script = new Script();
				script.setName("New");
				script.setCreator("System");
			} else {
				script = new ScriptDao().findById(scriptId);
			}
			ScriptProcessor scriptProcessor = new ServletInjector<ScriptProcessor>().getManagedBean(servletContext,
					ScriptProcessor.class);

			scriptProcessor.setScript(script);
			if (StringUtils.isNotEmpty(scriptName)) {
				script.setName(scriptName);
			}
			BufferedReader bufferedReader = StringUtils.equalsIgnoreCase(contentEncoding, "gzip") ?
					new BufferedReader(new InputStreamReader(new GZIPInputStream(fileInputStream))) :
					new BufferedReader(new InputStreamReader(fileInputStream));
			scriptProcessor.getScriptSteps(bufferedReader, new ArrayList<>());
			script = new ScriptDao().saveOrUpdate(script);
			sendMsg(script, ModificationType.UPDATE);
			responseBuilder.entity(Integer.toString(script.getId()));
		} catch (Exception e) {
			LOG.error("Error starting script: " + e, e);
			responseBuilder = Response.status(Status.INTERNAL_SERVER_ERROR);
			responseBuilder.entity("An External Script failed with Exception: " + e.toString());
		} finally {
			try {
				fileInputStream.close();
			} catch (IOException e) {}
		}
		return responseBuilder.build();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	@Nonnull
	public Response applyFilters(@Nonnull ApplyFiltersRequest request) {
		if (StringUtils.isNotEmpty(request.getScriptId())) {
			LOG.info(request.toString());
			Script script = new ScriptDao().findById(Integer.parseInt(request.getScriptId()));
			List<Integer> filterIds = new ArrayList<>(request.getFilterIds());
			FilterGroupDao dao = new FilterGroupDao();
			for (Integer id : request.getFilterGroupIds()) {
				ScriptFilterGroup group = dao.findById(id);
				if (group != null) {
					for (ScriptFilter filter : group.getFilters()) {
						filterIds.add(filter.getId());
					}
				}
			}
			if (!filterIds.isEmpty()) {
				ScriptFilterUtil.applyFilters(filterIds, script);
				script = new ScriptDao().saveOrUpdate(script);
				sendMsg(script, ModificationType.UPDATE);
				return Response.ok().entity("SUCCESS").build();
			}
			return Response.ok().entity("You failed to include any filterIds in your request").build();
		}
		return Response.status(Status.BAD_REQUEST).entity("Failed to recieve the json object\n").build();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	@Nonnull
	public Response createJob(@Nonnull CreateJobRequest request) {
		LOG.info(request.toString());
		if (StringUtils.isEmpty(request.getProjectName())) {
			return Response.status(Status.BAD_REQUEST)
					.entity("Creating a Job requires a name\n").build();
		} else {
			ProjectDao projectDao = new ProjectDao();
			Project project = projectDao.findByName(request.getProjectName());

			if (project != null) {
				buildJobConfiguration(request, project);
				project = projectDao.saveOrUpdateProject(project);
			}

			JobInstance job = addJobToQueue(project, request);
			return Response.ok().entity(Integer.toString(job.getId())).build();
		}
	}

	private void buildJobConfiguration(@Nonnull CreateJobRequest request, Project project) {
		JobConfiguration jobConfiguration = project.getWorkloads().get(0).getJobConfiguration();

		if (StringUtils.isNotEmpty(request.getRampTime())) {
            jobConfiguration.setRampTimeExpression(request.getRampTime());
		}
		
		if (StringUtils.isNotEmpty(request.getSimulationTime())) {
            jobConfiguration.setSimulationTimeExpression(request.getSimulationTime());
		}
		
		jobConfiguration.setUserIntervalIncrement(request.getUserIntervalIncrement());
		jobConfiguration.setStopBehavior(StringUtils.isNotEmpty(request.getStopBehavior())
				? request.getStopBehavior() : StopBehavior.END_OF_SCRIPT_GROUP.getDisplay());
				
		jobConfiguration.setVmInstanceType(request.getVmInstance());
		jobConfiguration.setNumUsersPerAgent(request.getNumUsersPerAgent());

		boolean hasSimTime = jobConfiguration.getSimulationTime() > 0
                || (StringUtils.isNotBlank(request.getSimulationTime())
                && !"0".equals(request.getSimulationTime()));
		jobConfiguration
                .setTerminationPolicy(hasSimTime ? TerminationPolicy.time : TerminationPolicy.script);

		if (request.getJobRegions() != null) {
			setJobRegions(request, jobConfiguration);
        }
	}

	private void setJobRegions(@Nonnull CreateJobRequest request, JobConfiguration jobConfiguration) {
		jobConfiguration.getJobRegions().clear();
		JobRegionDao jrd = new JobRegionDao();
		for (CreateJobRegion r : request.getJobRegions()) {
            if (StringUtils.isNotEmpty(r.getRegion())) {
                JobRegion jr = jrd.saveOrUpdate(
                        new JobRegion(VMRegion.getRegionFromZone(r.getRegion()), r.getUsers()));
                jobConfiguration.getJobRegions().add(jr);
            }
        }
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Response runJob(String jobId) {
		JobController controller = new ServletInjector<JobController>().getManagedBean(servletContext,
				JobController.class);
		controller.startJob(jobId);
		return Response.ok().entity(jobId).build();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public Response getStatus(String jobId) {
		if (StringUtils.isNotEmpty(jobId)) {
			JobInstance job = new JobInstanceDao().findById(Integer.parseInt(jobId));
			if (job != null) {
				return Response.ok().entity(job.getStatus()).build();
			}
		}
		return Response.status(Status.BAD_REQUEST).build();
	}

	/**
	 * @param request
	 * @return
	 */
	private synchronized Project getOrCreateProject(AutomationRequest request) {
		ProjectDao projectDao = new ProjectDao();
		ModificationType type = ModificationType.UPDATE;
		Project project = projectDao.findByName(request.getName());
		if (project == null) {
			Workload workload = new Workload();
			List<Workload> workloads = new ArrayList<>();
			project = new Project();
			project.setName(request.getName());
			project.setProductName(request.getProductName());
			project.setCreator(TankConstants.TANK_USER_SYSTEM);
			workload.setName(request.getName());
			workloads.add(workload);
			workload.setParent(project);
			project.setWorkloads(workloads);
			project.setScriptDriver(ScriptDriver.Tank);
			project = projectDao.saveOrUpdateProject(project);
			type = ModificationType.ADD;
		}
		JobConfiguration jobConfiguration = project.getWorkloads().get(0).getJobConfiguration();
		// jobConfiguration.setRampTime(TimeUtil.parseTimeString(request.getRampTime()));
		jobConfiguration.setRampTimeExpression(request.getRampTime());
		jobConfiguration.setStopBehavior(request.getStopBehavior() != null ? request.getStopBehavior().name()
				: StopBehavior.END_OF_SCRIPT_GROUP.name());
		jobConfiguration.setSimulationTimeExpression(request.getSimulationTime());
		boolean hasSimTime = jobConfiguration.getSimulationTime() > 0
				|| (StringUtils.isNotBlank(request.getSimulationTime()) && !"0".equals(request.getSimulationTime()));
		jobConfiguration.setTerminationPolicy(hasSimTime ? TerminationPolicy.time : TerminationPolicy.script);
		jobConfiguration.setUserIntervalIncrement(request.getUserIntervalIncrement());
		jobConfiguration.getJobRegions().clear();
		JobRegionDao jrd = new JobRegionDao();
		for (AutomationJobRegion r : request.getJobRegions()) {
			JobRegion jr = jrd.saveOrUpdate(new JobRegion(r.getRegion(), r.getUsers()));
			jobConfiguration.getJobRegions().add(jr);
		}

		Map<String, String> varMap = jobConfiguration.getVariables();
		varMap.putAll(request.getVariables());

		project = projectDao.saveOrUpdateProject(project);
		sendMsg(project, type);

		return project;
	}

	private void sendMsg(BaseEntity entity, ModificationType type) {
		MessageSender sender = new ServletInjector<MessageSender>().getManagedBean(servletContext, MessageSender.class);
		sender.sendEvent(new ModifiedEntityMessage(entity.getClass(), entity.getId(), type));
	}

	private JobInstance addJobToQueue(Project p, AutomationRequest request, Script script) {

		JobQueueDao jobQueueDao = new JobQueueDao();
		DataFileDao dataFileDao = new DataFileDao();
		JobNotificationDao jobNotificationDao = new JobNotificationDao();
		JobInstanceDao jobInstanceDao = new JobInstanceDao();

		Workload workload = p.getWorkloads().get(0);
		JobConfiguration jc = workload.getJobConfiguration();
		JobQueue queue = jobQueueDao.findOrCreateForProjectId(p.getId());
		String name = request.getScriptName() + "_" + workload.getJobConfiguration().getTotalVirtualUsers() + "_users_"
				+ ReportUtil.getTimestamp(new Date());
		JobInstance jobInstance = new JobInstance(workload, name);
		jobInstance.setScheduledTime(new Date());
		jobInstance.setLocation(jc.getLocation());
		jobInstance.setLoggingProfile(jc.getLoggingProfile());
		jobInstance.setStopBehavior(jc.getStopBehavior());
		jobInstance.setVmInstanceType(jc.getVmInstanceType());
		jobInstance.setNumUsersPerAgent(jc.getNumUsersPerAgent());
		jobInstance.setReportingMode(jc.getReportingMode());
		jobInstance.getVariables().putAll(jc.getVariables());
		// set version info
		if (request.getDataFileIds() != null && !request.getDataFileIds().isEmpty()) {
			jobInstance.getDataFileVersions()
					.addAll(getVersions(dataFileDao, request.getDataFileIds(), DataFile.class));
		} else {
			jobInstance.getDataFileVersions()
					.addAll(getVersions(dataFileDao, workload.getJobConfiguration().getDataFileIds(), DataFile.class));
		}
		jobInstance.getNotificationVersions()
				.addAll(getVersions(jobNotificationDao, workload.getJobConfiguration().getNotifications()));
		JobValidator validator = new JobValidator(workload.getTestPlans(), jobInstance.getVariables(), false);
		long maxDuration = 0;
		for (TestPlan plan : workload.getTestPlans()) {
			maxDuration = Math.max(validator.getDurationMs(plan.getName()), maxDuration);
		}
		TestParameterContainer times = TestParamUtil.evaluateTestTimes(maxDuration, jc.getRampTimeExpression(),
				jc.getSimulationTimeExpression());
		jobInstance.setExecutionTime(maxDuration);
		jobInstance.setRampTime(times.getRampTime());
		jobInstance.setSimulationTime(times.getSimulationTime());
		int totalVirtualUsers = 0;
		for (JobRegion region : jc.getJobRegions()) {
			totalVirtualUsers += TestParamUtil.evaluateExpression(region.getUsers(), maxDuration,
					times.getSimulationTime(), times.getRampTime());
			jobInstance.getJobRegionVersions().add(new EntityVersion(region.getId(), 0, JobRegion.class));
		}
		jobInstance.setTotalVirtualUsers(totalVirtualUsers);
		queue.addJob(jobInstance);
		if (script != null) {
			workload.getTestPlans().clear();
			TestPlan plan = TestPlan.builder().name("default test plan").usersPercentage(100).build();
			workload.addTestPlan(plan);
			ScriptGroup scriptGroup = new ScriptGroup();
			scriptGroup.setName("Script Group 1");
			scriptGroup.setLoop(1);
			ScriptGroupStep step = new ScriptGroupStep();
			step.setLoop(1);
			step.setScript(script);
			scriptGroup.addScriptGroupStep(step);
			plan.addScriptGroup(scriptGroup);
		}
		workload = new WorkloadDao().saveOrUpdate(workload);
		String jobDetails = JobDetailFormatter.createJobDetails(
				new JobValidator(workload.getTestPlans(), jobInstance.getVariables(), false), workload, jobInstance);
		jobInstance.setJobDetails(jobDetails);
		jobInstance = jobInstanceDao.saveOrUpdate(jobInstance);
		jobQueueDao.saveOrUpdate(queue);

		ResponseUtil.storeScript(Integer.toString(jobInstance.getId()), workload, jobInstance);
		return jobInstance;
	}

	private String buildJobInstanceName(CreateJobRequest request, Workload workload) {
		return StringUtils.isNotEmpty(request.getJobInstanceName()) ? request.getJobInstanceName()
				: request.getProjectName() + "_" + workload.getJobConfiguration().getTotalVirtualUsers() + "_users_"
				+ ReportUtil.getTimestamp(new Date());
	}

	private JobInstance addJobToQueue(Project project, CreateJobRequest request) {
		JobQueueDao jobQueueDao = new JobQueueDao();
		DataFileDao dataFileDao = new DataFileDao();
		JobNotificationDao jobNotificationDao = new JobNotificationDao();
		JobInstanceDao jobInstanceDao = new JobInstanceDao();

		Workload workload = project.getWorkloads().get(0);
		JobConfiguration jc = workload.getJobConfiguration();
		JobQueue queue = jobQueueDao.findOrCreateForProjectId(project.getId());
		JobInstance jobInstance = new JobInstance(workload, buildJobInstanceName(request, workload));
		jobInstance.setScheduledTime(new Date());
		jobInstance.setLocation(jc.getLocation());
		jobInstance.setLoggingProfile(jc.getLoggingProfile());
		jobInstance.setStopBehavior(jc.getStopBehavior());
		jobInstance.setVmInstanceType(jc.getVmInstanceType());
		jobInstance.setNumUsersPerAgent(jc.getNumUsersPerAgent());
		jobInstance.setReportingMode(jc.getReportingMode());
		jobInstance.getVariables().putAll(jc.getVariables());
		// set version info
		jobInstance.getDataFileVersions()
				.addAll(getVersions(dataFileDao, workload.getJobConfiguration().getDataFileIds(), DataFile.class));

		jobInstance.getNotificationVersions()
				.addAll(getVersions(jobNotificationDao, workload.getJobConfiguration().getNotifications()));
		JobValidator validator = new JobValidator(workload.getTestPlans(), jobInstance.getVariables(), false);
		long maxDuration = 0;
		for (TestPlan plan : workload.getTestPlans()) {
			maxDuration = Math.max(validator.getDurationMs(plan.getName()), maxDuration);
		}
		TestParameterContainer times = TestParamUtil.evaluateTestTimes(maxDuration, jc.getRampTimeExpression(),
				jc.getSimulationTimeExpression());
		jobInstance.setExecutionTime(maxDuration);
		jobInstance.setRampTime(times.getRampTime());
		jobInstance.setSimulationTime(times.getSimulationTime());
		int totalVirtualUsers = 0;
		for (JobRegion region : jc.getJobRegions()) {
			totalVirtualUsers += TestParamUtil.evaluateExpression(region.getUsers(), maxDuration,
					times.getSimulationTime(), times.getRampTime());
			jobInstance.getJobRegionVersions().add(new EntityVersion(region.getId(), 0, JobRegion.class));
		}
		jobInstance.setTotalVirtualUsers(totalVirtualUsers);
		queue.addJob(jobInstance);
		workload = new WorkloadDao().saveOrUpdate(workload);
		String jobDetails = JobDetailFormatter.createJobDetails(
				new JobValidator(workload.getTestPlans(), jobInstance.getVariables(), false), workload, jobInstance);
		jobInstance.setJobDetails(jobDetails);
		jobInstance = jobInstanceDao.saveOrUpdate(jobInstance);
		jobQueueDao.saveOrUpdate(queue);

		ResponseUtil.storeScript(Integer.toString(jobInstance.getId()), workload, jobInstance);
		return jobInstance;
	}

	/**
	 * @param dao
	 * @param dataFileIds
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private Set<EntityVersion> getVersions(BaseDao dao, Collection<Integer> dataFileIds,
                                           Class<? extends BaseEntity> entityClass) {
		HashSet<EntityVersion> result = new HashSet<>();
		for (Integer id : dataFileIds) {
			int versionId = dao.getHeadRevisionNumber(id);
			result.add(new EntityVersion(id, versionId, entityClass));
		}
		return result;
	}

	/**
	 * @param dao
	 * @param entities
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Set<EntityVersion> getVersions(BaseDao dao, Set<? extends BaseEntity> entities) {
		HashSet<Integer> ids = new HashSet<>();
		Class entityClass = null;
		for (BaseEntity entity : entities) {
			ids.add(entity.getId());
			entityClass = entity.getClass();
		}
		return getVersions(dao, ids, entityClass);
	}

	/**
	 * @param request
	 * @return
	 */
	private List<Integer> getFilterList(AutomationRequest request) {
		Set<Integer> ret = new HashSet<>(request.getFilterIds());
		FilterGroupDao dao = new FilterGroupDao();
		for (Integer id : request.getFilterGroupIds()) {
			ScriptFilterGroup group = dao.findById(id);
			if (group != null) {
				for (ScriptFilter filter : group.getFilters()) {
					ret.add(filter.getId());
				}
			}
		}
		return new ArrayList<>(ret);
	}

	private List<ScriptFilter> getFilters(List<Integer> filterIds) {
        return filterIds.stream().map(id -> new FilterDao().findById(id)).filter(Objects::nonNull).collect(Collectors.toList());
	}

}
