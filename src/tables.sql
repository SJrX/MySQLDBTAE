
CREATE TABLE IF NOT EXISTS `ACLIB_POOL_NAME_commandTable` (
  `commandID` int(11) NOT NULL AUTO_INCREMENT,
  `commandString` text NOT NULL,
  PRIMARY KEY (`commandID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;


CREATE TABLE IF NOT EXISTS `ACLIB_POOL_NAME_algoExecConfig` (
  `algorithmExecutionConfigID` int(11) NOT NULL AUTO_INCREMENT,
  `algorithmExecutionConfigHashCode` char(40) NOT NULL,
  `algorithmExecutable` TEXT NOT NULL,
  `algorithmExecutableDirectory` TEXT NOT NULL,
  `parameterFile` TEXT NOT NULL,
  `deterministicAlgorithm` tinyint(1) NOT NULL,
  `cutoffTime` double NOT NULL,
  `algorithmExecutionConfigurationJSON` TEXT NOT NULL, #Full JSON representation (this is a much better approach and this will likely supplant the other fields, in the interim this will be read first, otherwise the others will)
  `lastModified` timestamp NOT NULL DEFAULT NOW() ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`algorithmExecutionConfigID`),
  UNIQUE KEY `algorithmExecutionConfigHashCode` (`algorithmExecutionConfigHashCode`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;


CREATE TABLE IF NOT EXISTS `ACLIB_POOL_NAME_runs` (
`runID` int(11) NOT NULL AUTO_INCREMENT,
`runHashCode` char(48) NOT NULL,
`algorithmExecutionConfigID` int(11) NOT NULL,
`problemInstance` varchar(8172) NOT NULL,
`instanceSpecificInformation` LONGTEXT NOT NULL,
`seed` bigint(20) NOT NULL,
`cutoffTime` double NOT NULL,
`paramConfiguration` TEXT NOT NULL,
`cutoffLessThanMax` tinyint(1) NOT NULL,
`status` enum('NEW','ASSIGNED','COMPLETE','PAUSED') NOT NULL DEFAULT 'NEW',
`priority` enum('LOW','NORMAL','HIGH','UBER') NOT NULL DEFAULT 'NORMAL',
`workerUUID` char(48)  NOT NULL DEFAULT '0',
`killJob` tinyint(1) NOT NULL DEFAULT '0',
`retryAttempts` int(11) NOT NULL DEFAULT '0',
`runPartition` int(11) NOT NULL,
`result_status` enum('TIMEOUT','SAT','UNSAT','CRASHED','ABORT','KILLED') NOT NULL DEFAULT 'ABORT',
`result_runLength` double NOT NULL DEFAULT '0',
`result_quality` double NOT NULL DEFAULT '0',
`result_seed` bigint(20) NOT NULL DEFAULT 1,
`result_runtime` double NOT NULL DEFAULT '0',
`result_walltime` double NOT NULL DEFAULT '0',
`result_additionalRunData` LONGTEXT NOT NULL,
`worstCaseEndtime`  datetime NOT NULL DEFAULT '1900-01-01 00:00:00',
`worstCaseNextUpdateWhenAssigned` datetime NOT NULL DEFAULT '1900-01-01 00:00:00',
`lastInsertionTime` datetime NOT NULL DEFAULT '1900-01-01 00:00:00',
`lastAssignedTime` datetime NOT NULL DEFAULT '1900-01-01 00:00:00',
`lastCompletedTime` datetime NOT NULL DEFAULT '1900-01-01 00:00:00',
`lastModified` timestamp NOT NULL DEFAULT NOW() ON UPDATE CURRENT_TIMESTAMP,
 PRIMARY KEY (`runID`),
 UNIQUE KEY `runConfigUUID` (`runHashCode`),
 KEY `status2` (`status`,`priority`),
 KEY `status` (`status`,`workerUUID`,`priority`,`retryAttempts`,`runID`),
 KEY `statusCutoff` (`status`,`cutoffTime`),
 KEY `statusEndtime` (`status`,`worstCaseEndtime`),
 KEY `statusWorstCaseUpdate` (`status`,`worstCaseNextUpdateWhenAssigned`)
) ENGINE=InnoDB;


 
CREATE TABLE IF NOT EXISTS `ACLIB_POOL_NAME_workers` (
`workerID` int(11) NOT NULL AUTO_INCREMENT,
`workerUUID` char(40) NOT NULL,
`hostname` varchar(256) NOT NULL,
`username` varchar(256) NOT NULL,
`jobID` varchar(64) NOT NULL,
`status` enum('RUNNING','DONE') NOT NULL DEFAULT 'RUNNING',
`version` varchar(255) NOT NULL DEFAULT 'UNKNOWN',
`crashInfo` text,
`startTime` datetime NOT NULL,
`startWeekYear` int(11) NOT NULL,
`originalEndTime` datetime NOT NULL,
`currentlyIdle` BOOLEAN DEFAULT FALSE,
`endTime_UPDATEABLE` datetime NOT NULL,
`runsToBatch_UPDATEABLE` int(11) NOT NULL,
`minRunsToBatch_UPDATEABLE` int(11) NOT NULL,
`maxRunsToBatch_UPDATEABLE` int(11) NOT NULL,
`autoAdjustBatchSize_UPDATEABLE` BOOLEAN DEFAULT FALSE,
`delayBetweenRequests_UPDATEABLE` int(11) NOT NULL,
`pool_UPDATEABLE` varchar(64) NOT NULL,
`poolIdleTimeLimit_UPDATEABLE` int(11) NOT NULL,
`workerIdleTime_UPDATEABLE` int(11) NOT NULL,
`concurrencyFactor_UPDATEABLE` int(11) NOT NULL DEFAULT 0,
`upToDate` tinyint(1) NOT NULL,
`worstCaseNextUpdateWhenRunning` datetime NOT NULL DEFAULT '2028-10-10 12:34:56', #This date should be in the future
`lastModified` timestamp NOT NULL DEFAULT NOW() ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`workerID`),
UNIQUE KEY `workerUUID` (`workerUUID`),
KEY `sumIdleTime` (`startWeekYear`,`workerIdleTime_UPDATEABLE`),
KEY `currentlyIdleTime` (`currentlyIdle`,`worstCaseNextUpdateWhenRunning`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `ACLIB_POOL_NAME_version` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`version` varchar(255) NOT NULL,
`hash` varchar(128) NOT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `hash` (`hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*CREATE VIEW ACLIB_POOL_NAME_workers_alive AS(
SELECT * FROM ACLIB_POOL_NAME_workers WHERE status <> "DONE")*/
