-- phpMyAdmin SQL Dump
-- version 4.0.10deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Sep 27, 2014 at 10:05 AM
-- Server version: 5.5.38-0ubuntu0.14.04.1
-- PHP Version: 5.5.9-1ubuntu4.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `mysql_db_tae_junit`
--

-- --------------------------------------------------------

--
-- Table structure for table `junit_migratetest_commandTable`
--

DROP TABLE IF EXISTS `junit_migratetest_commandTable`;
CREATE TABLE IF NOT EXISTS `junit_migratetest_commandTable` (
  `commandID` int(11) NOT NULL AUTO_INCREMENT,
  `commandString` text NOT NULL,
  PRIMARY KEY (`commandID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=6 ;

--
-- Dumping data for table `junit_migratetest_commandTable`
--

INSERT INTO `junit_migratetest_commandTable` (`commandID`, `commandString`) VALUES
(1, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59410 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -test ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTAEMigrationTester:testMigration'),
(2, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 56835 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -test ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTAEMigrationTester:testMigration'),
(3, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 56086 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -test ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTAEMigrationTester:testMigration'),
(4, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 41317 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -test ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTAEMigrationTester:testMigration'),
(5, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 57743 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -test ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTAEMigrationTester:testMigration');

-- --------------------------------------------------------

--
-- Table structure for table `junit_migratetest_execConfig`
--

DROP TABLE IF EXISTS `junit_migratetest_execConfig`;
CREATE TABLE IF NOT EXISTS `junit_migratetest_execConfig` (
  `algorithmExecutionConfigID` int(11) NOT NULL AUTO_INCREMENT,
  `algorithmExecutionConfigHashCode` char(40) NOT NULL,
  `algorithmExecutable` text NOT NULL,
  `algorithmExecutableDirectory` text NOT NULL,
  `parameterFile` text NOT NULL,
  `executeOnCluster` tinyint(1) NOT NULL,
  `deterministicAlgorithm` tinyint(1) NOT NULL,
  `cutoffTime` double NOT NULL,
  `algorithmExecutionConfigurationJSON` text NOT NULL,
  `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`algorithmExecutionConfigID`),
  UNIQUE KEY `algorithmExecutionConfigHashCode` (`algorithmExecutionConfigHashCode`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=6 ;

--
-- Dumping data for table `junit_migratetest_execConfig`
--

INSERT INTO `junit_migratetest_execConfig` (`algorithmExecutionConfigID`, `algorithmExecutionConfigHashCode`, `algorithmExecutable`, `algorithmExecutableDirectory`, `parameterFile`, `executeOnCluster`, `deterministicAlgorithm`, `cutoffTime`, `algorithmExecutionConfigurationJSON`, `lastModified`) VALUES
(1, 'BADHASH-255', 'ignore', '/home/sjr/git/MySQLDBTAE', '/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt', 0, 0, 500, '{"@algo-exec-config-id":2,"algo-exec":"ignore","algo-exec-dir":"/home/sjr/git/MySQLDBTAE","algo-pcs":{"@pcs-id":2,"pcs-filename":"/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt","pcs-text":"#Used for getting specific results out from a target wrapper\\n#Some invalid values are specified so that we can test code\\n\\nsolved { SAT, UNSAT, TIMEOUT, CRASHED, ABORT, INVALID } [SAT]\\nruntime [0,1000] [0]\\nrunlength [0,1000000][0]\\nquality [0, 1000000] [0]\\nseed [ -1,4294967295][1]i\\n","pcs-subspace":{}},"algo-cutoff":500.0,"algo-deterministic":false,"algo-tae-context":{}}', '2014-09-27 17:03:00');

-- --------------------------------------------------------

--
-- Table structure for table `junit_migratetest_runConfigs`
--

DROP TABLE IF EXISTS `junit_migratetest_runConfigs`;
CREATE TABLE IF NOT EXISTS `junit_migratetest_runConfigs` (
  `runConfigID` int(11) NOT NULL AUTO_INCREMENT,
  `runConfigUUID` char(48) NOT NULL,
  `execConfigID` int(11) NOT NULL,
  `problemInstance` varchar(8172) NOT NULL,
  `instanceSpecificInformation` longtext NOT NULL,
  `seed` bigint(20) NOT NULL,
  `cutoffTime` double NOT NULL,
  `paramConfiguration` text NOT NULL,
  `cutoffLessThanMax` tinyint(1) NOT NULL,
  `status` enum('NEW','ASSIGNED','COMPLETE','PAUSED') NOT NULL DEFAULT 'NEW',
  `priority` enum('LOW','NORMAL','HIGH','UBER') NOT NULL DEFAULT 'NORMAL',
  `workerUUID` char(48) NOT NULL DEFAULT '0',
  `killJob` tinyint(1) NOT NULL DEFAULT '0',
  `retryAttempts` int(11) NOT NULL DEFAULT '0',
  `runPartition` int(11) NOT NULL,
  `noop` tinyint(1) NOT NULL DEFAULT '0',
  `runResult` enum('TIMEOUT','SAT','UNSAT','CRASHED','ABORT','KILLED') NOT NULL DEFAULT 'ABORT',
  `runLength` double NOT NULL DEFAULT '0',
  `quality` double NOT NULL DEFAULT '0',
  `resultSeed` bigint(20) NOT NULL DEFAULT '1',
  `runtime` double NOT NULL DEFAULT '0',
  `walltime` double NOT NULL DEFAULT '0',
  `additionalRunData` longtext NOT NULL,
  `worstCaseEndtime` datetime NOT NULL DEFAULT '1900-01-01 00:00:00',
  `worstCaseNextUpdateWhenAssigned` datetime NOT NULL DEFAULT '1900-01-01 00:00:00',
  `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`runConfigID`),
  UNIQUE KEY `runConfigUUID` (`runConfigUUID`),
  KEY `status2` (`status`,`priority`),
  KEY `status` (`status`,`workerUUID`,`priority`,`retryAttempts`,`runConfigID`),
  KEY `statusCutoff` (`status`,`cutoffTime`),
  KEY `statusEndtime` (`status`,`worstCaseEndtime`),
  KEY `statusWorstCaseUpdate` (`status`,`worstCaseNextUpdateWhenAssigned`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=26 ;

--
-- Dumping data for table `junit_migratetest_runConfigs`
--

INSERT INTO `junit_migratetest_runConfigs` (`runConfigID`, `runConfigUUID`, `execConfigID`, `problemInstance`, `instanceSpecificInformation`, `seed`, `cutoffTime`, `paramConfiguration`, `cutoffLessThanMax`, `status`, `priority`, `workerUUID`, `killJob`, `retryAttempts`, `runPartition`, `noop`, `runResult`, `runLength`, `quality`, `resultSeed`, `runtime`, `walltime`, `additionalRunData`, `worstCaseEndtime`, `worstCaseNextUpdateWhenAssigned`, `lastModified`) VALUES
(1, 'BADHASH-1', 1, 'TestInstance', '0', 1515946401, 1001, '0.6976859694807698,0.09929272822371915,0.7936173117865861,0.3529587765566635,1.0', 0, 'COMPLETE', 'NORMAL', '0', 0, 0, 2, 0, 'SAT', 0, 0, 1515946401, 10101981, 0, '', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 17:04:28'),
(2, 'BADHASH-2', 1, 'TestInstance', '0', 1120695991, 1001, '0.6528597061778862,0.6705773967041941,0.2563006099110542,0.2609323692133342,3.0', 0, 'COMPLETE', 'NORMAL', '0', 0, 0, 2, 0, 'SAT', 0, 0, 1120695991, 10101982, 0, '', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 17:04:28'),
(3, 'BADHASH-3', 1, 'TestInstance', '0', 2925533823, 1001, '0.9660083223477697,0.29799831412539557,0.9430851471696299,0.6811539232309084,3.0', 0, 'COMPLETE', 'NORMAL', '0', 0, 0, 2, 0, 'SAT', 0, 0, 2925533823, 10101983, 0, '', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 17:04:28'),
(4, 'BADHASH-4', 1, 'TestInstance', '0', 374745277, 1001, '0.026638248286117938,0.22992364956581068,0.57402790700688,0.08725218437908865,3.0', 0, 'COMPLETE', 'NORMAL', '0', 0, 0, 2, 0, 'SAT', 0, 0, 374745277, 10101984, 0, '', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 17:04:28'),
(5, 'BADHASH-5', 1, 'TestInstance', '0', 2420327734, 1001, '0.3635103225740778,0.30123209256489847,0.47148377840708,0.5635264643785715,1.0', 0, 'COMPLETE', 'NORMAL', '0', 0, 0, 2, 0, 'SAT', 0, 0, 2420327734, 10101985, 0, '', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 17:04:28'),
(6, 'BADHASH-6', 1, 'TestInstance', '0', 1515946401, 1001, '0.6976859694807698,0.09929272822371915,0.7936173117865861,0.3529587765566635,1.0', 0, 'COMPLETE', 'NORMAL', '0', 0, 0, 1, 0, 'SAT', 0, 0, 1515946401, 10101986, 0, '', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 17:04:28'),
(7, 'BADHASH-7', 1, 'TestInstance', '0', 1120695991, 1001, '0.6528597061778862,0.6705773967041941,0.2563006099110542,0.2609323692133342,3.0', 0, 'COMPLETE', 'NORMAL', '0', 0, 0, 1, 0, 'SAT', 0, 0, 1120695991, 10101987, 0, '', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 17:04:28'),
(8, 'BADHASH-8', 1, 'TestInstance', '0', 2925533823, 1001, '0.9660083223477697,0.29799831412539557,0.9430851471696299,0.6811539232309084,3.0', 0, 'COMPLETE', 'NORMAL', '0', 0, 0, 1, 0, 'SAT', 0, 0, 2925533823, 10101988, 0, '', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 17:04:28'),
(9, 'BADHASH-9', 1, 'TestInstance', '0', 374745277, 1001, '0.026638248286117938,0.22992364956581068,0.57402790700688,0.08725218437908865,3.0', 0, 'COMPLETE', 'NORMAL', '0', 0, 0, 1, 0, 'SAT', 0, 0, 374745277, 10101989, 0, '', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 17:04:28'),
(10, 'BADHASH-10', 1, 'TestInstance', '0', 2420327734, 1001, '0.3635103225740778,0.30123209256489847,0.47148377840708,0.5635264643785715,1.0', 0, 'COMPLETE', 'NORMAL', '0', 0, 0, 1, 0, 'SAT', 0, 0, 2420327734, 10101990, 0, '', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 17:04:28'),
(11, 'BADHASH-11', 1, 'TestInstance', '0', 2420327734, 1001, '0.3635103225740778,0.30123209256489847,0.47148377840708,0.5635264643785715,1.0', 0, 'ASSIGNED', 'NORMAL', '0', 0, 0, 3, 0, 'SAT', 0, 0, 2420327734, 10101991, 0, '', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 17:04:28');

-- --------------------------------------------------------

--
-- Table structure for table `junit_migratetest_version`
--

DROP TABLE IF EXISTS `junit_migratetest_version`;
CREATE TABLE IF NOT EXISTS `junit_migratetest_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` varchar(255) NOT NULL,
  `hash` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash` (`hash`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=6 ;

--
-- Dumping data for table `junit_migratetest_version`
--

INSERT INTO `junit_migratetest_version` (`id`, `version`, `hash`) VALUES
(1, 'v0.92.00dev-development-112 (8a5109587631)', '84ed1641e09245a2869dfe555e546f1bfc369c1c');

-- --------------------------------------------------------

--
-- Table structure for table `junit_migratetest_workers`
--

DROP TABLE IF EXISTS `junit_migratetest_workers`;
CREATE TABLE IF NOT EXISTS `junit_migratetest_workers` (
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
  `currentlyIdle` tinyint(1) DEFAULT '0',
  `endTime_UPDATEABLE` datetime NOT NULL,
  `runsToBatch_UPDATEABLE` int(11) NOT NULL,
  `minRunsToBatch_UPDATEABLE` int(11) NOT NULL,
  `maxRunsToBatch_UPDATEABLE` int(11) NOT NULL,
  `autoAdjustBatchSize_UPDATEABLE` tinyint(1) DEFAULT '0',
  `delayBetweenRequests_UPDATEABLE` int(11) NOT NULL,
  `pool_UPDATEABLE` varchar(64) NOT NULL,
  `poolIdleTimeLimit_UPDATEABLE` int(11) NOT NULL,
  `workerIdleTime_UPDATEABLE` int(11) NOT NULL,
  `concurrencyFactor_UPDATEABLE` int(11) NOT NULL DEFAULT '0',
  `upToDate` tinyint(1) NOT NULL,
  `worstCaseNextUpdateWhenRunning` datetime NOT NULL DEFAULT '2028-10-10 12:34:56',
  `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`workerUUID`),
  KEY `sumIdleTime` (`startWeekYear`,`workerIdleTime_UPDATEABLE`),
  KEY `currentlyIdleTime` (`currentlyIdle`,`worstCaseNextUpdateWhenRunning`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
