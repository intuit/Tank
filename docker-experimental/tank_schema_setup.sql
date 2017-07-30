SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for data_file
-- ----------------------------
DROP TABLE IF EXISTS `data_file`;
CREATE TABLE `data_file` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `creator` varchar(255) NOT NULL,
  `comments` varchar(1024) DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `path` varchar(256) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_PATH` (`path`(255)),
  KEY `IDX_CREATOR` (`creator`)
) ENGINE=InnoDB AUTO_INCREMENT=137 DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for data_file_version
-- ----------------------------
DROP TABLE IF EXISTS `data_file_version`;
CREATE TABLE `data_file_version` (
  `id` int(11) NOT NULL,
  `rev` int(11) NOT NULL,
  `rev_type` tinyint(4) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `comments` varchar(1024) DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `path` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK1BCA3B8AB8747159` (`rev`),
  CONSTRAINT `FK1BCA3B8AB8747159` FOREIGN KEY (`rev`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for external_script
-- ----------------------------
DROP TABLE IF EXISTS `external_script`;
CREATE TABLE `external_script` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `creator` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `product_name` varchar(255) DEFAULT NULL,
  `script` mediumtext,
  PRIMARY KEY (`id`),
  KEY `IDX_CREATOR` (`creator`)
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for external_script_version
-- ----------------------------
DROP TABLE IF EXISTS `external_script_version`;
CREATE TABLE `external_script_version` (
  `id` int(11) NOT NULL,
  `rev` int(11) NOT NULL,
  `rev_type` tinyint(4) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `product_name` varchar(255) DEFAULT NULL,
  `script` mediumtext,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK6E2A3458B8747159` (`rev`),
  CONSTRAINT `FK6E2A3458B8747159` FOREIGN KEY (`rev`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for job_config_varibles
-- ----------------------------
DROP TABLE IF EXISTS `job_config_varibles`;
CREATE TABLE `job_config_varibles` (
  `job_configuration_id` int(11) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`job_configuration_id`,`name`),
  KEY `FK4C537B756109B9A3` (`job_configuration_id`),
  CONSTRAINT `FK4C537B756109B9A3` FOREIGN KEY (`job_configuration_id`) REFERENCES `job_configuration` (`id`),
  CONSTRAINT `FKlrx6t40vmi1sllalltnkxm13k` FOREIGN KEY (`job_configuration_id`) REFERENCES `job_configuration` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for job_config_varibles_version
-- ----------------------------
DROP TABLE IF EXISTS `job_config_varibles_version`;
CREATE TABLE `job_config_varibles_version` (
  `rev` int(11) NOT NULL,
  `job_configuration_id` int(11) NOT NULL,
  `value` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `rev_type` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`rev`,`job_configuration_id`,`value`,`name`),
  KEY `FK8F73FA0EB8747159` (`rev`),
  CONSTRAINT `FK8F73FA0EB8747159` FOREIGN KEY (`rev`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for job_configuration
-- ----------------------------
DROP TABLE IF EXISTS `job_configuration`;
CREATE TABLE `job_configuration` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `baseline_virtual_users` int(11) DEFAULT NULL,
  `workload_type` varchar(255) NOT NULL,
  `location` varchar(255) DEFAULT NULL,
  `ramp_time_seconds` int(11) DEFAULT NULL,
  `reporting_mode` varchar(255) NOT NULL,
  `simulation_time` int(11) DEFAULT NULL,
  `termination_policy` varchar(255) NOT NULL,
  `user_interval_increment_seconds` int(11) DEFAULT NULL,
  `workload_id` int(11) DEFAULT NULL,
  `allow_variable_override` bit(1) DEFAULT NULL,
  `ramp_time_ms` bigint(20) DEFAULT NULL,
  `simulation_time_ms` bigint(20) DEFAULT NULL,
  `logging_profile` varchar(255) DEFAULT NULL,
  `execution_time_ms` bigint(20) DEFAULT NULL,
  `ramp_time_exp` varchar(255) DEFAULT NULL,
  `simulation_time_exp` varchar(255) DEFAULT NULL,
  `stop_behavior` varchar(255) DEFAULT NULL,
  `num_users_per_agent` int(11) NOT NULL DEFAULT '4000',
  `vm_instance_type` varchar(255) DEFAULT 'c3.2xlarge',
  `use_eips` bit(1) DEFAULT NULL,
  `tank_client_class` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKDF1513F4F5269A7E` (`workload_id`),
  CONSTRAINT `FK733ab6rfnuhp0ef09wob35yre` FOREIGN KEY (`workload_id`) REFERENCES `workload` (`id`),
  CONSTRAINT `FKDF1513F4F5269A7E` FOREIGN KEY (`workload_id`) REFERENCES `workload` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=194 DEFAULT CHARSET=utf8;;

INSERT INTO `job_configuration`(`id`, `created`, `modified`, `baseline_virtual_users`, `workload_type`, `location`, `ramp_time_seconds`, `reporting_mode`, `simulation_time`, `termination_policy`, `user_interval_increment_seconds`, `workload_id`, `allow_variable_override`, `ramp_time_ms`, `simulation_time_ms`, `logging_profile`, `execution_time_ms`, `ramp_time_exp`, `simulation_time_exp`, `stop_behavior`, `num_users_per_agent`, `vm_instance_type`, `use_eips`, `tank_client_class`) VALUES (1, '2016-02-13 03:18:04', '2017-07-07 21:07:30', 0, 'increasing', 'unspecified', 0, 'no_results', 0, 'time', 1, NULL, b'0', NULL, NULL, 'STANDARD', NULL, '30m', '100m', 'END_OF_STEP', 4000, 'c4.2xlarge', b'0', 'com.intuit.tank.httpclient4.TankHttpClient4');
COMMIT;

-- ----------------------------
-- Table structure for job_configuration_to_data_file
-- ----------------------------
DROP TABLE IF EXISTS `job_configuration_to_data_file`;
CREATE TABLE `job_configuration_to_data_file` (
  `job_configuration_id` int(11) NOT NULL,
  `data_file_id` int(11) DEFAULT NULL,
  KEY `FKE83FC6986109B9A3` (`job_configuration_id`),
  CONSTRAINT `FKE83FC6986109B9A3` FOREIGN KEY (`job_configuration_id`) REFERENCES `job_configuration` (`id`),
  CONSTRAINT `FKjqafuggnato232c5d7x1sq883` FOREIGN KEY (`job_configuration_id`) REFERENCES `job_configuration` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for job_configuration_to_data_file_AUD
-- ----------------------------
DROP TABLE IF EXISTS `job_configuration_to_data_file_AUD`;
CREATE TABLE `job_configuration_to_data_file_AUD` (
  `REV` int(11) NOT NULL,
  `job_configuration_id` int(11) NOT NULL,
  `data_file_id` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`job_configuration_id`,`data_file_id`),
  CONSTRAINT `FK_876qkewg8ah6myg124qenoh26` FOREIGN KEY (`REV`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;;

-- ----------------------------
-- Table structure for job_configuration_to_data_file_version
-- ----------------------------
DROP TABLE IF EXISTS `job_configuration_to_data_file_version`;
CREATE TABLE `job_configuration_to_data_file_version` (
  `rev` int(11) NOT NULL,
  `job_configuration_id` int(11) NOT NULL,
  `data_file_id` int(11) NOT NULL,
  `rev_type` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`rev`,`job_configuration_id`,`data_file_id`),
  KEY `FKD1407231B8747159` (`rev`),
  CONSTRAINT `FKD1407231B8747159` FOREIGN KEY (`rev`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for job_configuration_to_job_notification
-- ----------------------------
DROP TABLE IF EXISTS `job_configuration_to_job_notification`;
CREATE TABLE `job_configuration_to_job_notification` (
  `job_configuration_id` int(11) NOT NULL,
  `notification_id` int(11) NOT NULL,
  PRIMARY KEY (`job_configuration_id`,`notification_id`),
  UNIQUE KEY `notification_id` (`notification_id`),
  UNIQUE KEY `UK_5fdjhi4mde22f3q4mcq9qnus1` (`notification_id`),
  KEY `FKA8AEEAA66109B9A3` (`job_configuration_id`),
  KEY `FKA8AEEAA62B7AE193` (`notification_id`),
  CONSTRAINT `FK9wxgnfly9xsrkx0560vbxd4fy` FOREIGN KEY (`job_configuration_id`) REFERENCES `job_configuration` (`id`),
  CONSTRAINT `FKA8AEEAA62B7AE193` FOREIGN KEY (`notification_id`) REFERENCES `job_notification` (`id`),
  CONSTRAINT `FKA8AEEAA66109B9A3` FOREIGN KEY (`job_configuration_id`) REFERENCES `job_configuration` (`id`),
  CONSTRAINT `FKk9jk1m8b8gwk1incfyh492j3a` FOREIGN KEY (`notification_id`) REFERENCES `job_notification` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for job_configuration_to_job_notification_AUD
-- ----------------------------
DROP TABLE IF EXISTS `job_configuration_to_job_notification_AUD`;
CREATE TABLE `job_configuration_to_job_notification_AUD` (
  `REV` int(11) NOT NULL,
  `job_configuration_id` int(11) NOT NULL,
  `notification_id` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`job_configuration_id`,`notification_id`),
  CONSTRAINT `FK_751ghtprrdaffidffmlncd9ga` FOREIGN KEY (`REV`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;;

-- ----------------------------
-- Table structure for job_configuration_to_job_notification_version
-- ----------------------------
DROP TABLE IF EXISTS `job_configuration_to_job_notification_version`;
CREATE TABLE `job_configuration_to_job_notification_version` (
  `rev` int(11) NOT NULL,
  `job_configuration_id` int(11) NOT NULL,
  `notification_id` int(11) NOT NULL,
  `rev_type` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`rev`,`job_configuration_id`,`notification_id`),
  KEY `FK6E09A83FB8747159` (`rev`),
  CONSTRAINT `FK6E09A83FB8747159` FOREIGN KEY (`rev`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for job_configuration_to_job_regions
-- ----------------------------
DROP TABLE IF EXISTS `job_configuration_to_job_regions`;
CREATE TABLE `job_configuration_to_job_regions` (
  `job_configuration_id` int(11) NOT NULL,
  `region_id` int(11) NOT NULL,
  PRIMARY KEY (`job_configuration_id`,`region_id`),
  UNIQUE KEY `region_id` (`region_id`),
  UNIQUE KEY `UK_908bxdq2e9asaib7lrm8gmnpu` (`region_id`),
  KEY `FK5F7AED646109B9A3` (`job_configuration_id`),
  KEY `FK5F7AED64611BB7B3` (`region_id`),
  CONSTRAINT `FK5F7AED646109B9A3` FOREIGN KEY (`job_configuration_id`) REFERENCES `job_configuration` (`id`),
  CONSTRAINT `FK5F7AED64611BB7B3` FOREIGN KEY (`region_id`) REFERENCES `job_region` (`id`),
  CONSTRAINT `FK8hjbqjfgg2259epc0pun1i1vb` FOREIGN KEY (`job_configuration_id`) REFERENCES `job_configuration` (`id`),
  CONSTRAINT `FKqgqipbrcqygidok9j3c055b9s` FOREIGN KEY (`region_id`) REFERENCES `job_region` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

INSERT INTO `job_configuration_to_job_regions`(`job_configuration_id`, `region_id`) VALUES (1, 1);
COMMIT;

-- ----------------------------
-- Table structure for job_configuration_to_job_regions_AUD
-- ----------------------------
DROP TABLE IF EXISTS `job_configuration_to_job_regions_AUD`;
CREATE TABLE `job_configuration_to_job_regions_AUD` (
  `REV` int(11) NOT NULL,
  `job_configuration_id` int(11) NOT NULL,
  `region_id` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`job_configuration_id`,`region_id`),
  CONSTRAINT `FK_8ed2wsqqcfwdbpqbco5m78v57` FOREIGN KEY (`REV`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;;

-- ----------------------------
-- Table structure for job_configuration_to_job_regions_version
-- ----------------------------
DROP TABLE IF EXISTS `job_configuration_to_job_regions_version`;
CREATE TABLE `job_configuration_to_job_regions_version` (
  `rev` int(11) NOT NULL,
  `job_configuration_id` int(11) NOT NULL,
  `region_id` int(11) NOT NULL,
  `rev_type` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`rev`,`job_configuration_id`,`region_id`),
  KEY `FK2C7E0CFDB8747159` (`rev`),
  CONSTRAINT `FK2C7E0CFDB8747159` FOREIGN KEY (`rev`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for job_configuration_version
-- ----------------------------
DROP TABLE IF EXISTS `job_configuration_version`;
CREATE TABLE `job_configuration_version` (
  `id` int(11) NOT NULL,
  `rev` int(11) NOT NULL,
  `rev_type` tinyint(4) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `baseline_virtual_users` int(11) DEFAULT NULL,
  `workload_type` varchar(255) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `ramp_time_seconds` int(11) DEFAULT NULL,
  `reporting_mode` varchar(255) DEFAULT NULL,
  `simulation_time` int(11) DEFAULT NULL,
  `termination_policy` varchar(255) DEFAULT NULL,
  `user_interval_increment_seconds` int(11) DEFAULT NULL,
  `workload_id` int(11) DEFAULT NULL,
  `allow_variable_override` bit(1) DEFAULT NULL,
  `ramp_time_ms` bigint(20) DEFAULT NULL,
  `simulation_time_ms` bigint(20) DEFAULT NULL,
  `logging_profile` varchar(255) DEFAULT NULL,
  `execution_time_ms` bigint(20) DEFAULT NULL,
  `ramp_time_exp` varchar(255) DEFAULT NULL,
  `simulation_time_exp` varchar(255) DEFAULT NULL,
  `stop_behavior` varchar(255) DEFAULT NULL,
  `num_users_per_agent` int(11) NOT NULL DEFAULT '4000',
  `vm_instance_type` varchar(255) DEFAULT 'c3.2xlarge',
  `use_eips` bit(1) DEFAULT NULL,
  `tank_client_class` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK110A38DB8747159` (`rev`),
  CONSTRAINT `FK110A38DB8747159` FOREIGN KEY (`rev`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for job_instance
-- ----------------------------
DROP TABLE IF EXISTS `job_instance`;
CREATE TABLE `job_instance` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `baseline_virtual_users` int(11) DEFAULT NULL,
  `workload_type` varchar(255) NOT NULL,
  `location` varchar(255) DEFAULT NULL,
  `ramp_time_seconds` int(11) DEFAULT NULL,
  `reporting_mode` varchar(255) NOT NULL,
  `simulation_time` int(11) DEFAULT NULL,
  `termination_policy` varchar(255) NOT NULL,
  `user_interval_increment_seconds` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `scheduled_time` datetime DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `total_virtual_users` int(11) DEFAULT NULL,
  `workload_id` int(11) DEFAULT NULL,
  `workload_version` int(11) DEFAULT NULL,
  `end` datetime DEFAULT NULL,
  `start` datetime DEFAULT NULL,
  `allow_variable_override` bit(1) DEFAULT NULL,
  `ramp_time_ms` bigint(20) DEFAULT NULL,
  `simulation_time_ms` bigint(20) DEFAULT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `job_details` longtext,
  `logging_profile` varchar(255) DEFAULT NULL,
  `execution_time_ms` bigint(20) DEFAULT NULL,
  `ramp_time_exp` varchar(255) DEFAULT NULL,
  `simulation_time_exp` varchar(255) DEFAULT NULL,
  `stop_behavior` varchar(255) DEFAULT NULL,
  `num_users_per_agent` int(11) NOT NULL DEFAULT '4000',
  `vm_instance_type` varchar(255) DEFAULT 'c3.2xlarge',
  `use_eips` bit(1) DEFAULT NULL,
  `tank_client_class` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_JQ_STATUS` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1389 DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for job_instance_to_data_file
-- ----------------------------
DROP TABLE IF EXISTS `job_instance_to_data_file`;
CREATE TABLE `job_instance_to_data_file` (
  `job_instance_id` int(11) NOT NULL,
  `datafile_object_class` varchar(255) DEFAULT NULL,
  `datafile_id` int(11) DEFAULT NULL,
  `datafile_version_id` int(11) DEFAULT NULL,
  KEY `FKF473D4956E5451` (`job_instance_id`),
  CONSTRAINT `FKF473D4956E5451` FOREIGN KEY (`job_instance_id`) REFERENCES `job_instance` (`id`),
  CONSTRAINT `FKn38j3crwmlehs9qc848p1p3ir` FOREIGN KEY (`job_instance_id`) REFERENCES `job_instance` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for job_instance_to_job_notification_version
-- ----------------------------
DROP TABLE IF EXISTS `job_instance_to_job_notification_version`;
CREATE TABLE `job_instance_to_job_notification_version` (
  `job_instance_id` int(11) NOT NULL,
  `notification_object_class` varchar(255) DEFAULT NULL,
  `notification_id` int(11) DEFAULT NULL,
  `notification_version_id` int(11) DEFAULT NULL,
  KEY `FK48C240A26E5451` (`job_instance_id`),
  CONSTRAINT `FK48C240A26E5451` FOREIGN KEY (`job_instance_id`) REFERENCES `job_instance` (`id`),
  CONSTRAINT `FK4k22elgks24it034o2hog8u7e` FOREIGN KEY (`job_instance_id`) REFERENCES `job_instance` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for job_instance_to_job_regions
-- ----------------------------
DROP TABLE IF EXISTS `job_instance_to_job_regions`;
CREATE TABLE `job_instance_to_job_regions` (
  `job_instance_id` int(11) NOT NULL,
  `jobregion_object_class` varchar(255) DEFAULT NULL,
  `jobregion_id` int(11) DEFAULT NULL,
  `jobregion_version_id` int(11) DEFAULT NULL,
  KEY `FK2EE370216E5451` (`job_instance_id`),
  CONSTRAINT `FK2EE370216E5451` FOREIGN KEY (`job_instance_id`) REFERENCES `job_instance` (`id`),
  CONSTRAINT `FK46vhdbje2wdvpevl07jaku2uo` FOREIGN KEY (`job_instance_id`) REFERENCES `job_instance` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for job_instance_varibles
-- ----------------------------
DROP TABLE IF EXISTS `job_instance_varibles`;
CREATE TABLE `job_instance_varibles` (
  `job_id` int(11) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`job_id`,`name`),
  KEY `FK6A975002EBCAD5AB` (`job_id`),
  CONSTRAINT `FK6A975002EBCAD5AB` FOREIGN KEY (`job_id`) REFERENCES `job_instance` (`id`),
  CONSTRAINT `FKtpe2ge1j9snhffyb054ppo2rv` FOREIGN KEY (`job_id`) REFERENCES `job_instance` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for job_notification
-- ----------------------------
DROP TABLE IF EXISTS `job_notification`;
CREATE TABLE `job_notification` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `body` varchar(4096) DEFAULT NULL,
  `lifecycle_event` int(11) DEFAULT NULL,
  `subject` varchar(255) DEFAULT NULL,
  `recipient_list` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for job_notification_recipient_version
-- ----------------------------
DROP TABLE IF EXISTS `job_notification_recipient_version`;
CREATE TABLE `job_notification_recipient_version` (
  `rev` int(11) NOT NULL,
  `notification_id` int(11) NOT NULL,
  `id` int(11) NOT NULL,
  `rev_type` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`rev`,`notification_id`,`id`),
  KEY `FK5482B720B8747159` (`rev`),
  CONSTRAINT `FK5482B720B8747159` FOREIGN KEY (`rev`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for job_notification_to_event
-- ----------------------------
DROP TABLE IF EXISTS `job_notification_to_event`;
CREATE TABLE `job_notification_to_event` (
  `job_notification_id` int(11) NOT NULL,
  `lifecycle_events` varchar(255) DEFAULT NULL,
  UNIQUE KEY `job_notification_id` (`job_notification_id`,`lifecycle_events`),
  UNIQUE KEY `UK_o5wobwexdd0e8maqcjtdxnogk` (`job_notification_id`,`lifecycle_events`),
  KEY `FK6BB71DA82570B911` (`job_notification_id`),
  CONSTRAINT `FK6BB71DA82570B911` FOREIGN KEY (`job_notification_id`) REFERENCES `job_notification` (`id`),
  CONSTRAINT `FKhh1lngu86pqj5xmh42r4ybaxl` FOREIGN KEY (`job_notification_id`) REFERENCES `job_notification` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for job_notification_to_event_version
-- ----------------------------
DROP TABLE IF EXISTS `job_notification_to_event_version`;
CREATE TABLE `job_notification_to_event_version` (
  `rev` int(11) NOT NULL,
  `job_notification_id` int(11) NOT NULL,
  `lifecycle_events` varchar(255) NOT NULL,
  `rev_type` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`rev`,`job_notification_id`,`lifecycle_events`),
  KEY `FK73B7B941B8747159` (`rev`),
  CONSTRAINT `FK73B7B941B8747159` FOREIGN KEY (`rev`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for job_notification_version
-- ----------------------------
DROP TABLE IF EXISTS `job_notification_version`;
CREATE TABLE `job_notification_version` (
  `id` int(11) NOT NULL,
  `rev` int(11) NOT NULL,
  `rev_type` tinyint(4) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `body` varchar(4096) DEFAULT NULL,
  `lifecycle_event` int(11) DEFAULT NULL,
  `subject` varchar(255) DEFAULT NULL,
  `recipient_list` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FKD0E458C6B8747159` (`rev`),
  CONSTRAINT `FKD0E458C6B8747159` FOREIGN KEY (`rev`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for job_queue
-- ----------------------------
DROP TABLE IF EXISTS `job_queue`;
CREATE TABLE `job_queue` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `project_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_PROJ_ID` (`project_id`)
) ENGINE=InnoDB AUTO_INCREMENT=71119 DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for job_region
-- ----------------------------
DROP TABLE IF EXISTS `job_region`;
CREATE TABLE `job_region` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `region` varchar(255) DEFAULT NULL,
  `users` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2439 DEFAULT CHARSET=utf8;;

INSERT INTO `job_region`(`id`, `created`, `modified`, `region`, `users`) VALUES (1, '2017-07-07 21:04:56', '2017-07-07 21:07:30', 'US_WEST_2', '0');
COMMIT;

-- ----------------------------
-- Table structure for job_region_version
-- ----------------------------
DROP TABLE IF EXISTS `job_region_version`;
CREATE TABLE `job_region_version` (
  `id` int(11) NOT NULL,
  `rev` int(11) NOT NULL,
  `rev_type` tinyint(4) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `region` varchar(255) DEFAULT NULL,
  `users` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK2BD2F74FB8747159` (`rev`),
  CONSTRAINT `FK2BD2F74FB8747159` FOREIGN KEY (`rev`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for job_vm_instance
-- ----------------------------
DROP TABLE IF EXISTS `job_vm_instance`;
CREATE TABLE `job_vm_instance` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `user_count` int(11) DEFAULT NULL,
  `vm_role` varchar(255) DEFAULT NULL,
  `job_id` int(11) DEFAULT NULL,
  `vm_instance_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK18449B9BEBCAD5AB` (`job_id`),
  KEY `FK18449B9BC657313A` (`job_id`),
  KEY `FK18449B9BBFC522ED` (`vm_instance_id`),
  CONSTRAINT `FK18449B9BBFC522ED` FOREIGN KEY (`vm_instance_id`) REFERENCES `vm_instance` (`id`),
  CONSTRAINT `FK18449B9BC657313A` FOREIGN KEY (`job_id`) REFERENCES `job_configuration` (`id`),
  CONSTRAINT `FK18449B9BEBCAD5AB` FOREIGN KEY (`job_id`) REFERENCES `job_instance` (`id`),
  CONSTRAINT `FK420wxv83h9uo05qh2ikjgoe75` FOREIGN KEY (`job_id`) REFERENCES `job_configuration` (`id`),
  CONSTRAINT `FKaygbx80mc6aqi4p7qegbhp1g` FOREIGN KEY (`job_id`) REFERENCES `job_instance` (`id`),
  CONSTRAINT `FKk1jysc72hcyg6egh708evm65x` FOREIGN KEY (`vm_instance_id`) REFERENCES `vm_instance` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for project
-- ----------------------------
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `creator` varchar(255) NOT NULL,
  `comments` varchar(1024) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `product_name` varchar(255) DEFAULT NULL,
  `script_driver` varchar(255) NOT NULL,
  `project_id` int(11) DEFAULT NULL,
  `workload_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `IDX_CREATOR` (`creator`),
  KEY `FKED904B19A8AAE881` (`project_id`),
  KEY `FKED904B19C5E55363` (`workload_id`),
  CONSTRAINT `FKED904B19A8AAE881` FOREIGN KEY (`project_id`) REFERENCES `workload` (`id`),
  CONSTRAINT `FKED904B19C5E55363` FOREIGN KEY (`workload_id`) REFERENCES `workload` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=197 DEFAULT CHARSET=utf8;;

INSERT INTO `project`(`id`, `created`, `modified`, `creator`, `comments`, `name`, `product_name`, `script_driver`, `project_id`, `workload_id`) VALUES (1, '2016-02-13 03:18:04', '2017-07-07 20:40:59', 'tank', '', 'demo project', NULL, 'Tank', NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for project_version
-- ----------------------------
DROP TABLE IF EXISTS `project_version`;
CREATE TABLE `project_version` (
  `id` int(11) NOT NULL,
  `rev` int(11) NOT NULL,
  `rev_type` tinyint(4) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `comments` varchar(1024) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `product_name` varchar(255) DEFAULT NULL,
  `script_driver` varchar(255) DEFAULT NULL,
  `project_id` int(11) DEFAULT NULL,
  `workload_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK8648E5B2B8747159` (`rev`),
  CONSTRAINT `FK8648E5B2B8747159` FOREIGN KEY (`rev`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for recipient
-- ----------------------------
DROP TABLE IF EXISTS `recipient`;
CREATE TABLE `recipient` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `recipient_type` varchar(255) DEFAULT NULL,
  `notification_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK30E172192B7AE193` (`notification_id`),
  CONSTRAINT `FK30E172192B7AE193` FOREIGN KEY (`notification_id`) REFERENCES `job_notification` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for recipient_version
-- ----------------------------
DROP TABLE IF EXISTS `recipient_version`;
CREATE TABLE `recipient_version` (
  `id` int(11) NOT NULL,
  `rev` int(11) NOT NULL,
  `rev_type` tinyint(4) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `recipient_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK55830CB2B8747159` (`rev`),
  CONSTRAINT `FK55830CB2B8747159` FOREIGN KEY (`rev`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for revision_info
-- ----------------------------
DROP TABLE IF EXISTS `revision_info`;
CREATE TABLE `revision_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `timestamp` bigint(20) NOT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9133 DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for script
-- ----------------------------
DROP TABLE IF EXISTS `script`;
CREATE TABLE `script` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `creator` varchar(255) NOT NULL,
  `comments` varchar(1024) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `product_name` varchar(255) DEFAULT NULL,
  `runtime` int(11) DEFAULT NULL,
  `serial_step_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKC9E5D0CBD63BDD22` (`serial_step_id`),
  KEY `IDX_CREATOR` (`creator`),
  CONSTRAINT `FKC9E5D0CBD63BDD22` FOREIGN KEY (`serial_step_id`) REFERENCES `serialized_script_step` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=488 DEFAULT CHARSET=utf8;;

INSERT INTO `script`(`id`, `created`, `modified`, `creator`, `comments`, `name`, `product_name`, `runtime`, `serial_step_id`) VALUES (1, '2016-02-13 03:17:07', '2017-07-07 17:42:39', 'tank', NULL, 'Tank.REST', NULL, 0, 1);

-- ----------------------------
-- Table structure for script_filter
-- ----------------------------
DROP TABLE IF EXISTS `script_filter`;
CREATE TABLE `script_filter` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `creator` varchar(255) NOT NULL,
  `all_conditions_must_pass` bit(1) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `persist` bit(1) DEFAULT NULL,
  `product_name` varchar(255) DEFAULT NULL,
  `external_script_id` int(11) DEFAULT NULL,
  `filter_type` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_CREATOR` (`creator`)
) ENGINE=InnoDB AUTO_INCREMENT=312 DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for script_filter_action
-- ----------------------------
DROP TABLE IF EXISTS `script_filter_action`;
CREATE TABLE `script_filter_action` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `filter_action` varchar(255) DEFAULT NULL,
  `filter_key` varchar(255) DEFAULT NULL,
  `filter_scope` varchar(255) DEFAULT NULL,
  `filter_value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=456 DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for script_filter_action_version
-- ----------------------------
DROP TABLE IF EXISTS `script_filter_action_version`;
CREATE TABLE `script_filter_action_version` (
  `id` int(11) NOT NULL,
  `rev` int(11) NOT NULL,
  `rev_type` tinyint(4) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `filter_action` varchar(255) DEFAULT NULL,
  `filter_key` varchar(255) DEFAULT NULL,
  `filter_scope` varchar(255) DEFAULT NULL,
  `filter_value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK4B188702B8747159` (`rev`),
  CONSTRAINT `FK4B188702B8747159` FOREIGN KEY (`rev`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for script_filter_condition
-- ----------------------------
DROP TABLE IF EXISTS `script_filter_condition`;
CREATE TABLE `script_filter_condition` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `filter_condition` varchar(255) DEFAULT NULL,
  `filter_scope` varchar(255) DEFAULT NULL,
  `filter_value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=503 DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for script_filter_condition_version
-- ----------------------------
DROP TABLE IF EXISTS `script_filter_condition_version`;
CREATE TABLE `script_filter_condition_version` (
  `id` int(11) NOT NULL,
  `rev` int(11) NOT NULL,
  `rev_type` tinyint(4) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `filter_condition` varchar(255) DEFAULT NULL,
  `filter_scope` varchar(255) DEFAULT NULL,
  `filter_value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK7DBB59A1B8747159` (`rev`),
  CONSTRAINT `FK7DBB59A1B8747159` FOREIGN KEY (`rev`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for script_filter_group
-- ----------------------------
DROP TABLE IF EXISTS `script_filter_group`;
CREATE TABLE `script_filter_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `creator` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `product_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_CREATOR` (`creator`),
  KEY `IDX_PRODUCT_NAME` (`product_name`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for script_filter_group_script_filter
-- ----------------------------
DROP TABLE IF EXISTS `script_filter_group_script_filter`;
CREATE TABLE `script_filter_group_script_filter` (
  `filter_id` int(11) NOT NULL,
  `filter_group_id` int(11) NOT NULL,
  PRIMARY KEY (`filter_id`,`filter_group_id`),
  KEY `FK265EB859F5E1EBBA` (`filter_id`),
  KEY `FK265EB859E9310609` (`filter_group_id`),
  CONSTRAINT `FK265EB859E9310609` FOREIGN KEY (`filter_group_id`) REFERENCES `script_filter` (`id`),
  CONSTRAINT `FK265EB859F5E1EBBA` FOREIGN KEY (`filter_id`) REFERENCES `script_filter_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for script_filter_group_script_filter_version
-- ----------------------------
DROP TABLE IF EXISTS `script_filter_group_script_filter_version`;
CREATE TABLE `script_filter_group_script_filter_version` (
  `rev` int(11) NOT NULL,
  `filter_id` int(11) NOT NULL,
  `filter_group_id` int(11) NOT NULL,
  `rev_type` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`rev`,`filter_id`,`filter_group_id`),
  KEY `FK457612F2B8747159` (`rev`),
  CONSTRAINT `FK457612F2B8747159` FOREIGN KEY (`rev`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for script_filter_group_version
-- ----------------------------
DROP TABLE IF EXISTS `script_filter_group_version`;
CREATE TABLE `script_filter_group_version` (
  `id` int(11) NOT NULL,
  `rev` int(11) NOT NULL,
  `rev_type` tinyint(4) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `product_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FKD31E3D85B8747159` (`rev`),
  CONSTRAINT `FKD31E3D85B8747159` FOREIGN KEY (`rev`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for script_filter_script_filter_action
-- ----------------------------
DROP TABLE IF EXISTS `script_filter_script_filter_action`;
CREATE TABLE `script_filter_script_filter_action` (
  `filter_id` int(11) NOT NULL,
  `action_id` int(11) NOT NULL,
  PRIMARY KEY (`filter_id`,`action_id`),
  UNIQUE KEY `action_id` (`action_id`),
  UNIQUE KEY `UK_8fmy9wpojpanbbca8psf8kkku` (`action_id`),
  KEY `FK4EDEA55C23ABDB21` (`action_id`),
  KEY `FK4EDEA55C7CFC1589` (`filter_id`),
  CONSTRAINT `FK4EDEA55C23ABDB21` FOREIGN KEY (`action_id`) REFERENCES `script_filter_action` (`id`),
  CONSTRAINT `FK4EDEA55C7CFC1589` FOREIGN KEY (`filter_id`) REFERENCES `script_filter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for script_filter_script_filter_action_version
-- ----------------------------
DROP TABLE IF EXISTS `script_filter_script_filter_action_version`;
CREATE TABLE `script_filter_script_filter_action_version` (
  `rev` int(11) NOT NULL,
  `filter_id` int(11) NOT NULL,
  `action_id` int(11) NOT NULL,
  `rev_type` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`rev`,`filter_id`,`action_id`),
  KEY `FK96864CF5B8747159` (`rev`),
  CONSTRAINT `FK96864CF5B8747159` FOREIGN KEY (`rev`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for script_filter_script_filter_condition
-- ----------------------------
DROP TABLE IF EXISTS `script_filter_script_filter_condition`;
CREATE TABLE `script_filter_script_filter_condition` (
  `filter_id` int(11) NOT NULL,
  `condition_id` int(11) NOT NULL,
  PRIMARY KEY (`filter_id`,`condition_id`),
  UNIQUE KEY `condition_id` (`condition_id`),
  UNIQUE KEY `UK_ryd8peo8jv5e37e0ad41sfajl` (`condition_id`),
  KEY `FKE8662C3527809553` (`condition_id`),
  KEY `FKE8662C357CFC1589` (`filter_id`),
  CONSTRAINT `FKE8662C3527809553` FOREIGN KEY (`condition_id`) REFERENCES `script_filter_condition` (`id`),
  CONSTRAINT `FKE8662C357CFC1589` FOREIGN KEY (`filter_id`) REFERENCES `script_filter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for script_filter_script_filter_condition_version
-- ----------------------------
DROP TABLE IF EXISTS `script_filter_script_filter_condition_version`;
CREATE TABLE `script_filter_script_filter_condition_version` (
  `rev` int(11) NOT NULL,
  `filter_id` int(11) NOT NULL,
  `condition_id` int(11) NOT NULL,
  `rev_type` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`rev`,`filter_id`,`condition_id`),
  KEY `FK3929EACEB8747159` (`rev`),
  CONSTRAINT `FK3929EACEB8747159` FOREIGN KEY (`rev`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for script_filter_version
-- ----------------------------
DROP TABLE IF EXISTS `script_filter_version`;
CREATE TABLE `script_filter_version` (
  `id` int(11) NOT NULL,
  `rev` int(11) NOT NULL,
  `rev_type` tinyint(4) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `all_conditions_must_pass` bit(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `persist` bit(1) DEFAULT NULL,
  `product_name` varchar(255) DEFAULT NULL,
  `external_script_id` int(11) DEFAULT NULL,
  `filter_type` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FKDC7E2505B8747159` (`rev`),
  CONSTRAINT `FKDC7E2505B8747159` FOREIGN KEY (`rev`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for script_group
-- ----------------------------
DROP TABLE IF EXISTS `script_group`;
CREATE TABLE `script_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `loop_count` int(11) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `position` int(11) DEFAULT NULL,
  `workload_id` int(11) DEFAULT NULL,
  `test_plan_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKB4D8260BF5269A7E` (`workload_id`),
  KEY `FKB4D8260B9CB57E43` (`test_plan_id`),
  CONSTRAINT `FK30k9kvdbk8mdlt6xqecph68o8` FOREIGN KEY (`test_plan_id`) REFERENCES `test_plan` (`id`),
  CONSTRAINT `FKB4D8260B9CB57E43` FOREIGN KEY (`test_plan_id`) REFERENCES `test_plan` (`id`),
  CONSTRAINT `FKB4D8260BF5269A7E` FOREIGN KEY (`workload_id`) REFERENCES `workload` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=398 DEFAULT CHARSET=utf8;;

INSERT INTO `script_group`(`id`, `created`, `modified`, `loop_count`, `name`, `position`, `workload_id`, `test_plan_id`) VALUES (1, '2016-04-22 17:20:00', '2016-04-22 17:20:06', 2000, 'Script Group', 0, NULL, 1);
COMMIT;

-- ----------------------------
-- Table structure for script_group_step
-- ----------------------------
DROP TABLE IF EXISTS `script_group_step`;
CREATE TABLE `script_group_step` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `loop_count` int(11) DEFAULT NULL,
  `position` int(11) DEFAULT NULL,
  `script_id` int(11) DEFAULT NULL,
  `script_group_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8285A880390A62FE` (`script_id`),
  KEY `FK8285A880B027F59F` (`script_group_id`),
  CONSTRAINT `FK1ed2cbr83fxbi4mmakri0fy5g` FOREIGN KEY (`script_group_id`) REFERENCES `script_group` (`id`),
  CONSTRAINT `FK8285A880390A62FE` FOREIGN KEY (`script_id`) REFERENCES `script` (`id`),
  CONSTRAINT `FK8285A880B027F59F` FOREIGN KEY (`script_group_id`) REFERENCES `script_group` (`id`),
  CONSTRAINT `FKaeanagxar1hhdhtqh3pahxlk7` FOREIGN KEY (`script_id`) REFERENCES `script` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=817 DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for script_group_step_version
-- ----------------------------
DROP TABLE IF EXISTS `script_group_step_version`;
CREATE TABLE `script_group_step_version` (
  `id` int(11) NOT NULL,
  `rev` int(11) NOT NULL,
  `rev_type` tinyint(4) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `loop_count` int(11) DEFAULT NULL,
  `position` int(11) DEFAULT NULL,
  `script_id` int(11) DEFAULT NULL,
  `script_group_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FKDA19EC19B8747159` (`rev`),
  CONSTRAINT `FKDA19EC19B8747159` FOREIGN KEY (`rev`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for script_group_version
-- ----------------------------
DROP TABLE IF EXISTS `script_group_version`;
CREATE TABLE `script_group_version` (
  `id` int(11) NOT NULL,
  `rev` int(11) NOT NULL,
  `rev_type` tinyint(4) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `loop_count` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `position` int(11) DEFAULT NULL,
  `workload_id` int(11) DEFAULT NULL,
  `test_plan_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK85C7AEA4B8747159` (`rev`),
  CONSTRAINT `FK85C7AEA4B8747159` FOREIGN KEY (`rev`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for script_version
-- ----------------------------
DROP TABLE IF EXISTS `script_version`;
CREATE TABLE `script_version` (
  `id` int(11) NOT NULL,
  `rev` int(11) NOT NULL,
  `rev_type` tinyint(4) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `creator` varchar(255) DEFAULT NULL,
  `comments` varchar(1024) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `product_name` varchar(255) DEFAULT NULL,
  `runtime` int(11) DEFAULT NULL,
  `serial_step_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FKE2DE9964B8747159` (`rev`),
  CONSTRAINT `FKE2DE9964B8747159` FOREIGN KEY (`rev`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for serialized_script_step
-- ----------------------------
DROP TABLE IF EXISTS `serialized_script_step`;
CREATE TABLE `serialized_script_step` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `serialized_data` longblob NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2351 DEFAULT CHARSET=utf8;;

INSERT INTO `serialized_script_step`(`id`, `created`, `modified`, `serialized_data`) VALUES (1, '2017-07-07 17:42:39', '2017-07-07 17:42:39', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A6578700000000177040000000173720022636F6D2E696E747569742E74616E6B2E70726F6A6563742E5363726970745374657000000000000000010200014C000F73637269707447726F75704E616D657400124C6A6176612F6C616E672F537472696E673B7872001F636F6D2E696E747569742E74616E6B2E70726F6A6563742E52657175657374000000000000000102001B49000973746570496E6465784C0008636F6D6D656E747371007E00034C00046461746174000F4C6A6176612F7574696C2F5365743B4C0008686F73746E616D6571007E00034C00056C6162656C71007E00034C000A6C6F6767696E674B657971007E00034C00066D6574686F6471007E00034C00086D696D657479706571007E00034C00046E616D6571007E00034C00066F6E4661696C71007E00034C00077061796C6F616471007E00034C0009706F7374446174617371007E00054C000870726F746F636F6C71007E00034C000C7175657279537472696E677371007E00054C0009726571466F726D617471007E00034C000E72657175657374436F6F6B69657371007E00054C000E726571756573746865616465727371007E00054C000A72657370466F726D617471007E00034C0008726573706F6E736571007E00034C000F726573706F6E7365436F6F6B69657371007E00054C000C726573706F6E73654461746171007E00054C000F726573706F6E73656865616465727371007E00054C0006726573756C7471007E00034C000A73696D706C655061746871007E00034C00047479706571007E00034C000375726C71007E00034C00047575696471007E000378700000000170737200116A6176612E7574696C2E48617368536574BA44859596B8B7340300007870770C000000103F400000000000007874001E706172746E65722D74616E6B2E706572662E612E696E747569742E636F6D740046687474703A2F2F706172746E65722D74616E6B2E706572662E612E696E747569742E636F6D2F726573742F76312F70726F6A6563742D736572766963652F70726F6A656374737400007400034745547074000074000561626F7274707371007E0007770C000000103F4000000000000078740004687474707371007E0007770C000000103F40000000000000787400036E76707371007E0007770C000000103F40000000000000787371007E0007770C000000103F4000000000000173720023636F6D2E696E747569742E74616E6B2E70726F6A6563742E526571756573744461746100000000000000010200044C00036B657971007E00034C000570686173657400294C636F6D2F696E747569742F74616E6B2F7363726970742F526571756573744461746150686173653B4C00047479706571007E00034C000576616C756571007E0003787074000F4163636570742D456E636F64696E677E720027636F6D2E696E747569742E74616E6B2E7363726970742E5265717565737444617461506861736500000000000000001200007872000E6A6176612E6C616E672E456E756D0000000000000000120000787074000C504F53545F5245515545535474000D7265717565737448656164657274000D677A69702C206465666C6174657870707371007E0007770C000000103F40000000000000787371007E0007770C000000103F40000000000000787371007E0007770C000000103F4000000000000078707400212F726573742F76312F70726F6A6563742D736572766963652F70726F6A65637473740007726571756573747074002465333065636239322D346561382D343861632D396564632D30353766343438333330343574000078);
COMMIT;

-- ----------------------------
-- Table structure for serialized_script_step_version
-- ----------------------------
DROP TABLE IF EXISTS `serialized_script_step_version`;
CREATE TABLE `serialized_script_step_version` (
  `id` int(11) NOT NULL,
  `rev` int(11) NOT NULL,
  `rev_type` tinyint(4) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK4D63D21EB8747159` (`rev`),
  CONSTRAINT `FK4D63D21EB8747159` FOREIGN KEY (`rev`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for table_column
-- ----------------------------
DROP TABLE IF EXISTS `table_column`;
CREATE TABLE `table_column` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `creator` varchar(255) NOT NULL,
  `colname` varchar(256) NOT NULL,
  `displayname` varchar(256) DEFAULT NULL,
  `hideable` bit(1) NOT NULL,
  `size` int(11) NOT NULL,
  `visible` bit(1) NOT NULL,
  `scripts_table_id` int(11) DEFAULT NULL,
  `scripts_position` int(11) DEFAULT NULL,
  `script_table_id` int(11) DEFAULT NULL,
  `project_table_id` int(11) DEFAULT NULL,
  `project_position` int(11) DEFAULT NULL,
  `jobs_table_id` int(11) DEFAULT NULL,
  `jobs_position` int(11) DEFAULT NULL,
  `datafiles_table_id` int(11) DEFAULT NULL,
  `datafiles_position` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK44A850E7256DD272` (`datafiles_table_id`),
  KEY `FK44A850E7B15299E6` (`project_table_id`),
  KEY `FK44A850E7CCBD8D7` (`scripts_table_id`),
  KEY `FK44A850E7D2641D49` (`jobs_table_id`),
  KEY `FK44A850E7E7735C74` (`script_table_id`),
  KEY `IDX_CREATOR` (`creator`),
  CONSTRAINT `FK17tnhk0sbay5l7nl9rrg9afly` FOREIGN KEY (`script_table_id`) REFERENCES `table_preferences` (`id`),
  CONSTRAINT `FK2n1vqlwv37nr1pdtypdai4snd` FOREIGN KEY (`datafiles_table_id`) REFERENCES `table_preferences` (`id`),
  CONSTRAINT `FK44A850E7256DD272` FOREIGN KEY (`datafiles_table_id`) REFERENCES `table_preferences` (`id`),
  CONSTRAINT `FK44A850E7B15299E6` FOREIGN KEY (`project_table_id`) REFERENCES `table_preferences` (`id`),
  CONSTRAINT `FK44A850E7CCBD8D7` FOREIGN KEY (`scripts_table_id`) REFERENCES `table_preferences` (`id`),
  CONSTRAINT `FK44A850E7D2641D49` FOREIGN KEY (`jobs_table_id`) REFERENCES `table_preferences` (`id`),
  CONSTRAINT `FK44A850E7E7735C74` FOREIGN KEY (`script_table_id`) REFERENCES `table_preferences` (`id`),
  CONSTRAINT `FK7wefiot33m07263wr8ejpfbmq` FOREIGN KEY (`jobs_table_id`) REFERENCES `table_preferences` (`id`),
  CONSTRAINT `FKb36jltqf1plq7h5oed1oqyl44` FOREIGN KEY (`scripts_table_id`) REFERENCES `table_preferences` (`id`),
  CONSTRAINT `FKr3qcgr3xh7vn6elsdcx23olns` FOREIGN KEY (`project_table_id`) REFERENCES `table_preferences` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11216 DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for table_preferences
-- ----------------------------
DROP TABLE IF EXISTS `table_preferences`;
CREATE TABLE `table_preferences` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `creator` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_CREATOR` (`creator`)
) ENGINE=InnoDB AUTO_INCREMENT=221 DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for test_instance_jobs
-- ----------------------------
DROP TABLE IF EXISTS `test_instance_jobs`;
CREATE TABLE `test_instance_jobs` (
  `test_id` int(11) NOT NULL,
  `job_id` int(11) NOT NULL,
  PRIMARY KEY (`test_id`,`job_id`),
  UNIQUE KEY `job_id` (`job_id`),
  UNIQUE KEY `UK_8h9yvlp66uts5v5urnokv2e29` (`job_id`),
  KEY `FK6FF444D395675760` (`test_id`),
  KEY `FK6FF444D3EBCAD5AB` (`job_id`),
  CONSTRAINT `FK5xyss29s0p7l64rldlfc0mqgg` FOREIGN KEY (`test_id`) REFERENCES `job_queue` (`id`),
  CONSTRAINT `FK6FF444D395675760` FOREIGN KEY (`test_id`) REFERENCES `job_queue` (`id`),
  CONSTRAINT `FK6FF444D3EBCAD5AB` FOREIGN KEY (`job_id`) REFERENCES `job_instance` (`id`),
  CONSTRAINT `FKt8w0qpfybpwqsawfu47lus9kc` FOREIGN KEY (`job_id`) REFERENCES `job_instance` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for test_plan
-- ----------------------------
DROP TABLE IF EXISTS `test_plan`;
CREATE TABLE `test_plan` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `name` varchar(255) NOT NULL,
  `position` int(11) DEFAULT NULL,
  `user_percentage` int(11) DEFAULT NULL,
  `workload_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKB9A70AB6F5269A7E` (`workload_id`),
  CONSTRAINT `FK3mu48r2nxdu0ojgkdwbgepwi2` FOREIGN KEY (`workload_id`) REFERENCES `workload` (`id`),
  CONSTRAINT `FKB9A70AB6F5269A7E` FOREIGN KEY (`workload_id`) REFERENCES `workload` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=304 DEFAULT CHARSET=utf8;;

INSERT INTO `test_plan`(`id`, `created`, `modified`, `name`, `position`, `user_percentage`, `workload_id`) VALUES (1, '2016-04-22 17:19:30', '2016-04-22 17:19:30', 'Main', 0, 100, 1);
COMMIT;

-- ----------------------------
-- Table structure for test_plan_version
-- ----------------------------
DROP TABLE IF EXISTS `test_plan_version`;
CREATE TABLE `test_plan_version` (
  `id` int(11) NOT NULL,
  `rev` int(11) NOT NULL,
  `rev_type` tinyint(4) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `position` int(11) DEFAULT NULL,
  `user_percentage` int(11) DEFAULT NULL,
  `workload_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FKD928B84FB8747159` (`rev`),
  CONSTRAINT `FKD928B84FB8747159` FOREIGN KEY (`rev`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for timing_periodic
-- ----------------------------
DROP TABLE IF EXISTS `timing_periodic`;
CREATE TABLE `timing_periodic` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `job_id` int(11) NOT NULL,
  `max` double NOT NULL,
  `mean` double NOT NULL,
  `min` double NOT NULL,
  `page_id` varchar(255) NOT NULL,
  `period` int(11) NOT NULL,
  `sample_size` int(11) NOT NULL,
  `timestamp` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_TS_TIME` (`timestamp`),
  KEY `IDX_TS_PAGE_ID` (`page_id`),
  KEY `IDX_TS_JOB_ID` (`job_id`)
) ENGINE=InnoDB AUTO_INCREMENT=833111 DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for timing_summary
-- ----------------------------
DROP TABLE IF EXISTS `timing_summary`;
CREATE TABLE `timing_summary` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `job_id` int(11) NOT NULL,
  `kurtosis` double NOT NULL,
  `max` double NOT NULL,
  `mean` double NOT NULL,
  `min` double NOT NULL,
  `page_id` varchar(255) NOT NULL,
  `percentile_10` double NOT NULL,
  `percentile_20` double NOT NULL,
  `percentile_30` double NOT NULL,
  `percentile_40` double NOT NULL,
  `percentile_50` double NOT NULL,
  `percentile_60` double NOT NULL,
  `percentile_70` double NOT NULL,
  `percentile_80` double NOT NULL,
  `percentile_90` double NOT NULL,
  `percentile_95` double NOT NULL DEFAULT '0',
  `percentile_99` double NOT NULL,
  `sample_size` int(11) NOT NULL,
  `skewness` double NOT NULL,
  `std_dev` double NOT NULL,
  `varience` double NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_TS_PAGE_ID` (`page_id`),
  KEY `IDX_TS_JOB_ID` (`job_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6686 DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `email` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `IDX_USER_NAME` (`name`),
  KEY `IDX_USER_EMAIL` (`email`),
  KEY `IDX_USER_TOKEN` (`token`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;;

INSERT INTO `user` (`id`,`created`,`modified`,`email`,`name`,`password`,`token`) VALUES (1,'2015-09-30 16:57:29','2015-09-30 16:57:29','tank@intuit.com','tank','y/2sYAj5yrQIN4TL0YdPdmGNKpc=',NULL);
COMMIT;

-- ----------------------------
-- Table structure for user_group
-- ----------------------------
DROP TABLE IF EXISTS `user_group`;
CREATE TABLE `user_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_GROUP_NAME` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;;

INSERT INTO `user_group` (`id`,`created`,`modified`,`name`) VALUES (1,'2012-06-15 21:30:07','2012-06-15 21:30:07','event_manager');
INSERT INTO `user_group` (`id`,`created`,`modified`,`name`) VALUES (2,'2012-06-15 21:30:07','2012-06-15 21:30:07','script-manager');
INSERT INTO `user_group` (`id`,`created`,`modified`,`name`) VALUES (3,'2012-06-15 21:30:07','2012-06-15 21:30:07','admin');
INSERT INTO `user_group` (`id`,`created`,`modified`,`name`) VALUES (4,'2012-06-15 21:30:07','2012-06-15 21:30:07','job-manager');
INSERT INTO `user_group` (`id`,`created`,`modified`,`name`) VALUES (5,'2012-06-15 21:30:07','2012-06-15 21:30:07','project-manager');
INSERT INTO `user_group` (`id`,`created`,`modified`,`name`) VALUES (6,'2012-06-15 21:30:07','2012-06-15 21:30:07','report_manager');
INSERT INTO `user_group` (`id`,`created`,`modified`,`name`) VALUES (7,'2012-06-15 21:30:07','2012-06-15 21:30:07','guest');
INSERT INTO `user_group` (`id`,`created`,`modified`,`name`) VALUES (8,'2012-06-15 21:30:07','2012-06-15 21:30:07','metric_manager');
INSERT INTO `user_group` (`id`,`created`,`modified`,`name`) VALUES (9,'2012-06-15 21:30:07','2012-06-15 21:30:07','user');
INSERT INTO `user_group` (`id`,`created`,`modified`,`name`) VALUES (10,'2012-06-15 21:30:07','2012-06-15 21:30:07','environment_manager');
COMMIT;

-- ----------------------------
-- Table structure for user_group_version
-- ----------------------------
DROP TABLE IF EXISTS `user_group_version`;
CREATE TABLE `user_group_version` (
  `id` int(11) NOT NULL,
  `rev` int(11) NOT NULL,
  `rev_type` tinyint(4) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FKFE8D89A4B8747159` (`rev`),
  CONSTRAINT `FKFE8D89A4B8747159` FOREIGN KEY (`rev`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for user_user_group
-- ----------------------------
DROP TABLE IF EXISTS `user_user_group`;
CREATE TABLE `user_user_group` (
  `group_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`group_id`,`user_id`),
  KEY `FKC23DADFF6B9186EA` (`user_id`),
  KEY `FKC23DADFF5BC404CA` (`group_id`),
  CONSTRAINT `FKC23DADFF5BC404CA` FOREIGN KEY (`group_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKC23DADFF6B9186EA` FOREIGN KEY (`user_id`) REFERENCES `user_group` (`id`),
  CONSTRAINT `FKk04fx3s29j9ixa4n10x6oo0ky` FOREIGN KEY (`user_id`) REFERENCES `user_group` (`id`),
  CONSTRAINT `FKq46ilgjd3nm03u7gygpvb7ury` FOREIGN KEY (`group_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

INSERT INTO `user_user_group` (`group_id`,`user_id`) VALUES (1,1);
INSERT INTO `user_user_group` (`group_id`,`user_id`) VALUES (1,2);
INSERT INTO `user_user_group` (`group_id`,`user_id`) VALUES (1,3);
INSERT INTO `user_user_group` (`group_id`,`user_id`) VALUES (1,4);
INSERT INTO `user_user_group` (`group_id`,`user_id`) VALUES (1,5);
INSERT INTO `user_user_group` (`group_id`,`user_id`) VALUES (1,6);
INSERT INTO `user_user_group` (`group_id`,`user_id`) VALUES (1,7);
INSERT INTO `user_user_group` (`group_id`,`user_id`) VALUES (1,8);
INSERT INTO `user_user_group` (`group_id`,`user_id`) VALUES (1,9);
INSERT INTO `user_user_group` (`group_id`,`user_id`) VALUES (1,10);
COMMIT;

-- ----------------------------
-- Table structure for user_user_group_version
-- ----------------------------
DROP TABLE IF EXISTS `user_user_group_version`;
CREATE TABLE `user_user_group_version` (
  `rev` int(11) NOT NULL,
  `group_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `rev_type` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`rev`,`group_id`,`user_id`),
  KEY `FKC5F00298B8747159` (`rev`),
  CONSTRAINT `FKC5F00298B8747159` FOREIGN KEY (`rev`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for user_version
-- ----------------------------
DROP TABLE IF EXISTS `user_version`;
CREATE TABLE `user_version` (
  `id` int(11) NOT NULL,
  `rev` int(11) NOT NULL,
  `rev_type` tinyint(4) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FK6FE4B464B8747159` (`rev`),
  CONSTRAINT `FK6FE4B464B8747159` FOREIGN KEY (`rev`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for vm_instance
-- ----------------------------
DROP TABLE IF EXISTS `vm_instance`;
CREATE TABLE `vm_instance` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `ami_id` varchar(255) DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `instance_id` varchar(255) DEFAULT NULL,
  `job_id` varchar(255) DEFAULT NULL,
  `region` varchar(255) DEFAULT NULL,
  `size` varchar(255) DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2686 DEFAULT CHARSET=utf8;;

-- ----------------------------
-- Table structure for workload
-- ----------------------------
DROP TABLE IF EXISTS `workload`;
CREATE TABLE `workload` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  `modified` datetime NOT NULL,
  `name` varchar(255) NOT NULL,
  `position` int(11) DEFAULT NULL,
  `job_configuration_id` int(11) DEFAULT NULL,
  `project_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK21E9B776109B9A3` (`job_configuration_id`),
  KEY `FK21E9B771FBB6A76` (`project_id`),
  CONSTRAINT `FK21E9B771FBB6A76` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `FK21E9B776109B9A3` FOREIGN KEY (`job_configuration_id`) REFERENCES `job_configuration` (`id`),
  CONSTRAINT `FKjh0r6jl21bd7r0eu6m621wpjs` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `FKr7qhjf4r9bi9ejsawo507txxe` FOREIGN KEY (`job_configuration_id`) REFERENCES `job_configuration` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=194 DEFAULT CHARSET=utf8;;

INSERT INTO `workload`(`id`, `created`, `modified`, `name`, `position`, `job_configuration_id`, `project_id`) VALUES (1, '2016-02-13 03:18:04', '2016-02-13 03:18:04', 'REST TEST', 0, 1, 1);
COMMIT;

-- ----------------------------
-- Table structure for workload_version
-- ----------------------------
DROP TABLE IF EXISTS `workload_version`;
CREATE TABLE `workload_version` (
  `id` int(11) NOT NULL,
  `rev` int(11) NOT NULL,
  `rev_type` tinyint(4) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `modified` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `position` int(11) DEFAULT NULL,
  `job_configuration_id` int(11) DEFAULT NULL,
  `project_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`,`rev`),
  KEY `FKA7F810B8747159` (`rev`),
  CONSTRAINT `FKA7F810B8747159` FOREIGN KEY (`rev`) REFERENCES `revision_info` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;;

SET FOREIGN_KEY_CHECKS = 1;
