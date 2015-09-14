/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.common;

/*
 * #%L
 * Intuit Tank Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

/**
 * TankConstants
 * 
 * @author dangleton
 * 
 */
public class TankConstants {
    public static final String TANK_BUILD_VERSION = "6.0.0";
    public static final String TOTAL_TIME_KEY = "_totalTime";
    public static final String START_TIME_KEY = "_startTime";

    public static final String KEY_IPS = "public_ips";
    public static final String KEY_JOB_ID = "jobId";
    public static final String KEY_LOGGING_PROFILE = "loggingProfile";
    public static final String KEY_STOP_BEHAVIOR = "stopBehavior";
    public static final String KEY_PROJECT_NAME = "projectName";
    public static final String KEY_USING_BIND_EIP = "usingBindEip";
    public static final String KEY_CONTROLLER_URL = "controllerUrl";
    public static final String KEY_REGION = "region";
    public static final String KEY_ENVIRONMENT = "environment";
    public static final String KEY_NUMBER_OF_USERS = "numberOfUsers";
    public static final String KEY_REPORTING_MODE = "reporting.mode";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_VM_INSTANCE_TYPE = "vmInstanceType";
    public static final String KEY_NUM_USERS_PER_AGENT = "numUsersPerAgent";
    public static final String KEY_USE_EIPS = "useEips";
    public static final String KEY_NUMBER_OF_INSTANCES = "numberOfInstances";
    public static final String KEY_SIZE = "size";
    public static final String RESULTS_PERF = "perf_results";
    public static final String RESULTS_NONE = "no_results";
    public static final String RESULTS_DB = "perf_db_results";
    public static final String KEY_REQUEST = "request";
    public static final String KEY_REUSE_STOPPED_INSTANCE = "reuseStoppedInstances";
    public static final String KEY_ZONE = "zone";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_SUBNET_ID = "subnetId";
    public static final String KEY_SECRET_KEY_ID = "secretKeyId";
    public static final String KEY_SECRET_KEY = "secretKey";
    public static final String KEY_JVM_ARGS = "jvmArgs";
    public static final String KEY_AWS_SECRET_KEY_ID = "AWS_SECRET_KEY_ID";
    public static final String KEY_AWS_SECRET_KEY = "AWS_SECRET_KEY";

    public static final String HTTP_CASE_SKIP = "SKIP";
    public static final String HTTP_CASE_SKIPGROUP = "SKIPGROUP";
    public static final String HTTP_CASE_HTTP = "HTTP";
    public static final String HTTP_CASE_HTTPS = "HTTPS";
    public static final String HTTP_CASE_FAIL = "FAIL";
    public static final String HTTP_CASE_PASS = "PASS";
    public static final String HTTP_CASE_KILL = "KILL";
    public static final String HTTP_CASE_ABORT = "ABORT";
    public static final String HTTP_CASE_RESTART = "RESTART";

    public static final String TANK_GROUP_TYPE = "tankUser";
    public static final String TANK_GROUP_ADMIN = "admin";
    public static final String TANK_USER_SYSTEM = "System";

    public static final String REST_SERVICE_CONTEXT = "/rest";

    public static final String NOTIFICATIONS_EVENT_JOB_NAME_KEY = "jobName";
    public static final String NOTIFICATIONS_EVENT_JOB_ID_KEY = "jobId";
    public static final String NOTIFICATIONS_EVENT_PROJECT_NAME_KEY = "projectName";
    public static final String NOTIFICATIONS_EVENT_PROJECT_ID_KEY = "projectId";
    public static final String NOTIFICATIONS_EVENT_EVENT_TIME_KEY = "eventTime";
    public static final String NOTIFICATIONS_EVENT_START_TIME_KEY = "startTime";
    public static final String NOTIFICATIONS_EVENT_END_TIME_KEY = "endTime";
    public static final String NOTIFICATIONS_EVENT_PREDICTED_END_TIME_KEY = "predictedEndTime";
    public static final String NOTIFICATIONS_EVENT_LOAD_START_TIME_KEY = "loadStartTime";
    public static final String NOTIFICATIONS_EVENT_RAMP_KEY = "ramp";
    public static final String NOTIFICATIONS_EVENT_DURATION_KEY = "duration";
    public static final String NOTIFICATIONS_EVENT_STEADY_STATE_START_KEY = "steadyStateStart";
    public static final String NOTIFICATIONS_EVENT_STEADY_STATE_END_KEY = "steadyStateEnd";
    public static final String NOTIFICATIONS_EVENT_NUM_USERS_KEY = "numUsers";
    public static final String NOTIFICATIONS_EVENT_TERMINATION_POLICY_KEY = "terminationPolicy";
    public static final String NOTIFICATIONS_EVENT_TIMING_DATA_URL_KEY = "timingDataUrl";
    public static final String NOTIFICATIONS_EVENT_SUMMARY_DATA_URL_KEY = "summaryDataUrl";
    public static final String NOTIFICATIONS_EVENT_SCRIPT_INFO_KEY = "scriptInfo";
    public static final String NOTIFICATIONS_EVENT_VARIABLES_KEY = "variables";
    public static final String NOTIFICATIONS_EVENT_LOCATION_KEY = "location";
    public static final String NOTIFICATIONS_EVENT_LOGGING_PROFILE_KEY = "loggingProfile";
    public static final String NOTIFICATIONS_EVENT_STOP_BEHAVIOR_KEY = "stopBehavior";
    public static final String NOTIFICATIONS_EVENT_VALIDATION_FAILURES_KEY = "validationFailures";
    public static final String NOTIFICATIONS_EVENT_EVENT_NAME_KEY = "eventName";
    public static final String NOTIFICATIONS_EVENT_DATA_FILES_KEY = "dataFiles";
    public static final String NOTIFICATIONS_EVENT_ACTUAL_DURTION = "actualDuration";
    public static final String NOTIFICATIONS_EVENT_BASE_URL_KEY = "tankBaseUrl";


    public static final String DATE_FORMAT = "MM/dd/yy HH:mm";
    public static final String DATE_FORMAT_WITH_TIMEZONE = DATE_FORMAT + " z";

    public static final String EXPRESSION_REGEX = "[\\#\\$]\\{([^\\}]*)\\}";
    public static final String CSV_EXPRESSION_REGEX = "[\\#\\$]\\{ioFunctions.getCSVData\\(([^\\)]*)\\).*";
    public static final String DEFAULT_CSV_FILE_NAME = "csv-data.txt";

}
