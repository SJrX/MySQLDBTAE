




CREATE TABLE IF NOT EXISTS `commandTable_ACLIB_POOL_NAME` (
  `commandID` int(11) NOT NULL AUTO_INCREMENT,
  `commandString` text COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`commandID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;


CREATE TABLE IF NOT EXISTS `execConfig_ACLIB_POOL_NAME` (
  `algorithmExecutionConfigID` int(11) NOT NULL AUTO_INCREMENT,
  `algorithmExecutionConfigHashCode` char(40) COLLATE utf8_unicode_ci NOT NULL,
  `algorithmExecutable` varchar(4096) COLLATE utf8_unicode_ci NOT NULL,
  `algorithmExecutableDirectory` varchar(4096) COLLATE utf8_unicode_ci NOT NULL,
  `parameterFile` varchar(4096) COLLATE utf8_unicode_ci NOT NULL,
  `executeOnCluster` tinyint(1) NOT NULL,
  `deterministicAlgorithm` tinyint(1) NOT NULL,
  `cutoffTime` double NOT NULL,
  `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`algorithmExecutionConfigID`),
  UNIQUE KEY `algorithmExecutionConfigHashCode` (`algorithmExecutionConfigHashCode`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;



CREATE TABLE IF NOT EXISTS `runConfigs_ACLIB_POOL_NAME` (
 `runConfigID` int(11) NOT NULL AUTO_INCREMENT,
 `runConfigUUID` char(48) NOT NULL,
 `execConfigID` int(11) NOT NULL,
 `problemInstance` varchar(2048) NOT NULL,
 `instanceSpecificInformation` varchar(2048) NOT NULL DEFAULT '0',
 `seed` bigint(20) NOT NULL,
 `cutoffTime` double NOT NULL,
 `paramConfiguration` varchar(2048) NOT NULL,
 `cutoffLessThanMax` tinyint(1) NOT NULL,
 `status` enum('NEW','ASSIGNED','COMPLETE','PAUSED') NOT NULL DEFAULT 'NEW',
 `priority` enum('LOW','NORMAL','HIGH','UBER') NOT NULL DEFAULT 'NORMAL',
 `workerUUID` char(48)  NOT NULL DEFAULT '0',
 `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
 `killJob` tinyint(1) NOT NULL DEFAULT '0',
 `retryAttempts` int(11) NOT NULL DEFAULT '0',
 `runPartition` int(11) NOT NULL,
 `noop` tinyint(1) NOT NULL DEFAULT '0',
 `runResult` enum('TIMEOUT','SAT','UNSAT','CRASHED','ABORT') NOT NULL DEFAULT 'ABORT',
`runLength` double NOT NULL DEFAULT '0',
`quality` double NOT NULL DEFAULT '0',
`result_seed` bigint(20) NOT NULL DEFAULT 1,
`runtime` double NOT NULL DEFAULT '0',
`additional_run_data` varchar(2048) NOT NULL DEFAULT '',
 PRIMARY KEY (`runConfigID`),
 UNIQUE KEY `runConfigUUID` (`runConfigUUID`),
 KEY `status` (`status`,`workerUUID`)
) ENGINE=InnoDB;


CREATE TABLE IF NOT EXISTS `workers_ACLIB_POOL_NAME` (
`workerUUID` char(40) COLLATE utf8_unicode_ci NOT NULL,
`hostname` varchar(256) COLLATE utf8_unicode_ci NOT NULL,
`jobID` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
`status` enum('RUNNING','DONE') COLLATE utf8_unicode_ci NOT NULL
DEFAULT 'RUNNING',
`startTime` datetime NOT NULL,
`endTime` datetime NOT NULL,
`version` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'UNKNOWN',
`crashInfo` text COLLATE utf8_unicode_ci,
`runsToBatch` int(11) NOT NULL,
`delayBetweenRequests` int(11) NOT NULL,
`pool` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
`upToDate` tinyint(1) NOT NULL,
`lastModified` timestamp NOT NULL DEFAULT NOW() ON UPDATE
CURRENT_TIMESTAMP,
PRIMARY KEY (`workerUUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci

