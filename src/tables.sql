
CREATE TABLE IF NOT EXISTS `ACLIB_POOL_NAME_commandTable` (
  `commandID` int(11) NOT NULL AUTO_INCREMENT,
  `commandString` text COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`commandID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;


CREATE TABLE IF NOT EXISTS `ACLIB_POOL_NAME_execConfig` (
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



CREATE TABLE IF NOT EXISTS `ACLIB_POOL_NAME_runConfigs` (
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
`killJob` tinyint(1) NOT NULL DEFAULT '0',
`retryAttempts` int(11) NOT NULL DEFAULT '0',
`runPartition` int(11) NOT NULL,
`noop` tinyint(1) NOT NULL DEFAULT '0',
`runResult` enum('TIMEOUT','SAT','UNSAT','CRASHED','ABORT','KILLED') NOT NULL DEFAULT 'ABORT',
`runLength` double NOT NULL DEFAULT '0',
`quality` double NOT NULL DEFAULT '0',
`resultSeed` bigint(20) NOT NULL DEFAULT 1,
`runtime` double NOT NULL DEFAULT '0',
`additionalRunData` varchar(2048) NOT NULL DEFAULT '',
`lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
 PRIMARY KEY (`runConfigID`),
 UNIQUE KEY `runConfigUUID` (`runConfigUUID`),
 KEY `status2` (`status`,`priority`),
 KEY `status` (`status`,`workerUUID`,`priority`,`retryAttempts`)
) ENGINE=InnoDB;


CREATE TABLE IF NOT EXISTS `ACLIB_POOL_NAME_workers` (
`workerUUID` char(40) COLLATE utf8_unicode_ci NOT NULL,
`hostname` varchar(256) COLLATE utf8_unicode_ci NOT NULL,
`jobID` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
`status` enum('RUNNING','DONE') COLLATE utf8_unicode_ci NOT NULL DEFAULT 'RUNNING',
`startTime` datetime NOT NULL,
`endTime` datetime NOT NULL,
`version` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'UNKNOWN',
`crashInfo` text COLLATE utf8_unicode_ci,
`runsToBatch_UPDATEABLE` int(11) NOT NULL,
`delayBetweenRequests_UPDATEABLE` int(11) NOT NULL,
`pool_UPDATEABLE` varchar(64) COLLATE utf8_unicode_ci NOT NULL,
`upToDate` tinyint(1) NOT NULL,
`lastModified` timestamp NOT NULL DEFAULT NOW() ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`workerUUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `ACLIB_POOL_NAME_version` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`version` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
`hash` varchar(128) COLLATE utf8_unicode_ci NOT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `hash` (`hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

