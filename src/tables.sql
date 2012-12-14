




CREATE TABLE IF NOT EXISTS `commandTable_ACLIB_POOL_NAME` (
  `commandID` int(11) NOT NULL AUTO_INCREMENT,
  `commandString` text COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`commandID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;


CREATE TABLE IF NOT EXISTS `execConfig_ACLIB_POOL_NAME` (
  `algorithmExecutionConfigID` int(11) NOT NULL AUTO_INCREMENT,
  `algorithmExecutionConfigHashCode` char(40) COLLATE utf8_unicode_ci NOT NULL,
  `algorithmExecutable` varchar(256) COLLATE utf8_unicode_ci NOT NULL,
  `algorithmExecutableDirectory` varchar(256) COLLATE utf8_unicode_ci NOT NULL,
  `parameterFile` varchar(256) COLLATE utf8_unicode_ci NOT NULL,
  `executeOnCluster` tinyint(1) NOT NULL,
  `deterministicAlgorithm` tinyint(1) NOT NULL,
  `cutoffTime` double NOT NULL,
  `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`algorithmExecutionConfigID`),
  UNIQUE KEY `algorithmExecutionConfigHashCode` (`algorithmExecutionConfigHashCode`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci AUTO_INCREMENT=1 ;



CREATE TABLE IF NOT EXISTS `runConfigs_ACLIB_POOL_NAME` (
 `runConfigUUID` char(48) COLLATE utf8_unicode_ci NOT NULL,
 `execConfigID` int(11) NOT NULL,
 `problemInstance` varchar(128) COLLATE utf8_unicode_ci NOT NULL,
 `seed` bigint(20) NOT NULL,
 `cutoffTime` double NOT NULL,
 `paramConfiguration` varchar(2048) COLLATE utf8_unicode_ci NOT NULL,
 `paramConfigurationHash` char(40) COLLATE utf8_unicode_ci NOT NULL,
 `cutoffLessThanMax` tinyint(1) NOT NULL,
 `status` enum('NEW','ASSIGNED','COMPLETE') COLLATE utf8_unicode_ci NOT NULL DEFAULT 'NEW',
 `priority` int(11) NOT NULL DEFAULT '0',
 `workerUUID` char(48) COLLATE utf8_unicode_ci DEFAULT NULL,
 `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
 `noop` tinyint(1) NOT NULL DEFAULT '0',
 PRIMARY KEY (`runConfigUUID`),
 KEY `execConfigID` (`execConfigID`),
 KEY `status` (`status`,`workerUUID`),
 KEY `runConfigUUID_sort` (`priority`,`runConfigUUID`),
 CONSTRAINT `runConfigs_ACLIB_POOL_NAME_ibfk_1` FOREIGN KEY (`execConfigID`) REFERENCES `execConfig_ACLIB_POOL_NAME` (`algorithmExecutionConfigID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


CREATE TABLE IF NOT EXISTS `algorithmRuns_ACLIB_POOL_NAME` (
`runConfigUUID` char(48) COLLATE utf8_unicode_ci NOT NULL,
`algorithmRunID` int(11) NOT NULL AUTO_INCREMENT,
`runResult` enum('TIMEOUT','SAT','UNSAT','CRASHED','ABORT') COLLATE
utf8_unicode_ci NOT NULL DEFAULT 'ABORT',
`runLength` double NOT NULL DEFAULT '0',
`quality` double NOT NULL DEFAULT '0',
`result_seed` int(11) NOT NULL DEFAULT 1,
`result_line` varchar(2048) COLLATE utf8_unicode_ci NOT NULL DEFAULT
'ABORT,0,0,0,0',
`runtime` double NOT NULL DEFAULT '0',
`additional_run_data` varchar(2048) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
PRIMARY KEY (`algorithmRunID`),
UNIQUE KEY `runConfigUUID_2` (`runConfigUUID`),
CONSTRAINT `algorithmRuns_ACLIB_POOL_NAME_ibfk_1` FOREIGN KEY (`runConfigUUID`)
REFERENCES `runConfigs_ACLIB_POOL_NAME` (`runConfigUUID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8
COLLATE=utf8_unicode_ci;