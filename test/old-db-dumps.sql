-- phpMyAdmin SQL Dump
-- version 4.0.10deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Sep 27, 2014 at 12:01 PM
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
-- Table structure for table `junit_aborttest_commandTable`
--

CREATE TABLE IF NOT EXISTS `junit_aborttest_commandTable` (
  `commandID` int(11) NOT NULL AUTO_INCREMENT,
  `commandString` text NOT NULL,
  PRIMARY KEY (`commandID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;

--
-- Dumping data for table `junit_aborttest_commandTable`
--

INSERT INTO `junit_aborttest_commandTable` (`commandID`, `commandString`) VALUES
(1, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite'),
(2, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite'),
(3, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite');

-- --------------------------------------------------------

--
-- Table structure for table `junit_aborttest_execConfig`
--

CREATE TABLE IF NOT EXISTS `junit_aborttest_execConfig` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;

--
-- Dumping data for table `junit_aborttest_execConfig`
--

INSERT INTO `junit_aborttest_execConfig` (`algorithmExecutionConfigID`, `algorithmExecutionConfigHashCode`, `algorithmExecutable`, `algorithmExecutableDirectory`, `parameterFile`, `executeOnCluster`, `deterministicAlgorithm`, `cutoffTime`, `algorithmExecutionConfigurationJSON`, `lastModified`) VALUES
(1, 'c5c1133754733b1e7924f8944dbe954ae2222482', 'java -cp /home/sjr/git/MySQLDBTAE/bin:/home/sjr/git/AEATK/bin:/home/sjr/git/AEATK/lib/commons-collections-3.2.1.jar:/home/sjr/git/AEATK/lib/commons-math-2.2.jar:/home/sjr/git/AEATK/lib/opencsv-2.3.jar:/home/sjr/git/AEATK/lib/spi-0.2.4.jar:/home/sjr/git/AEATK/lib/jcip-annotations.jar:/home/sjr/git/AEATK/lib/Jama-1.0.2.jar:/home/sjr/git/AEATK/lib/numerics4j-1.3.jar:/home/sjr/git/AEATK/lib/guava-14.0.1.jar:/home/sjr/git/AEATK/lib/exp4j-0.3.10.jar:/home/sjr/git/AEATK/lib/commons-io-2.1.jar:/home/sjr/git/AEATK/lib/slf4j-api-1.7.5.jar:/home/sjr/git/AEATK/lib/jackson-annotations-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-core-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-databind-2.3.1.jar:/home/sjr/git/AEATK/lib/logback-access-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-classic-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-core-1.1.2.jar:/home/sjr/git/RandomForests/bin:/opt/eclipse/plugins/org.junit_4.11.0.v201303080030/junit.jar:/opt/eclipse/plugins/org.hamcrest.core_1.3.0.v201303031735.jar:/home/sjr/git/AEATK/lib/jcommander.jar:/home/sjr/git/AEATK/lib/commons-math3-3.3.jar:/home/sjr/git/MySQLDBTAE/lib/commons-codec-1.7.jar:/home/sjr/git/MySQLDBTAE/lib/mchange-commons-java-0.2.3.3.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-0.9.2-pre8.jar:/home/sjr/git/MySQLDBTAE/lib/mysql-connector-java-5.1.26-bin.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-oracle-thin-extras-0.9.2-pre8.jar:/opt/eclipse/configuration/org.eclipse.osgi/bundles/164/1/.cp/:/opt/eclipse/configuration/org.eclipse.osgi/bundles/163/1/.cp/ ca.ubc.cs.beta.targetalgorithmevaluator.ParamEchoExecutorThreeFifthChanceAbort', '/home/sjr/git/MySQLDBTAE', '/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt', 0, 0, 500, '{"@algo-exec-config-id":2,"algo-exec":"java -cp /home/sjr/git/MySQLDBTAE/bin:/home/sjr/git/AEATK/bin:/home/sjr/git/AEATK/lib/commons-collections-3.2.1.jar:/home/sjr/git/AEATK/lib/commons-math-2.2.jar:/home/sjr/git/AEATK/lib/opencsv-2.3.jar:/home/sjr/git/AEATK/lib/spi-0.2.4.jar:/home/sjr/git/AEATK/lib/jcip-annotations.jar:/home/sjr/git/AEATK/lib/Jama-1.0.2.jar:/home/sjr/git/AEATK/lib/numerics4j-1.3.jar:/home/sjr/git/AEATK/lib/guava-14.0.1.jar:/home/sjr/git/AEATK/lib/exp4j-0.3.10.jar:/home/sjr/git/AEATK/lib/commons-io-2.1.jar:/home/sjr/git/AEATK/lib/slf4j-api-1.7.5.jar:/home/sjr/git/AEATK/lib/jackson-annotations-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-core-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-databind-2.3.1.jar:/home/sjr/git/AEATK/lib/logback-access-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-classic-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-core-1.1.2.jar:/home/sjr/git/RandomForests/bin:/opt/eclipse/plugins/org.junit_4.11.0.v201303080030/junit.jar:/opt/eclipse/plugins/org.hamcrest.core_1.3.0.v201303031735.jar:/home/sjr/git/AEATK/lib/jcommander.jar:/home/sjr/git/AEATK/lib/commons-math3-3.3.jar:/home/sjr/git/MySQLDBTAE/lib/commons-codec-1.7.jar:/home/sjr/git/MySQLDBTAE/lib/mchange-commons-java-0.2.3.3.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-0.9.2-pre8.jar:/home/sjr/git/MySQLDBTAE/lib/mysql-connector-java-5.1.26-bin.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-oracle-thin-extras-0.9.2-pre8.jar:/opt/eclipse/configuration/org.eclipse.osgi/bundles/164/1/.cp/:/opt/eclipse/configuration/org.eclipse.osgi/bundles/163/1/.cp/ ca.ubc.cs.beta.targetalgorithmevaluator.ParamEchoExecutorThreeFifthChanceAbort","algo-exec-dir":"/home/sjr/git/MySQLDBTAE","algo-pcs":{"@pcs-id":2,"pcs-filename":"/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt","pcs-text":"#Used for getting specific results out from a target wrapper\\n#Some invalid values are specified so that we can test code\\n\\nsolved { SAT, UNSAT, TIMEOUT, CRASHED, ABORT, INVALID } [SAT]\\nruntime [0,1000] [0]\\nrunlength [0,1000000][0]\\nquality [0, 1000000] [0]\\nseed [ -1,4294967295][1]i\\n","pcs-subspace":{}},"algo-cutoff":500.0,"algo-deterministic":false,"algo-tae-context":{}}', '2014-09-27 16:17:08');

-- --------------------------------------------------------

--
-- Table structure for table `junit_aborttest_runConfigs`
--

CREATE TABLE IF NOT EXISTS `junit_aborttest_runConfigs` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=5 ;

--
-- Dumping data for table `junit_aborttest_runConfigs`
--

INSERT INTO `junit_aborttest_runConfigs` (`runConfigID`, `runConfigUUID`, `execConfigID`, `problemInstance`, `instanceSpecificInformation`, `seed`, `cutoffTime`, `paramConfiguration`, `cutoffLessThanMax`, `status`, `priority`, `workerUUID`, `killJob`, `retryAttempts`, `runPartition`, `noop`, `runResult`, `runLength`, `quality`, `resultSeed`, `runtime`, `walltime`, `additionalRunData`, `worstCaseEndtime`, `worstCaseNextUpdateWhenAssigned`, `lastModified`) VALUES
(1, 'd5991519ff2b6f5f415fe77d8d05124866b9fd62', 1, 'TestInstance', 'SLEEP', 351135185, 1001, '0.8942760347302873,0.7272961599236979,5.0E-4,0.08175503146328147,1.0', 0, 'COMPLETE', 'HIGH', '5414b982-3a07-4445-b248-6dd2d93488a9', 0, 1, 0, 0, 'SAT', 727296.1599236979, 894276.0347302873, 351135185, 0.5, 0.586, '', '1900-01-01 00:00:00', '2014-09-27 10:40:25', '2014-09-27 16:17:05'),
(2, '2ebee687682eb9526b62ccab3e0a3710eb76af7c', 1, 'TestInstance', 'SLEEP', 3664665098, 1001, '0.018619507950320524,0.5567087910663919,5.0E-4,0.8532463336937953,1.0', 0, 'COMPLETE', 'HIGH', '5414b982-3a07-4445-b248-6dd2d93488a9', 0, 1, 0, 0, 'SAT', 556708.791066392, 18619.507950320523, 3664665098, 0.5, 0.583, '', '1900-01-01 00:00:00', '2014-09-27 10:40:26', '2014-09-27 16:17:07'),
(3, '3f13ed76864f3bcb75f7a3cdea60589835821331', 1, 'TestInstance', 'SLEEP', 924424735, 1001, '0.5477029021997973,0.6692920914372854,5.0E-4,0.21523440635874067,1.0', 0, 'COMPLETE', 'HIGH', '5414b982-3a07-4445-b248-6dd2d93488a9', 0, 1, 0, 0, 'SAT', 669292.0914372854, 547702.9021997973, 924424735, 0.5, 0.581, '', '1900-01-01 00:00:00', '2014-09-27 10:40:30', '2014-09-27 16:17:11');

-- --------------------------------------------------------

--
-- Table structure for table `junit_aborttest_version`
--

CREATE TABLE IF NOT EXISTS `junit_aborttest_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` varchar(255) NOT NULL,
  `hash` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash` (`hash`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=7 ;

--
-- Dumping data for table `junit_aborttest_version`
--

INSERT INTO `junit_aborttest_version` (`id`, `version`, `hash`) VALUES
(1, 'v0.92.00dev-development-112 (8a5109587631)', '84ed1641e09245a2869dfe555e546f1bfc369c1c');

-- --------------------------------------------------------

--
-- Table structure for table `junit_aborttest_workers`
--

CREATE TABLE IF NOT EXISTS `junit_aborttest_workers` (
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

--
-- Dumping data for table `junit_aborttest_workers`
--

INSERT INTO `junit_aborttest_workers` (`workerUUID`, `hostname`, `username`, `jobID`, `status`, `version`, `crashInfo`, `startTime`, `startWeekYear`, `originalEndTime`, `currentlyIdle`, `endTime_UPDATEABLE`, `runsToBatch_UPDATEABLE`, `minRunsToBatch_UPDATEABLE`, `maxRunsToBatch_UPDATEABLE`, `autoAdjustBatchSize_UPDATEABLE`, `delayBetweenRequests_UPDATEABLE`, `pool_UPDATEABLE`, `poolIdleTimeLimit_UPDATEABLE`, `workerIdleTime_UPDATEABLE`, `concurrencyFactor_UPDATEABLE`, `upToDate`, `worstCaseNextUpdateWhenRunning`, `lastModified`) VALUES
('5414b982-3a07-4445-b248-6dd2d93488a9', 'hilbert', 'sjr', 'CLI/19791@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Idle limit reached: 61.0 versus limit: 60', '2014-09-27 09:17:03', 392014, '2014-09-28 09:16:02', 1, '2014-09-28 09:16:02', 1, 1, 100, 1, 1, 'junit_aborttest', 14400000, 69, 4, 1, '2014-09-27 09:18:12', '2014-09-27 16:18:12'),
('90b27b33-9e24-448f-9842-7ccbe40097b0', 'hilbert', 'sjr', 'CLI/19858@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Idle limit reached: 61.0 versus limit: 60', '2014-09-27 09:17:06', 392014, '2014-09-28 09:16:06', 1, '2014-09-28 09:16:06', 1, 1, 100, 1, 1, 'junit_aborttest', 14400000, 66, 4, 1, '2014-09-27 09:18:07', '2014-09-27 16:18:07'),
('d216b78a-8a3b-4eb5-9d07-5702a1171883', 'hilbert', 'sjr', 'CLI/19922@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Idle limit reached: 61.0 versus limit: 60', '2014-09-27 09:17:08', 392014, '2014-09-28 09:16:08', 1, '2014-09-28 09:16:08', 1, 1, 100, 1, 1, 'junit_aborttest', 14400000, 66, 4, 1, '2014-09-27 09:18:09', '2014-09-27 16:18:09');

-- --------------------------------------------------------

--
-- Table structure for table `junit_AutoAdjustTest_commandTable`
--

CREATE TABLE IF NOT EXISTS `junit_AutoAdjustTest_commandTable` (
  `commandID` int(11) NOT NULL AUTO_INCREMENT,
  `commandString` text NOT NULL,
  PRIMARY KEY (`commandID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `junit_AutoAdjustTest_commandTable`
--

INSERT INTO `junit_AutoAdjustTest_commandTable` (`commandID`, `commandString`) VALUES
(1, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite');

-- --------------------------------------------------------

--
-- Table structure for table `junit_AutoAdjustTest_execConfig`
--

CREATE TABLE IF NOT EXISTS `junit_AutoAdjustTest_execConfig` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `junit_AutoAdjustTest_execConfig`
--

INSERT INTO `junit_AutoAdjustTest_execConfig` (`algorithmExecutionConfigID`, `algorithmExecutionConfigHashCode`, `algorithmExecutable`, `algorithmExecutableDirectory`, `parameterFile`, `executeOnCluster`, `deterministicAlgorithm`, `cutoffTime`, `algorithmExecutionConfigurationJSON`, `lastModified`) VALUES
(1, '2625865e6eafb0a4fa16ff33c3ac6d6469acb9eb', 'java -cp /home/sjr/git/MySQLDBTAE/bin:/home/sjr/git/AEATK/bin:/home/sjr/git/AEATK/lib/commons-collections-3.2.1.jar:/home/sjr/git/AEATK/lib/commons-math-2.2.jar:/home/sjr/git/AEATK/lib/opencsv-2.3.jar:/home/sjr/git/AEATK/lib/spi-0.2.4.jar:/home/sjr/git/AEATK/lib/jcip-annotations.jar:/home/sjr/git/AEATK/lib/Jama-1.0.2.jar:/home/sjr/git/AEATK/lib/numerics4j-1.3.jar:/home/sjr/git/AEATK/lib/guava-14.0.1.jar:/home/sjr/git/AEATK/lib/exp4j-0.3.10.jar:/home/sjr/git/AEATK/lib/commons-io-2.1.jar:/home/sjr/git/AEATK/lib/slf4j-api-1.7.5.jar:/home/sjr/git/AEATK/lib/jackson-annotations-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-core-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-databind-2.3.1.jar:/home/sjr/git/AEATK/lib/logback-access-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-classic-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-core-1.1.2.jar:/home/sjr/git/RandomForests/bin:/opt/eclipse/plugins/org.junit_4.11.0.v201303080030/junit.jar:/opt/eclipse/plugins/org.hamcrest.core_1.3.0.v201303031735.jar:/home/sjr/git/AEATK/lib/jcommander.jar:/home/sjr/git/AEATK/lib/commons-math3-3.3.jar:/home/sjr/git/MySQLDBTAE/lib/commons-codec-1.7.jar:/home/sjr/git/MySQLDBTAE/lib/mchange-commons-java-0.2.3.3.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-0.9.2-pre8.jar:/home/sjr/git/MySQLDBTAE/lib/mysql-connector-java-5.1.26-bin.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-oracle-thin-extras-0.9.2-pre8.jar:/opt/eclipse/configuration/org.eclipse.osgi/bundles/164/1/.cp/:/opt/eclipse/configuration/org.eclipse.osgi/bundles/163/1/.cp/ ca.ubc.cs.beta.targetalgorithmevaluator.TrueSleepyParamEchoExecutor', '/home/sjr/git/MySQLDBTAE', '/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt', 0, 0, 500, '{"@algo-exec-config-id":2,"algo-exec":"java -cp /home/sjr/git/MySQLDBTAE/bin:/home/sjr/git/AEATK/bin:/home/sjr/git/AEATK/lib/commons-collections-3.2.1.jar:/home/sjr/git/AEATK/lib/commons-math-2.2.jar:/home/sjr/git/AEATK/lib/opencsv-2.3.jar:/home/sjr/git/AEATK/lib/spi-0.2.4.jar:/home/sjr/git/AEATK/lib/jcip-annotations.jar:/home/sjr/git/AEATK/lib/Jama-1.0.2.jar:/home/sjr/git/AEATK/lib/numerics4j-1.3.jar:/home/sjr/git/AEATK/lib/guava-14.0.1.jar:/home/sjr/git/AEATK/lib/exp4j-0.3.10.jar:/home/sjr/git/AEATK/lib/commons-io-2.1.jar:/home/sjr/git/AEATK/lib/slf4j-api-1.7.5.jar:/home/sjr/git/AEATK/lib/jackson-annotations-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-core-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-databind-2.3.1.jar:/home/sjr/git/AEATK/lib/logback-access-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-classic-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-core-1.1.2.jar:/home/sjr/git/RandomForests/bin:/opt/eclipse/plugins/org.junit_4.11.0.v201303080030/junit.jar:/opt/eclipse/plugins/org.hamcrest.core_1.3.0.v201303031735.jar:/home/sjr/git/AEATK/lib/jcommander.jar:/home/sjr/git/AEATK/lib/commons-math3-3.3.jar:/home/sjr/git/MySQLDBTAE/lib/commons-codec-1.7.jar:/home/sjr/git/MySQLDBTAE/lib/mchange-commons-java-0.2.3.3.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-0.9.2-pre8.jar:/home/sjr/git/MySQLDBTAE/lib/mysql-connector-java-5.1.26-bin.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-oracle-thin-extras-0.9.2-pre8.jar:/opt/eclipse/configuration/org.eclipse.osgi/bundles/164/1/.cp/:/opt/eclipse/configuration/org.eclipse.osgi/bundles/163/1/.cp/ ca.ubc.cs.beta.targetalgorithmevaluator.TrueSleepyParamEchoExecutor","algo-exec-dir":"/home/sjr/git/MySQLDBTAE","algo-pcs":{"@pcs-id":2,"pcs-filename":"/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt","pcs-text":"#Used for getting specific results out from a target wrapper\\n#Some invalid values are specified so that we can test code\\n\\nsolved { SAT, UNSAT, TIMEOUT, CRASHED, ABORT, INVALID } [SAT]\\nruntime [0,1000] [0]\\nrunlength [0,1000000][0]\\nquality [0, 1000000] [0]\\nseed [ -1,4294967295][1]i\\n","pcs-subspace":{}},"algo-cutoff":500.0,"algo-deterministic":false,"algo-tae-context":{}}', '2014-09-27 16:16:57');

-- --------------------------------------------------------

--
-- Table structure for table `junit_AutoAdjustTest_runConfigs`
--

CREATE TABLE IF NOT EXISTS `junit_AutoAdjustTest_runConfigs` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=31 ;

--
-- Dumping data for table `junit_AutoAdjustTest_runConfigs`
--

INSERT INTO `junit_AutoAdjustTest_runConfigs` (`runConfigID`, `runConfigUUID`, `execConfigID`, `problemInstance`, `instanceSpecificInformation`, `seed`, `cutoffTime`, `paramConfiguration`, `cutoffLessThanMax`, `status`, `priority`, `workerUUID`, `killJob`, `retryAttempts`, `runPartition`, `noop`, `runResult`, `runLength`, `quality`, `resultSeed`, `runtime`, `walltime`, `additionalRunData`, `worstCaseEndtime`, `worstCaseNextUpdateWhenAssigned`, `lastModified`) VALUES
(1, '03e28e06f352dd26245161dac75a39f90064c514', 1, 'TestInstance', '0', 3115298154, 20, '0.5496388514457652,0.28131494605893603,0.001,0.7253368745498971,1.0', 1, 'COMPLETE', 'HIGH', '6b7a1528-b953-437e-899f-839bb4469e1d', 0, 1, -9, 0, 'SAT', -1, 0, 3115298154, 8.228485455244346, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:19', '2014-09-27 16:16:59'),
(2, 'f2a394a86738caf46a0d79e8be981eddb79d42fc', 1, 'TestInstance', '0', 756485305, 20, '0.5213508574378005,0.9106466946713202,0.001,0.1761329607860807,2.0', 1, 'COMPLETE', 'HIGH', '6b7a1528-b953-437e-899f-839bb4469e1d', 0, 1, -9, 0, 'SAT', -1, 0, 756485305, 2.959319115008998, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:20', '2014-09-27 16:17:00'),
(3, '523925c5fc7f60670d1a03742583dfaf0c5ed631', 1, 'TestInstance', '0', 1636809671, 20, '0.4004116686189725,0.5076881809905597,0.001,0.3810994494983229,2.0', 1, 'COMPLETE', 'HIGH', '6b7a1528-b953-437e-899f-839bb4469e1d', 0, 1, -9, 0, 'SAT', -1, 0, 1636809671, 3.887685505255516, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:20', '2014-09-27 16:17:00'),
(4, '1600a8386227563bfadcc09308f55d207fc63e72', 1, 'TestInstance', '0', 392687854, 20, '0.6766093207631775,0.43490910204699795,0.001,0.0914297661298351,1.0', 1, 'COMPLETE', 'HIGH', '6b7a1528-b953-437e-899f-839bb4469e1d', 0, 1, -9, 0, 'SAT', -1, 0, 392687854, 9.967479192280646, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:21', '2014-09-27 16:17:01'),
(5, 'e3dd1a4bdfd1659da795f1b234bda2036be34b81', 1, 'TestInstance', '0', 3256399943, 20, '0.7713553622748148,0.17468516718653182,0.001,0.7581896948958305,1.0', 1, 'COMPLETE', 'HIGH', '6b7a1528-b953-437e-899f-839bb4469e1d', 0, 1, -9, 0, 'SAT', -1, 0, 3256399943, 6.280757305388906, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:21', '2014-09-27 16:17:01'),
(6, '4587fd5db557d884cde7280b38a11cf049e007b0', 1, 'TestInstance', '0', 1579166714, 20, '0.16500086985111084,0.9057864213655807,0.001,0.3676784027210254,2.0', 1, 'COMPLETE', 'HIGH', '6b7a1528-b953-437e-899f-839bb4469e1d', 0, 1, -9, 0, 'SAT', -1, 0, 1579166714, 2.6802204793081383, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:21', '2014-09-27 16:17:01'),
(7, 'a9c598e4ce50250c0c7a2dae9f091041bc467fb7', 1, 'TestInstance', '0', 3872060473, 20, '0.8595381792878389,0.39047063344054944,0.001,0.901534332334638,1.0', 1, 'COMPLETE', 'HIGH', '6b7a1528-b953-437e-899f-839bb4469e1d', 0, 1, -9, 0, 'SAT', -1, 0, 3872060473, 9.952234076697982, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:21', '2014-09-27 16:17:01'),
(8, '842dd2abcfdad1f208dd637d8e2cc0936de6c300', 1, 'TestInstance', '0', 3837122936, 20, '0.6965787529401661,0.5274485731208065,0.001,0.8933998031091411,2.0', 1, 'COMPLETE', 'HIGH', '6b7a1528-b953-437e-899f-839bb4469e1d', 0, 1, -9, 0, 'SAT', -1, 0, 3837122936, 3.4271223321836866, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:22', '2014-09-27 16:17:02'),
(9, 'c3d13112a21d235522928eff28e06335319f7487', 1, 'TestInstance', '0', 276070639, 20, '0.02962744782292759,0.17824541818805417,0.001,0.06427770490658523,1.0', 1, 'COMPLETE', 'HIGH', '6b7a1528-b953-437e-899f-839bb4469e1d', 0, 1, -9, 0, 'SAT', -1, 0, 276070639, 1.515451907990547, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:22', '2014-09-27 16:17:02'),
(10, '6a2c341615fb76c9c9d59cb31071ea97021b1fe0', 1, 'TestInstance', '0', 2447905300, 20, '0.6000068086661227,0.3633420058894752,0.001,0.5699473668192636,2.0', 1, 'COMPLETE', 'HIGH', '6b7a1528-b953-437e-899f-839bb4469e1d', 0, 1, -9, 0, 'SAT', -1, 0, 2447905300, 8.556724516527584, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:22', '2014-09-27 16:17:02'),
(11, '58aa0404c4afc790ad0988bceacfb18c3d5f698e', 1, 'TestInstance', '0', 2034730019, 20, '0.34530951998258635,0.4702705683273255,0.001,0.4737475002245634,2.0', 1, 'COMPLETE', 'HIGH', '6b7a1528-b953-437e-899f-839bb4469e1d', 0, 1, -9, 0, 'SAT', -1, 0, 2034730019, 0.17045860262527524, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:22', '2014-09-27 16:17:02'),
(12, '05fdb7d3e09d40aff38e9f27fc1bd5bf0c1620a4', 1, 'TestInstance', '0', 909632482, 20, '0.6881710941842699,0.7694131117966091,0.001,0.21179031657246167,2.0', 1, 'COMPLETE', 'HIGH', '6b7a1528-b953-437e-899f-839bb4469e1d', 0, 1, -9, 0, 'SAT', -1, 0, 909632482, 8.239639055046485, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:22', '2014-09-27 16:17:02'),
(13, 'e98a401bdc8f5215b311cd954c243dec72edce73', 1, 'TestInstance', '0', 911424384, 20, '0.6001250665162603,0.8350124751904788,0.001,0.21220752626838918,1.0', 1, 'COMPLETE', 'HIGH', '6b7a1528-b953-437e-899f-839bb4469e1d', 0, 1, -9, 0, 'SAT', -1, 0, 911424384, 8.357200380277728, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:22', '2014-09-27 16:17:02'),
(14, 'b702124b2e29d303ca19d4990b71eaeb258e61be', 1, 'TestInstance', '0', 1769107956, 20, '0.24320582170832072,0.35515352061095273,0.001,0.41190254434200413,2.0', 1, 'COMPLETE', 'HIGH', '6b7a1528-b953-437e-899f-839bb4469e1d', 0, 1, -9, 0, 'SAT', -1, 0, 1769107956, 8.317604279821968, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:22', '2014-09-27 16:17:02'),
(15, '338c1e31ec1213738425967cead246593d77fbb5', 1, 'TestInstance', '0', 2764238304, 20, '0.6907034094850626,0.8277966214988481,0.001,0.6435993837323972,1.0', 1, 'COMPLETE', 'HIGH', '6b7a1528-b953-437e-899f-839bb4469e1d', 0, 1, -9, 0, 'SAT', -1, 0, 2764238304, 8.944920099129574, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:22', '2014-09-27 16:17:02'),
(16, 'a241409c7e8e13d54683c38844e53beab32603df', 1, 'TestInstance', '0', 415352567, 20, '0.567965351077233,0.11762324109970423,0.001,0.09670680584462667,2.0', 1, 'COMPLETE', 'HIGH', '6b7a1528-b953-437e-899f-839bb4469e1d', 0, 1, -9, 0, 'SAT', -1, 0, 415352567, 5.895648985426999, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:23', '2014-09-27 16:17:03'),
(17, '2d7779556132d1ca982ce0ad17a37bd555b528b0', 1, 'TestInstance', '0', 1312486554, 20, '0.5292213770312744,0.2618033318915982,0.001,0.30558708943296525,2.0', 1, 'COMPLETE', 'HIGH', '6b7a1528-b953-437e-899f-839bb4469e1d', 0, 1, -9, 0, 'SAT', -1, 0, 1312486554, 6.278029629517041, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:23', '2014-09-27 16:17:03'),
(18, '3dce82f12f1387beb3367382a99c38b7c428a909', 1, 'TestInstance', '0', 2596691713, 20, '0.4669817377562724,0.5329548508889452,0.001,0.6045894031169383,1.0', 1, 'COMPLETE', 'HIGH', '6b7a1528-b953-437e-899f-839bb4469e1d', 0, 1, -9, 0, 'SAT', -1, 0, 2596691713, 6.526082552186061, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:23', '2014-09-27 16:17:03'),
(19, '6e3950deb76a862fc616a8a4ea45be5991b771b9', 1, 'TestInstance', '0', 2547179927, 20, '0.058700463548049586,0.04024463948299084,0.001,0.5930615421167897,2.0', 1, 'COMPLETE', 'HIGH', '6b7a1528-b953-437e-899f-839bb4469e1d', 0, 1, -9, 0, 'SAT', -1, 0, 2547179927, 3.1047888657963996, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:23', '2014-09-27 16:17:03'),
(20, '38481a838eb92643abf90e660928751270b873c5', 1, 'TestInstance', '0', 1066472738, 20, '0.4338585910923045,0.011360108906099842,0.001,0.24830753431927702,2.0', 1, 'COMPLETE', 'HIGH', '6b7a1528-b953-437e-899f-839bb4469e1d', 0, 1, -9, 0, 'SAT', -1, 0, 1066472738, 8.095840685407667, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:23', '2014-09-27 16:17:03'),
(21, '52db1cd82c4e51d6c71d54e05f7b2bf8c63ba5c3', 1, 'TestInstance', '0', 2343677710, 20, '0.1459584424267535,0.6844835933191237,0.001,0.5456799899587221,2.0', 1, 'COMPLETE', 'HIGH', '6b7a1528-b953-437e-899f-839bb4469e1d', 0, 1, -9, 0, 'SAT', -1, 0, 2343677710, 0.10073129311607087, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:23', '2014-09-27 16:17:03'),
(22, 'e3c6c51cc701c0dde5672f38623ea594cd866a44', 1, 'TestInstance', '0', 708839297, 20, '0.07294801687889496,0.26754402131484833,0.001,0.16503951007848616,1.0', 1, 'COMPLETE', 'HIGH', '6b7a1528-b953-437e-899f-839bb4469e1d', 0, 1, -9, 0, 'SAT', -1, 0, 708839297, 8.924426906547305, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:23', '2014-09-27 16:17:03'),
(23, '0449a2b71fce0da5217bff4328858bcdd6faed60', 1, 'TestInstance', '0', 785896763, 20, '0.7243393404301108,0.19816809156335735,0.001,0.18298084948142504,2.0', 1, 'COMPLETE', 'HIGH', '6b7a1528-b953-437e-899f-839bb4469e1d', 0, 1, -9, 0, 'SAT', -1, 0, 785896763, 4.976194347285579, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:23', '2014-09-27 16:17:03'),
(24, '748d65db93321f38e436dce616899eedcded7033', 1, 'TestInstance', '0', 462012509, 20, '0.5273229316682686,0.5124675255439665,0.001,0.10757067017080946,1.0', 1, 'COMPLETE', 'HIGH', '6b7a1528-b953-437e-899f-839bb4469e1d', 0, 1, -9, 0, 'SAT', -1, 0, 462012509, 0.4679634939602739, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:23', '2014-09-27 16:17:03'),
(25, '6e6340bd5406ff73e77739bf9b4d5725658919ff', 1, 'TestInstance', '0', 1278063938, 20, '0.5192159014072061,0.8784696775513869,0.001,0.2975724495953013,1.0', 1, 'COMPLETE', 'HIGH', '6b7a1528-b953-437e-899f-839bb4469e1d', 0, 1, -9, 0, 'SAT', -1, 0, 1278063938, 2.3089923272702952, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:23', '2014-09-27 16:17:03'),
(26, '5df94f3169e23b2f2e4848782283ce25264f8d53', 1, 'TestInstance', '0', 2753355577, 20, '0.9312990519066663,0.9562654910312703,0.001,0.6410655514008679,1.0', 1, 'COMPLETE', 'HIGH', '6b7a1528-b953-437e-899f-839bb4469e1d', 0, 1, -9, 0, 'SAT', -1, 0, 2753355577, 7.235439518330792, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:23', '2014-09-27 16:17:03'),
(27, '573a83b593d2c5a1c2c6b1beb4fca6d1f96a5305', 1, 'TestInstance', '0', 2393748079, 20, '0.3204647334580607,0.8710810297596115,0.001,0.5573379061982646,2.0', 1, 'COMPLETE', 'HIGH', '6b7a1528-b953-437e-899f-839bb4469e1d', 0, 1, -9, 0, 'SAT', -1, 0, 2393748079, 7.882780228971912, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:23', '2014-09-27 16:17:03'),
(28, 'c28c12105f453110ce137e0c6453e658011d40c0', 1, 'TestInstance', '0', 3722100990, 20, '0.20224025560558,0.4715702334939381,0.001,0.8666191693938758,2.0', 1, 'COMPLETE', 'HIGH', '6b7a1528-b953-437e-899f-839bb4469e1d', 0, 1, -9, 0, 'SAT', -1, 0, 3722100990, 4.908018328984914, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:23', '2014-09-27 16:17:03'),
(29, 'f86b5e4aa0ecb198e88d2405f246931a1948f4fe', 1, 'TestInstance', '0', 3429033451, 20, '0.9426860653901376,0.465624522663314,0.001,0.7983840656703375,2.0', 1, 'COMPLETE', 'HIGH', '6b7a1528-b953-437e-899f-839bb4469e1d', 0, 1, -9, 0, 'SAT', -1, 0, 3429033451, 3.294648589959345, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:23', '2014-09-27 16:17:03'),
(30, '06a2d072cce7bdca45e1da3411b66d4320e10d16', 1, 'TestInstance', '0', 3290606440, 20, '0.20351873565842282,0.37364237451978277,0.001,0.7661540156076304,2.0', 1, 'COMPLETE', 'HIGH', '6b7a1528-b953-437e-899f-839bb4469e1d', 0, 1, -9, 0, 'SAT', -1, 0, 3290606440, 9.496138398698994, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:23', '2014-09-27 16:17:03');

-- --------------------------------------------------------

--
-- Table structure for table `junit_AutoAdjustTest_version`
--

CREATE TABLE IF NOT EXISTS `junit_AutoAdjustTest_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` varchar(255) NOT NULL,
  `hash` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash` (`hash`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Dumping data for table `junit_AutoAdjustTest_version`
--

INSERT INTO `junit_AutoAdjustTest_version` (`id`, `version`, `hash`) VALUES
(1, 'v0.92.00dev-development-112 (8a5109587631)', '84ed1641e09245a2869dfe555e546f1bfc369c1c');

-- --------------------------------------------------------

--
-- Table structure for table `junit_AutoAdjustTest_workers`
--

CREATE TABLE IF NOT EXISTS `junit_AutoAdjustTest_workers` (
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

--
-- Dumping data for table `junit_AutoAdjustTest_workers`
--

INSERT INTO `junit_AutoAdjustTest_workers` (`workerUUID`, `hostname`, `username`, `jobID`, `status`, `version`, `crashInfo`, `startTime`, `startWeekYear`, `originalEndTime`, `currentlyIdle`, `endTime_UPDATEABLE`, `runsToBatch_UPDATEABLE`, `minRunsToBatch_UPDATEABLE`, `maxRunsToBatch_UPDATEABLE`, `autoAdjustBatchSize_UPDATEABLE`, `delayBetweenRequests_UPDATEABLE`, `pool_UPDATEABLE`, `poolIdleTimeLimit_UPDATEABLE`, `workerIdleTime_UPDATEABLE`, `concurrencyFactor_UPDATEABLE`, `upToDate`, `worstCaseNextUpdateWhenRunning`, `lastModified`) VALUES
('6b7a1528-b953-437e-899f-839bb4469e1d', 'hilbert', 'sjr', 'CLI/19726@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Triggered By Shutdown Hook (probably shutting down due to SIGTERM or SIGINT)', '2014-09-27 09:16:57', 392014, '2014-09-27 09:21:56', 0, '2014-09-27 09:21:56', 8, 1, 100, 1, 1, 'junit_AutoAdjustTest', 14400000, 0, 4, 1, '2014-09-27 09:17:03', '2014-09-27 16:17:03');

-- --------------------------------------------------------

--
-- Table structure for table `junit_client_kill_commandTable`
--

CREATE TABLE IF NOT EXISTS `junit_client_kill_commandTable` (
  `commandID` int(11) NOT NULL AUTO_INCREMENT,
  `commandString` text NOT NULL,
  PRIMARY KEY (`commandID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `junit_client_kill_commandTable`
--

INSERT INTO `junit_client_kill_commandTable` (`commandID`, `commandString`) VALUES
(1, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite');

-- --------------------------------------------------------

--
-- Table structure for table `junit_client_kill_execConfig`
--

CREATE TABLE IF NOT EXISTS `junit_client_kill_execConfig` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `junit_client_kill_execConfig`
--

INSERT INTO `junit_client_kill_execConfig` (`algorithmExecutionConfigID`, `algorithmExecutionConfigHashCode`, `algorithmExecutable`, `algorithmExecutableDirectory`, `parameterFile`, `executeOnCluster`, `deterministicAlgorithm`, `cutoffTime`, `algorithmExecutionConfigurationJSON`, `lastModified`) VALUES
(1, '9823afd624fe4ccba80e314dae4a919eace39e34', 'java -cp /home/sjr/git/MySQLDBTAE/bin:/home/sjr/git/AEATK/bin:/home/sjr/git/AEATK/lib/commons-collections-3.2.1.jar:/home/sjr/git/AEATK/lib/commons-math-2.2.jar:/home/sjr/git/AEATK/lib/opencsv-2.3.jar:/home/sjr/git/AEATK/lib/spi-0.2.4.jar:/home/sjr/git/AEATK/lib/jcip-annotations.jar:/home/sjr/git/AEATK/lib/Jama-1.0.2.jar:/home/sjr/git/AEATK/lib/numerics4j-1.3.jar:/home/sjr/git/AEATK/lib/guava-14.0.1.jar:/home/sjr/git/AEATK/lib/exp4j-0.3.10.jar:/home/sjr/git/AEATK/lib/commons-io-2.1.jar:/home/sjr/git/AEATK/lib/slf4j-api-1.7.5.jar:/home/sjr/git/AEATK/lib/jackson-annotations-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-core-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-databind-2.3.1.jar:/home/sjr/git/AEATK/lib/logback-access-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-classic-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-core-1.1.2.jar:/home/sjr/git/RandomForests/bin:/opt/eclipse/plugins/org.junit_4.11.0.v201303080030/junit.jar:/opt/eclipse/plugins/org.hamcrest.core_1.3.0.v201303031735.jar:/home/sjr/git/AEATK/lib/jcommander.jar:/home/sjr/git/AEATK/lib/commons-math3-3.3.jar:/home/sjr/git/MySQLDBTAE/lib/commons-codec-1.7.jar:/home/sjr/git/MySQLDBTAE/lib/mchange-commons-java-0.2.3.3.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-0.9.2-pre8.jar:/home/sjr/git/MySQLDBTAE/lib/mysql-connector-java-5.1.26-bin.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-oracle-thin-extras-0.9.2-pre8.jar:/opt/eclipse/configuration/org.eclipse.osgi/bundles/164/1/.cp/:/opt/eclipse/configuration/org.eclipse.osgi/bundles/163/1/.cp/ ca.ubc.cs.beta.targetalgorithmevaluator.TrueSleepyParamEchoExecutor', '/home/sjr/git/MySQLDBTAE', '/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt', 0, 0, 0.01, '{"@algo-exec-config-id":2,"algo-exec":"java -cp /home/sjr/git/MySQLDBTAE/bin:/home/sjr/git/AEATK/bin:/home/sjr/git/AEATK/lib/commons-collections-3.2.1.jar:/home/sjr/git/AEATK/lib/commons-math-2.2.jar:/home/sjr/git/AEATK/lib/opencsv-2.3.jar:/home/sjr/git/AEATK/lib/spi-0.2.4.jar:/home/sjr/git/AEATK/lib/jcip-annotations.jar:/home/sjr/git/AEATK/lib/Jama-1.0.2.jar:/home/sjr/git/AEATK/lib/numerics4j-1.3.jar:/home/sjr/git/AEATK/lib/guava-14.0.1.jar:/home/sjr/git/AEATK/lib/exp4j-0.3.10.jar:/home/sjr/git/AEATK/lib/commons-io-2.1.jar:/home/sjr/git/AEATK/lib/slf4j-api-1.7.5.jar:/home/sjr/git/AEATK/lib/jackson-annotations-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-core-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-databind-2.3.1.jar:/home/sjr/git/AEATK/lib/logback-access-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-classic-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-core-1.1.2.jar:/home/sjr/git/RandomForests/bin:/opt/eclipse/plugins/org.junit_4.11.0.v201303080030/junit.jar:/opt/eclipse/plugins/org.hamcrest.core_1.3.0.v201303031735.jar:/home/sjr/git/AEATK/lib/jcommander.jar:/home/sjr/git/AEATK/lib/commons-math3-3.3.jar:/home/sjr/git/MySQLDBTAE/lib/commons-codec-1.7.jar:/home/sjr/git/MySQLDBTAE/lib/mchange-commons-java-0.2.3.3.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-0.9.2-pre8.jar:/home/sjr/git/MySQLDBTAE/lib/mysql-connector-java-5.1.26-bin.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-oracle-thin-extras-0.9.2-pre8.jar:/opt/eclipse/configuration/org.eclipse.osgi/bundles/164/1/.cp/:/opt/eclipse/configuration/org.eclipse.osgi/bundles/163/1/.cp/ ca.ubc.cs.beta.targetalgorithmevaluator.TrueSleepyParamEchoExecutor","algo-exec-dir":"/home/sjr/git/MySQLDBTAE","algo-pcs":{"@pcs-id":2,"pcs-filename":"/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt","pcs-text":"#Used for getting specific results out from a target wrapper\\n#Some invalid values are specified so that we can test code\\n\\nsolved { SAT, UNSAT, TIMEOUT, CRASHED, ABORT, INVALID } [SAT]\\nruntime [0,1000] [0]\\nrunlength [0,1000000][0]\\nquality [0, 1000000] [0]\\nseed [ -1,4294967295][1]i\\n","pcs-subspace":{}},"algo-cutoff":0.01,"algo-deterministic":false,"algo-tae-context":{}}', '2014-09-27 16:16:04');

-- --------------------------------------------------------

--
-- Table structure for table `junit_client_kill_runConfigs`
--

CREATE TABLE IF NOT EXISTS `junit_client_kill_runConfigs` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=27 ;

--
-- Dumping data for table `junit_client_kill_runConfigs`
--

INSERT INTO `junit_client_kill_runConfigs` (`runConfigID`, `runConfigUUID`, `execConfigID`, `problemInstance`, `instanceSpecificInformation`, `seed`, `cutoffTime`, `paramConfiguration`, `cutoffLessThanMax`, `status`, `priority`, `workerUUID`, `killJob`, `retryAttempts`, `runPartition`, `noop`, `runResult`, `runLength`, `quality`, `resultSeed`, `runtime`, `walltime`, `additionalRunData`, `worstCaseEndtime`, `worstCaseNextUpdateWhenAssigned`, `lastModified`) VALUES
(1, '998c67237b02b72ff8327831f22452f440d8bb10', 1, 'TestInstance', '0', 2646423628, 3000, '0.40112887280010234,0.07318812986473444,0.1,0.6161685168938319,2.0', 0, 'COMPLETE', 'HIGH', '07c4b710-9219-41c2-8aca-c9ec2eacf05d', 1, 1, 0, 0, 'KILLED', 0, 0, 2646423628, 0, 0, 'Killed By Client (19063@hilbert ) While Status ASSIGNED', '1900-01-01 00:00:00', '2014-09-27 09:18:10', '2014-09-27 16:16:10'),
(2, 'cfc5c14c7ed366c6fc603d3e40618c57d436bdd3', 1, 'TestInstance', '0', 2761996473, 3000, '0.6445632644529786,0.2175974246553437,0.1,0.6430774167778256,2.0', 0, 'COMPLETE', 'HIGH', '07c4b710-9219-41c2-8aca-c9ec2eacf05d', 1, 1, 0, 0, 'KILLED', 0, 0, 2761996473, 0, 1.539, 'Killed Manually', '1900-01-01 00:00:00', '2014-09-27 09:18:10', '2014-09-27 16:16:16'),
(3, 'cf6a14f175da1135d41f96e62279278471f1f8cb', 1, 'TestInstance', '0', 686528301, 3000, '0.8566785534628831,0.8312683654999733,0.1,0.15984482652045673,2.0', 0, 'COMPLETE', 'HIGH', '07c4b710-9219-41c2-8aca-c9ec2eacf05d', 1, 1, 0, 0, 'KILLED', 0, 0, 686528301, 0, 1.537, 'Killed Manually', '1900-01-01 00:00:00', '2014-09-27 09:18:10', '2014-09-27 16:16:14'),
(4, 'f3ab5485e42f29d7b1377c4a909a230236a17d0a', 1, 'TestInstance', '0', 1041929584, 3000, '0.9575189581354451,0.5107451475257508,0.1,0.24259313597749146,2.0', 0, 'COMPLETE', 'HIGH', '07c4b710-9219-41c2-8aca-c9ec2eacf05d', 1, 1, 0, 0, 'KILLED', 0, 0, 1041929584, 0, 1.538, 'Killed Manually', '1900-01-01 00:00:00', '2014-09-27 09:18:10', '2014-09-27 16:16:12'),
(5, 'b8e96b739c199034a97c79dacb73c8789592ba26', 1, 'TestInstance', '0', 815655912, 3000, '0.6614960836540541,0.5108626594674456,0.1,0.1899096912960732,1.0', 0, 'COMPLETE', 'HIGH', '07c4b710-9219-41c2-8aca-c9ec2eacf05d', 1, 1, 0, 0, 'KILLED', 0, 0, 815655912, 0, 5.562, 'Killed Manually', '2014-09-27 10:33:05', '2014-09-27 09:18:10', '2014-09-27 16:16:11'),
(6, '1c04da19624d1a5b7a48467230928995d6f48aeb', 1, 'TestInstance', '0', 377557141, 3000, '0.677867776174524,0.10971340329193291,0.1,0.08790687248392336,2.0', 0, 'COMPLETE', 'HIGH', '0', 1, 0, 0, 0, 'KILLED', 0, 0, 377557141, 0, 0, 'Killed By Client (19063@hilbert ) While Status NEW', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 16:16:10'),
(7, 'd32c7053bb3178246c144d2af0c6a775aa46896d', 1, 'TestInstance', '0', 2928910721, 3000, '0.3850096155273147,0.5867248777707239,0.1,0.6819401685656188,1.0', 0, 'COMPLETE', 'HIGH', '0', 1, 0, 0, 0, 'KILLED', 0, 0, 2928910721, 0, 0, 'Killed By Client (19063@hilbert ) While Status NEW', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 16:16:10'),
(8, '988dadee789911988ff63e4a5b05d80d1ff57d47', 1, 'TestInstance', '0', 2968904417, 3000, '0.7066752410759909,0.061504907396557784,0.1,0.6912519265452279,1.0', 0, 'COMPLETE', 'HIGH', '0', 1, 0, 0, 0, 'KILLED', 0, 0, 2968904417, 0, 0, 'Killed By Client (19063@hilbert ) While Status NEW', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 16:16:10'),
(9, 'd61216be5cd9fc191e8368a126811a94b930e7f0', 1, 'TestInstance', '0', 2988230134, 3000, '0.010942202152102398,0.18531654673180972,0.1,0.6957515456723627,2.0', 0, 'COMPLETE', 'HIGH', '0', 1, 0, 0, 0, 'KILLED', 0, 0, 2988230134, 0, 0, 'Killed By Client (19063@hilbert ) While Status NEW', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 16:16:10'),
(10, 'ecc7fede1c3faf8b8c73b88f386c73c1b57864d5', 1, 'TestInstance', '0', 1970325444, 3000, '0.4294612751762027,0.15091973942157066,0.1,0.45875214157655086,2.0', 0, 'COMPLETE', 'HIGH', '0', 1, 0, 0, 0, 'KILLED', 0, 0, 1970325444, 0, 0, 'Killed By Client (19063@hilbert ) While Status NEW', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 16:16:10'),
(11, '66f0a461c6fd916ba08fecc5fd3aa3fdc9cd3ba9', 1, 'TestInstance', '0', 1956513640, 3000, '0.41542335848715584,0.9060359470792138,0.1,0.4555363303619585,2.0', 0, 'COMPLETE', 'HIGH', '0', 1, 0, 0, 0, 'KILLED', 0, 0, 1956513640, 0, 0, 'Killed By Client (19063@hilbert ) While Status NEW', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 16:16:10'),
(12, '5ee7e25759d6c3e6d504271a0c48bbe493bcd129', 1, 'TestInstance', '0', 3089177649, 3000, '0.5496843559068467,0.6919897859179481,0.1,0.719255220559599,1.0', 0, 'COMPLETE', 'HIGH', '0', 1, 0, 0, 0, 'KILLED', 0, 0, 3089177649, 0, 0, 'Killed By Client (19063@hilbert ) While Status NEW', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 16:16:10'),
(13, 'a1a1b8190355dc9c0b070140ca212a4c90949c2c', 1, 'TestInstance', '0', 260051645, 3000, '0.6237240093544325,0.31635050612039606,0.1,0.06054799222374615,1.0', 0, 'COMPLETE', 'HIGH', '0', 1, 0, 0, 0, 'KILLED', 0, 0, 260051645, 0, 0, 'Killed By Client (19063@hilbert ) While Status NEW', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 16:16:10'),
(14, 'd77966148fdb83172f3faa59c7a4c40c0b744fef', 1, 'TestInstance', '0', 897308132, 3000, '0.5989868645368132,0.3019600445623585,0.1,0.2089208302300142,1.0', 0, 'COMPLETE', 'HIGH', '0', 1, 0, 0, 0, 'KILLED', 0, 0, 897308132, 0, 0, 'Killed By Client (19063@hilbert ) While Status NEW', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 16:16:10'),
(15, '8b825d7c190bbbb307d646e989794d4865e45b79', 1, 'TestInstance', '0', 2966750453, 3000, '0.3450746905651265,0.09501830205868877,0.1,0.6907504177208174,2.0', 0, 'COMPLETE', 'HIGH', '0', 1, 0, 0, 0, 'KILLED', 0, 0, 2966750453, 0, 0, 'Killed By Client (19063@hilbert ) While Status NEW', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 16:16:10'),
(16, '00e1274eef0bfa672d6207788e89278fc3ca9db6', 1, 'TestInstance', '0', 3363637734, 3000, '0.08233566811958748,0.4623327419250455,0.1,0.7831579387925663,2.0', 0, 'COMPLETE', 'HIGH', '0', 1, 0, 0, 0, 'KILLED', 0, 0, 3363637734, 0, 0, 'Killed By Client (19063@hilbert ) While Status NEW', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 16:16:10'),
(17, '2769f03a1e503d04feeb5c7a658ef8f72635e7d8', 1, 'TestInstance', '0', 2959227096, 3000, '0.45572328486971614,0.5614940942296517,0.1,0.6889987496684774,1.0', 0, 'COMPLETE', 'HIGH', '0', 1, 0, 0, 0, 'KILLED', 0, 0, 2959227096, 0, 0, 'Killed By Client (19063@hilbert ) While Status NEW', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 16:16:10'),
(18, '36c1215a51781f7151afefb66b74a14b76a5502b', 1, 'TestInstance', '0', 3185658955, 3000, '0.6050627445999469,0.4832316035548747,0.1,0.7417190251309147,2.0', 0, 'COMPLETE', 'HIGH', '0', 1, 0, 0, 0, 'KILLED', 0, 0, 3185658955, 0, 0, 'Killed By Client (19063@hilbert ) While Status NEW', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 16:16:10'),
(19, '54ce7bd515a255439c8aed3e0f1ce3f4b029a3bf', 1, 'TestInstance', '0', 822131641, 3000, '0.5319484092424969,0.7572762097846437,0.1,0.1914174394469202,1.0', 0, 'COMPLETE', 'HIGH', '0', 1, 0, 0, 0, 'KILLED', 0, 0, 822131641, 0, 0, 'Killed By Client (19063@hilbert ) While Status NEW', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 16:16:10'),
(20, '3257f64dcf2f3f6f2cfb9fdf186debc0febfabb2', 1, 'TestInstance', '0', 2406709941, 3000, '0.43292147578345697,0.0437154521816544,0.1,0.5603558248699746,2.0', 0, 'COMPLETE', 'HIGH', '0', 1, 0, 0, 0, 'KILLED', 0, 0, 2406709941, 0, 0, 'Killed By Client (19063@hilbert ) While Status NEW', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 16:16:10'),
(21, 'ba95f58406a3389f6a607dc2d47c021f932369a7', 1, 'TestInstance', '0', 879691925, 3000, '0.5668910094512996,0.035300626002148894,0.1,0.2048192374164194,2.0', 0, 'COMPLETE', 'HIGH', '0', 1, 0, 0, 0, 'KILLED', 0, 0, 879691925, 0, 0, 'Killed By Client (19063@hilbert ) While Status NEW', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 16:16:10'),
(22, '29b3285a24ef153ee6622f1c09a5af835f3f8e00', 1, 'TestInstance', '0', 420548912, 3000, '0.48177796953807017,0.08325095010266548,0.1,0.09791667419534254,1.0', 0, 'COMPLETE', 'HIGH', '0', 1, 0, 0, 0, 'KILLED', 0, 0, 420548912, 0, 0, 'Killed By Client (19063@hilbert ) While Status NEW', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 16:16:10'),
(23, 'f49b0dd028000ef4a8e6a3ff3d556fe70f5b0095', 1, 'TestInstance', '0', 2572098777, 3000, '0.1668118329742101,0.9686916980185273,0.1,0.598863414000053,2.0', 0, 'COMPLETE', 'HIGH', '0', 1, 0, 0, 0, 'KILLED', 0, 0, 2572098777, 0, 0, 'Killed By Client (19063@hilbert ) While Status NEW', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 16:16:10'),
(24, '1888707bb3d2193e30c19068347d94c2a3feb32d', 1, 'TestInstance', '0', 3101991844, 3000, '0.04550671144688134,0.22260315021921506,0.1,0.7222387578286606,1.0', 0, 'COMPLETE', 'HIGH', '0', 1, 0, 0, 0, 'KILLED', 0, 0, 3101991844, 0, 0, 'Killed By Client (19063@hilbert ) While Status NEW', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 16:16:10'),
(25, 'dea0abeea3c4c394d71f3219682684f0469d66fc', 1, 'TestInstance', '0', 2923694652, 3000, '0.28446304978356907,0.2582982852998692,0.1,0.6807257078632886,1.0', 0, 'COMPLETE', 'HIGH', '0', 1, 0, 0, 0, 'KILLED', 0, 0, 2923694652, 0, 0, 'Killed By Client (19063@hilbert ) While Status NEW', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 16:16:10'),
(26, 'c14faf66c7cb4ee6b4283136582265b5d8828636', 1, 'TestInstance', '0', 2964820444, 3000, '0.1317457545604227,0.39415372353965905,0.1,0.6903010524831943,1.0', 0, 'COMPLETE', 'HIGH', '0', 1, 0, 0, 0, 'KILLED', 0, 0, 2964820444, 0, 0, 'Killed By Client (19063@hilbert ) While Status NEW', '1900-01-01 00:00:00', '1900-01-01 00:00:00', '2014-09-27 16:16:14');

-- --------------------------------------------------------

--
-- Table structure for table `junit_client_kill_version`
--

CREATE TABLE IF NOT EXISTS `junit_client_kill_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` varchar(255) NOT NULL,
  `hash` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash` (`hash`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Dumping data for table `junit_client_kill_version`
--

INSERT INTO `junit_client_kill_version` (`id`, `version`, `hash`) VALUES
(1, 'v0.92.00dev-development-112 (8a5109587631)', '84ed1641e09245a2869dfe555e546f1bfc369c1c');

-- --------------------------------------------------------

--
-- Table structure for table `junit_client_kill_workers`
--

CREATE TABLE IF NOT EXISTS `junit_client_kill_workers` (
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

--
-- Dumping data for table `junit_client_kill_workers`
--

INSERT INTO `junit_client_kill_workers` (`workerUUID`, `hostname`, `username`, `jobID`, `status`, `version`, `crashInfo`, `startTime`, `startWeekYear`, `originalEndTime`, `currentlyIdle`, `endTime_UPDATEABLE`, `runsToBatch_UPDATEABLE`, `minRunsToBatch_UPDATEABLE`, `maxRunsToBatch_UPDATEABLE`, `autoAdjustBatchSize_UPDATEABLE`, `delayBetweenRequests_UPDATEABLE`, `pool_UPDATEABLE`, `poolIdleTimeLimit_UPDATEABLE`, `workerIdleTime_UPDATEABLE`, `concurrencyFactor_UPDATEABLE`, `upToDate`, `worstCaseNextUpdateWhenRunning`, `lastModified`) VALUES
('07c4b710-9219-41c2-8aca-c9ec2eacf05d', 'hilbert', 'sjr', 'CLI/19255@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Triggered By Shutdown Hook (probably shutting down due to SIGTERM or SIGINT)', '2014-09-27 09:16:04', 392014, '2014-09-28 09:15:03', 0, '2014-09-28 09:15:03', 5, 1, 100, 1, 1, 'junit_client_kill', 14400000, 0, 4, 1, '2014-09-27 09:16:16', '2014-09-27 16:16:16');

-- --------------------------------------------------------

--
-- Table structure for table `junit_concurrencyFactor_commandTable`
--

CREATE TABLE IF NOT EXISTS `junit_concurrencyFactor_commandTable` (
  `commandID` int(11) NOT NULL AUTO_INCREMENT,
  `commandString` text NOT NULL,
  PRIMARY KEY (`commandID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `junit_concurrencyFactor_commandTable`
--

INSERT INTO `junit_concurrencyFactor_commandTable` (`commandID`, `commandString`) VALUES
(1, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite');

-- --------------------------------------------------------

--
-- Table structure for table `junit_concurrencyFactor_execConfig`
--

CREATE TABLE IF NOT EXISTS `junit_concurrencyFactor_execConfig` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `junit_concurrencyFactor_execConfig`
--

INSERT INTO `junit_concurrencyFactor_execConfig` (`algorithmExecutionConfigID`, `algorithmExecutionConfigHashCode`, `algorithmExecutable`, `algorithmExecutableDirectory`, `parameterFile`, `executeOnCluster`, `deterministicAlgorithm`, `cutoffTime`, `algorithmExecutionConfigurationJSON`, `lastModified`) VALUES
(1, 'ab7f44ad86b35e0d29489a964abfed9cff31fceb', 'ignore', '/home/sjr/git/MySQLDBTAE', '/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFileWithKilled.txt', 0, 0, 500, '{"@algo-exec-config-id":2,"algo-exec":"ignore","algo-exec-dir":"/home/sjr/git/MySQLDBTAE","algo-pcs":{"@pcs-id":2,"pcs-filename":"/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFileWithKilled.txt","pcs-text":"#Used for getting specific results out from a target wrapper\\n#Some invalid values are specified so that we can test code\\n\\nsolved { SAT, UNSAT, TIMEOUT, CRASHED, ABORT, INVALID, KILLED } [SAT]\\nruntime [0,1000] [0]\\nrunlength [0,1000000][0]\\nquality [0, 1000000] [0]\\nseed [ -1,4294967295][1]i\\n","pcs-subspace":{}},"algo-cutoff":500.0,"algo-deterministic":false,"algo-tae-context":{}}', '2014-09-27 16:17:13');

-- --------------------------------------------------------

--
-- Table structure for table `junit_concurrencyFactor_runConfigs`
--

CREATE TABLE IF NOT EXISTS `junit_concurrencyFactor_runConfigs` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=11 ;

--
-- Dumping data for table `junit_concurrencyFactor_runConfigs`
--

INSERT INTO `junit_concurrencyFactor_runConfigs` (`runConfigID`, `runConfigUUID`, `execConfigID`, `problemInstance`, `instanceSpecificInformation`, `seed`, `cutoffTime`, `paramConfiguration`, `cutoffLessThanMax`, `status`, `priority`, `workerUUID`, `killJob`, `retryAttempts`, `runPartition`, `noop`, `runResult`, `runLength`, `quality`, `resultSeed`, `runtime`, `walltime`, `additionalRunData`, `worstCaseEndtime`, `worstCaseNextUpdateWhenAssigned`, `lastModified`) VALUES
(1, 'bd74ada2986d679ea352800cb8d69d5fe53b2200', 1, 'TestInstance', '0', 1043050213, 1001, '0.5501807590473061,0.7724362654549447,0.019673472845846085,0.24285405274879793,1.0', 0, 'COMPLETE', 'HIGH', '704c7d32-efc1-433e-afb1-b5d64136eee7', 0, 1, -10, 0, 'SAT', 772436.2654549447, 550180.7590473061, 1043050213, 19.673472845846085, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:33', '2014-09-27 16:17:13'),
(2, 'f3d3ace3689145c2c6df9d242a97222c9cbd1b31', 1, 'TestInstance', '0', 501787228, 1001, '0.41133865491995025,0.9973319236453433,0.8802569120488059,0.11683144359457506,2.0', 0, 'COMPLETE', 'HIGH', '704c7d32-efc1-433e-afb1-b5d64136eee7', 0, 1, -10, 0, 'UNSAT', 997331.9236453433, 411338.65491995023, 501787228, 880.2569120488058, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:33', '2014-09-27 16:17:13'),
(3, '22f4eff702325df2f18f5c11940e43efcfdce0c2', 1, 'TestInstance', '0', 2490855477, 1001, '0.6227271673872781,0.4996804626987761,0.549094241161021,0.5799474841728929,2.0', 0, 'COMPLETE', 'HIGH', '704c7d32-efc1-433e-afb1-b5d64136eee7', 0, 1, -10, 0, 'UNSAT', 499680.4626987761, 622727.167387278, 2490855477, 549.094241161021, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:33', '2014-09-27 16:17:13'),
(4, '9ec6fbbfc4f0e6892101df7443dff04173239510', 1, 'TestInstance', '0', 2970263691, 1001, '0.9254353405412014,0.4642262766535894,0.458192461148501,0.6915684071854762,2.0', 0, 'COMPLETE', 'HIGH', '704c7d32-efc1-433e-afb1-b5d64136eee7', 0, 1, -10, 0, 'UNSAT', 464226.2766535894, 925435.3405412014, 2970263691, 458.19246114850097, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:33', '2014-09-27 16:17:13'),
(5, 'f8677e7e7b3dc7434cf922367ddf6fa08ce008c8', 1, 'TestInstance', '0', 614584320, 1001, '0.973100090358658,0.2693543286922804,0.7676620602220288,0.14309406312110506,3.0', 0, 'COMPLETE', 'HIGH', '704c7d32-efc1-433e-afb1-b5d64136eee7', 0, 1, -10, 0, 'TIMEOUT', 269354.3286922804, 973100.090358658, 614584320, 767.6620602220288, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:33', '2014-09-27 16:17:13'),
(6, 'b59b3479f6cec9f5579cf1e75aced990b65907dc', 1, 'TestInstance', '0', 1417711439, 1001, '0.6365750735936028,0.14459868122203123,0.3427449305273705,0.3300866671302154,3.0', 0, 'COMPLETE', 'HIGH', '704c7d32-efc1-433e-afb1-b5d64136eee7', 0, 1, -10, 0, 'TIMEOUT', 144598.68122203124, 636575.0735936027, 1417711439, 342.74493052737046, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:36', '2014-09-27 16:17:16'),
(7, '6f914c8c4472beac367cb0bda6db4ceed5c166a4', 1, 'TestInstance', '0', 2823877106, 1001, '0.7912202955252928,0.0804539512418454,0.16374147131199734,0.6574851243855699,7.0', 0, 'COMPLETE', 'HIGH', '704c7d32-efc1-433e-afb1-b5d64136eee7', 0, 1, -10, 0, 'KILLED', 80453.9512418454, 791220.2955252929, 2823877106, 163.74147131199734, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:37', '2014-09-27 16:17:17'),
(8, '8f30d1496cf4f6efd5f436043cd3916f01201f31', 1, 'TestInstance', '0', 1096187408, 1001, '0.797107145331384,0.8646185042245019,0.40547420977508786,0.2552260200597285,1.0', 0, 'COMPLETE', 'HIGH', '704c7d32-efc1-433e-afb1-b5d64136eee7', 0, 1, -10, 0, 'SAT', 864618.5042245019, 797107.145331384, 1096187408, 405.47420977508784, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:37', '2014-09-27 16:17:17'),
(9, 'fe66235aff14bd854f49450981b59960eaa182d9', 1, 'TestInstance', '0', 4266595332, 1001, '0.08317514613219978,0.6926880844966846,0.928487175584808,0.9933941374781089,7.0', 0, 'COMPLETE', 'HIGH', '704c7d32-efc1-433e-afb1-b5d64136eee7', 0, 1, -10, 0, 'KILLED', 692688.0844966846, 83175.14613219978, 4266595332, 928.487175584808, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:38', '2014-09-27 16:17:18'),
(10, '50c709ba4b3cbbcba213b2d2306aee72f8998d1d', 1, 'TestInstance', '0', 3162750496, 1001, '0.6185726002814785,0.2963459671082459,0.6477538506460987,0.7363852338780683,2.0', 0, 'COMPLETE', 'HIGH', '704c7d32-efc1-433e-afb1-b5d64136eee7', 0, 1, -10, 0, 'UNSAT', 296345.9671082459, 618572.6002814785, 3162750496, 647.7538506460987, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:40:38', '2014-09-27 16:17:18');

-- --------------------------------------------------------

--
-- Table structure for table `junit_concurrencyFactor_version`
--

CREATE TABLE IF NOT EXISTS `junit_concurrencyFactor_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` varchar(255) NOT NULL,
  `hash` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash` (`hash`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Dumping data for table `junit_concurrencyFactor_version`
--

INSERT INTO `junit_concurrencyFactor_version` (`id`, `version`, `hash`) VALUES
(1, 'v0.92.00dev-development-112 (8a5109587631)', '84ed1641e09245a2869dfe555e546f1bfc369c1c');

-- --------------------------------------------------------

--
-- Table structure for table `junit_concurrencyFactor_workers`
--

CREATE TABLE IF NOT EXISTS `junit_concurrencyFactor_workers` (
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

--
-- Dumping data for table `junit_concurrencyFactor_workers`
--

INSERT INTO `junit_concurrencyFactor_workers` (`workerUUID`, `hostname`, `username`, `jobID`, `status`, `version`, `crashInfo`, `startTime`, `startWeekYear`, `originalEndTime`, `currentlyIdle`, `endTime_UPDATEABLE`, `runsToBatch_UPDATEABLE`, `minRunsToBatch_UPDATEABLE`, `maxRunsToBatch_UPDATEABLE`, `autoAdjustBatchSize_UPDATEABLE`, `delayBetweenRequests_UPDATEABLE`, `pool_UPDATEABLE`, `poolIdleTimeLimit_UPDATEABLE`, `workerIdleTime_UPDATEABLE`, `concurrencyFactor_UPDATEABLE`, `upToDate`, `worstCaseNextUpdateWhenRunning`, `lastModified`) VALUES
('704c7d32-efc1-433e-afb1-b5d64136eee7', 'hilbert', 'sjr', 'CLI/20019@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Triggered By Shutdown Hook (probably shutting down due to SIGTERM or SIGINT)', '2014-09-27 09:17:12', 392014, '2014-09-28 09:16:12', 1, '2014-09-28 09:16:12', 2, 1, 100, 1, 1, 'junit_concurrencyFactor', 14400000, 1, 1, 1, '2014-09-27 09:17:19', '2014-09-27 16:17:19');

-- --------------------------------------------------------

--
-- Table structure for table `junit_CutoffIdleTest_isolated_commandTable`
--

CREATE TABLE IF NOT EXISTS `junit_CutoffIdleTest_isolated_commandTable` (
  `commandID` int(11) NOT NULL AUTO_INCREMENT,
  `commandString` text NOT NULL,
  PRIMARY KEY (`commandID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `junit_CutoffIdleTest_isolated_commandTable`
--

INSERT INTO `junit_CutoffIdleTest_isolated_commandTable` (`commandID`, `commandString`) VALUES
(1, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite');

-- --------------------------------------------------------

--
-- Table structure for table `junit_CutoffIdleTest_isolated_execConfig`
--

CREATE TABLE IF NOT EXISTS `junit_CutoffIdleTest_isolated_execConfig` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `junit_CutoffIdleTest_isolated_execConfig`
--

INSERT INTO `junit_CutoffIdleTest_isolated_execConfig` (`algorithmExecutionConfigID`, `algorithmExecutionConfigHashCode`, `algorithmExecutable`, `algorithmExecutableDirectory`, `parameterFile`, `executeOnCluster`, `deterministicAlgorithm`, `cutoffTime`, `algorithmExecutionConfigurationJSON`, `lastModified`) VALUES
(1, '2625865e6eafb0a4fa16ff33c3ac6d6469acb9eb', 'java -cp /home/sjr/git/MySQLDBTAE/bin:/home/sjr/git/AEATK/bin:/home/sjr/git/AEATK/lib/commons-collections-3.2.1.jar:/home/sjr/git/AEATK/lib/commons-math-2.2.jar:/home/sjr/git/AEATK/lib/opencsv-2.3.jar:/home/sjr/git/AEATK/lib/spi-0.2.4.jar:/home/sjr/git/AEATK/lib/jcip-annotations.jar:/home/sjr/git/AEATK/lib/Jama-1.0.2.jar:/home/sjr/git/AEATK/lib/numerics4j-1.3.jar:/home/sjr/git/AEATK/lib/guava-14.0.1.jar:/home/sjr/git/AEATK/lib/exp4j-0.3.10.jar:/home/sjr/git/AEATK/lib/commons-io-2.1.jar:/home/sjr/git/AEATK/lib/slf4j-api-1.7.5.jar:/home/sjr/git/AEATK/lib/jackson-annotations-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-core-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-databind-2.3.1.jar:/home/sjr/git/AEATK/lib/logback-access-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-classic-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-core-1.1.2.jar:/home/sjr/git/RandomForests/bin:/opt/eclipse/plugins/org.junit_4.11.0.v201303080030/junit.jar:/opt/eclipse/plugins/org.hamcrest.core_1.3.0.v201303031735.jar:/home/sjr/git/AEATK/lib/jcommander.jar:/home/sjr/git/AEATK/lib/commons-math3-3.3.jar:/home/sjr/git/MySQLDBTAE/lib/commons-codec-1.7.jar:/home/sjr/git/MySQLDBTAE/lib/mchange-commons-java-0.2.3.3.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-0.9.2-pre8.jar:/home/sjr/git/MySQLDBTAE/lib/mysql-connector-java-5.1.26-bin.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-oracle-thin-extras-0.9.2-pre8.jar:/opt/eclipse/configuration/org.eclipse.osgi/bundles/164/1/.cp/:/opt/eclipse/configuration/org.eclipse.osgi/bundles/163/1/.cp/ ca.ubc.cs.beta.targetalgorithmevaluator.TrueSleepyParamEchoExecutor', '/home/sjr/git/MySQLDBTAE', '/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt', 0, 0, 500, '{"@algo-exec-config-id":2,"algo-exec":"java -cp /home/sjr/git/MySQLDBTAE/bin:/home/sjr/git/AEATK/bin:/home/sjr/git/AEATK/lib/commons-collections-3.2.1.jar:/home/sjr/git/AEATK/lib/commons-math-2.2.jar:/home/sjr/git/AEATK/lib/opencsv-2.3.jar:/home/sjr/git/AEATK/lib/spi-0.2.4.jar:/home/sjr/git/AEATK/lib/jcip-annotations.jar:/home/sjr/git/AEATK/lib/Jama-1.0.2.jar:/home/sjr/git/AEATK/lib/numerics4j-1.3.jar:/home/sjr/git/AEATK/lib/guava-14.0.1.jar:/home/sjr/git/AEATK/lib/exp4j-0.3.10.jar:/home/sjr/git/AEATK/lib/commons-io-2.1.jar:/home/sjr/git/AEATK/lib/slf4j-api-1.7.5.jar:/home/sjr/git/AEATK/lib/jackson-annotations-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-core-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-databind-2.3.1.jar:/home/sjr/git/AEATK/lib/logback-access-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-classic-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-core-1.1.2.jar:/home/sjr/git/RandomForests/bin:/opt/eclipse/plugins/org.junit_4.11.0.v201303080030/junit.jar:/opt/eclipse/plugins/org.hamcrest.core_1.3.0.v201303031735.jar:/home/sjr/git/AEATK/lib/jcommander.jar:/home/sjr/git/AEATK/lib/commons-math3-3.3.jar:/home/sjr/git/MySQLDBTAE/lib/commons-codec-1.7.jar:/home/sjr/git/MySQLDBTAE/lib/mchange-commons-java-0.2.3.3.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-0.9.2-pre8.jar:/home/sjr/git/MySQLDBTAE/lib/mysql-connector-java-5.1.26-bin.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-oracle-thin-extras-0.9.2-pre8.jar:/opt/eclipse/configuration/org.eclipse.osgi/bundles/164/1/.cp/:/opt/eclipse/configuration/org.eclipse.osgi/bundles/163/1/.cp/ ca.ubc.cs.beta.targetalgorithmevaluator.TrueSleepyParamEchoExecutor","algo-exec-dir":"/home/sjr/git/MySQLDBTAE","algo-pcs":{"@pcs-id":2,"pcs-filename":"/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt","pcs-text":"#Used for getting specific results out from a target wrapper\\n#Some invalid values are specified so that we can test code\\n\\nsolved { SAT, UNSAT, TIMEOUT, CRASHED, ABORT, INVALID } [SAT]\\nruntime [0,1000] [0]\\nrunlength [0,1000000][0]\\nquality [0, 1000000] [0]\\nseed [ -1,4294967295][1]i\\n","pcs-subspace":{}},"algo-cutoff":500.0,"algo-deterministic":false,"algo-tae-context":{}}', '2014-09-27 16:17:19');

-- --------------------------------------------------------

--
-- Table structure for table `junit_CutoffIdleTest_isolated_runConfigs`
--

CREATE TABLE IF NOT EXISTS `junit_CutoffIdleTest_isolated_runConfigs` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=6 ;

--
-- Dumping data for table `junit_CutoffIdleTest_isolated_runConfigs`
--

INSERT INTO `junit_CutoffIdleTest_isolated_runConfigs` (`runConfigID`, `runConfigUUID`, `execConfigID`, `problemInstance`, `instanceSpecificInformation`, `seed`, `cutoffTime`, `paramConfiguration`, `cutoffLessThanMax`, `status`, `priority`, `workerUUID`, `killJob`, `retryAttempts`, `runPartition`, `noop`, `runResult`, `runLength`, `quality`, `resultSeed`, `runtime`, `walltime`, `additionalRunData`, `worstCaseEndtime`, `worstCaseNextUpdateWhenAssigned`, `lastModified`) VALUES
(1, '01cca77eda3cd349f9d25f26d870935a02795afb', 1, 'TestInstance', '0', 3748970363, 600, '0.5523817565668581,0.5697627434884664,0.005,0.8728751828025851,1.0', 0, 'NEW', 'LOW', '449e809e-99d9-4ab1-a369-82c5908d0379', 0, 6, -9, 0, 'ABORT', 0, 0, 1, 0, 0, '', '1900-01-01 00:00:00', '2014-09-27 09:19:25', '2014-09-27 16:17:25'),
(2, '402d8bb00aa23863ef38c0b29f41e0b2c70faa4d', 1, 'TestInstance', '0', 547334004, 600, '0.8006468527889211,0.47449770926199875,0.005,0.12743612876454458,1.0', 0, 'NEW', 'LOW', '449e809e-99d9-4ab1-a369-82c5908d0379', 0, 6, -9, 0, 'ABORT', 0, 0, 1, 0, 0, '', '1900-01-01 00:00:00', '2014-09-27 09:19:25', '2014-09-27 16:17:25'),
(3, 'c56c7bad6d0ffeb54b9ec5dd6f88a9c149880054', 1, 'TestInstance', '0', 597184628, 600, '0.3428000556422013,0.30820948199055664,0.005,0.13904288163430922,2.0', 0, 'NEW', 'LOW', '449e809e-99d9-4ab1-a369-82c5908d0379', 0, 6, -9, 0, 'ABORT', 0, 0, 1, 0, 0, '', '1900-01-01 00:00:00', '2014-09-27 09:19:25', '2014-09-27 16:17:25'),
(4, '5c120c95266ab736d5f3cc8979bf0f4d59c624cc', 1, 'TestInstance', '0', 3695556388, 600, '0.4094847818634162,0.28486029990444794,0.005,0.860438772626119,1.0', 0, 'NEW', 'LOW', '449e809e-99d9-4ab1-a369-82c5908d0379', 0, 6, -9, 0, 'ABORT', 0, 0, 1, 0, 0, '', '1900-01-01 00:00:00', '2014-09-27 09:19:25', '2014-09-27 16:17:25'),
(5, 'd037221e35b00491d693e3340ef9922deb8fd296', 1, 'TestInstance', '0', 1094562015, 600, '0.4891867735821195,0.27592978749472385,0.005,0.25484757876143616,1.0', 0, 'NEW', 'LOW', '449e809e-99d9-4ab1-a369-82c5908d0379', 0, 6, -9, 0, 'ABORT', 0, 0, 1, 0, 0, '', '1900-01-01 00:00:00', '2014-09-27 09:19:25', '2014-09-27 16:17:25');

-- --------------------------------------------------------

--
-- Table structure for table `junit_CutoffIdleTest_isolated_version`
--

CREATE TABLE IF NOT EXISTS `junit_CutoffIdleTest_isolated_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` varchar(255) NOT NULL,
  `hash` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash` (`hash`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Dumping data for table `junit_CutoffIdleTest_isolated_version`
--

INSERT INTO `junit_CutoffIdleTest_isolated_version` (`id`, `version`, `hash`) VALUES
(1, 'v0.92.00dev-development-112 (8a5109587631)', '84ed1641e09245a2869dfe555e546f1bfc369c1c');

-- --------------------------------------------------------

--
-- Table structure for table `junit_CutoffIdleTest_isolated_workers`
--

CREATE TABLE IF NOT EXISTS `junit_CutoffIdleTest_isolated_workers` (
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

--
-- Dumping data for table `junit_CutoffIdleTest_isolated_workers`
--

INSERT INTO `junit_CutoffIdleTest_isolated_workers` (`workerUUID`, `hostname`, `username`, `jobID`, `status`, `version`, `crashInfo`, `startTime`, `startWeekYear`, `originalEndTime`, `currentlyIdle`, `endTime_UPDATEABLE`, `runsToBatch_UPDATEABLE`, `minRunsToBatch_UPDATEABLE`, `maxRunsToBatch_UPDATEABLE`, `autoAdjustBatchSize_UPDATEABLE`, `delayBetweenRequests_UPDATEABLE`, `pool_UPDATEABLE`, `poolIdleTimeLimit_UPDATEABLE`, `workerIdleTime_UPDATEABLE`, `concurrencyFactor_UPDATEABLE`, `upToDate`, `worstCaseNextUpdateWhenRunning`, `lastModified`) VALUES
('449e809e-99d9-4ab1-a369-82c5908d0379', 'hilbert', 'sjr', 'CLI/20109@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Minimum Job Cutoff in DB is too high: 600 seconds verus: 14', '2014-09-27 09:17:19', 392014, '2014-09-27 09:17:38', 0, '2014-09-27 09:17:38', 5, 1, 100, 1, 1, 'junit_CutoffIdleTest_isolated', 14400000, 4, 4, 1, '2014-09-27 09:17:25', '2014-09-27 16:17:25');

-- --------------------------------------------------------

--
-- Table structure for table `junit_dzq_test_commandTable`
--

CREATE TABLE IF NOT EXISTS `junit_dzq_test_commandTable` (
  `commandID` int(11) NOT NULL AUTO_INCREMENT,
  `commandString` text NOT NULL,
  PRIMARY KEY (`commandID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `junit_dzq_test_commandTable`
--

INSERT INTO `junit_dzq_test_commandTable` (`commandID`, `commandString`) VALUES
(1, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite');

-- --------------------------------------------------------

--
-- Table structure for table `junit_dzq_test_execConfig`
--

CREATE TABLE IF NOT EXISTS `junit_dzq_test_execConfig` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `junit_dzq_test_runConfigs`
--

CREATE TABLE IF NOT EXISTS `junit_dzq_test_runConfigs` (
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `junit_dzq_test_version`
--

CREATE TABLE IF NOT EXISTS `junit_dzq_test_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` varchar(255) NOT NULL,
  `hash` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash` (`hash`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Dumping data for table `junit_dzq_test_version`
--

INSERT INTO `junit_dzq_test_version` (`id`, `version`, `hash`) VALUES
(1, 'v0.92.00dev-development-112 (8a5109587631)', '84ed1641e09245a2869dfe555e546f1bfc369c1c');

-- --------------------------------------------------------

--
-- Table structure for table `junit_dzq_test_workers`
--

CREATE TABLE IF NOT EXISTS `junit_dzq_test_workers` (
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

--
-- Dumping data for table `junit_dzq_test_workers`
--

INSERT INTO `junit_dzq_test_workers` (`workerUUID`, `hostname`, `username`, `jobID`, `status`, `version`, `crashInfo`, `startTime`, `startWeekYear`, `originalEndTime`, `currentlyIdle`, `endTime_UPDATEABLE`, `runsToBatch_UPDATEABLE`, `minRunsToBatch_UPDATEABLE`, `maxRunsToBatch_UPDATEABLE`, `autoAdjustBatchSize_UPDATEABLE`, `delayBetweenRequests_UPDATEABLE`, `pool_UPDATEABLE`, `poolIdleTimeLimit_UPDATEABLE`, `workerIdleTime_UPDATEABLE`, `concurrencyFactor_UPDATEABLE`, `upToDate`, `worstCaseNextUpdateWhenRunning`, `lastModified`) VALUES
('f1c70d75-f41f-4ec6-83d1-1ea541400826', 'hilbert', 'sjr', 'CLI/19063@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'RuntimeException of some kind:java.lang.IllegalStateException: Couldn''t get connection\n	at ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistence.getConnection(MySQLPersistence.java:87)\n	at ca.ubc.cs.beta.mysqldbtae.persistence.worker.MySQLPersistenceWorker.updateIdleTime(MySQLPersistenceWorker.java:634)\n	at ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorkerTaskProcessor.checkForUpdatedParameters(MySQLTAEWorkerTaskProcessor.java:371)\n	at ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorkerTaskProcessor.process(MySQLTAEWorkerTaskProcessor.java:289)\n	at ca.ubc.cpsc.beta.mysqldbtae.MySQLDZQTester$1.run(MySQLDZQTester.java:120)\n	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)\n	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:615)\n	at java.lang.Thread.run(Thread.java:745)\nCaused by: java.sql.SQLException: An SQLException was provoked by the following failure: java.lang.InterruptedException\n	at com.mchange.v2.sql.SqlUtils.toSQLException(SqlUtils.java:106)\n	at com.mchange.v2.sql.SqlUtils.toSQLException(SqlUtils.java:65)\n	at com.mchange.v2.sql.SqlUtils.toSQLException(SqlUtils.java:62)\n	at com.mchange.v2.c3p0.impl.C3P0PooledConnectionPool.checkoutPooledConnection(C3P0PooledConnectionPool.java:679)\n	at com.mchange.v2.c3p0.impl.AbstractPoolBackedDataSource.getConnection(AbstractPoolBackedDataSource.java:128)\n	at ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistence.getConnection(MySQLPersistence.java:84)\n	... 7 more\nCaused by: java.lang.InterruptedException\n	at java.lang.Object.wait(Native Method)\n	at com.mchange.v2.resourcepool.BasicResourcePool.awaitAvailable(BasicResourcePool.java:1402)\n	at com.mchange.v2.resourcepool.BasicResourcePool.prelimCheckoutResource(BasicResourcePool.java:594)\n	at com.mchange.v2.resourcepool.BasicResourcePool.checkoutResource(BasicResourcePool.java:514)\n	at com.mchange.v2.c3p0.impl.C3P0PooledConnectionPool.checkoutAndMarkConnectionInUse(C3P0PooledConnectionPool.java:743)\n	at com.mchange.v2.c3p0.impl.C3P0PooledConnectionPool.checkoutPooledConnection(C3P0PooledConnectionPool.java:670)\n	... 9 more\n', '2014-09-27 09:21:48', 392014, '2014-09-28 09:20:48', 1, '2014-09-28 09:20:48', 1, 1, 100, 1, 1, 'junit_dzq_test', 14400000, 0, 4, 1, '2014-09-27 09:21:48', '2014-09-27 16:21:48');

-- --------------------------------------------------------

--
-- Table structure for table `junit_endtimetest_commandTable`
--

CREATE TABLE IF NOT EXISTS `junit_endtimetest_commandTable` (
  `commandID` int(11) NOT NULL AUTO_INCREMENT,
  `commandString` text NOT NULL,
  PRIMARY KEY (`commandID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `junit_endtimetest_commandTable`
--

INSERT INTO `junit_endtimetest_commandTable` (`commandID`, `commandString`) VALUES
(1, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite');

-- --------------------------------------------------------

--
-- Table structure for table `junit_endtimetest_execConfig`
--

CREATE TABLE IF NOT EXISTS `junit_endtimetest_execConfig` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `junit_endtimetest_runConfigs`
--

CREATE TABLE IF NOT EXISTS `junit_endtimetest_runConfigs` (
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `junit_endtimetest_version`
--

CREATE TABLE IF NOT EXISTS `junit_endtimetest_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` varchar(255) NOT NULL,
  `hash` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash` (`hash`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

--
-- Dumping data for table `junit_endtimetest_version`
--

INSERT INTO `junit_endtimetest_version` (`id`, `version`, `hash`) VALUES
(1, 'v0.92.00dev-development-112 (8a5109587631)', '84ed1641e09245a2869dfe555e546f1bfc369c1c');

-- --------------------------------------------------------

--
-- Table structure for table `junit_endtimetest_workers`
--

CREATE TABLE IF NOT EXISTS `junit_endtimetest_workers` (
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

--
-- Dumping data for table `junit_endtimetest_workers`
--

INSERT INTO `junit_endtimetest_workers` (`workerUUID`, `hostname`, `username`, `jobID`, `status`, `version`, `crashInfo`, `startTime`, `startWeekYear`, `originalEndTime`, `currentlyIdle`, `endTime_UPDATEABLE`, `runsToBatch_UPDATEABLE`, `minRunsToBatch_UPDATEABLE`, `maxRunsToBatch_UPDATEABLE`, `autoAdjustBatchSize_UPDATEABLE`, `delayBetweenRequests_UPDATEABLE`, `pool_UPDATEABLE`, `poolIdleTimeLimit_UPDATEABLE`, `workerIdleTime_UPDATEABLE`, `concurrencyFactor_UPDATEABLE`, `upToDate`, `worstCaseNextUpdateWhenRunning`, `lastModified`) VALUES
('06c4ad83-475a-4ad2-b23c-4474b72386ce', 'hilbert', 'sjr', 'CLI/20341@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Idle limit reached: 3.0 versus limit: 2', '2014-09-27 09:17:44', 392014, '2014-09-27 09:19:43', 1, '2014-09-27 09:19:43', 1, 1, 100, 1, 1, 'junit_endtimetest', 14400000, 2, 4, 1, '2014-09-27 09:17:48', '2014-09-27 16:17:48'),
('c2f9c5d6-d825-41e1-a1df-767e1a70f327', 'hilbert', 'sjr', 'proc1/20265@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Time Limit Expired (Wait time remaining is higher than seconds we have left)', '2014-09-27 09:17:35', 392014, '2014-09-27 09:19:34', 1, '2014-09-27 09:17:42', 1, 1, 100, 1, 1, 'junit_endtimetest', 14400000, 4, 4, 1, '2014-09-27 09:17:42', '2014-09-27 16:17:42'),
('d8ac48df-5814-477c-b620-688dd2563fcc', 'hilbert', 'sjr', 'CLI/20205@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Time Limit Expired (Wait time remaining is higher than seconds we have left)', '2014-09-27 09:17:30', 392014, '2014-09-27 09:17:31', 1, '2014-09-27 09:17:31', 1, 1, 100, 1, 1, 'junit_endtimetest', 14400000, 0, 4, 1, '2014-09-27 09:17:32', '2014-09-27 16:17:32');

-- --------------------------------------------------------

--
-- Table structure for table `junit_equalityTester_commandTable`
--

CREATE TABLE IF NOT EXISTS `junit_equalityTester_commandTable` (
  `commandID` int(11) NOT NULL AUTO_INCREMENT,
  `commandString` text NOT NULL,
  PRIMARY KEY (`commandID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `junit_equalityTester_commandTable`
--

INSERT INTO `junit_equalityTester_commandTable` (`commandID`, `commandString`) VALUES
(1, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite');

-- --------------------------------------------------------

--
-- Table structure for table `junit_equalityTester_execConfig`
--

CREATE TABLE IF NOT EXISTS `junit_equalityTester_execConfig` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Dumping data for table `junit_equalityTester_execConfig`
--

INSERT INTO `junit_equalityTester_execConfig` (`algorithmExecutionConfigID`, `algorithmExecutionConfigHashCode`, `algorithmExecutable`, `algorithmExecutableDirectory`, `parameterFile`, `executeOnCluster`, `deterministicAlgorithm`, `cutoffTime`, `algorithmExecutionConfigurationJSON`, `lastModified`) VALUES
(1, '9108cb412ecff3cc29c5ea44cdf67a177e23fecd', 'java -cp /home/sjr/git/MySQLDBTAE/bin:/home/sjr/git/AEATK/bin:/home/sjr/git/AEATK/lib/commons-collections-3.2.1.jar:/home/sjr/git/AEATK/lib/commons-math-2.2.jar:/home/sjr/git/AEATK/lib/opencsv-2.3.jar:/home/sjr/git/AEATK/lib/spi-0.2.4.jar:/home/sjr/git/AEATK/lib/jcip-annotations.jar:/home/sjr/git/AEATK/lib/Jama-1.0.2.jar:/home/sjr/git/AEATK/lib/numerics4j-1.3.jar:/home/sjr/git/AEATK/lib/guava-14.0.1.jar:/home/sjr/git/AEATK/lib/exp4j-0.3.10.jar:/home/sjr/git/AEATK/lib/commons-io-2.1.jar:/home/sjr/git/AEATK/lib/slf4j-api-1.7.5.jar:/home/sjr/git/AEATK/lib/jackson-annotations-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-core-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-databind-2.3.1.jar:/home/sjr/git/AEATK/lib/logback-access-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-classic-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-core-1.1.2.jar:/home/sjr/git/RandomForests/bin:/opt/eclipse/plugins/org.junit_4.11.0.v201303080030/junit.jar:/opt/eclipse/plugins/org.hamcrest.core_1.3.0.v201303031735.jar:/home/sjr/git/AEATK/lib/jcommander.jar:/home/sjr/git/AEATK/lib/commons-math3-3.3.jar:/home/sjr/git/MySQLDBTAE/lib/commons-codec-1.7.jar:/home/sjr/git/MySQLDBTAE/lib/mchange-commons-java-0.2.3.3.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-0.9.2-pre8.jar:/home/sjr/git/MySQLDBTAE/lib/mysql-connector-java-5.1.26-bin.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-oracle-thin-extras-0.9.2-pre8.jar:/opt/eclipse/configuration/org.eclipse.osgi/bundles/164/1/.cp/:/opt/eclipse/configuration/org.eclipse.osgi/bundles/163/1/.cp/ ca.ubc.cs.beta.targetalgorithmevaluator.TrueSleepyParamEchoExecutor', '/home/sjr/git/MySQLDBTAE', '/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt', 0, 0, 500, '{"@algo-exec-config-id":2,"algo-exec":"java -cp /home/sjr/git/MySQLDBTAE/bin:/home/sjr/git/AEATK/bin:/home/sjr/git/AEATK/lib/commons-collections-3.2.1.jar:/home/sjr/git/AEATK/lib/commons-math-2.2.jar:/home/sjr/git/AEATK/lib/opencsv-2.3.jar:/home/sjr/git/AEATK/lib/spi-0.2.4.jar:/home/sjr/git/AEATK/lib/jcip-annotations.jar:/home/sjr/git/AEATK/lib/Jama-1.0.2.jar:/home/sjr/git/AEATK/lib/numerics4j-1.3.jar:/home/sjr/git/AEATK/lib/guava-14.0.1.jar:/home/sjr/git/AEATK/lib/exp4j-0.3.10.jar:/home/sjr/git/AEATK/lib/commons-io-2.1.jar:/home/sjr/git/AEATK/lib/slf4j-api-1.7.5.jar:/home/sjr/git/AEATK/lib/jackson-annotations-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-core-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-databind-2.3.1.jar:/home/sjr/git/AEATK/lib/logback-access-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-classic-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-core-1.1.2.jar:/home/sjr/git/RandomForests/bin:/opt/eclipse/plugins/org.junit_4.11.0.v201303080030/junit.jar:/opt/eclipse/plugins/org.hamcrest.core_1.3.0.v201303031735.jar:/home/sjr/git/AEATK/lib/jcommander.jar:/home/sjr/git/AEATK/lib/commons-math3-3.3.jar:/home/sjr/git/MySQLDBTAE/lib/commons-codec-1.7.jar:/home/sjr/git/MySQLDBTAE/lib/mchange-commons-java-0.2.3.3.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-0.9.2-pre8.jar:/home/sjr/git/MySQLDBTAE/lib/mysql-connector-java-5.1.26-bin.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-oracle-thin-extras-0.9.2-pre8.jar:/opt/eclipse/configuration/org.eclipse.osgi/bundles/164/1/.cp/:/opt/eclipse/configuration/org.eclipse.osgi/bundles/163/1/.cp/ ca.ubc.cs.beta.targetalgorithmevaluator.TrueSleepyParamEchoExecutor","algo-exec-dir":"/home/sjr/git/MySQLDBTAE","algo-pcs":{"@pcs-id":2,"pcs-filename":"/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt","pcs-text":"#Used for getting specific results out from a target wrapper\\n#Some invalid values are specified so that we can test code\\n\\nsolved { SAT, UNSAT, TIMEOUT, CRASHED, ABORT, INVALID } [SAT]\\nruntime [0,1000] [0]\\nrunlength [0,1000000][0]\\nquality [0, 1000000] [0]\\nseed [ -1,4294967295][1]i\\n","pcs-subspace":{}},"algo-cutoff":500.0,"algo-deterministic":false,"algo-tae-context":{"Key":"Value"}}', '2014-09-27 16:20:58'),
(2, '2625865e6eafb0a4fa16ff33c3ac6d6469acb9eb', 'java -cp /home/sjr/git/MySQLDBTAE/bin:/home/sjr/git/AEATK/bin:/home/sjr/git/AEATK/lib/commons-collections-3.2.1.jar:/home/sjr/git/AEATK/lib/commons-math-2.2.jar:/home/sjr/git/AEATK/lib/opencsv-2.3.jar:/home/sjr/git/AEATK/lib/spi-0.2.4.jar:/home/sjr/git/AEATK/lib/jcip-annotations.jar:/home/sjr/git/AEATK/lib/Jama-1.0.2.jar:/home/sjr/git/AEATK/lib/numerics4j-1.3.jar:/home/sjr/git/AEATK/lib/guava-14.0.1.jar:/home/sjr/git/AEATK/lib/exp4j-0.3.10.jar:/home/sjr/git/AEATK/lib/commons-io-2.1.jar:/home/sjr/git/AEATK/lib/slf4j-api-1.7.5.jar:/home/sjr/git/AEATK/lib/jackson-annotations-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-core-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-databind-2.3.1.jar:/home/sjr/git/AEATK/lib/logback-access-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-classic-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-core-1.1.2.jar:/home/sjr/git/RandomForests/bin:/opt/eclipse/plugins/org.junit_4.11.0.v201303080030/junit.jar:/opt/eclipse/plugins/org.hamcrest.core_1.3.0.v201303031735.jar:/home/sjr/git/AEATK/lib/jcommander.jar:/home/sjr/git/AEATK/lib/commons-math3-3.3.jar:/home/sjr/git/MySQLDBTAE/lib/commons-codec-1.7.jar:/home/sjr/git/MySQLDBTAE/lib/mchange-commons-java-0.2.3.3.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-0.9.2-pre8.jar:/home/sjr/git/MySQLDBTAE/lib/mysql-connector-java-5.1.26-bin.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-oracle-thin-extras-0.9.2-pre8.jar:/opt/eclipse/configuration/org.eclipse.osgi/bundles/164/1/.cp/:/opt/eclipse/configuration/org.eclipse.osgi/bundles/163/1/.cp/ ca.ubc.cs.beta.targetalgorithmevaluator.TrueSleepyParamEchoExecutor', '/home/sjr/git/MySQLDBTAE', '/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt', 0, 0, 500, '{"@algo-exec-config-id":2,"algo-exec":"java -cp /home/sjr/git/MySQLDBTAE/bin:/home/sjr/git/AEATK/bin:/home/sjr/git/AEATK/lib/commons-collections-3.2.1.jar:/home/sjr/git/AEATK/lib/commons-math-2.2.jar:/home/sjr/git/AEATK/lib/opencsv-2.3.jar:/home/sjr/git/AEATK/lib/spi-0.2.4.jar:/home/sjr/git/AEATK/lib/jcip-annotations.jar:/home/sjr/git/AEATK/lib/Jama-1.0.2.jar:/home/sjr/git/AEATK/lib/numerics4j-1.3.jar:/home/sjr/git/AEATK/lib/guava-14.0.1.jar:/home/sjr/git/AEATK/lib/exp4j-0.3.10.jar:/home/sjr/git/AEATK/lib/commons-io-2.1.jar:/home/sjr/git/AEATK/lib/slf4j-api-1.7.5.jar:/home/sjr/git/AEATK/lib/jackson-annotations-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-core-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-databind-2.3.1.jar:/home/sjr/git/AEATK/lib/logback-access-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-classic-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-core-1.1.2.jar:/home/sjr/git/RandomForests/bin:/opt/eclipse/plugins/org.junit_4.11.0.v201303080030/junit.jar:/opt/eclipse/plugins/org.hamcrest.core_1.3.0.v201303031735.jar:/home/sjr/git/AEATK/lib/jcommander.jar:/home/sjr/git/AEATK/lib/commons-math3-3.3.jar:/home/sjr/git/MySQLDBTAE/lib/commons-codec-1.7.jar:/home/sjr/git/MySQLDBTAE/lib/mchange-commons-java-0.2.3.3.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-0.9.2-pre8.jar:/home/sjr/git/MySQLDBTAE/lib/mysql-connector-java-5.1.26-bin.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-oracle-thin-extras-0.9.2-pre8.jar:/opt/eclipse/configuration/org.eclipse.osgi/bundles/164/1/.cp/:/opt/eclipse/configuration/org.eclipse.osgi/bundles/163/1/.cp/ ca.ubc.cs.beta.targetalgorithmevaluator.TrueSleepyParamEchoExecutor","algo-exec-dir":"/home/sjr/git/MySQLDBTAE","algo-pcs":{"@pcs-id":2,"pcs-filename":"/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt","pcs-text":"#Used for getting specific results out from a target wrapper\\n#Some invalid values are specified so that we can test code\\n\\nsolved { SAT, UNSAT, TIMEOUT, CRASHED, ABORT, INVALID } [SAT]\\nruntime [0,1000] [0]\\nrunlength [0,1000000][0]\\nquality [0, 1000000] [0]\\nseed [ -1,4294967295][1]i\\n","pcs-subspace":{}},"algo-cutoff":500.0,"algo-deterministic":false,"algo-tae-context":{}}', '2014-09-27 16:20:58');

-- --------------------------------------------------------

--
-- Table structure for table `junit_equalityTester_runConfigs`
--

CREATE TABLE IF NOT EXISTS `junit_equalityTester_runConfigs` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=3 ;

--
-- Dumping data for table `junit_equalityTester_runConfigs`
--

INSERT INTO `junit_equalityTester_runConfigs` (`runConfigID`, `runConfigUUID`, `execConfigID`, `problemInstance`, `instanceSpecificInformation`, `seed`, `cutoffTime`, `paramConfiguration`, `cutoffLessThanMax`, `status`, `priority`, `workerUUID`, `killJob`, `retryAttempts`, `runPartition`, `noop`, `runResult`, `runLength`, `quality`, `resultSeed`, `runtime`, `walltime`, `additionalRunData`, `worstCaseEndtime`, `worstCaseNextUpdateWhenAssigned`, `lastModified`) VALUES
(1, '3c265823600494dadd34f2bfb0c195088c99b555', 1, 'TestInstance', '0', 789050610, 20, '0.4517501144624977,0.0881101155097167,0.001,0.18371516170824992,2.0', 1, 'COMPLETE', 'HIGH', '3f36d8a7-8862-4b6d-9b3a-fa562609b1e3', 0, 1, -9, 0, 'SAT', 3, 3, 789050610, 3, 0, '', '1900-01-01 00:00:00', '2014-09-27 09:30:58', '2014-09-27 16:20:58'),
(2, '965533e9b37f8551361ffcbf51739884c5c3942d', 2, 'TestInstance', '0', 1043870693, 20, '0.6478603743992867,0.4323416240579038,0.001,0.24304508563525856,1.0', 1, 'ASSIGNED', 'HIGH', 'ead7981c-3656-4488-bf3b-eb3fa6462231', 0, 1, -9, 0, 'ABORT', 0, 0, 1, 0, 0, '', '1900-01-01 00:00:00', '2014-09-27 09:30:58', '2014-09-27 16:20:58');

-- --------------------------------------------------------

--
-- Table structure for table `junit_equalityTester_version`
--

CREATE TABLE IF NOT EXISTS `junit_equalityTester_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` varchar(255) NOT NULL,
  `hash` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash` (`hash`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;

--
-- Dumping data for table `junit_equalityTester_version`
--

INSERT INTO `junit_equalityTester_version` (`id`, `version`, `hash`) VALUES
(1, 'v0.92.00dev-development-112 (8a5109587631)', '84ed1641e09245a2869dfe555e546f1bfc369c1c');

-- --------------------------------------------------------

--
-- Table structure for table `junit_equalityTester_workers`
--

CREATE TABLE IF NOT EXISTS `junit_equalityTester_workers` (
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

--
-- Dumping data for table `junit_equalityTester_workers`
--

INSERT INTO `junit_equalityTester_workers` (`workerUUID`, `hostname`, `username`, `jobID`, `status`, `version`, `crashInfo`, `startTime`, `startWeekYear`, `originalEndTime`, `currentlyIdle`, `endTime_UPDATEABLE`, `runsToBatch_UPDATEABLE`, `minRunsToBatch_UPDATEABLE`, `maxRunsToBatch_UPDATEABLE`, `autoAdjustBatchSize_UPDATEABLE`, `delayBetweenRequests_UPDATEABLE`, `pool_UPDATEABLE`, `poolIdleTimeLimit_UPDATEABLE`, `workerIdleTime_UPDATEABLE`, `concurrencyFactor_UPDATEABLE`, `upToDate`, `worstCaseNextUpdateWhenRunning`, `lastModified`) VALUES
('3f36d8a7-8862-4b6d-9b3a-fa562609b1e3', 'hilbert', 'sjr', 'CLI/19063@hilbert', 'RUNNING', 'v0.92.00dev-development-112 (8a5109587631)', NULL, '2014-09-27 09:20:58', 392014, '2014-09-27 09:22:38', 0, '2014-09-27 09:22:38', 3, 1, 100, 1, 10, 'junit_equalityTester', 14400000, 0, 4, 1, '2014-09-27 09:30:58', '2014-09-27 16:20:58'),
('ead7981c-3656-4488-bf3b-eb3fa6462231', 'hilbert', 'sjr', 'CLI/19063@hilbert', 'RUNNING', 'v0.92.00dev-development-112 (8a5109587631)', NULL, '2014-09-27 09:20:58', 392014, '2014-09-27 09:22:38', 0, '2014-09-27 09:22:38', 3, 1, 100, 1, 10, 'junit_equalityTester', 14400000, 0, 4, 1, '2014-09-27 09:30:58', '2014-09-27 16:20:58');

-- --------------------------------------------------------

--
-- Table structure for table `junit_JobPushBackTest_commandTable`
--

CREATE TABLE IF NOT EXISTS `junit_JobPushBackTest_commandTable` (
  `commandID` int(11) NOT NULL AUTO_INCREMENT,
  `commandString` text NOT NULL,
  PRIMARY KEY (`commandID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `junit_JobPushBackTest_commandTable`
--

INSERT INTO `junit_JobPushBackTest_commandTable` (`commandID`, `commandString`) VALUES
(1, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite');

-- --------------------------------------------------------

--
-- Table structure for table `junit_JobPushBackTest_execConfig`
--

CREATE TABLE IF NOT EXISTS `junit_JobPushBackTest_execConfig` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `junit_JobPushBackTest_execConfig`
--

INSERT INTO `junit_JobPushBackTest_execConfig` (`algorithmExecutionConfigID`, `algorithmExecutionConfigHashCode`, `algorithmExecutable`, `algorithmExecutableDirectory`, `parameterFile`, `executeOnCluster`, `deterministicAlgorithm`, `cutoffTime`, `algorithmExecutionConfigurationJSON`, `lastModified`) VALUES
(1, '2625865e6eafb0a4fa16ff33c3ac6d6469acb9eb', 'java -cp /home/sjr/git/MySQLDBTAE/bin:/home/sjr/git/AEATK/bin:/home/sjr/git/AEATK/lib/commons-collections-3.2.1.jar:/home/sjr/git/AEATK/lib/commons-math-2.2.jar:/home/sjr/git/AEATK/lib/opencsv-2.3.jar:/home/sjr/git/AEATK/lib/spi-0.2.4.jar:/home/sjr/git/AEATK/lib/jcip-annotations.jar:/home/sjr/git/AEATK/lib/Jama-1.0.2.jar:/home/sjr/git/AEATK/lib/numerics4j-1.3.jar:/home/sjr/git/AEATK/lib/guava-14.0.1.jar:/home/sjr/git/AEATK/lib/exp4j-0.3.10.jar:/home/sjr/git/AEATK/lib/commons-io-2.1.jar:/home/sjr/git/AEATK/lib/slf4j-api-1.7.5.jar:/home/sjr/git/AEATK/lib/jackson-annotations-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-core-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-databind-2.3.1.jar:/home/sjr/git/AEATK/lib/logback-access-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-classic-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-core-1.1.2.jar:/home/sjr/git/RandomForests/bin:/opt/eclipse/plugins/org.junit_4.11.0.v201303080030/junit.jar:/opt/eclipse/plugins/org.hamcrest.core_1.3.0.v201303031735.jar:/home/sjr/git/AEATK/lib/jcommander.jar:/home/sjr/git/AEATK/lib/commons-math3-3.3.jar:/home/sjr/git/MySQLDBTAE/lib/commons-codec-1.7.jar:/home/sjr/git/MySQLDBTAE/lib/mchange-commons-java-0.2.3.3.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-0.9.2-pre8.jar:/home/sjr/git/MySQLDBTAE/lib/mysql-connector-java-5.1.26-bin.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-oracle-thin-extras-0.9.2-pre8.jar:/opt/eclipse/configuration/org.eclipse.osgi/bundles/164/1/.cp/:/opt/eclipse/configuration/org.eclipse.osgi/bundles/163/1/.cp/ ca.ubc.cs.beta.targetalgorithmevaluator.TrueSleepyParamEchoExecutor', '/home/sjr/git/MySQLDBTAE', '/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt', 0, 0, 500, '{"@algo-exec-config-id":2,"algo-exec":"java -cp /home/sjr/git/MySQLDBTAE/bin:/home/sjr/git/AEATK/bin:/home/sjr/git/AEATK/lib/commons-collections-3.2.1.jar:/home/sjr/git/AEATK/lib/commons-math-2.2.jar:/home/sjr/git/AEATK/lib/opencsv-2.3.jar:/home/sjr/git/AEATK/lib/spi-0.2.4.jar:/home/sjr/git/AEATK/lib/jcip-annotations.jar:/home/sjr/git/AEATK/lib/Jama-1.0.2.jar:/home/sjr/git/AEATK/lib/numerics4j-1.3.jar:/home/sjr/git/AEATK/lib/guava-14.0.1.jar:/home/sjr/git/AEATK/lib/exp4j-0.3.10.jar:/home/sjr/git/AEATK/lib/commons-io-2.1.jar:/home/sjr/git/AEATK/lib/slf4j-api-1.7.5.jar:/home/sjr/git/AEATK/lib/jackson-annotations-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-core-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-databind-2.3.1.jar:/home/sjr/git/AEATK/lib/logback-access-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-classic-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-core-1.1.2.jar:/home/sjr/git/RandomForests/bin:/opt/eclipse/plugins/org.junit_4.11.0.v201303080030/junit.jar:/opt/eclipse/plugins/org.hamcrest.core_1.3.0.v201303031735.jar:/home/sjr/git/AEATK/lib/jcommander.jar:/home/sjr/git/AEATK/lib/commons-math3-3.3.jar:/home/sjr/git/MySQLDBTAE/lib/commons-codec-1.7.jar:/home/sjr/git/MySQLDBTAE/lib/mchange-commons-java-0.2.3.3.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-0.9.2-pre8.jar:/home/sjr/git/MySQLDBTAE/lib/mysql-connector-java-5.1.26-bin.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-oracle-thin-extras-0.9.2-pre8.jar:/opt/eclipse/configuration/org.eclipse.osgi/bundles/164/1/.cp/:/opt/eclipse/configuration/org.eclipse.osgi/bundles/163/1/.cp/ ca.ubc.cs.beta.targetalgorithmevaluator.TrueSleepyParamEchoExecutor","algo-exec-dir":"/home/sjr/git/MySQLDBTAE","algo-pcs":{"@pcs-id":2,"pcs-filename":"/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt","pcs-text":"#Used for getting specific results out from a target wrapper\\n#Some invalid values are specified so that we can test code\\n\\nsolved { SAT, UNSAT, TIMEOUT, CRASHED, ABORT, INVALID } [SAT]\\nruntime [0,1000] [0]\\nrunlength [0,1000000][0]\\nquality [0, 1000000] [0]\\nseed [ -1,4294967295][1]i\\n","pcs-subspace":{}},"algo-cutoff":500.0,"algo-deterministic":false,"algo-tae-context":{}}', '2014-09-27 16:18:20');

-- --------------------------------------------------------

--
-- Table structure for table `junit_JobPushBackTest_runConfigs`
--

CREATE TABLE IF NOT EXISTS `junit_JobPushBackTest_runConfigs` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=41 ;

--
-- Dumping data for table `junit_JobPushBackTest_runConfigs`
--

INSERT INTO `junit_JobPushBackTest_runConfigs` (`runConfigID`, `runConfigUUID`, `execConfigID`, `problemInstance`, `instanceSpecificInformation`, `seed`, `cutoffTime`, `paramConfiguration`, `cutoffLessThanMax`, `status`, `priority`, `workerUUID`, `killJob`, `retryAttempts`, `runPartition`, `noop`, `runResult`, `runLength`, `quality`, `resultSeed`, `runtime`, `walltime`, `additionalRunData`, `worstCaseEndtime`, `worstCaseNextUpdateWhenAssigned`, `lastModified`) VALUES
(1, '482d43541f142d748f70e72b6fa81dfc90e8c21f', 1, 'TestInstance', '0', 3162971990, 20, '0.22385039429340126,0.30844812726644577,0.001,0.7364368044686418,2.0', 1, 'COMPLETE', 'HIGH', '43a5b1c0-020b-4714-bc6e-419f204dc3c2', 0, 1, -9, 0, 'UNSAT', 308448.1272664458, 223850.39429340124, 3162971990, 1, 1.09, '', '2014-09-27 09:21:01', '2014-09-27 09:20:33', '2014-09-27 16:18:33'),
(2, 'b1dd78bcb41c5c8e8266fb88f87a716e301c9fd3', 1, 'TestInstance', '0', 1493336447, 20, '0.6733379068926855,0.7000783401535626,0.001,0.3476944864150848,2.0', 1, 'COMPLETE', 'HIGH', 'ff54d947-54f8-41f0-af64-fc3c67e96dbf', 0, 2, -9, 0, 'UNSAT', 700078.3401535626, 673337.9068926855, 1493336447, 1, 1.083, '', '2014-09-27 09:21:01', '2014-09-27 09:20:33', '2014-09-27 16:18:33'),
(3, 'd587590aab526362f6089d6fadd4fae9141e9970', 1, 'TestInstance', '0', 1979551703, 20, '0.9095221358058948,0.8999646040068511,0.001,0.460900297397538,2.0', 1, 'COMPLETE', 'HIGH', '43a5b1c0-020b-4714-bc6e-419f204dc3c2', 0, 1, -9, 0, 'UNSAT', 899964.6040068511, 909522.1358058949, 1979551703, 1, 1.087, '', '2014-09-27 09:21:00', '2014-09-27 09:20:32', '2014-09-27 16:18:32'),
(4, '9f0c9c71011849fb0e02e7029b4363d5727778b7', 1, 'TestInstance', '0', 3852227832, 20, '0.07237078426782395,0.5131768585726175,0.001,0.896916685766327,2.0', 1, 'COMPLETE', 'HIGH', '43a5b1c0-020b-4714-bc6e-419f204dc3c2', 0, 1, -9, 0, 'UNSAT', 513176.8585726175, 72370.78426782395, 3852227832, 1, 1.086, '', '2014-09-27 09:20:59', '2014-09-27 09:20:31', '2014-09-27 16:18:31'),
(5, 'e68d6aee261cfd5f7dbc20ed92fae1fbe8ca14d7', 1, 'TestInstance', '0', 2505102608, 20, '0.39349598480599546,0.934571093434214,0.001,0.5832646528530715,1.0', 1, 'COMPLETE', 'HIGH', '43a5b1c0-020b-4714-bc6e-419f204dc3c2', 0, 1, -9, 0, 'SAT', 934571.093434214, 393495.9848059955, 2505102608, 1, 1.063, '', '2014-09-27 09:20:58', '2014-09-27 09:20:30', '2014-09-27 16:18:30'),
(6, '048c6901bb03fda9c898541c995689d665f0afc4', 1, 'TestInstance', '0', 2821575293, 20, '0.1552735476355578,0.15765269454710062,0.001,0.6569491917833339,2.0', 1, 'COMPLETE', 'HIGH', '43a5b1c0-020b-4714-bc6e-419f204dc3c2', 0, 1, -9, 0, 'UNSAT', 157652.69454710063, 155273.5476355578, 2821575293, 1, 1.083, '', '2014-09-27 09:20:57', '2014-09-27 09:20:29', '2014-09-27 16:18:29'),
(7, '8d8ba12022b88ae3de93cfaf4c941514038dc15d', 1, 'TestInstance', '0', 3947659888, 20, '0.48308204903550156,0.7612713553759319,0.001,0.9191361927848458,2.0', 1, 'COMPLETE', 'HIGH', '43a5b1c0-020b-4714-bc6e-419f204dc3c2', 0, 1, -9, 0, 'UNSAT', 761271.355375932, 483082.04903550155, 3947659888, 1, 1.083, '', '2014-09-27 09:20:56', '2014-09-27 09:20:28', '2014-09-27 16:18:28'),
(8, 'b1aa74e85cdb241604f130b4278fde68432db44f', 1, 'TestInstance', '0', 1811479217, 20, '0.27678259182885845,0.9480342082916036,0.001,0.42176787231076324,1.0', 1, 'COMPLETE', 'HIGH', '43a5b1c0-020b-4714-bc6e-419f204dc3c2', 0, 1, -9, 0, 'SAT', 948034.2082916036, 276782.5918288584, 1811479217, 1, 1.083, '', '2014-09-27 09:20:54', '2014-09-27 09:20:26', '2014-09-27 16:18:26'),
(9, '3f0be2faace7a09cc3e8aac236ba18e191b654f4', 1, 'TestInstance', '0', 2200621157, 20, '0.9023361583733314,0.19189475830732472,0.001,0.5123720406525833,2.0', 1, 'COMPLETE', 'HIGH', '43a5b1c0-020b-4714-bc6e-419f204dc3c2', 0, 1, -9, 0, 'UNSAT', 191894.75830732472, 902336.1583733314, 2200621157, 1, 1.114, '', '2014-09-27 09:20:53', '2014-09-27 09:20:25', '2014-09-27 16:18:25'),
(10, 'c3f00836036d93adc7c824dae9d8ee9ab780f90a', 1, 'TestInstance', '0', 917117630, 20, '0.5590068458054495,0.8518887447062766,0.001,0.21353308839874038,2.0', 1, 'COMPLETE', 'HIGH', 'ff54d947-54f8-41f0-af64-fc3c67e96dbf', 0, 2, -9, 0, 'UNSAT', 851888.7447062766, 559006.8458054494, 917117630, 1, 1.096, '', '2014-09-27 09:20:53', '2014-09-27 09:20:25', '2014-09-27 16:18:25'),
(11, '737dd903f555f6cf644ab3c5deacc4b8f12c1a85', 1, 'TestInstance', '0', 2792870543, 20, '0.3153473086085944,0.9995687334262184,0.001,0.6502658463664666,1.0', 1, 'COMPLETE', 'HIGH', '3104655b-7926-45f6-a31e-ba9c5130b010', 0, 2, -9, 0, 'SAT', 999568.7334262184, 315347.3086085944, 2792870543, 1, 1.115, '', '2014-09-27 09:20:54', '2014-09-27 09:20:26', '2014-09-27 16:18:26'),
(12, '60c40f42db5973ac358a2a62cdbebeab5bca4838', 1, 'TestInstance', '0', 1652348754, 20, '0.1096352834476676,0.8835707274401181,0.001,0.38471742419416144,1.0', 1, 'COMPLETE', 'HIGH', '096dc6ad-4665-412e-8b93-d92b3ebb74bb', 0, 2, -9, 0, 'SAT', 883570.7274401181, 109635.28344766759, 1652348754, 1, 1.12, '', '2014-09-27 09:20:54', '2014-09-27 09:20:26', '2014-09-27 16:18:26'),
(13, 'd57bbfb369578e2e1e9b388bc2d63d4eab8c6b4c', 1, 'TestInstance', '0', 1651266296, 20, '0.124658917430027,0.1908561368636581,0.001,0.38446539480135183,2.0', 1, 'COMPLETE', 'HIGH', 'ff54d947-54f8-41f0-af64-fc3c67e96dbf', 0, 2, -9, 0, 'UNSAT', 190856.1368636581, 124658.917430027, 1651266296, 1, 1.114, '', '2014-09-27 09:20:55', '2014-09-27 09:20:27', '2014-09-27 16:18:27'),
(14, 'd9483feb0d543da33c296328a59e6723ec3e12ad', 1, 'TestInstance', '0', 1416169275, 20, '0.12524904319363206,0.9954844767277856,0.001,0.32972760409355917,2.0', 1, 'COMPLETE', 'HIGH', '3104655b-7926-45f6-a31e-ba9c5130b010', 0, 2, -9, 0, 'UNSAT', 995484.4767277857, 125249.04319363205, 1416169275, 1, 1.111, '', '2014-09-27 09:20:55', '2014-09-27 09:20:27', '2014-09-27 16:18:27'),
(15, 'bcbee0a37d70315ac284e04852e0415ff84075e4', 1, 'TestInstance', '0', 3185466689, 20, '0.2657765571224582,0.5344332507235305,0.001,0.7416742597143924,1.0', 1, 'COMPLETE', 'HIGH', '096dc6ad-4665-412e-8b93-d92b3ebb74bb', 0, 2, -9, 0, 'SAT', 534433.2507235305, 265776.5571224582, 3185466689, 1, 1.09, '', '2014-09-27 09:20:55', '2014-09-27 09:20:27', '2014-09-27 16:18:27'),
(16, '0b7e28a55e5fdb90444608273ea8521abf76d6d6', 1, 'TestInstance', '0', 1103692807, 20, '0.07337441994379768,0.36034056558744376,0.001,0.25697350693937077,2.0', 1, 'COMPLETE', 'HIGH', 'ff54d947-54f8-41f0-af64-fc3c67e96dbf', 0, 2, -9, 0, 'UNSAT', 360340.56558744377, 73374.41994379768, 1103692807, 1, 1.147, '', '2014-09-27 09:20:56', '2014-09-27 09:20:28', '2014-09-27 16:18:28'),
(17, '91465de6838ba07cdb92e9929e464a81460c1ec5', 1, 'TestInstance', '0', 568878144, 20, '0.4825255936524423,0.3399504298775432,0.001,0.13245226474654576,1.0', 1, 'COMPLETE', 'HIGH', '3104655b-7926-45f6-a31e-ba9c5130b010', 0, 2, -9, 0, 'SAT', 339950.4298775432, 482525.5936524423, 568878144, 1, 1.083, '', '2014-09-27 09:20:56', '2014-09-27 09:20:28', '2014-09-27 16:18:28'),
(18, 'a941e1aaf6c657d6773c8d142464912d572af628', 1, 'TestInstance', '0', 2068662750, 20, '0.9795674144127209,0.057755171993214005,0.001,0.48164807982238755,1.0', 1, 'COMPLETE', 'HIGH', '096dc6ad-4665-412e-8b93-d92b3ebb74bb', 0, 2, -9, 0, 'SAT', 57755.171993214004, 979567.4144127208, 2068662750, 1, 1.084, '', '2014-09-27 09:20:56', '2014-09-27 09:20:28', '2014-09-27 16:18:28'),
(19, 'ec68d2540c7e0f507a201e142390e8779decf407', 1, 'TestInstance', '0', 2621033102, 20, '0.6999161947279106,0.9685989328202256,0.001,0.610256824383918,1.0', 1, 'COMPLETE', 'HIGH', 'ff54d947-54f8-41f0-af64-fc3c67e96dbf', 0, 2, -9, 0, 'SAT', 968598.9328202256, 699916.1947279106, 2621033102, 1, 1.084, '', '2014-09-27 09:20:57', '2014-09-27 09:20:29', '2014-09-27 16:18:29'),
(20, '331a1b83e4951da061429002bed3c30a231e9b44', 1, 'TestInstance', '0', 3023793210, 20, '0.7136456109659544,0.2320381475411697,0.001,0.7040317195458263,2.0', 1, 'COMPLETE', 'HIGH', '43a5b1c0-020b-4714-bc6e-419f204dc3c2', 0, 1, -9, 0, 'UNSAT', 232038.1475411697, 713645.6109659544, 3023793210, 1, 1.144, '', '2014-09-27 09:20:52', '2014-09-27 09:20:24', '2014-09-27 16:18:24'),
(21, '27c8384e57fe217f76a8e9614378e9cd3ae179e6', 1, 'TestInstance', '0', 2745516339, 20, '0.21539343359733798,0.006605250905122095,0.001,0.639240336571997,2.0', 1, 'COMPLETE', 'HIGH', 'da25d15f-2083-4369-9eb5-1a75f78f4f5d', 0, 1, -9, 0, 'UNSAT', 6605.250905122095, 215393.43359733798, 2745516339, 1, 1.083, '', '2014-09-27 09:21:01', '2014-09-27 09:20:33', '2014-09-27 16:18:33'),
(22, '6830551795ef1d7ca8905d2b360fef8a2454ba6e', 1, 'TestInstance', '0', 2467940562, 20, '0.7222166347675355,0.29733976502008985,0.001,0.5746121897654114,2.0', 1, 'COMPLETE', 'HIGH', '096dc6ad-4665-412e-8b93-d92b3ebb74bb', 0, 2, -9, 0, 'UNSAT', 297339.76502008986, 722216.6347675355, 2467940562, 1, 1.087, '', '2014-09-27 09:21:02', '2014-09-27 09:20:34', '2014-09-27 16:18:34'),
(23, '4dafdcdb2612ee47273bae563e033c6dd35b3253', 1, 'TestInstance', '0', 3095956757, 20, '0.21775548886249252,0.11659736094920457,0.001,0.7208336046382706,1.0', 1, 'COMPLETE', 'HIGH', 'da25d15f-2083-4369-9eb5-1a75f78f4f5d', 0, 1, -9, 0, 'SAT', 116597.36094920458, 217755.48886249252, 3095956757, 1, 1.087, '', '2014-09-27 09:21:00', '2014-09-27 09:20:32', '2014-09-27 16:18:32'),
(24, 'cd8bec17b898ce2f0026b5608fc7f1c3e3e8461f', 1, 'TestInstance', '0', 3688611521, 20, '0.45696495994313147,0.6611220225503305,0.001,0.858821794772795,1.0', 1, 'COMPLETE', 'HIGH', 'da25d15f-2083-4369-9eb5-1a75f78f4f5d', 0, 1, -9, 0, 'SAT', 661122.0225503305, 456964.9599431315, 3688611521, 1, 1.086, '', '2014-09-27 09:20:59', '2014-09-27 09:20:31', '2014-09-27 16:18:31'),
(25, 'e06ad60f0c9e1fff879326667928cb72fc9b8af5', 1, 'TestInstance', '0', 3640842351, 20, '0.2444985834742035,0.9055850154989945,0.001,0.8476996681774734,2.0', 1, 'COMPLETE', 'HIGH', 'da25d15f-2083-4369-9eb5-1a75f78f4f5d', 0, 1, -9, 0, 'UNSAT', 905585.0154989945, 244498.5834742035, 3640842351, 1, 1.085, '', '2014-09-27 09:20:58', '2014-09-27 09:20:30', '2014-09-27 16:18:30'),
(26, '00fb809972f5bb2ea76346ee996f1bd4ca087208', 1, 'TestInstance', '0', 2079773467, 20, '0.14017685744040642,0.8573411124878827,0.001,0.4842349952123512,1.0', 1, 'COMPLETE', 'HIGH', 'da25d15f-2083-4369-9eb5-1a75f78f4f5d', 0, 1, -9, 0, 'SAT', 857341.1124878827, 140176.85744040643, 2079773467, 1, 1.083, '', '2014-09-27 09:20:57', '2014-09-27 09:20:29', '2014-09-27 16:18:29'),
(27, '0667eab45ddad767872266133f9d4f307e2f9c24', 1, 'TestInstance', '0', 3581205334, 20, '0.126882821903747,0.2138248319158983,0.001,0.8338143431269996,1.0', 1, 'COMPLETE', 'HIGH', 'da25d15f-2083-4369-9eb5-1a75f78f4f5d', 0, 1, -9, 0, 'SAT', 213824.8319158983, 126882.821903747, 3581205334, 1, 1.111, '', '2014-09-27 09:20:56', '2014-09-27 09:20:28', '2014-09-27 16:18:28'),
(28, 'dd9f32ac6d1a273c978155c94abdc667289904c8', 1, 'TestInstance', '0', 1975652617, 20, '0.061362904473329816,0.41879707891537254,0.001,0.4599924706947076,2.0', 1, 'COMPLETE', 'HIGH', 'da25d15f-2083-4369-9eb5-1a75f78f4f5d', 0, 1, -9, 0, 'UNSAT', 418797.07891537255, 61362.904473329814, 1975652617, 1, 1.111, '', '2014-09-27 09:20:54', '2014-09-27 09:20:26', '2014-09-27 16:18:27'),
(29, 'c740689e00731ffde8b92085a8ddaad0428d502f', 1, 'TestInstance', '0', 146393185, 20, '0.6984166409003374,0.380186748579339,0.001,0.034084819831399986,2.0', 1, 'COMPLETE', 'HIGH', 'da25d15f-2083-4369-9eb5-1a75f78f4f5d', 0, 1, -9, 0, 'UNSAT', 380186.748579339, 698416.6409003374, 146393185, 1, 1.117, '', '2014-09-27 09:20:53', '2014-09-27 09:20:25', '2014-09-27 16:18:25'),
(30, 'd46b8725b1bb403f043f40cd9f086f55f6b057bc', 1, 'TestInstance', '0', 590929146, 20, '0.4614429490936385,0.920776478764969,0.001,0.13758641373422315,1.0', 1, 'COMPLETE', 'HIGH', '3104655b-7926-45f6-a31e-ba9c5130b010', 0, 2, -9, 0, 'SAT', 920776.478764969, 461442.94909363845, 590929146, 1, 1.084, '', '2014-09-27 09:20:57', '2014-09-27 09:20:29', '2014-09-27 16:18:29'),
(31, '107d9af3e21b3b70bda840c08353f34cb3cb5841', 1, 'TestInstance', '0', 2654710347, 20, '0.6291949386469528,0.8452717060710025,0.001,0.6180979190119315,1.0', 1, 'COMPLETE', 'HIGH', '096dc6ad-4665-412e-8b93-d92b3ebb74bb', 0, 2, -9, 0, 'SAT', 845271.7060710025, 629194.9386469527, 2654710347, 1, 1.083, '', '2014-09-27 09:20:57', '2014-09-27 09:20:29', '2014-09-27 16:18:30'),
(32, 'bd24d999c343b7f51061324b6409be48dfbffdaa', 1, 'TestInstance', '0', 4075808419, 20, '0.9264259643135617,0.16533320161633136,0.001,0.9489730977339267,1.0', 1, 'COMPLETE', 'HIGH', 'ff54d947-54f8-41f0-af64-fc3c67e96dbf', 0, 2, -9, 0, 'SAT', 165333.20161633135, 926425.9643135617, 4075808419, 1, 1.069, '', '2014-09-27 09:20:58', '2014-09-27 09:20:30', '2014-09-27 16:18:30'),
(33, '62cd0567ecb819245e2b49ba0ddab99e5c73f16c', 1, 'TestInstance', '0', 2754831620, 20, '0.632513400902829,0.5847674993798648,0.001,0.6414092194425386,2.0', 1, 'COMPLETE', 'HIGH', '3104655b-7926-45f6-a31e-ba9c5130b010', 0, 2, -9, 0, 'UNSAT', 584767.4993798648, 632513.400902829, 2754831620, 1, 1.11, '', '2014-09-27 09:20:59', '2014-09-27 09:20:31', '2014-09-27 16:18:31'),
(34, '9d75e609125b3f4459b11d8111be1dd2c31bdaab', 1, 'TestInstance', '0', 4198521868, 20, '0.6099152902335546,0.07820305231499991,0.001,0.9775445490429306,2.0', 1, 'COMPLETE', 'HIGH', '096dc6ad-4665-412e-8b93-d92b3ebb74bb', 0, 2, -9, 0, 'UNSAT', 78203.0523149999, 609915.2902335547, 4198521868, 1, 1.104, '', '2014-09-27 09:20:59', '2014-09-27 09:20:31', '2014-09-27 16:18:31'),
(35, '2d61eb19eb6583e1717dd34f967e6d70f5b8e16d', 1, 'TestInstance', '0', 671690603, 20, '0.3689897543711138,0.8177009500304009,0.001,0.15639015574557935,2.0', 1, 'COMPLETE', 'HIGH', 'ff54d947-54f8-41f0-af64-fc3c67e96dbf', 0, 2, -9, 0, 'UNSAT', 817700.9500304009, 368989.7543711138, 671690603, 1, 1.084, '', '2014-09-27 09:20:59', '2014-09-27 09:20:31', '2014-09-27 16:18:31'),
(36, '4572f74ab78079415d3df6c49c0016f6fc6ca342', 1, 'TestInstance', '0', 3775537264, 20, '0.9332290050766966,0.100482024948223,0.001,0.8790607714608636,1.0', 1, 'COMPLETE', 'HIGH', '3104655b-7926-45f6-a31e-ba9c5130b010', 0, 2, -9, 0, 'SAT', 100482.024948223, 933229.0050766966, 3775537264, 1, 1.091, '', '2014-09-27 09:21:00', '2014-09-27 09:20:32', '2014-09-27 16:18:32'),
(37, 'fdacaae07c8715d24a4133873a2f0540547e1e7b', 1, 'TestInstance', '0', 288438966, 20, '0.08122700443192654,0.6884923875138547,0.001,0.06715743044224627,1.0', 1, 'COMPLETE', 'HIGH', '096dc6ad-4665-412e-8b93-d92b3ebb74bb', 0, 2, -9, 0, 'SAT', 688492.3875138547, 81227.00443192654, 288438966, 1, 1.084, '', '2014-09-27 09:21:00', '2014-09-27 09:20:32', '2014-09-27 16:18:32'),
(38, 'e29cbbc7627c25c880d114f595a6502bf04d5f78', 1, 'TestInstance', '0', 55928477, 20, '0.11655700721684881,0.16188803320698997,0.001,0.01302186364470472,1.0', 1, 'COMPLETE', 'HIGH', 'ff54d947-54f8-41f0-af64-fc3c67e96dbf', 0, 2, -9, 0, 'SAT', 161888.03320698996, 116557.00721684881, 55928477, 1, 1.082, '', '2014-09-27 09:21:00', '2014-09-27 09:20:32', '2014-09-27 16:18:32'),
(39, '865572d49e0405e26abc46a0e18940c6ae6119bd', 1, 'TestInstance', '0', 554410623, 20, '0.1282077912883477,0.9607895195631704,0.001,0.12908378252082417,1.0', 1, 'COMPLETE', 'HIGH', '3104655b-7926-45f6-a31e-ba9c5130b010', 0, 2, -9, 0, 'SAT', 960789.5195631704, 128207.7912883477, 554410623, 1, 1.083, '', '2014-09-27 09:21:01', '2014-09-27 09:20:33', '2014-09-27 16:18:33'),
(40, '37166db22ec5be050fda20b77b5bcbf621e86937', 1, 'TestInstance', '0', 210906299, 20, '0.2866493359995804,0.7180376675314928,0.001,0.049105449684638196,2.0', 1, 'COMPLETE', 'HIGH', 'da25d15f-2083-4369-9eb5-1a75f78f4f5d', 0, 1, -9, 0, 'UNSAT', 718037.6675314928, 286649.33599958045, 210906299, 1, 1.098, '', '2014-09-27 09:20:52', '2014-09-27 09:20:24', '2014-09-27 16:18:24');

-- --------------------------------------------------------

--
-- Table structure for table `junit_JobPushBackTest_version`
--

CREATE TABLE IF NOT EXISTS `junit_JobPushBackTest_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` varchar(255) NOT NULL,
  `hash` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash` (`hash`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=7 ;

--
-- Dumping data for table `junit_JobPushBackTest_version`
--

INSERT INTO `junit_JobPushBackTest_version` (`id`, `version`, `hash`) VALUES
(1, 'v0.92.00dev-development-112 (8a5109587631)', '84ed1641e09245a2869dfe555e546f1bfc369c1c');

-- --------------------------------------------------------

--
-- Table structure for table `junit_JobPushBackTest_workers`
--

CREATE TABLE IF NOT EXISTS `junit_JobPushBackTest_workers` (
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

--
-- Dumping data for table `junit_JobPushBackTest_workers`
--

INSERT INTO `junit_JobPushBackTest_workers` (`workerUUID`, `hostname`, `username`, `jobID`, `status`, `version`, `crashInfo`, `startTime`, `startWeekYear`, `originalEndTime`, `currentlyIdle`, `endTime_UPDATEABLE`, `runsToBatch_UPDATEABLE`, `minRunsToBatch_UPDATEABLE`, `maxRunsToBatch_UPDATEABLE`, `autoAdjustBatchSize_UPDATEABLE`, `delayBetweenRequests_UPDATEABLE`, `pool_UPDATEABLE`, `poolIdleTimeLimit_UPDATEABLE`, `workerIdleTime_UPDATEABLE`, `concurrencyFactor_UPDATEABLE`, `upToDate`, `worstCaseNextUpdateWhenRunning`, `lastModified`) VALUES
('096dc6ad-4665-412e-8b93-d92b3ebb74bb', 'hilbert', 'sjr', 'CLI/20627@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Triggered By Shutdown Hook (probably shutting down due to SIGTERM or SIGINT)', '2014-09-27 09:18:20', 392014, '2014-09-27 09:23:19', 1, '2014-09-27 09:23:19', 1, 1, 100, 1, 1, 'junit_JobPushBackTest', 14400000, 2, 4, 1, '2014-09-27 09:18:36', '2014-09-27 16:18:36'),
('3104655b-7926-45f6-a31e-ba9c5130b010', 'hilbert', 'sjr', 'CLI/20609@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Triggered By Shutdown Hook (probably shutting down due to SIGTERM or SIGINT)', '2014-09-27 09:18:20', 392014, '2014-09-27 09:23:19', 1, '2014-09-27 09:23:19', 1, 1, 100, 1, 1, 'junit_JobPushBackTest', 14400000, 3, 4, 1, '2014-09-27 09:18:36', '2014-09-27 16:18:36'),
('43a5b1c0-020b-4714-bc6e-419f204dc3c2', 'hilbert', 'sjr', 'CLI/20620@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Triggered By Shutdown Hook (probably shutting down due to SIGTERM or SIGINT)', '2014-09-27 09:18:20', 392014, '2014-09-27 09:23:19', 1, '2014-09-27 09:23:19', 1, 1, 100, 1, 1, 'junit_JobPushBackTest', 14400000, 1, 4, 1, '2014-09-27 09:18:36', '2014-09-27 16:18:36'),
('da25d15f-2083-4369-9eb5-1a75f78f4f5d', 'hilbert', 'sjr', 'CLI/20605@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Triggered By Shutdown Hook (probably shutting down due to SIGTERM or SIGINT)', '2014-09-27 09:18:20', 392014, '2014-09-27 09:23:19', 1, '2014-09-27 09:23:19', 1, 1, 100, 1, 1, 'junit_JobPushBackTest', 14400000, 1, 4, 1, '2014-09-27 09:18:36', '2014-09-27 16:18:36'),
('ff54d947-54f8-41f0-af64-fc3c67e96dbf', 'hilbert', 'sjr', 'CLI/20614@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Triggered By Shutdown Hook (probably shutting down due to SIGTERM or SIGINT)', '2014-09-27 09:18:20', 392014, '2014-09-27 09:23:18', 1, '2014-09-27 09:23:18', 1, 1, 100, 1, 1, 'junit_JobPushBackTest', 14400000, 2, 4, 1, '2014-09-27 09:18:36', '2014-09-27 16:18:36');

-- --------------------------------------------------------

--
-- Table structure for table `junit_JobReclaimTest_commandTable`
--

CREATE TABLE IF NOT EXISTS `junit_JobReclaimTest_commandTable` (
  `commandID` int(11) NOT NULL AUTO_INCREMENT,
  `commandString` text NOT NULL,
  PRIMARY KEY (`commandID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Dumping data for table `junit_JobReclaimTest_commandTable`
--

INSERT INTO `junit_JobReclaimTest_commandTable` (`commandID`, `commandString`) VALUES
(1, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite'),
(2, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite');

-- --------------------------------------------------------

--
-- Table structure for table `junit_JobReclaimTest_execConfig`
--

CREATE TABLE IF NOT EXISTS `junit_JobReclaimTest_execConfig` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Dumping data for table `junit_JobReclaimTest_execConfig`
--

INSERT INTO `junit_JobReclaimTest_execConfig` (`algorithmExecutionConfigID`, `algorithmExecutionConfigHashCode`, `algorithmExecutable`, `algorithmExecutableDirectory`, `parameterFile`, `executeOnCluster`, `deterministicAlgorithm`, `cutoffTime`, `algorithmExecutionConfigurationJSON`, `lastModified`) VALUES
(1, '2625865e6eafb0a4fa16ff33c3ac6d6469acb9eb', 'java -cp /home/sjr/git/MySQLDBTAE/bin:/home/sjr/git/AEATK/bin:/home/sjr/git/AEATK/lib/commons-collections-3.2.1.jar:/home/sjr/git/AEATK/lib/commons-math-2.2.jar:/home/sjr/git/AEATK/lib/opencsv-2.3.jar:/home/sjr/git/AEATK/lib/spi-0.2.4.jar:/home/sjr/git/AEATK/lib/jcip-annotations.jar:/home/sjr/git/AEATK/lib/Jama-1.0.2.jar:/home/sjr/git/AEATK/lib/numerics4j-1.3.jar:/home/sjr/git/AEATK/lib/guava-14.0.1.jar:/home/sjr/git/AEATK/lib/exp4j-0.3.10.jar:/home/sjr/git/AEATK/lib/commons-io-2.1.jar:/home/sjr/git/AEATK/lib/slf4j-api-1.7.5.jar:/home/sjr/git/AEATK/lib/jackson-annotations-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-core-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-databind-2.3.1.jar:/home/sjr/git/AEATK/lib/logback-access-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-classic-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-core-1.1.2.jar:/home/sjr/git/RandomForests/bin:/opt/eclipse/plugins/org.junit_4.11.0.v201303080030/junit.jar:/opt/eclipse/plugins/org.hamcrest.core_1.3.0.v201303031735.jar:/home/sjr/git/AEATK/lib/jcommander.jar:/home/sjr/git/AEATK/lib/commons-math3-3.3.jar:/home/sjr/git/MySQLDBTAE/lib/commons-codec-1.7.jar:/home/sjr/git/MySQLDBTAE/lib/mchange-commons-java-0.2.3.3.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-0.9.2-pre8.jar:/home/sjr/git/MySQLDBTAE/lib/mysql-connector-java-5.1.26-bin.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-oracle-thin-extras-0.9.2-pre8.jar:/opt/eclipse/configuration/org.eclipse.osgi/bundles/164/1/.cp/:/opt/eclipse/configuration/org.eclipse.osgi/bundles/163/1/.cp/ ca.ubc.cs.beta.targetalgorithmevaluator.TrueSleepyParamEchoExecutor', '/home/sjr/git/MySQLDBTAE', '/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt', 0, 0, 500, '{"@algo-exec-config-id":2,"algo-exec":"java -cp /home/sjr/git/MySQLDBTAE/bin:/home/sjr/git/AEATK/bin:/home/sjr/git/AEATK/lib/commons-collections-3.2.1.jar:/home/sjr/git/AEATK/lib/commons-math-2.2.jar:/home/sjr/git/AEATK/lib/opencsv-2.3.jar:/home/sjr/git/AEATK/lib/spi-0.2.4.jar:/home/sjr/git/AEATK/lib/jcip-annotations.jar:/home/sjr/git/AEATK/lib/Jama-1.0.2.jar:/home/sjr/git/AEATK/lib/numerics4j-1.3.jar:/home/sjr/git/AEATK/lib/guava-14.0.1.jar:/home/sjr/git/AEATK/lib/exp4j-0.3.10.jar:/home/sjr/git/AEATK/lib/commons-io-2.1.jar:/home/sjr/git/AEATK/lib/slf4j-api-1.7.5.jar:/home/sjr/git/AEATK/lib/jackson-annotations-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-core-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-databind-2.3.1.jar:/home/sjr/git/AEATK/lib/logback-access-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-classic-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-core-1.1.2.jar:/home/sjr/git/RandomForests/bin:/opt/eclipse/plugins/org.junit_4.11.0.v201303080030/junit.jar:/opt/eclipse/plugins/org.hamcrest.core_1.3.0.v201303031735.jar:/home/sjr/git/AEATK/lib/jcommander.jar:/home/sjr/git/AEATK/lib/commons-math3-3.3.jar:/home/sjr/git/MySQLDBTAE/lib/commons-codec-1.7.jar:/home/sjr/git/MySQLDBTAE/lib/mchange-commons-java-0.2.3.3.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-0.9.2-pre8.jar:/home/sjr/git/MySQLDBTAE/lib/mysql-connector-java-5.1.26-bin.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-oracle-thin-extras-0.9.2-pre8.jar:/opt/eclipse/configuration/org.eclipse.osgi/bundles/164/1/.cp/:/opt/eclipse/configuration/org.eclipse.osgi/bundles/163/1/.cp/ ca.ubc.cs.beta.targetalgorithmevaluator.TrueSleepyParamEchoExecutor","algo-exec-dir":"/home/sjr/git/MySQLDBTAE","algo-pcs":{"@pcs-id":2,"pcs-filename":"/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt","pcs-text":"#Used for getting specific results out from a target wrapper\\n#Some invalid values are specified so that we can test code\\n\\nsolved { SAT, UNSAT, TIMEOUT, CRASHED, ABORT, INVALID } [SAT]\\nruntime [0,1000] [0]\\nrunlength [0,1000000][0]\\nquality [0, 1000000] [0]\\nseed [ -1,4294967295][1]i\\n","pcs-subspace":{}},"algo-cutoff":500.0,"algo-deterministic":false,"algo-tae-context":{}}', '2014-09-27 16:19:01');

-- --------------------------------------------------------

--
-- Table structure for table `junit_JobReclaimTest_runConfigs`
--

CREATE TABLE IF NOT EXISTS `junit_JobReclaimTest_runConfigs` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=6 ;

--
-- Dumping data for table `junit_JobReclaimTest_runConfigs`
--

INSERT INTO `junit_JobReclaimTest_runConfigs` (`runConfigID`, `runConfigUUID`, `execConfigID`, `problemInstance`, `instanceSpecificInformation`, `seed`, `cutoffTime`, `paramConfiguration`, `cutoffLessThanMax`, `status`, `priority`, `workerUUID`, `killJob`, `retryAttempts`, `runPartition`, `noop`, `runResult`, `runLength`, `quality`, `resultSeed`, `runtime`, `walltime`, `additionalRunData`, `worstCaseEndtime`, `worstCaseNextUpdateWhenAssigned`, `lastModified`) VALUES
(1, '43cac3c0383221756d17244a84a170f4c0045b1c', 1, 'TestInstance', '0', 637052920, 20, '0.015976940738139156,0.2469173032790115,0.001,0.14832544171988837,1.0', 1, 'COMPLETE', 'HIGH', '96e715e1-0052-4b8a-adec-f87e5bdbfaac', 0, 1, -9, 0, 'SAT', 246917.3032790115, 15976.940738139156, 637052920, 1, 1.086, '', '2014-09-27 09:21:31', '2014-09-27 09:19:06', '2014-09-27 16:19:03'),
(2, 'c28227250045872b39877f2ab3bd9d5ea8ea48bc', 1, 'TestInstance', '0', 1112078603, 20, '0.05032567565962087,0.5525706102761133,0.001,0.25892597721914623,1.0', 1, 'COMPLETE', 'HIGH', '96e715e1-0052-4b8a-adec-f87e5bdbfaac', 0, 1, -9, 0, 'SAT', 552570.6102761133, 50325.67565962087, 1112078603, 1, 1.084, '', '2014-09-27 09:21:32', '2014-09-27 09:19:07', '2014-09-27 16:19:04'),
(3, 'd2701258cc8aa5f312cc4db9ebf1ea69a368bd61', 1, 'TestInstance', '0', 4248476791, 20, '0.15727574991182636,0.512924294706756,0.001,0.9891755859159921,1.0', 1, 'COMPLETE', 'HIGH', '96e715e1-0052-4b8a-adec-f87e5bdbfaac', 0, 1, -9, 0, 'SAT', 512924.2947067561, 157275.74991182637, 4248476791, 1, 1.083, '', '2014-09-27 09:21:33', '2014-09-27 09:19:08', '2014-09-27 16:19:05'),
(4, 'b4e52b1d68e0fe1095741ed0ee0fc1cea402924b', 1, 'TestInstance', '0', 740891526, 20, '0.5627148961318166,0.691364702053807,0.001,0.17250225118535983,1.0', 1, 'ASSIGNED', 'HIGH', '96e715e1-0052-4b8a-adec-f87e5bdbfaac', 0, 1, -9, 0, 'ABORT', 0, 0, 1, 0, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:09:05', '2014-09-27 16:19:05'),
(5, '7beec7eaf4f2ea4cde95bca5e9f9d0a0de70d9ba', 1, 'TestInstance', '0', 3353012586, 20, '0.4264906278695877,0.6553155077832885,0.001,0.7806840787453847,2.0', 1, 'ASSIGNED', 'HIGH', '96e715e1-0052-4b8a-adec-f87e5bdbfaac', 0, 1, -9, 0, 'ABORT', 0, 0, 1, 0, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:09:05', '2014-09-27 16:19:05');

-- --------------------------------------------------------

--
-- Table structure for table `junit_JobReclaimTest_version`
--

CREATE TABLE IF NOT EXISTS `junit_JobReclaimTest_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` varchar(255) NOT NULL,
  `hash` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash` (`hash`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=7 ;

--
-- Dumping data for table `junit_JobReclaimTest_version`
--

INSERT INTO `junit_JobReclaimTest_version` (`id`, `version`, `hash`) VALUES
(1, 'v0.92.00dev-development-112 (8a5109587631)', '84ed1641e09245a2869dfe555e546f1bfc369c1c');

-- --------------------------------------------------------

--
-- Table structure for table `junit_JobReclaimTest_workers`
--

CREATE TABLE IF NOT EXISTS `junit_JobReclaimTest_workers` (
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

--
-- Dumping data for table `junit_JobReclaimTest_workers`
--

INSERT INTO `junit_JobReclaimTest_workers` (`workerUUID`, `hostname`, `username`, `jobID`, `status`, `version`, `crashInfo`, `startTime`, `startWeekYear`, `originalEndTime`, `currentlyIdle`, `endTime_UPDATEABLE`, `runsToBatch_UPDATEABLE`, `minRunsToBatch_UPDATEABLE`, `maxRunsToBatch_UPDATEABLE`, `autoAdjustBatchSize_UPDATEABLE`, `delayBetweenRequests_UPDATEABLE`, `pool_UPDATEABLE`, `poolIdleTimeLimit_UPDATEABLE`, `workerIdleTime_UPDATEABLE`, `concurrencyFactor_UPDATEABLE`, `upToDate`, `worstCaseNextUpdateWhenRunning`, `lastModified`) VALUES
('95e30a55-76fe-4713-8152-b398c27ce2b0', 'hilbert', 'sjr', 'CLI/21957@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Idle limit reached: 11.0 versus limit: 10', '2014-09-27 09:19:06', 392014, '2014-09-27 09:20:06', 1, '2014-09-27 09:20:06', 1, 1, 100, 1, 1, 'junit_JobReclaimTest', 14400000, 10, 4, 1, '2014-09-27 09:19:18', '2014-09-27 16:19:18'),
('96e715e1-0052-4b8a-adec-f87e5bdbfaac', 'hilbert', 'sjr', 'CLI/21840@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Missed worstcase end time checkin', '2014-09-27 09:19:01', 392014, '2014-09-27 09:20:01', 0, '2014-09-27 09:20:01', 5, 1, 100, 1, 1, 'junit_JobReclaimTest', 14400000, 0, 4, 1, '2014-09-27 09:19:47', '2014-09-27 16:19:48'),
('b6abc488-ba5e-4b12-aa1c-a720d227ae61', 'hilbert', 'sjr', 'CLI/21780@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Idle limit reached: 11.0 versus limit: 10', '2014-09-27 09:18:41', 392014, '2014-09-27 09:19:41', 1, '2014-09-27 09:19:41', 1, 1, 100, 1, 1, 'junit_JobReclaimTest', 14400000, 10, 4, 1, '2014-09-27 09:18:53', '2014-09-27 16:18:53'),
('c33e66ef-f0ee-4768-af47-1f08a79cdfdd', 'hilbert', 'sjr', 'CLI/21654@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Missed worstcase end time checkin', '2014-09-27 09:18:36', 392014, '2014-09-27 09:19:36', 0, '2014-09-27 09:19:36', 5, 1, 100, 1, 1, 'junit_JobReclaimTest', 14400000, 0, 4, 1, '2014-09-27 09:19:22', '2014-09-27 16:19:23');

-- --------------------------------------------------------

--
-- Table structure for table `junit_killtest_commandTable`
--

CREATE TABLE IF NOT EXISTS `junit_killtest_commandTable` (
  `commandID` int(11) NOT NULL AUTO_INCREMENT,
  `commandString` text NOT NULL,
  PRIMARY KEY (`commandID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=9 ;

--
-- Dumping data for table `junit_killtest_commandTable`
--

INSERT INTO `junit_killtest_commandTable` (`commandID`, `commandString`) VALUES
(1, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 60125 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -test ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTAEKillRetryTester:testKillRetry'),
(2, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 45216 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -test ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTAEKillRetryTester:testKillRetry'),
(3, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 57773 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTAEKillRetryTester'),
(4, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 54097 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -test ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTAEKillRetryTester:testKillRetry'),
(5, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 44742 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -test ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTAEKillRetryTester:testKillRetry'),
(6, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite'),
(7, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite'),
(8, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite');

-- --------------------------------------------------------

--
-- Table structure for table `junit_killtest_execConfig`
--

CREATE TABLE IF NOT EXISTS `junit_killtest_execConfig` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

--
-- Dumping data for table `junit_killtest_execConfig`
--

INSERT INTO `junit_killtest_execConfig` (`algorithmExecutionConfigID`, `algorithmExecutionConfigHashCode`, `algorithmExecutable`, `algorithmExecutableDirectory`, `parameterFile`, `executeOnCluster`, `deterministicAlgorithm`, `cutoffTime`, `algorithmExecutionConfigurationJSON`, `lastModified`) VALUES
(1, '8584a65f79f8188e4386d5bad380490fbc7f66d7', 'java -cp /home/sjr/git/MySQLDBTAE/bin:/home/sjr/git/AEATK/bin:/home/sjr/git/AEATK/lib/commons-collections-3.2.1.jar:/home/sjr/git/AEATK/lib/commons-math-2.2.jar:/home/sjr/git/AEATK/lib/opencsv-2.3.jar:/home/sjr/git/AEATK/lib/spi-0.2.4.jar:/home/sjr/git/AEATK/lib/jcip-annotations.jar:/home/sjr/git/AEATK/lib/Jama-1.0.2.jar:/home/sjr/git/AEATK/lib/numerics4j-1.3.jar:/home/sjr/git/AEATK/lib/guava-14.0.1.jar:/home/sjr/git/AEATK/lib/exp4j-0.3.10.jar:/home/sjr/git/AEATK/lib/commons-io-2.1.jar:/home/sjr/git/AEATK/lib/slf4j-api-1.7.5.jar:/home/sjr/git/AEATK/lib/jackson-annotations-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-core-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-databind-2.3.1.jar:/home/sjr/git/AEATK/lib/logback-access-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-classic-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-core-1.1.2.jar:/home/sjr/git/RandomForests/bin:/opt/eclipse/plugins/org.junit_4.11.0.v201303080030/junit.jar:/opt/eclipse/plugins/org.hamcrest.core_1.3.0.v201303031735.jar:/home/sjr/git/AEATK/lib/jcommander.jar:/home/sjr/git/AEATK/lib/commons-math3-3.3.jar:/home/sjr/git/MySQLDBTAE/lib/commons-codec-1.7.jar:/home/sjr/git/MySQLDBTAE/lib/mchange-commons-java-0.2.3.3.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-0.9.2-pre8.jar:/home/sjr/git/MySQLDBTAE/lib/mysql-connector-java-5.1.26-bin.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-oracle-thin-extras-0.9.2-pre8.jar:/opt/eclipse/configuration/org.eclipse.osgi/bundles/164/1/.cp/:/opt/eclipse/configuration/org.eclipse.osgi/bundles/163/1/.cp/ ca.ubc.cs.beta.targetalgorithmevaluator.ParamEchoExecutor', '/home/sjr/git/MySQLDBTAE', '/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt', 0, 0, 500, '{"@algo-exec-config-id":2,"algo-exec":"java -cp /home/sjr/git/MySQLDBTAE/bin:/home/sjr/git/AEATK/bin:/home/sjr/git/AEATK/lib/commons-collections-3.2.1.jar:/home/sjr/git/AEATK/lib/commons-math-2.2.jar:/home/sjr/git/AEATK/lib/opencsv-2.3.jar:/home/sjr/git/AEATK/lib/spi-0.2.4.jar:/home/sjr/git/AEATK/lib/jcip-annotations.jar:/home/sjr/git/AEATK/lib/Jama-1.0.2.jar:/home/sjr/git/AEATK/lib/numerics4j-1.3.jar:/home/sjr/git/AEATK/lib/guava-14.0.1.jar:/home/sjr/git/AEATK/lib/exp4j-0.3.10.jar:/home/sjr/git/AEATK/lib/commons-io-2.1.jar:/home/sjr/git/AEATK/lib/slf4j-api-1.7.5.jar:/home/sjr/git/AEATK/lib/jackson-annotations-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-core-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-databind-2.3.1.jar:/home/sjr/git/AEATK/lib/logback-access-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-classic-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-core-1.1.2.jar:/home/sjr/git/RandomForests/bin:/opt/eclipse/plugins/org.junit_4.11.0.v201303080030/junit.jar:/opt/eclipse/plugins/org.hamcrest.core_1.3.0.v201303031735.jar:/home/sjr/git/AEATK/lib/jcommander.jar:/home/sjr/git/AEATK/lib/commons-math3-3.3.jar:/home/sjr/git/MySQLDBTAE/lib/commons-codec-1.7.jar:/home/sjr/git/MySQLDBTAE/lib/mchange-commons-java-0.2.3.3.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-0.9.2-pre8.jar:/home/sjr/git/MySQLDBTAE/lib/mysql-connector-java-5.1.26-bin.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-oracle-thin-extras-0.9.2-pre8.jar:/opt/eclipse/configuration/org.eclipse.osgi/bundles/164/1/.cp/:/opt/eclipse/configuration/org.eclipse.osgi/bundles/163/1/.cp/ ca.ubc.cs.beta.targetalgorithmevaluator.ParamEchoExecutor","algo-exec-dir":"/home/sjr/git/MySQLDBTAE","algo-pcs":{"@pcs-id":2,"pcs-filename":"/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt","pcs-text":"#Used for getting specific results out from a target wrapper\\n#Some invalid values are specified so that we can test code\\n\\nsolved { SAT, UNSAT, TIMEOUT, CRASHED, ABORT, INVALID } [SAT]\\nruntime [0,1000] [0]\\nrunlength [0,1000000][0]\\nquality [0, 1000000] [0]\\nseed [ -1,4294967295][1]i\\n","pcs-subspace":{}},"algo-cutoff":500.0,"algo-deterministic":false,"algo-tae-context":{}}', '2014-09-27 16:19:26'),
(3, '57922f4090e9e5a9a7633f2e0898e81b400a02e7', 'ignore', '/home/sjr/git/MySQLDBTAE', '/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt', 0, 0, 500, '{"@algo-exec-config-id":2,"algo-exec":"ignore","algo-exec-dir":"/home/sjr/git/MySQLDBTAE","algo-pcs":{"@pcs-id":2,"pcs-filename":"/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt","pcs-text":"#Used for getting specific results out from a target wrapper\\n#Some invalid values are specified so that we can test code\\n\\nsolved { SAT, UNSAT, TIMEOUT, CRASHED, ABORT, INVALID } [SAT]\\nruntime [0,1000] [0]\\nrunlength [0,1000000][0]\\nquality [0, 1000000] [0]\\nseed [ -1,4294967295][1]i\\n","pcs-subspace":{}},"algo-cutoff":500.0,"algo-deterministic":false,"algo-tae-context":{}}', '2014-09-27 16:21:46');

-- --------------------------------------------------------

--
-- Table structure for table `junit_killtest_runConfigs`
--

CREATE TABLE IF NOT EXISTS `junit_killtest_runConfigs` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=7 ;

-- --------------------------------------------------------

--
-- Table structure for table `junit_killtest_version`
--

CREATE TABLE IF NOT EXISTS `junit_killtest_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` varchar(255) NOT NULL,
  `hash` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash` (`hash`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=8 ;

--
-- Dumping data for table `junit_killtest_version`
--

INSERT INTO `junit_killtest_version` (`id`, `version`, `hash`) VALUES
(1, 'v0.92.00dev-development-112 (8a5109587631)', '84ed1641e09245a2869dfe555e546f1bfc369c1c');

-- --------------------------------------------------------

--
-- Table structure for table `junit_killtest_workers`
--

CREATE TABLE IF NOT EXISTS `junit_killtest_workers` (
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

--
-- Dumping data for table `junit_killtest_workers`
--

INSERT INTO `junit_killtest_workers` (`workerUUID`, `hostname`, `username`, `jobID`, `status`, `version`, `crashInfo`, `startTime`, `startWeekYear`, `originalEndTime`, `currentlyIdle`, `endTime_UPDATEABLE`, `runsToBatch_UPDATEABLE`, `minRunsToBatch_UPDATEABLE`, `maxRunsToBatch_UPDATEABLE`, `autoAdjustBatchSize_UPDATEABLE`, `delayBetweenRequests_UPDATEABLE`, `pool_UPDATEABLE`, `poolIdleTimeLimit_UPDATEABLE`, `workerIdleTime_UPDATEABLE`, `concurrencyFactor_UPDATEABLE`, `upToDate`, `worstCaseNextUpdateWhenRunning`, `lastModified`) VALUES
('0211317a-31b9-4a92-ac9b-171aedeffba1', 'hilbert', 'sjr', 'CLI/23244@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Triggered By Shutdown Hook (probably shutting down due to SIGTERM or SIGINT)', '2014-09-27 09:21:43', 392014, '2014-09-28 09:20:42', 0, '2014-09-27 09:21:45', 1, 1, 100, 1, 240, 'junit_killtest', 14400000, 0, 4, 0, '2014-09-27 09:21:45', '2014-09-27 16:21:45'),
('1cc12fa9-f156-4cd4-b51a-2e738176b6d5', 'hilbert', 'sjr', 'CLI/22023@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Idle limit reached: 61.0 versus limit: 60', '2014-09-27 09:19:26', 392014, '2014-09-28 09:18:26', 1, '2014-09-28 09:18:26', 1, 1, 100, 1, 1, 'junit_killtest', 14400000, 69, 4, 1, '2014-09-27 09:20:46', '2014-09-27 16:20:46'),
('276e1a92-9032-43ca-9da3-7b6fc5f6398a', 'hilbert', 'sjr', 'CLI/23292@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Triggered By Shutdown Hook (probably shutting down due to SIGTERM or SIGINT)', '2014-09-27 09:21:46', 392014, '2014-09-28 09:20:46', 0, '2014-09-27 09:21:48', 1, 1, 100, 1, 240, 'junit_killtest', 14400000, 0, 4, 0, '2014-09-27 09:21:48', '2014-09-27 16:21:48'),
('48549749-d19c-4971-a379-fe2d5a363d8b', 'hilbert', 'sjr', 'CLI/7454@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Idle limit reached: 61.0 versus limit: 60', '2014-09-26 18:19:55', 392014, '2014-09-27 18:18:54', 1, '2014-09-27 18:18:54', 1, 1, 100, 1, 1, 'junit_killtest', 14400000, 66, 4, 1, '2014-09-26 18:20:57', '2014-09-27 01:20:57'),
('847e21f0-d884-44cc-a94f-67d14c265604', 'hilbert', 'sjr', 'CLI/5856@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Idle limit reached: 61.0 versus limit: 60', '2014-09-26 17:48:48', 392014, '2014-09-27 17:47:48', 1, '2014-09-27 17:47:48', 1, 1, 100, 1, 1, 'junit_killtest', 14400000, 68, 4, 1, '2014-09-26 17:50:08', '2014-09-27 00:50:08'),
('85a41ec1-24e5-453d-8100-4f3a3bc0721b', 'hilbert', 'sjr', 'CLI/7790@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Idle limit reached: 61.0 versus limit: 60', '2014-09-26 18:31:37', 392014, '2014-09-27 18:30:37', 1, '2014-09-27 18:30:37', 1, 1, 100, 1, 1, 'junit_killtest', 14400000, 67, 4, 1, '2014-09-26 18:32:50', '2014-09-27 01:32:50'),
('8c4a29a3-ce04-4978-8633-b85f273dd47c', 'hilbert', 'sjr', 'CLI/5793@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Idle limit reached: 61.0 versus limit: 60', '2014-09-26 17:46:58', 392014, '2014-09-27 17:43:14', 1, '2014-09-27 17:43:14', 1, 1, 100, 1, 1, 'junit_killtest', 14400000, 66, 4, 1, '2014-09-26 17:50:43', '2014-09-27 00:50:43'),
('c87c9788-715b-4487-a7b6-c05e85596dc6', 'hilbert', 'sjr', 'CLI/7315@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Idle limit reached: 61.0 versus limit: 60', '2014-09-26 18:19:04', 392014, '2014-09-27 18:18:04', 1, '2014-09-27 18:18:04', 1, 1, 100, 1, 1, 'junit_killtest', 14400000, 112, 4, 1, '2014-09-26 18:21:08', '2014-09-27 01:21:08'),
('ef791610-a05d-4afb-9480-ed02829659c2', 'hilbert', 'sjr', 'CLI/13878@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Idle limit reached: 61.0 versus limit: 60', '2014-09-26 22:38:33', 392014, '2014-09-27 22:37:33', 1, '2014-09-27 22:37:33', 1, 1, 100, 1, 1, 'junit_killtest', 14400000, 65, 4, 1, '2014-09-26 22:39:45', '2014-09-27 05:39:45');

-- --------------------------------------------------------

--
-- Table structure for table `junit_kill_single_run_commandTable`
--

CREATE TABLE IF NOT EXISTS `junit_kill_single_run_commandTable` (
  `commandID` int(11) NOT NULL AUTO_INCREMENT,
  `commandString` text NOT NULL,
  PRIMARY KEY (`commandID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `junit_kill_single_run_commandTable`
--

INSERT INTO `junit_kill_single_run_commandTable` (`commandID`, `commandString`) VALUES
(1, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite');

-- --------------------------------------------------------

--
-- Table structure for table `junit_kill_single_run_execConfig`
--

CREATE TABLE IF NOT EXISTS `junit_kill_single_run_execConfig` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `junit_kill_single_run_execConfig`
--

INSERT INTO `junit_kill_single_run_execConfig` (`algorithmExecutionConfigID`, `algorithmExecutionConfigHashCode`, `algorithmExecutable`, `algorithmExecutableDirectory`, `parameterFile`, `executeOnCluster`, `deterministicAlgorithm`, `cutoffTime`, `algorithmExecutionConfigurationJSON`, `lastModified`) VALUES
(1, '9823afd624fe4ccba80e314dae4a919eace39e34', 'java -cp /home/sjr/git/MySQLDBTAE/bin:/home/sjr/git/AEATK/bin:/home/sjr/git/AEATK/lib/commons-collections-3.2.1.jar:/home/sjr/git/AEATK/lib/commons-math-2.2.jar:/home/sjr/git/AEATK/lib/opencsv-2.3.jar:/home/sjr/git/AEATK/lib/spi-0.2.4.jar:/home/sjr/git/AEATK/lib/jcip-annotations.jar:/home/sjr/git/AEATK/lib/Jama-1.0.2.jar:/home/sjr/git/AEATK/lib/numerics4j-1.3.jar:/home/sjr/git/AEATK/lib/guava-14.0.1.jar:/home/sjr/git/AEATK/lib/exp4j-0.3.10.jar:/home/sjr/git/AEATK/lib/commons-io-2.1.jar:/home/sjr/git/AEATK/lib/slf4j-api-1.7.5.jar:/home/sjr/git/AEATK/lib/jackson-annotations-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-core-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-databind-2.3.1.jar:/home/sjr/git/AEATK/lib/logback-access-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-classic-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-core-1.1.2.jar:/home/sjr/git/RandomForests/bin:/opt/eclipse/plugins/org.junit_4.11.0.v201303080030/junit.jar:/opt/eclipse/plugins/org.hamcrest.core_1.3.0.v201303031735.jar:/home/sjr/git/AEATK/lib/jcommander.jar:/home/sjr/git/AEATK/lib/commons-math3-3.3.jar:/home/sjr/git/MySQLDBTAE/lib/commons-codec-1.7.jar:/home/sjr/git/MySQLDBTAE/lib/mchange-commons-java-0.2.3.3.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-0.9.2-pre8.jar:/home/sjr/git/MySQLDBTAE/lib/mysql-connector-java-5.1.26-bin.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-oracle-thin-extras-0.9.2-pre8.jar:/opt/eclipse/configuration/org.eclipse.osgi/bundles/164/1/.cp/:/opt/eclipse/configuration/org.eclipse.osgi/bundles/163/1/.cp/ ca.ubc.cs.beta.targetalgorithmevaluator.TrueSleepyParamEchoExecutor', '/home/sjr/git/MySQLDBTAE', '/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt', 0, 0, 0.01, '{"@algo-exec-config-id":2,"algo-exec":"java -cp /home/sjr/git/MySQLDBTAE/bin:/home/sjr/git/AEATK/bin:/home/sjr/git/AEATK/lib/commons-collections-3.2.1.jar:/home/sjr/git/AEATK/lib/commons-math-2.2.jar:/home/sjr/git/AEATK/lib/opencsv-2.3.jar:/home/sjr/git/AEATK/lib/spi-0.2.4.jar:/home/sjr/git/AEATK/lib/jcip-annotations.jar:/home/sjr/git/AEATK/lib/Jama-1.0.2.jar:/home/sjr/git/AEATK/lib/numerics4j-1.3.jar:/home/sjr/git/AEATK/lib/guava-14.0.1.jar:/home/sjr/git/AEATK/lib/exp4j-0.3.10.jar:/home/sjr/git/AEATK/lib/commons-io-2.1.jar:/home/sjr/git/AEATK/lib/slf4j-api-1.7.5.jar:/home/sjr/git/AEATK/lib/jackson-annotations-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-core-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-databind-2.3.1.jar:/home/sjr/git/AEATK/lib/logback-access-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-classic-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-core-1.1.2.jar:/home/sjr/git/RandomForests/bin:/opt/eclipse/plugins/org.junit_4.11.0.v201303080030/junit.jar:/opt/eclipse/plugins/org.hamcrest.core_1.3.0.v201303031735.jar:/home/sjr/git/AEATK/lib/jcommander.jar:/home/sjr/git/AEATK/lib/commons-math3-3.3.jar:/home/sjr/git/MySQLDBTAE/lib/commons-codec-1.7.jar:/home/sjr/git/MySQLDBTAE/lib/mchange-commons-java-0.2.3.3.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-0.9.2-pre8.jar:/home/sjr/git/MySQLDBTAE/lib/mysql-connector-java-5.1.26-bin.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-oracle-thin-extras-0.9.2-pre8.jar:/opt/eclipse/configuration/org.eclipse.osgi/bundles/164/1/.cp/:/opt/eclipse/configuration/org.eclipse.osgi/bundles/163/1/.cp/ ca.ubc.cs.beta.targetalgorithmevaluator.TrueSleepyParamEchoExecutor","algo-exec-dir":"/home/sjr/git/MySQLDBTAE","algo-pcs":{"@pcs-id":2,"pcs-filename":"/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt","pcs-text":"#Used for getting specific results out from a target wrapper\\n#Some invalid values are specified so that we can test code\\n\\nsolved { SAT, UNSAT, TIMEOUT, CRASHED, ABORT, INVALID } [SAT]\\nruntime [0,1000] [0]\\nrunlength [0,1000000][0]\\nquality [0, 1000000] [0]\\nseed [ -1,4294967295][1]i\\n","pcs-subspace":{}},"algo-cutoff":0.01,"algo-deterministic":false,"algo-tae-context":{}}', '2014-09-27 16:15:48');

-- --------------------------------------------------------

--
-- Table structure for table `junit_kill_single_run_runConfigs`
--

CREATE TABLE IF NOT EXISTS `junit_kill_single_run_runConfigs` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=4 ;

--
-- Dumping data for table `junit_kill_single_run_runConfigs`
--

INSERT INTO `junit_kill_single_run_runConfigs` (`runConfigID`, `runConfigUUID`, `execConfigID`, `problemInstance`, `instanceSpecificInformation`, `seed`, `cutoffTime`, `paramConfiguration`, `cutoffLessThanMax`, `status`, `priority`, `workerUUID`, `killJob`, `retryAttempts`, `runPartition`, `noop`, `runResult`, `runLength`, `quality`, `resultSeed`, `runtime`, `walltime`, `additionalRunData`, `worstCaseEndtime`, `worstCaseNextUpdateWhenAssigned`, `lastModified`) VALUES
(1, 'f5e1beff76dc10854aa3573b8c9c0f48379e1b29', 1, 'TestInstance', '0', 1307903961, 3000, '0.09744865476958131,0.7216791231954726,0.008,0.30452012135541995,1.0', 0, 'COMPLETE', 'HIGH', '8e22bfc6-c42b-4265-80b0-1f78d4398ba4', 1, 1, 0, 0, 'KILLED', 0, 0, 1307903961, 0, 6.546, 'Killed Manually', '2014-09-27 10:32:47', '2014-09-27 09:17:53', '2014-09-27 16:15:55'),
(2, '030121b36fc3a4c41c66b10ee36f3f90f33be57d', 1, 'TestInstance', '0', 2674515284, 3000, '0.9227293839227141,0.0671391137187366,0.008,0.6227091152400921,1.0', 0, 'COMPLETE', 'HIGH', '8e22bfc6-c42b-4265-80b0-1f78d4398ba4', 0, 1, 0, 0, 'SAT', 67139.1137187366, 922729.383922714, 2674515284, 8, 8.102, '', '2014-09-27 10:32:54', '2014-09-27 09:18:02', '2014-09-27 16:16:03'),
(3, '258bcd65a06243eb45130aba3ed8478cfcb72f67', 1, 'TestInstance', '0', 505592879, 3000, '0.8557881058353884,0.18771158418708878,0.008,0.11771751576622075,1.0', 0, 'COMPLETE', 'HIGH', '8e22bfc6-c42b-4265-80b0-1f78d4398ba4', 1, 1, 0, 0, 'KILLED', 0, 0, 505592879, 0, 0, 'Killed By Client (19063@hilbert ) While Status ASSIGNED', '1900-01-01 00:00:00', '2014-09-27 09:17:53', '2014-09-27 16:15:54');

-- --------------------------------------------------------

--
-- Table structure for table `junit_kill_single_run_version`
--

CREATE TABLE IF NOT EXISTS `junit_kill_single_run_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` varchar(255) NOT NULL,
  `hash` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash` (`hash`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Dumping data for table `junit_kill_single_run_version`
--

INSERT INTO `junit_kill_single_run_version` (`id`, `version`, `hash`) VALUES
(1, 'v0.92.00dev-development-112 (8a5109587631)', '84ed1641e09245a2869dfe555e546f1bfc369c1c');

-- --------------------------------------------------------

--
-- Table structure for table `junit_kill_single_run_workers`
--

CREATE TABLE IF NOT EXISTS `junit_kill_single_run_workers` (
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

--
-- Dumping data for table `junit_kill_single_run_workers`
--

INSERT INTO `junit_kill_single_run_workers` (`workerUUID`, `hostname`, `username`, `jobID`, `status`, `version`, `crashInfo`, `startTime`, `startWeekYear`, `originalEndTime`, `currentlyIdle`, `endTime_UPDATEABLE`, `runsToBatch_UPDATEABLE`, `minRunsToBatch_UPDATEABLE`, `maxRunsToBatch_UPDATEABLE`, `autoAdjustBatchSize_UPDATEABLE`, `delayBetweenRequests_UPDATEABLE`, `pool_UPDATEABLE`, `poolIdleTimeLimit_UPDATEABLE`, `workerIdleTime_UPDATEABLE`, `concurrencyFactor_UPDATEABLE`, `upToDate`, `worstCaseNextUpdateWhenRunning`, `lastModified`) VALUES
('8e22bfc6-c42b-4265-80b0-1f78d4398ba4', 'hilbert', 'sjr', 'CLI/19102@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Triggered By Shutdown Hook (probably shutting down due to SIGTERM or SIGINT)', '2014-09-27 09:15:47', 392014, '2014-09-28 09:14:46', 0, '2014-09-28 09:14:46', 2, 1, 100, 1, 1, 'junit_kill_single_run', 14400000, 0, 4, 1, '2014-09-27 09:16:04', '2014-09-27 16:16:04');

-- --------------------------------------------------------

--
-- Table structure for table `junit_markCompleteTest_commandTable`
--

CREATE TABLE IF NOT EXISTS `junit_markCompleteTest_commandTable` (
  `commandID` int(11) NOT NULL AUTO_INCREMENT,
  `commandString` text NOT NULL,
  PRIMARY KEY (`commandID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `junit_markCompleteTest_commandTable`
--

INSERT INTO `junit_markCompleteTest_commandTable` (`commandID`, `commandString`) VALUES
(1, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite');

-- --------------------------------------------------------

--
-- Table structure for table `junit_markCompleteTest_execConfig`
--

CREATE TABLE IF NOT EXISTS `junit_markCompleteTest_execConfig` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `junit_markCompleteTest_execConfig`
--

INSERT INTO `junit_markCompleteTest_execConfig` (`algorithmExecutionConfigID`, `algorithmExecutionConfigHashCode`, `algorithmExecutable`, `algorithmExecutableDirectory`, `parameterFile`, `executeOnCluster`, `deterministicAlgorithm`, `cutoffTime`, `algorithmExecutionConfigurationJSON`, `lastModified`) VALUES
(1, '2625865e6eafb0a4fa16ff33c3ac6d6469acb9eb', 'java -cp /home/sjr/git/MySQLDBTAE/bin:/home/sjr/git/AEATK/bin:/home/sjr/git/AEATK/lib/commons-collections-3.2.1.jar:/home/sjr/git/AEATK/lib/commons-math-2.2.jar:/home/sjr/git/AEATK/lib/opencsv-2.3.jar:/home/sjr/git/AEATK/lib/spi-0.2.4.jar:/home/sjr/git/AEATK/lib/jcip-annotations.jar:/home/sjr/git/AEATK/lib/Jama-1.0.2.jar:/home/sjr/git/AEATK/lib/numerics4j-1.3.jar:/home/sjr/git/AEATK/lib/guava-14.0.1.jar:/home/sjr/git/AEATK/lib/exp4j-0.3.10.jar:/home/sjr/git/AEATK/lib/commons-io-2.1.jar:/home/sjr/git/AEATK/lib/slf4j-api-1.7.5.jar:/home/sjr/git/AEATK/lib/jackson-annotations-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-core-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-databind-2.3.1.jar:/home/sjr/git/AEATK/lib/logback-access-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-classic-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-core-1.1.2.jar:/home/sjr/git/RandomForests/bin:/opt/eclipse/plugins/org.junit_4.11.0.v201303080030/junit.jar:/opt/eclipse/plugins/org.hamcrest.core_1.3.0.v201303031735.jar:/home/sjr/git/AEATK/lib/jcommander.jar:/home/sjr/git/AEATK/lib/commons-math3-3.3.jar:/home/sjr/git/MySQLDBTAE/lib/commons-codec-1.7.jar:/home/sjr/git/MySQLDBTAE/lib/mchange-commons-java-0.2.3.3.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-0.9.2-pre8.jar:/home/sjr/git/MySQLDBTAE/lib/mysql-connector-java-5.1.26-bin.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-oracle-thin-extras-0.9.2-pre8.jar:/opt/eclipse/configuration/org.eclipse.osgi/bundles/164/1/.cp/:/opt/eclipse/configuration/org.eclipse.osgi/bundles/163/1/.cp/ ca.ubc.cs.beta.targetalgorithmevaluator.TrueSleepyParamEchoExecutor', '/home/sjr/git/MySQLDBTAE', '/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt', 0, 0, 500, '{"@algo-exec-config-id":2,"algo-exec":"java -cp /home/sjr/git/MySQLDBTAE/bin:/home/sjr/git/AEATK/bin:/home/sjr/git/AEATK/lib/commons-collections-3.2.1.jar:/home/sjr/git/AEATK/lib/commons-math-2.2.jar:/home/sjr/git/AEATK/lib/opencsv-2.3.jar:/home/sjr/git/AEATK/lib/spi-0.2.4.jar:/home/sjr/git/AEATK/lib/jcip-annotations.jar:/home/sjr/git/AEATK/lib/Jama-1.0.2.jar:/home/sjr/git/AEATK/lib/numerics4j-1.3.jar:/home/sjr/git/AEATK/lib/guava-14.0.1.jar:/home/sjr/git/AEATK/lib/exp4j-0.3.10.jar:/home/sjr/git/AEATK/lib/commons-io-2.1.jar:/home/sjr/git/AEATK/lib/slf4j-api-1.7.5.jar:/home/sjr/git/AEATK/lib/jackson-annotations-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-core-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-databind-2.3.1.jar:/home/sjr/git/AEATK/lib/logback-access-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-classic-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-core-1.1.2.jar:/home/sjr/git/RandomForests/bin:/opt/eclipse/plugins/org.junit_4.11.0.v201303080030/junit.jar:/opt/eclipse/plugins/org.hamcrest.core_1.3.0.v201303031735.jar:/home/sjr/git/AEATK/lib/jcommander.jar:/home/sjr/git/AEATK/lib/commons-math3-3.3.jar:/home/sjr/git/MySQLDBTAE/lib/commons-codec-1.7.jar:/home/sjr/git/MySQLDBTAE/lib/mchange-commons-java-0.2.3.3.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-0.9.2-pre8.jar:/home/sjr/git/MySQLDBTAE/lib/mysql-connector-java-5.1.26-bin.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-oracle-thin-extras-0.9.2-pre8.jar:/opt/eclipse/configuration/org.eclipse.osgi/bundles/164/1/.cp/:/opt/eclipse/configuration/org.eclipse.osgi/bundles/163/1/.cp/ ca.ubc.cs.beta.targetalgorithmevaluator.TrueSleepyParamEchoExecutor","algo-exec-dir":"/home/sjr/git/MySQLDBTAE","algo-pcs":{"@pcs-id":2,"pcs-filename":"/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt","pcs-text":"#Used for getting specific results out from a target wrapper\\n#Some invalid values are specified so that we can test code\\n\\nsolved { SAT, UNSAT, TIMEOUT, CRASHED, ABORT, INVALID } [SAT]\\nruntime [0,1000] [0]\\nrunlength [0,1000000][0]\\nquality [0, 1000000] [0]\\nseed [ -1,4294967295][1]i\\n","pcs-subspace":{}},"algo-cutoff":500.0,"algo-deterministic":false,"algo-tae-context":{}}', '2014-09-27 16:19:46');

-- --------------------------------------------------------

--
-- Table structure for table `junit_markCompleteTest_runConfigs`
--

CREATE TABLE IF NOT EXISTS `junit_markCompleteTest_runConfigs` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=6 ;

--
-- Dumping data for table `junit_markCompleteTest_runConfigs`
--

INSERT INTO `junit_markCompleteTest_runConfigs` (`runConfigID`, `runConfigUUID`, `execConfigID`, `problemInstance`, `instanceSpecificInformation`, `seed`, `cutoffTime`, `paramConfiguration`, `cutoffLessThanMax`, `status`, `priority`, `workerUUID`, `killJob`, `retryAttempts`, `runPartition`, `noop`, `runResult`, `runLength`, `quality`, `resultSeed`, `runtime`, `walltime`, `additionalRunData`, `worstCaseEndtime`, `worstCaseNextUpdateWhenAssigned`, `lastModified`) VALUES
(1, '138683385c6b42a3c742cc0358a3ee8892f351fd', 1, 'TestInstance', '0', 3593195852, 20, '0.3092290878986853,0.9314120116595662,0.005,0.8366061031500329,2.0', 1, 'COMPLETE', 'HIGH', '62ad73aa-0fe4-4805-b662-d95ac19217c9', 0, 1, -9, 0, 'UNSAT', 931412.0116595662, 309229.0878986853, 3593195852, 5, 5.119, '', '2014-09-27 09:22:17', '2014-09-27 09:21:52', '2014-09-27 16:19:53'),
(2, '4619ab34119a853e2dd3cc194bb62d11dbbf3dfe', 1, 'TestInstance', '0', 1539457237, 20, '0.038224128587044826,0.1685478053671141,0.005,0.35843281963410956,2.0', 1, 'COMPLETE', 'HIGH', '62ad73aa-0fe4-4805-b662-d95ac19217c9', 1, 1, -9, 0, 'KILLED', 0, 0, 1539457237, 0, 4.562, 'Killed Manually', '2014-09-27 09:22:22', '2014-09-27 09:21:56', '2014-09-27 16:19:57'),
(3, '4f25dac1fb4607bbb71fbcf048ba45b44b784310', 1, 'TestInstance', '0', 3696646850, 20, '0.7101786069097303,0.6661171108919195,0.005,0.8606926655954,1.0', 1, 'COMPLETE', 'HIGH', '62ad73aa-0fe4-4805-b662-d95ac19217c9', 1, 1, -9, 0, 'KILLED', 0, 0, 3696646850, 0, 1.538, 'Killed Manually', '1900-01-01 00:00:00', '2014-09-27 09:21:56', '2014-09-27 16:19:59'),
(4, '5a1450839b97a98a9951ab5d72f609e3b304619a', 1, 'TestInstance', '0', 2326709851, 20, '0.9734318374091655,0.6235585330010517,0.005,0.5417293524272438,2.0', 1, 'COMPLETE', 'HIGH', '62ad73aa-0fe4-4805-b662-d95ac19217c9', 1, 1, -9, 0, 'KILLED', 0, 0, 2326709851, 0, 1.536, 'Killed Manually', '1900-01-01 00:00:00', '2014-09-27 09:21:56', '2014-09-27 16:20:00'),
(5, '41978d8780b0e5f7ca2b74cfba234fde0ec8be29', 1, 'TestInstance', '0', 2905536945, 20, '0.5528512269966525,0.6237129117682473,0.005,0.6764980372561845,2.0', 1, 'COMPLETE', 'HIGH', '62ad73aa-0fe4-4805-b662-d95ac19217c9', 1, 1, -9, 0, 'KILLED', 0, 0, 2905536945, 0, 1.562, 'Killed Manually', '1900-01-01 00:00:00', '2014-09-27 09:21:56', '2014-09-27 16:20:02');

-- --------------------------------------------------------

--
-- Table structure for table `junit_markCompleteTest_version`
--

CREATE TABLE IF NOT EXISTS `junit_markCompleteTest_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` varchar(255) NOT NULL,
  `hash` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash` (`hash`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Dumping data for table `junit_markCompleteTest_version`
--

INSERT INTO `junit_markCompleteTest_version` (`id`, `version`, `hash`) VALUES
(1, 'v0.92.00dev-development-112 (8a5109587631)', '84ed1641e09245a2869dfe555e546f1bfc369c1c');

-- --------------------------------------------------------

--
-- Table structure for table `junit_markCompleteTest_workers`
--

CREATE TABLE IF NOT EXISTS `junit_markCompleteTest_workers` (
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

--
-- Dumping data for table `junit_markCompleteTest_workers`
--

INSERT INTO `junit_markCompleteTest_workers` (`workerUUID`, `hostname`, `username`, `jobID`, `status`, `version`, `crashInfo`, `startTime`, `startWeekYear`, `originalEndTime`, `currentlyIdle`, `endTime_UPDATEABLE`, `runsToBatch_UPDATEABLE`, `minRunsToBatch_UPDATEABLE`, `maxRunsToBatch_UPDATEABLE`, `autoAdjustBatchSize_UPDATEABLE`, `delayBetweenRequests_UPDATEABLE`, `pool_UPDATEABLE`, `poolIdleTimeLimit_UPDATEABLE`, `workerIdleTime_UPDATEABLE`, `concurrencyFactor_UPDATEABLE`, `upToDate`, `worstCaseNextUpdateWhenRunning`, `lastModified`) VALUES
('62ad73aa-0fe4-4805-b662-d95ac19217c9', 'hilbert', 'sjr', 'proc1/22132@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Idle limit reached: 6.0 versus limit: 5', '2014-09-27 09:19:46', 392014, '2014-09-27 09:22:45', 1, '2014-09-27 09:22:45', 1, 1, 100, 1, 1, 'junit_markCompleteTest', 14400000, 5, 4, 1, '2014-09-27 09:20:08', '2014-09-27 16:20:08');

-- --------------------------------------------------------

--
-- Table structure for table `junit_migratetest_algoExecConfig`
--

CREATE TABLE IF NOT EXISTS `junit_migratetest_algoExecConfig` (
  `algorithmExecutionConfigID` int(11) NOT NULL AUTO_INCREMENT,
  `algorithmExecutionConfigHashCode` char(40) NOT NULL,
  `algorithmExecutable` text NOT NULL,
  `algorithmExecutableDirectory` text NOT NULL,
  `parameterFile` text NOT NULL,
  `deterministicAlgorithm` tinyint(1) NOT NULL,
  `cutoffTime` double NOT NULL,
  `algorithmExecutionConfigurationJSON` text NOT NULL,
  `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`algorithmExecutionConfigID`),
  UNIQUE KEY `algorithmExecutionConfigHashCode` (`algorithmExecutionConfigHashCode`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=8 ;

--
-- Dumping data for table `junit_migratetest_algoExecConfig`
--

INSERT INTO `junit_migratetest_algoExecConfig` (`algorithmExecutionConfigID`, `algorithmExecutionConfigHashCode`, `algorithmExecutable`, `algorithmExecutableDirectory`, `parameterFile`, `deterministicAlgorithm`, `cutoffTime`, `algorithmExecutionConfigurationJSON`, `lastModified`) VALUES
(1, '57922f4090e9e5a9a7633f2e0898e81b400a02e7', 'ignore', '/home/sjr/git/MySQLDBTAE', '/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt', 0, 500, '{"@algo-exec-config-id":2,"algo-exec":"ignore","algo-exec-dir":"/home/sjr/git/MySQLDBTAE","algo-pcs":{"@pcs-id":2,"pcs-filename":"/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt","pcs-text":"#Used for getting specific results out from a target wrapper\\n#Some invalid values are specified so that we can test code\\n\\nsolved { SAT, UNSAT, TIMEOUT, CRASHED, ABORT, INVALID } [SAT]\\nruntime [0,1000] [0]\\nrunlength [0,1000000][0]\\nquality [0, 1000000] [0]\\nseed [ -1,4294967295][1]i\\n","pcs-subspace":{}},"algo-cutoff":500.0,"algo-deterministic":false,"algo-tae-context":{}}', '2014-09-27 18:16:05');

-- --------------------------------------------------------

--
-- Table structure for table `junit_migratetest_commandTable`
--

CREATE TABLE IF NOT EXISTS `junit_migratetest_commandTable` (
  `commandID` int(11) NOT NULL AUTO_INCREMENT,
  `commandString` text NOT NULL,
  PRIMARY KEY (`commandID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=8 ;

--
-- Dumping data for table `junit_migratetest_commandTable`
--

INSERT INTO `junit_migratetest_commandTable` (`commandID`, `commandString`) VALUES
(1, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59410 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -test ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTAEMigrationTester:testMigration'),
(2, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 56835 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -test ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTAEMigrationTester:testMigration'),
(3, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 56086 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -test ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTAEMigrationTester:testMigration'),
(4, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 41317 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -test ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTAEMigrationTester:testMigration'),
(5, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 57743 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -test ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTAEMigrationTester:testMigration'),
(6, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 50650 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -test ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTAEMigrationTester:testMigration'),
(7, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 50650 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -test ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTAEMigrationTester:testMigration');

-- --------------------------------------------------------

--
-- Table structure for table `junit_migratetest_runs`
--

CREATE TABLE IF NOT EXISTS `junit_migratetest_runs` (
  `runID` int(11) NOT NULL AUTO_INCREMENT,
  `runHashCode` char(48) NOT NULL,
  `algorithmExecutionConfigID` int(11) NOT NULL,
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
  `result_status` enum('TIMEOUT','SAT','UNSAT','CRASHED','ABORT','KILLED') NOT NULL DEFAULT 'ABORT',
  `result_runLength` double NOT NULL DEFAULT '0',
  `result_quality` double NOT NULL DEFAULT '0',
  `result_seed` bigint(20) NOT NULL DEFAULT '1',
  `result_runtime` double NOT NULL DEFAULT '0',
  `result_walltime` double NOT NULL DEFAULT '0',
  `result_additionalRunData` longtext NOT NULL,
  `worstCaseEndtime` datetime NOT NULL DEFAULT '1900-01-01 00:00:00',
  `worstCaseNextUpdateWhenAssigned` datetime NOT NULL DEFAULT '1900-01-01 00:00:00',
  `lastModified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`runID`),
  UNIQUE KEY `runConfigUUID` (`runHashCode`),
  KEY `status2` (`status`,`priority`),
  KEY `status` (`status`,`workerUUID`,`priority`,`retryAttempts`,`runID`),
  KEY `statusCutoff` (`status`,`cutoffTime`),
  KEY `statusEndtime` (`status`,`worstCaseEndtime`),
  KEY `statusWorstCaseUpdate` (`status`,`worstCaseNextUpdateWhenAssigned`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=36 ;

--
-- Dumping data for table `junit_migratetest_runs`
--

INSERT INTO `junit_migratetest_runs` (`runID`, `runHashCode`, `algorithmExecutionConfigID`, `problemInstance`, `instanceSpecificInformation`, `seed`, `cutoffTime`, `paramConfiguration`, `cutoffLessThanMax`, `status`, `priority`, `workerUUID`, `killJob`, `retryAttempts`, `runPartition`, `result_status`, `result_runLength`, `result_quality`, `result_seed`, `result_runtime`, `result_walltime`, `result_additionalRunData`, `worstCaseEndtime`, `worstCaseNextUpdateWhenAssigned`, `lastModified`) VALUES
(1, 'c7effecb7afa85ba4ce38506bc03cb1b33525f1a', 1, 'TestInstance', '0', 1515946401, 1001, '0.6976859694807698,0.09929272822371915,0.7936173117865861,0.3529587765566635,1.0', 0, 'COMPLETE', 'NORMAL', 'MigrationUtility-(8a5109587631)', 0, 0, 2, 'SAT', 0, 0, 1515946401, 10101981, 0, '', '1900-01-01 00:00:00', '2014-10-17 11:16:05', '2014-09-27 18:16:05'),
(2, 'c708ea8ce0fb5f137702b018d5d95f632e33049f', 1, 'TestInstance', '0', 1120695991, 1001, '0.6528597061778862,0.6705773967041941,0.2563006099110542,0.2609323692133342,3.0', 0, 'COMPLETE', 'NORMAL', 'MigrationUtility-(8a5109587631)', 0, 0, 2, 'SAT', 0, 0, 1120695991, 10101982, 0, '', '1900-01-01 00:00:00', '2014-10-17 11:16:05', '2014-09-27 18:16:05'),
(3, '8dcb9c998deec5e4e861d54fd292184e065116bd', 1, 'TestInstance', '0', 2925533823, 1001, '0.9660083223477697,0.29799831412539557,0.9430851471696299,0.6811539232309084,3.0', 0, 'COMPLETE', 'NORMAL', 'MigrationUtility-(8a5109587631)', 0, 0, 2, 'SAT', 0, 0, 2925533823, 10101983, 0, '', '1900-01-01 00:00:00', '2014-10-17 11:16:05', '2014-09-27 18:16:05'),
(4, 'b22402031ddba509c42af9d80a369671facd44c3', 1, 'TestInstance', '0', 374745277, 1001, '0.026638248286117938,0.22992364956581068,0.57402790700688,0.08725218437908865,3.0', 0, 'COMPLETE', 'NORMAL', 'MigrationUtility-(8a5109587631)', 0, 0, 2, 'SAT', 0, 0, 374745277, 10101984, 0, '', '1900-01-01 00:00:00', '2014-10-17 11:16:05', '2014-09-27 18:16:05'),
(5, 'a0d401c70018dc63cb3df46ef599c06194999e21', 1, 'TestInstance', '0', 2420327734, 1001, '0.3635103225740778,0.30123209256489847,0.47148377840708,0.5635264643785715,1.0', 0, 'COMPLETE', 'NORMAL', 'MigrationUtility-(8a5109587631)', 0, 0, 2, 'SAT', 0, 0, 2420327734, 10101985, 0, '', '1900-01-01 00:00:00', '2014-10-17 11:16:05', '2014-09-27 18:16:05'),
(6, '2221bd6ab466f55e8f1d98df8414356c4b05bed9', 1, 'TestInstance', '0', 1515946401, 1001, '0.6976859694807698,0.09929272822371915,0.7936173117865861,0.3529587765566635,1.0', 0, 'COMPLETE', 'NORMAL', 'MigrationUtility-(8a5109587631)', 0, 0, 1, 'SAT', 0, 0, 1515946401, 10101986, 0, '', '1900-01-01 00:00:00', '2014-10-17 11:16:05', '2014-09-27 18:16:05'),
(7, '827c0903495b69c37fffd18b4bf8e4078c5d4206', 1, 'TestInstance', '0', 1120695991, 1001, '0.6528597061778862,0.6705773967041941,0.2563006099110542,0.2609323692133342,3.0', 0, 'COMPLETE', 'NORMAL', 'MigrationUtility-(8a5109587631)', 0, 0, 1, 'SAT', 0, 0, 1120695991, 10101987, 0, '', '1900-01-01 00:00:00', '2014-10-17 11:16:05', '2014-09-27 18:16:05'),
(8, 'a7305a5dabe65641a2040df6e227471589c2afc2', 1, 'TestInstance', '0', 2925533823, 1001, '0.9660083223477697,0.29799831412539557,0.9430851471696299,0.6811539232309084,3.0', 0, 'COMPLETE', 'NORMAL', 'MigrationUtility-(8a5109587631)', 0, 0, 1, 'SAT', 0, 0, 2925533823, 10101988, 0, '', '1900-01-01 00:00:00', '2014-10-17 11:16:05', '2014-09-27 18:16:05'),
(9, '0afb2b7e5e0890f8e6bf914e438ea686816ae2f3', 1, 'TestInstance', '0', 374745277, 1001, '0.026638248286117938,0.22992364956581068,0.57402790700688,0.08725218437908865,3.0', 0, 'COMPLETE', 'NORMAL', 'MigrationUtility-(8a5109587631)', 0, 0, 1, 'SAT', 0, 0, 374745277, 10101989, 0, '', '1900-01-01 00:00:00', '2014-10-17 11:16:05', '2014-09-27 18:16:05'),
(10, '04926e32c614b9231b46238bf3b692ea8086fe25', 1, 'TestInstance', '0', 2420327734, 1001, '0.3635103225740778,0.30123209256489847,0.47148377840708,0.5635264643785715,1.0', 0, 'COMPLETE', 'NORMAL', 'MigrationUtility-(8a5109587631)', 0, 0, 1, 'SAT', 0, 0, 2420327734, 10101990, 0, '', '1900-01-01 00:00:00', '2014-10-17 11:16:05', '2014-09-27 18:16:05'),
(11, 'abab80928857aeb4007620784ecf4b502701f469', 1, 'TestInstance', '0', 2420327734, 1001, '0.3635103225740778,0.30123209256489847,0.47148377840708,0.5635264643785715,1.0', 0, 'NEW', 'NORMAL', 'MigrationUtility-(8a5109587631)', 0, 1, 3, 'ABORT', 0, 0, 2420327734, 10101991, 0, '', '1900-01-01 00:00:00', '2014-10-17 11:16:05', '2014-09-27 18:16:05');

-- --------------------------------------------------------

--
-- Table structure for table `junit_migratetest_version`
--

CREATE TABLE IF NOT EXISTS `junit_migratetest_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` varchar(255) NOT NULL,
  `hash` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash` (`hash`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=9 ;

--
-- Dumping data for table `junit_migratetest_version`
--

INSERT INTO `junit_migratetest_version` (`id`, `version`, `hash`) VALUES
(1, 'v0.92.00dev-development-112 (8a5109587631)', '84ed1641e09245a2869dfe555e546f1bfc369c1c');

-- --------------------------------------------------------

--
-- Table structure for table `junit_migratetest_workers`
--

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

-- --------------------------------------------------------

--
-- Table structure for table `junit_noObserverTest_commandTable`
--

CREATE TABLE IF NOT EXISTS `junit_noObserverTest_commandTable` (
  `commandID` int(11) NOT NULL AUTO_INCREMENT,
  `commandString` text NOT NULL,
  PRIMARY KEY (`commandID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `junit_noObserverTest_commandTable`
--

INSERT INTO `junit_noObserverTest_commandTable` (`commandID`, `commandString`) VALUES
(1, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite');

-- --------------------------------------------------------

--
-- Table structure for table `junit_noObserverTest_execConfig`
--

CREATE TABLE IF NOT EXISTS `junit_noObserverTest_execConfig` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `junit_noObserverTest_execConfig`
--

INSERT INTO `junit_noObserverTest_execConfig` (`algorithmExecutionConfigID`, `algorithmExecutionConfigHashCode`, `algorithmExecutable`, `algorithmExecutableDirectory`, `parameterFile`, `executeOnCluster`, `deterministicAlgorithm`, `cutoffTime`, `algorithmExecutionConfigurationJSON`, `lastModified`) VALUES
(1, '7ba59dfcdce961e70f6259e388e0b57240f16e2c', 'java -cp /home/sjr/git/MySQLDBTAE/bin:/home/sjr/git/AEATK/bin:/home/sjr/git/AEATK/lib/commons-collections-3.2.1.jar:/home/sjr/git/AEATK/lib/commons-math-2.2.jar:/home/sjr/git/AEATK/lib/opencsv-2.3.jar:/home/sjr/git/AEATK/lib/spi-0.2.4.jar:/home/sjr/git/AEATK/lib/jcip-annotations.jar:/home/sjr/git/AEATK/lib/Jama-1.0.2.jar:/home/sjr/git/AEATK/lib/numerics4j-1.3.jar:/home/sjr/git/AEATK/lib/guava-14.0.1.jar:/home/sjr/git/AEATK/lib/exp4j-0.3.10.jar:/home/sjr/git/AEATK/lib/commons-io-2.1.jar:/home/sjr/git/AEATK/lib/slf4j-api-1.7.5.jar:/home/sjr/git/AEATK/lib/jackson-annotations-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-core-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-databind-2.3.1.jar:/home/sjr/git/AEATK/lib/logback-access-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-classic-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-core-1.1.2.jar:/home/sjr/git/RandomForests/bin:/opt/eclipse/plugins/org.junit_4.11.0.v201303080030/junit.jar:/opt/eclipse/plugins/org.hamcrest.core_1.3.0.v201303031735.jar:/home/sjr/git/AEATK/lib/jcommander.jar:/home/sjr/git/AEATK/lib/commons-math3-3.3.jar:/home/sjr/git/MySQLDBTAE/lib/commons-codec-1.7.jar:/home/sjr/git/MySQLDBTAE/lib/mchange-commons-java-0.2.3.3.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-0.9.2-pre8.jar:/home/sjr/git/MySQLDBTAE/lib/mysql-connector-java-5.1.26-bin.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-oracle-thin-extras-0.9.2-pre8.jar:/opt/eclipse/configuration/org.eclipse.osgi/bundles/164/1/.cp/:/opt/eclipse/configuration/org.eclipse.osgi/bundles/163/1/.cp/ ca.ubc.cs.beta.targetalgorithmevaluator.TrueSleepyParamEchoExecutor', '/home/sjr/git/MySQLDBTAE', '/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt', 0, 0, 345600, '{"@algo-exec-config-id":2,"algo-exec":"java -cp /home/sjr/git/MySQLDBTAE/bin:/home/sjr/git/AEATK/bin:/home/sjr/git/AEATK/lib/commons-collections-3.2.1.jar:/home/sjr/git/AEATK/lib/commons-math-2.2.jar:/home/sjr/git/AEATK/lib/opencsv-2.3.jar:/home/sjr/git/AEATK/lib/spi-0.2.4.jar:/home/sjr/git/AEATK/lib/jcip-annotations.jar:/home/sjr/git/AEATK/lib/Jama-1.0.2.jar:/home/sjr/git/AEATK/lib/numerics4j-1.3.jar:/home/sjr/git/AEATK/lib/guava-14.0.1.jar:/home/sjr/git/AEATK/lib/exp4j-0.3.10.jar:/home/sjr/git/AEATK/lib/commons-io-2.1.jar:/home/sjr/git/AEATK/lib/slf4j-api-1.7.5.jar:/home/sjr/git/AEATK/lib/jackson-annotations-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-core-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-databind-2.3.1.jar:/home/sjr/git/AEATK/lib/logback-access-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-classic-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-core-1.1.2.jar:/home/sjr/git/RandomForests/bin:/opt/eclipse/plugins/org.junit_4.11.0.v201303080030/junit.jar:/opt/eclipse/plugins/org.hamcrest.core_1.3.0.v201303031735.jar:/home/sjr/git/AEATK/lib/jcommander.jar:/home/sjr/git/AEATK/lib/commons-math3-3.3.jar:/home/sjr/git/MySQLDBTAE/lib/commons-codec-1.7.jar:/home/sjr/git/MySQLDBTAE/lib/mchange-commons-java-0.2.3.3.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-0.9.2-pre8.jar:/home/sjr/git/MySQLDBTAE/lib/mysql-connector-java-5.1.26-bin.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-oracle-thin-extras-0.9.2-pre8.jar:/opt/eclipse/configuration/org.eclipse.osgi/bundles/164/1/.cp/:/opt/eclipse/configuration/org.eclipse.osgi/bundles/163/1/.cp/ ca.ubc.cs.beta.targetalgorithmevaluator.TrueSleepyParamEchoExecutor","algo-exec-dir":"/home/sjr/git/MySQLDBTAE","algo-pcs":{"@pcs-id":2,"pcs-filename":"/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt","pcs-text":"#Used for getting specific results out from a target wrapper\\n#Some invalid values are specified so that we can test code\\n\\nsolved { SAT, UNSAT, TIMEOUT, CRASHED, ABORT, INVALID } [SAT]\\nruntime [0,1000] [0]\\nrunlength [0,1000000][0]\\nquality [0, 1000000] [0]\\nseed [ -1,4294967295][1]i\\n","pcs-subspace":{}},"algo-cutoff":345600.0,"algo-deterministic":false,"algo-tae-context":{}}', '2014-09-27 16:19:58');

-- --------------------------------------------------------

--
-- Table structure for table `junit_noObserverTest_runConfigs`
--

CREATE TABLE IF NOT EXISTS `junit_noObserverTest_runConfigs` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `junit_noObserverTest_runConfigs`
--

INSERT INTO `junit_noObserverTest_runConfigs` (`runConfigID`, `runConfigUUID`, `execConfigID`, `problemInstance`, `instanceSpecificInformation`, `seed`, `cutoffTime`, `paramConfiguration`, `cutoffLessThanMax`, `status`, `priority`, `workerUUID`, `killJob`, `retryAttempts`, `runPartition`, `noop`, `runResult`, `runLength`, `quality`, `resultSeed`, `runtime`, `walltime`, `additionalRunData`, `worstCaseEndtime`, `worstCaseNextUpdateWhenAssigned`, `lastModified`) VALUES
(1, 'cf4e31a6182e4f69fb958d76daca0729f14be54a', 1, 'TestInstance', '0', 3477785454, 172800, '0.6316776916035806,0.859171756378059,0.001,0.8097350259056001,1.0', 1, 'NEW', 'LOW', '9564f385-f93c-408c-bce2-78d3aa39ac17', 0, 1, -9, 0, 'ABORT', 0, 0, 1, 0, 0, '', '1900-01-01 00:00:00', '2014-10-21 09:20:00', '2014-09-27 16:20:10');

-- --------------------------------------------------------

--
-- Table structure for table `junit_noObserverTest_version`
--

CREATE TABLE IF NOT EXISTS `junit_noObserverTest_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` varchar(255) NOT NULL,
  `hash` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash` (`hash`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Dumping data for table `junit_noObserverTest_version`
--

INSERT INTO `junit_noObserverTest_version` (`id`, `version`, `hash`) VALUES
(1, 'v0.92.00dev-development-112 (8a5109587631)', '84ed1641e09245a2869dfe555e546f1bfc369c1c');

-- --------------------------------------------------------

--
-- Table structure for table `junit_noObserverTest_workers`
--

CREATE TABLE IF NOT EXISTS `junit_noObserverTest_workers` (
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

--
-- Dumping data for table `junit_noObserverTest_workers`
--

INSERT INTO `junit_noObserverTest_workers` (`workerUUID`, `hostname`, `username`, `jobID`, `status`, `version`, `crashInfo`, `startTime`, `startWeekYear`, `originalEndTime`, `currentlyIdle`, `endTime_UPDATEABLE`, `runsToBatch_UPDATEABLE`, `minRunsToBatch_UPDATEABLE`, `maxRunsToBatch_UPDATEABLE`, `autoAdjustBatchSize_UPDATEABLE`, `delayBetweenRequests_UPDATEABLE`, `pool_UPDATEABLE`, `poolIdleTimeLimit_UPDATEABLE`, `workerIdleTime_UPDATEABLE`, `concurrencyFactor_UPDATEABLE`, `upToDate`, `worstCaseNextUpdateWhenRunning`, `lastModified`) VALUES
('9564f385-f93c-408c-bce2-78d3aa39ac17', 'hilbert', 'sjr', 'CLI/22260@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Triggered By Shutdown Hook (probably shutting down due to SIGTERM or SIGINT)', '2014-09-27 09:19:58', 392014, '2014-10-04 07:59:57', 0, '2014-10-04 07:59:57', 1, 1, 100, 1, 1, 'junit_noObserverTest', 14400000, 0, 4, 1, '2014-09-27 09:20:10', '2014-09-27 16:20:10');

-- --------------------------------------------------------

--
-- Table structure for table `junit_observer_commandTable`
--

CREATE TABLE IF NOT EXISTS `junit_observer_commandTable` (
  `commandID` int(11) NOT NULL AUTO_INCREMENT,
  `commandString` text NOT NULL,
  PRIMARY KEY (`commandID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

--
-- Dumping data for table `junit_observer_commandTable`
--

INSERT INTO `junit_observer_commandTable` (`commandID`, `commandString`) VALUES
(1, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite'),
(2, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite'),
(3, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite'),
(4, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite');

-- --------------------------------------------------------

--
-- Table structure for table `junit_observer_execConfig`
--

CREATE TABLE IF NOT EXISTS `junit_observer_execConfig` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

--
-- Dumping data for table `junit_observer_execConfig`
--

INSERT INTO `junit_observer_execConfig` (`algorithmExecutionConfigID`, `algorithmExecutionConfigHashCode`, `algorithmExecutable`, `algorithmExecutableDirectory`, `parameterFile`, `executeOnCluster`, `deterministicAlgorithm`, `cutoffTime`, `algorithmExecutionConfigurationJSON`, `lastModified`) VALUES
(1, '9823afd624fe4ccba80e314dae4a919eace39e34', 'java -cp /home/sjr/git/MySQLDBTAE/bin:/home/sjr/git/AEATK/bin:/home/sjr/git/AEATK/lib/commons-collections-3.2.1.jar:/home/sjr/git/AEATK/lib/commons-math-2.2.jar:/home/sjr/git/AEATK/lib/opencsv-2.3.jar:/home/sjr/git/AEATK/lib/spi-0.2.4.jar:/home/sjr/git/AEATK/lib/jcip-annotations.jar:/home/sjr/git/AEATK/lib/Jama-1.0.2.jar:/home/sjr/git/AEATK/lib/numerics4j-1.3.jar:/home/sjr/git/AEATK/lib/guava-14.0.1.jar:/home/sjr/git/AEATK/lib/exp4j-0.3.10.jar:/home/sjr/git/AEATK/lib/commons-io-2.1.jar:/home/sjr/git/AEATK/lib/slf4j-api-1.7.5.jar:/home/sjr/git/AEATK/lib/jackson-annotations-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-core-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-databind-2.3.1.jar:/home/sjr/git/AEATK/lib/logback-access-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-classic-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-core-1.1.2.jar:/home/sjr/git/RandomForests/bin:/opt/eclipse/plugins/org.junit_4.11.0.v201303080030/junit.jar:/opt/eclipse/plugins/org.hamcrest.core_1.3.0.v201303031735.jar:/home/sjr/git/AEATK/lib/jcommander.jar:/home/sjr/git/AEATK/lib/commons-math3-3.3.jar:/home/sjr/git/MySQLDBTAE/lib/commons-codec-1.7.jar:/home/sjr/git/MySQLDBTAE/lib/mchange-commons-java-0.2.3.3.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-0.9.2-pre8.jar:/home/sjr/git/MySQLDBTAE/lib/mysql-connector-java-5.1.26-bin.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-oracle-thin-extras-0.9.2-pre8.jar:/opt/eclipse/configuration/org.eclipse.osgi/bundles/164/1/.cp/:/opt/eclipse/configuration/org.eclipse.osgi/bundles/163/1/.cp/ ca.ubc.cs.beta.targetalgorithmevaluator.TrueSleepyParamEchoExecutor', '/home/sjr/git/MySQLDBTAE', '/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt', 0, 0, 0.01, '{"@algo-exec-config-id":2,"algo-exec":"java -cp /home/sjr/git/MySQLDBTAE/bin:/home/sjr/git/AEATK/bin:/home/sjr/git/AEATK/lib/commons-collections-3.2.1.jar:/home/sjr/git/AEATK/lib/commons-math-2.2.jar:/home/sjr/git/AEATK/lib/opencsv-2.3.jar:/home/sjr/git/AEATK/lib/spi-0.2.4.jar:/home/sjr/git/AEATK/lib/jcip-annotations.jar:/home/sjr/git/AEATK/lib/Jama-1.0.2.jar:/home/sjr/git/AEATK/lib/numerics4j-1.3.jar:/home/sjr/git/AEATK/lib/guava-14.0.1.jar:/home/sjr/git/AEATK/lib/exp4j-0.3.10.jar:/home/sjr/git/AEATK/lib/commons-io-2.1.jar:/home/sjr/git/AEATK/lib/slf4j-api-1.7.5.jar:/home/sjr/git/AEATK/lib/jackson-annotations-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-core-2.3.1.jar:/home/sjr/git/AEATK/lib/jackson-databind-2.3.1.jar:/home/sjr/git/AEATK/lib/logback-access-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-classic-1.1.2.jar:/home/sjr/git/AEATK/lib/logback-core-1.1.2.jar:/home/sjr/git/RandomForests/bin:/opt/eclipse/plugins/org.junit_4.11.0.v201303080030/junit.jar:/opt/eclipse/plugins/org.hamcrest.core_1.3.0.v201303031735.jar:/home/sjr/git/AEATK/lib/jcommander.jar:/home/sjr/git/AEATK/lib/commons-math3-3.3.jar:/home/sjr/git/MySQLDBTAE/lib/commons-codec-1.7.jar:/home/sjr/git/MySQLDBTAE/lib/mchange-commons-java-0.2.3.3.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-0.9.2-pre8.jar:/home/sjr/git/MySQLDBTAE/lib/mysql-connector-java-5.1.26-bin.jar:/home/sjr/git/MySQLDBTAE/lib/c3p0-oracle-thin-extras-0.9.2-pre8.jar:/opt/eclipse/configuration/org.eclipse.osgi/bundles/164/1/.cp/:/opt/eclipse/configuration/org.eclipse.osgi/bundles/163/1/.cp/ ca.ubc.cs.beta.targetalgorithmevaluator.TrueSleepyParamEchoExecutor","algo-exec-dir":"/home/sjr/git/MySQLDBTAE","algo-pcs":{"@pcs-id":2,"pcs-filename":"/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt","pcs-text":"#Used for getting specific results out from a target wrapper\\n#Some invalid values are specified so that we can test code\\n\\nsolved { SAT, UNSAT, TIMEOUT, CRASHED, ABORT, INVALID } [SAT]\\nruntime [0,1000] [0]\\nrunlength [0,1000000][0]\\nquality [0, 1000000] [0]\\nseed [ -1,4294967295][1]i\\n","pcs-subspace":{}},"algo-cutoff":0.01,"algo-deterministic":false,"algo-tae-context":{}}', '2014-09-27 16:16:49');

-- --------------------------------------------------------

--
-- Table structure for table `junit_observer_runConfigs`
--

CREATE TABLE IF NOT EXISTS `junit_observer_runConfigs` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=8 ;

--
-- Dumping data for table `junit_observer_runConfigs`
--

INSERT INTO `junit_observer_runConfigs` (`runConfigID`, `runConfigUUID`, `execConfigID`, `problemInstance`, `instanceSpecificInformation`, `seed`, `cutoffTime`, `paramConfiguration`, `cutoffLessThanMax`, `status`, `priority`, `workerUUID`, `killJob`, `retryAttempts`, `runPartition`, `noop`, `runResult`, `runLength`, `quality`, `resultSeed`, `runtime`, `walltime`, `additionalRunData`, `worstCaseEndtime`, `worstCaseNextUpdateWhenAssigned`, `lastModified`) VALUES
(7, 'e3fc208b924d3313d0e076e5e4cb96943dac0035', 1, 'TestInstance', '0', 2136004727, 3000, '0.5778160041695,0.553444670809442,0.1,0.497327355668571,1.0', 0, 'COMPLETE', 'HIGH', '1271853f-432e-4535-a5db-eb4641f18fa2', 1, 1, 0, 0, 'KILLED', 0, 0, 2136004727, 0, 6.564, 'Killed Manually', '2014-09-27 10:33:49', '2014-09-27 09:18:55', '2014-09-27 16:16:56');

-- --------------------------------------------------------

--
-- Table structure for table `junit_observer_version`
--

CREATE TABLE IF NOT EXISTS `junit_observer_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` varchar(255) NOT NULL,
  `hash` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash` (`hash`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=6 ;

--
-- Dumping data for table `junit_observer_version`
--

INSERT INTO `junit_observer_version` (`id`, `version`, `hash`) VALUES
(1, 'v0.92.00dev-development-112 (8a5109587631)', '84ed1641e09245a2869dfe555e546f1bfc369c1c');

-- --------------------------------------------------------

--
-- Table structure for table `junit_observer_workers`
--

CREATE TABLE IF NOT EXISTS `junit_observer_workers` (
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

--
-- Dumping data for table `junit_observer_workers`
--

INSERT INTO `junit_observer_workers` (`workerUUID`, `hostname`, `username`, `jobID`, `status`, `version`, `crashInfo`, `startTime`, `startWeekYear`, `originalEndTime`, `currentlyIdle`, `endTime_UPDATEABLE`, `runsToBatch_UPDATEABLE`, `minRunsToBatch_UPDATEABLE`, `maxRunsToBatch_UPDATEABLE`, `autoAdjustBatchSize_UPDATEABLE`, `delayBetweenRequests_UPDATEABLE`, `pool_UPDATEABLE`, `poolIdleTimeLimit_UPDATEABLE`, `workerIdleTime_UPDATEABLE`, `concurrencyFactor_UPDATEABLE`, `upToDate`, `worstCaseNextUpdateWhenRunning`, `lastModified`) VALUES
('1271853f-432e-4535-a5db-eb4641f18fa2', 'hilbert', 'sjr', 'CLI/19428@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Triggered By Shutdown Hook (probably shutting down due to SIGTERM or SIGINT)', '2014-09-27 09:16:17', 392014, '2014-09-28 09:15:17', 1, '2014-09-28 09:15:17', 1, 1, 100, 1, 1, 'junit_observer', 14400000, 8, 4, 1, '2014-09-27 09:16:57', '2014-09-27 16:16:57');

-- --------------------------------------------------------

--
-- Table structure for table `junit_poolauto_commandTable`
--

CREATE TABLE IF NOT EXISTS `junit_poolauto_commandTable` (
  `commandID` int(11) NOT NULL AUTO_INCREMENT,
  `commandString` text NOT NULL,
  PRIMARY KEY (`commandID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;

--
-- Dumping data for table `junit_poolauto_commandTable`
--

INSERT INTO `junit_poolauto_commandTable` (`commandID`, `commandString`) VALUES
(1, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite'),
(2, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite'),
(3, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite');

-- --------------------------------------------------------

--
-- Table structure for table `junit_poolauto_execConfig`
--

CREATE TABLE IF NOT EXISTS `junit_poolauto_execConfig` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `junit_poolauto_runConfigs`
--

CREATE TABLE IF NOT EXISTS `junit_poolauto_runConfigs` (
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `junit_poolauto_version`
--

CREATE TABLE IF NOT EXISTS `junit_poolauto_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` varchar(255) NOT NULL,
  `hash` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash` (`hash`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;

--
-- Dumping data for table `junit_poolauto_version`
--

INSERT INTO `junit_poolauto_version` (`id`, `version`, `hash`) VALUES
(1, 'v0.92.00dev-development-112 (8a5109587631)', '84ed1641e09245a2869dfe555e546f1bfc369c1c');

-- --------------------------------------------------------

--
-- Table structure for table `junit_poolauto_workers`
--

CREATE TABLE IF NOT EXISTS `junit_poolauto_workers` (
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

-- --------------------------------------------------------

--
-- Table structure for table `junit_poolIdleTimeTest_commandTable`
--

CREATE TABLE IF NOT EXISTS `junit_poolIdleTimeTest_commandTable` (
  `commandID` int(11) NOT NULL AUTO_INCREMENT,
  `commandString` text NOT NULL,
  PRIMARY KEY (`commandID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;

--
-- Dumping data for table `junit_poolIdleTimeTest_commandTable`
--

INSERT INTO `junit_poolIdleTimeTest_commandTable` (`commandID`, `commandString`) VALUES
(1, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite'),
(2, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite'),
(3, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite');

-- --------------------------------------------------------

--
-- Table structure for table `junit_poolIdleTimeTest_execConfig`
--

CREATE TABLE IF NOT EXISTS `junit_poolIdleTimeTest_execConfig` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `junit_poolIdleTimeTest_runConfigs`
--

CREATE TABLE IF NOT EXISTS `junit_poolIdleTimeTest_runConfigs` (
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `junit_poolIdleTimeTest_version`
--

CREATE TABLE IF NOT EXISTS `junit_poolIdleTimeTest_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` varchar(255) NOT NULL,
  `hash` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash` (`hash`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=13 ;

--
-- Dumping data for table `junit_poolIdleTimeTest_version`
--

INSERT INTO `junit_poolIdleTimeTest_version` (`id`, `version`, `hash`) VALUES
(1, 'v0.92.00dev-development-112 (8a5109587631)', '84ed1641e09245a2869dfe555e546f1bfc369c1c');

-- --------------------------------------------------------

--
-- Table structure for table `junit_poolIdleTimeTest_workers`
--

CREATE TABLE IF NOT EXISTS `junit_poolIdleTimeTest_workers` (
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

--
-- Dumping data for table `junit_poolIdleTimeTest_workers`
--

INSERT INTO `junit_poolIdleTimeTest_workers` (`workerUUID`, `hostname`, `username`, `jobID`, `status`, `version`, `crashInfo`, `startTime`, `startWeekYear`, `originalEndTime`, `currentlyIdle`, `endTime_UPDATEABLE`, `runsToBatch_UPDATEABLE`, `minRunsToBatch_UPDATEABLE`, `maxRunsToBatch_UPDATEABLE`, `autoAdjustBatchSize_UPDATEABLE`, `delayBetweenRequests_UPDATEABLE`, `pool_UPDATEABLE`, `poolIdleTimeLimit_UPDATEABLE`, `workerIdleTime_UPDATEABLE`, `concurrencyFactor_UPDATEABLE`, `upToDate`, `worstCaseNextUpdateWhenRunning`, `lastModified`) VALUES
('5ece36f7-79d6-4bc6-aff9-c574bd55b943', 'hilbert', 'sjr', 'CLI/22763@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Pool Idle Limit reached: 2 versus limit: 0', '2014-09-27 09:20:32', 372014, '2014-09-27 09:22:32', 1, '2014-09-27 09:22:32', 1, 1, 100, 1, 1, 'junit_poolIdleTimeTest', 10, 8, 4, 0, '2014-09-27 09:20:42', '2014-09-27 16:20:44'),
('75946168-bd80-430f-ab33-b6147a86518f', 'hilbert', 'sjr', 'CLI/22801@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Pool Idle Limit reached: 14 versus limit: 10', '2014-09-27 09:20:38', 372014, '2014-09-27 09:22:38', 1, '2014-09-27 09:22:38', 1, 1, 100, 1, 1, 'junit_poolIdleTimeTest', 10, 6, 4, 1, '2014-09-27 09:20:45', '2014-09-27 16:20:45');

-- --------------------------------------------------------

--
-- Table structure for table `junit_pool_switch1_commandTable`
--

CREATE TABLE IF NOT EXISTS `junit_pool_switch1_commandTable` (
  `commandID` int(11) NOT NULL AUTO_INCREMENT,
  `commandString` text NOT NULL,
  PRIMARY KEY (`commandID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `junit_pool_switch1_commandTable`
--

INSERT INTO `junit_pool_switch1_commandTable` (`commandID`, `commandString`) VALUES
(1, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite');

-- --------------------------------------------------------

--
-- Table structure for table `junit_pool_switch1_execConfig`
--

CREATE TABLE IF NOT EXISTS `junit_pool_switch1_execConfig` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `junit_pool_switch1_execConfig`
--

INSERT INTO `junit_pool_switch1_execConfig` (`algorithmExecutionConfigID`, `algorithmExecutionConfigHashCode`, `algorithmExecutable`, `algorithmExecutableDirectory`, `parameterFile`, `executeOnCluster`, `deterministicAlgorithm`, `cutoffTime`, `algorithmExecutionConfigurationJSON`, `lastModified`) VALUES
(1, '57922f4090e9e5a9a7633f2e0898e81b400a02e7', 'ignore', '/home/sjr/git/MySQLDBTAE', '/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt', 0, 0, 500, '{"@algo-exec-config-id":2,"algo-exec":"ignore","algo-exec-dir":"/home/sjr/git/MySQLDBTAE","algo-pcs":{"@pcs-id":2,"pcs-filename":"/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt","pcs-text":"#Used for getting specific results out from a target wrapper\\n#Some invalid values are specified so that we can test code\\n\\nsolved { SAT, UNSAT, TIMEOUT, CRASHED, ABORT, INVALID } [SAT]\\nruntime [0,1000] [0]\\nrunlength [0,1000000][0]\\nquality [0, 1000000] [0]\\nseed [ -1,4294967295][1]i\\n","pcs-subspace":{}},"algo-cutoff":500.0,"algo-deterministic":false,"algo-tae-context":{}}', '2014-09-27 16:20:47');

-- --------------------------------------------------------

--
-- Table structure for table `junit_pool_switch1_runConfigs`
--

CREATE TABLE IF NOT EXISTS `junit_pool_switch1_runConfigs` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=11 ;

--
-- Dumping data for table `junit_pool_switch1_runConfigs`
--

INSERT INTO `junit_pool_switch1_runConfigs` (`runConfigID`, `runConfigUUID`, `execConfigID`, `problemInstance`, `instanceSpecificInformation`, `seed`, `cutoffTime`, `paramConfiguration`, `cutoffLessThanMax`, `status`, `priority`, `workerUUID`, `killJob`, `retryAttempts`, `runPartition`, `noop`, `runResult`, `runLength`, `quality`, `resultSeed`, `runtime`, `walltime`, `additionalRunData`, `worstCaseEndtime`, `worstCaseNextUpdateWhenAssigned`, `lastModified`) VALUES
(1, 'df597efc191e91723a6276edb1e5343d9a4fb5b6', 1, 'RunPartition', '0', 0, 1001, '0.0,0.0,0.001,3.492459653994893E-10,1.0', 0, 'COMPLETE', 'HIGH', 'dca30411-bae0-46bf-80de-a3ba49e9ca6c', 0, 0, -10, 0, 'SAT', 0, 0, 0, 1, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:08', '2014-09-27 16:20:49'),
(2, 'ebaf670296829b4ec7d5455348eb9512e674e55a', 1, 'RunPartition', '0', 1, 1001, '0.0,0.0,0.003,5.820766089991488E-10,1.0', 0, 'COMPLETE', 'HIGH', 'dca30411-bae0-46bf-80de-a3ba49e9ca6c', 0, 0, -10, 0, 'SAT', 0, 0, 1, 3, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:08', '2014-09-27 16:20:49'),
(3, '8106e8d3021ea545d39069b2386545837b894b82', 1, 'RunPartition', '0', 2, 1001, '0.0,0.0,0.005,8.149072525988083E-10,1.0', 0, 'COMPLETE', 'HIGH', 'dca30411-bae0-46bf-80de-a3ba49e9ca6c', 0, 0, -10, 0, 'SAT', 0, 0, 2, 5, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:08', '2014-09-27 16:20:49'),
(4, '5f7ed643be25b5185c976f0d6c70aa8c7f33cb97', 1, 'RunPartition', '0', 3, 1001, '0.0,0.0,0.007,1.0477378961984678E-9,1.0', 0, 'COMPLETE', 'HIGH', 'dca30411-bae0-46bf-80de-a3ba49e9ca6c', 0, 0, -10, 0, 'SAT', 0, 0, 3, 7, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:08', '2014-09-27 16:20:49'),
(5, '955b23678a94fa32625bf993e0af084e24472196', 1, 'RunPartition', '0', 4, 1001, '0.0,0.0,0.009,1.2805685397981274E-9,1.0', 0, 'COMPLETE', 'HIGH', 'dca30411-bae0-46bf-80de-a3ba49e9ca6c', 0, 0, -10, 0, 'SAT', 0, 0, 4, 9, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:08', '2014-09-27 16:20:49');

-- --------------------------------------------------------

--
-- Table structure for table `junit_pool_switch1_version`
--

CREATE TABLE IF NOT EXISTS `junit_pool_switch1_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` varchar(255) NOT NULL,
  `hash` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash` (`hash`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Dumping data for table `junit_pool_switch1_version`
--

INSERT INTO `junit_pool_switch1_version` (`id`, `version`, `hash`) VALUES
(1, 'v0.92.00dev-development-112 (8a5109587631)', '84ed1641e09245a2869dfe555e546f1bfc369c1c');

-- --------------------------------------------------------

--
-- Table structure for table `junit_pool_switch1_workers`
--

CREATE TABLE IF NOT EXISTS `junit_pool_switch1_workers` (
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

--
-- Dumping data for table `junit_pool_switch1_workers`
--

INSERT INTO `junit_pool_switch1_workers` (`workerUUID`, `hostname`, `username`, `jobID`, `status`, `version`, `crashInfo`, `startTime`, `startWeekYear`, `originalEndTime`, `currentlyIdle`, `endTime_UPDATEABLE`, `runsToBatch_UPDATEABLE`, `minRunsToBatch_UPDATEABLE`, `maxRunsToBatch_UPDATEABLE`, `autoAdjustBatchSize_UPDATEABLE`, `delayBetweenRequests_UPDATEABLE`, `pool_UPDATEABLE`, `poolIdleTimeLimit_UPDATEABLE`, `workerIdleTime_UPDATEABLE`, `concurrencyFactor_UPDATEABLE`, `upToDate`, `worstCaseNextUpdateWhenRunning`, `lastModified`) VALUES
('dca30411-bae0-46bf-80de-a3ba49e9ca6c', 'hilbert', 'sjr', 'CLI/22882@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Pool changed to junit_pool_switch2', '2014-09-27 09:20:47', 392014, '2014-09-28 09:19:46', 1, '2014-09-28 09:19:46', 11, 1, 100, 1, 1, 'junit_pool_switch2', 14400000, 1, 4, 0, '2014-09-27 09:20:51', '2014-09-27 16:20:52');

-- --------------------------------------------------------

--
-- Table structure for table `junit_pool_switch2_commandTable`
--

CREATE TABLE IF NOT EXISTS `junit_pool_switch2_commandTable` (
  `commandID` int(11) NOT NULL AUTO_INCREMENT,
  `commandString` text NOT NULL,
  PRIMARY KEY (`commandID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `junit_pool_switch2_commandTable`
--

INSERT INTO `junit_pool_switch2_commandTable` (`commandID`, `commandString`) VALUES
(1, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite');

-- --------------------------------------------------------

--
-- Table structure for table `junit_pool_switch2_execConfig`
--

CREATE TABLE IF NOT EXISTS `junit_pool_switch2_execConfig` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `junit_pool_switch2_execConfig`
--

INSERT INTO `junit_pool_switch2_execConfig` (`algorithmExecutionConfigID`, `algorithmExecutionConfigHashCode`, `algorithmExecutable`, `algorithmExecutableDirectory`, `parameterFile`, `executeOnCluster`, `deterministicAlgorithm`, `cutoffTime`, `algorithmExecutionConfigurationJSON`, `lastModified`) VALUES
(1, '57922f4090e9e5a9a7633f2e0898e81b400a02e7', 'ignore', '/home/sjr/git/MySQLDBTAE', '/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt', 0, 0, 500, '{"@algo-exec-config-id":2,"algo-exec":"ignore","algo-exec-dir":"/home/sjr/git/MySQLDBTAE","algo-pcs":{"@pcs-id":2,"pcs-filename":"/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt","pcs-text":"#Used for getting specific results out from a target wrapper\\n#Some invalid values are specified so that we can test code\\n\\nsolved { SAT, UNSAT, TIMEOUT, CRASHED, ABORT, INVALID } [SAT]\\nruntime [0,1000] [0]\\nrunlength [0,1000000][0]\\nquality [0, 1000000] [0]\\nseed [ -1,4294967295][1]i\\n","pcs-subspace":{}},"algo-cutoff":500.0,"algo-deterministic":false,"algo-tae-context":{}}', '2014-09-27 16:20:49');

-- --------------------------------------------------------

--
-- Table structure for table `junit_pool_switch2_runConfigs`
--

CREATE TABLE IF NOT EXISTS `junit_pool_switch2_runConfigs` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=6 ;

--
-- Dumping data for table `junit_pool_switch2_runConfigs`
--

INSERT INTO `junit_pool_switch2_runConfigs` (`runConfigID`, `runConfigUUID`, `execConfigID`, `problemInstance`, `instanceSpecificInformation`, `seed`, `cutoffTime`, `paramConfiguration`, `cutoffLessThanMax`, `status`, `priority`, `workerUUID`, `killJob`, `retryAttempts`, `runPartition`, `noop`, `runResult`, `runLength`, `quality`, `resultSeed`, `runtime`, `walltime`, `additionalRunData`, `worstCaseEndtime`, `worstCaseNextUpdateWhenAssigned`, `lastModified`) VALUES
(1, 'df597efc191e91723a6276edb1e5343d9a4fb5b6', 1, 'RunPartition', '0', 0, 1001, '0.0,0.0,0.001,3.492459653994893E-10,1.0', 0, 'COMPLETE', 'HIGH', 'aad2b109-bd7e-44fd-9295-80fef885b853', 0, 1, -10, 0, 'SAT', 0, 0, 0, 1, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:11', '2014-09-27 16:20:51'),
(2, 'ebaf670296829b4ec7d5455348eb9512e674e55a', 1, 'RunPartition', '0', 1, 1001, '0.0,0.0,0.003,5.820766089991488E-10,1.0', 0, 'COMPLETE', 'HIGH', 'aad2b109-bd7e-44fd-9295-80fef885b853', 0, 1, -10, 0, 'SAT', 0, 0, 1, 3, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:11', '2014-09-27 16:20:51'),
(3, '8106e8d3021ea545d39069b2386545837b894b82', 1, 'RunPartition', '0', 2, 1001, '0.0,0.0,0.005,8.149072525988083E-10,1.0', 0, 'COMPLETE', 'HIGH', 'aad2b109-bd7e-44fd-9295-80fef885b853', 0, 1, -10, 0, 'SAT', 0, 0, 2, 5, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:11', '2014-09-27 16:20:51'),
(4, '5f7ed643be25b5185c976f0d6c70aa8c7f33cb97', 1, 'RunPartition', '0', 3, 1001, '0.0,0.0,0.007,1.0477378961984678E-9,1.0', 0, 'COMPLETE', 'HIGH', 'aad2b109-bd7e-44fd-9295-80fef885b853', 0, 1, -10, 0, 'SAT', 0, 0, 3, 7, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:11', '2014-09-27 16:20:51'),
(5, '955b23678a94fa32625bf993e0af084e24472196', 1, 'RunPartition', '0', 4, 1001, '0.0,0.0,0.009,1.2805685397981274E-9,1.0', 0, 'COMPLETE', 'HIGH', 'aad2b109-bd7e-44fd-9295-80fef885b853', 0, 1, -10, 0, 'SAT', 0, 0, 4, 9, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:11', '2014-09-27 16:20:51');

-- --------------------------------------------------------

--
-- Table structure for table `junit_pool_switch2_version`
--

CREATE TABLE IF NOT EXISTS `junit_pool_switch2_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` varchar(255) NOT NULL,
  `hash` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash` (`hash`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Dumping data for table `junit_pool_switch2_version`
--

INSERT INTO `junit_pool_switch2_version` (`id`, `version`, `hash`) VALUES
(1, 'v0.92.00dev-development-112 (8a5109587631)', '84ed1641e09245a2869dfe555e546f1bfc369c1c');

-- --------------------------------------------------------

--
-- Table structure for table `junit_pool_switch2_workers`
--

CREATE TABLE IF NOT EXISTS `junit_pool_switch2_workers` (
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

--
-- Dumping data for table `junit_pool_switch2_workers`
--

INSERT INTO `junit_pool_switch2_workers` (`workerUUID`, `hostname`, `username`, `jobID`, `status`, `version`, `crashInfo`, `startTime`, `startWeekYear`, `originalEndTime`, `currentlyIdle`, `endTime_UPDATEABLE`, `runsToBatch_UPDATEABLE`, `minRunsToBatch_UPDATEABLE`, `maxRunsToBatch_UPDATEABLE`, `autoAdjustBatchSize_UPDATEABLE`, `delayBetweenRequests_UPDATEABLE`, `pool_UPDATEABLE`, `poolIdleTimeLimit_UPDATEABLE`, `workerIdleTime_UPDATEABLE`, `concurrencyFactor_UPDATEABLE`, `upToDate`, `worstCaseNextUpdateWhenRunning`, `lastModified`) VALUES
('aad2b109-bd7e-44fd-9295-80fef885b853', 'hilbert', 'sjr', 'CLI/22882@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Triggered By Shutdown Hook (probably shutting down due to SIGTERM or SIGINT)', '2014-09-27 09:20:47', 392014, '2014-09-28 09:18:42', 1, '2014-09-28 09:18:42', 5, 1, 100, 1, 1, 'junit_pool_switch2', 14400000, 0, 4, 1, '2014-09-27 09:20:52', '2014-09-27 16:20:52');

-- --------------------------------------------------------

--
-- Table structure for table `junit_priority_order_commandTable`
--

CREATE TABLE IF NOT EXISTS `junit_priority_order_commandTable` (
  `commandID` int(11) NOT NULL AUTO_INCREMENT,
  `commandString` text NOT NULL,
  PRIMARY KEY (`commandID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Dumping data for table `junit_priority_order_commandTable`
--

INSERT INTO `junit_priority_order_commandTable` (`commandID`, `commandString`) VALUES
(1, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite'),
(2, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite');

-- --------------------------------------------------------

--
-- Table structure for table `junit_priority_order_execConfig`
--

CREATE TABLE IF NOT EXISTS `junit_priority_order_execConfig` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Dumping data for table `junit_priority_order_execConfig`
--

INSERT INTO `junit_priority_order_execConfig` (`algorithmExecutionConfigID`, `algorithmExecutionConfigHashCode`, `algorithmExecutable`, `algorithmExecutableDirectory`, `parameterFile`, `executeOnCluster`, `deterministicAlgorithm`, `cutoffTime`, `algorithmExecutionConfigurationJSON`, `lastModified`) VALUES
(1, '57922f4090e9e5a9a7633f2e0898e81b400a02e7', 'ignore', '/home/sjr/git/MySQLDBTAE', '/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt', 0, 0, 500, '{"@algo-exec-config-id":2,"algo-exec":"ignore","algo-exec-dir":"/home/sjr/git/MySQLDBTAE","algo-pcs":{"@pcs-id":2,"pcs-filename":"/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFile.txt","pcs-text":"#Used for getting specific results out from a target wrapper\\n#Some invalid values are specified so that we can test code\\n\\nsolved { SAT, UNSAT, TIMEOUT, CRASHED, ABORT, INVALID } [SAT]\\nruntime [0,1000] [0]\\nrunlength [0,1000000][0]\\nquality [0, 1000000] [0]\\nseed [ -1,4294967295][1]i\\n","pcs-subspace":{}},"algo-cutoff":500.0,"algo-deterministic":false,"algo-tae-context":{}}', '2014-09-27 16:20:52');

-- --------------------------------------------------------

--
-- Table structure for table `junit_priority_order_runConfigs`
--

CREATE TABLE IF NOT EXISTS `junit_priority_order_runConfigs` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=7 ;

-- --------------------------------------------------------

--
-- Table structure for table `junit_priority_order_version`
--

CREATE TABLE IF NOT EXISTS `junit_priority_order_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` varchar(255) NOT NULL,
  `hash` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash` (`hash`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;

--
-- Dumping data for table `junit_priority_order_version`
--

INSERT INTO `junit_priority_order_version` (`id`, `version`, `hash`) VALUES
(1, 'v0.92.00dev-development-112 (8a5109587631)', '84ed1641e09245a2869dfe555e546f1bfc369c1c');

-- --------------------------------------------------------

--
-- Table structure for table `junit_priority_order_workers`
--

CREATE TABLE IF NOT EXISTS `junit_priority_order_workers` (
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

--
-- Dumping data for table `junit_priority_order_workers`
--

INSERT INTO `junit_priority_order_workers` (`workerUUID`, `hostname`, `username`, `jobID`, `status`, `version`, `crashInfo`, `startTime`, `startWeekYear`, `originalEndTime`, `currentlyIdle`, `endTime_UPDATEABLE`, `runsToBatch_UPDATEABLE`, `minRunsToBatch_UPDATEABLE`, `maxRunsToBatch_UPDATEABLE`, `autoAdjustBatchSize_UPDATEABLE`, `delayBetweenRequests_UPDATEABLE`, `pool_UPDATEABLE`, `poolIdleTimeLimit_UPDATEABLE`, `workerIdleTime_UPDATEABLE`, `concurrencyFactor_UPDATEABLE`, `upToDate`, `worstCaseNextUpdateWhenRunning`, `lastModified`) VALUES
('f5bb59aa-dc58-4f39-9176-cfbadfc90d33', 'hilbert', 'sjr', 'CLI/22979@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Triggered By Shutdown Hook (probably shutting down due to SIGTERM or SIGINT)', '2014-09-27 09:20:52', 392014, '2014-09-28 09:19:51', 0, '2014-09-28 09:19:51', 2, 1, 100, 1, 2, 'junit_priority_order', 14400000, 0, 4, 1, '2014-09-27 09:20:58', '2014-09-27 16:20:58');

-- --------------------------------------------------------

--
-- Table structure for table `junit_tae_test_commandTable`
--

CREATE TABLE IF NOT EXISTS `junit_tae_test_commandTable` (
  `commandID` int(11) NOT NULL AUTO_INCREMENT,
  `commandString` text NOT NULL,
  PRIMARY KEY (`commandID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=8 ;

--
-- Dumping data for table `junit_tae_test_commandTable`
--

INSERT INTO `junit_tae_test_commandTable` (`commandID`, `commandString`) VALUES
(1, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite'),
(2, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite'),
(3, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite'),
(4, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite'),
(5, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite'),
(6, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite'),
(7, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite');

-- --------------------------------------------------------

--
-- Table structure for table `junit_tae_test_execConfig`
--

CREATE TABLE IF NOT EXISTS `junit_tae_test_execConfig` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=8 ;

--
-- Dumping data for table `junit_tae_test_execConfig`
--

INSERT INTO `junit_tae_test_execConfig` (`algorithmExecutionConfigID`, `algorithmExecutionConfigHashCode`, `algorithmExecutable`, `algorithmExecutableDirectory`, `parameterFile`, `executeOnCluster`, `deterministicAlgorithm`, `cutoffTime`, `algorithmExecutionConfigurationJSON`, `lastModified`) VALUES
(1, 'ab7f44ad86b35e0d29489a964abfed9cff31fceb', 'ignore', '/home/sjr/git/MySQLDBTAE', '/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFileWithKilled.txt', 0, 0, 500, '{"@algo-exec-config-id":2,"algo-exec":"ignore","algo-exec-dir":"/home/sjr/git/MySQLDBTAE","algo-pcs":{"@pcs-id":2,"pcs-filename":"/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFileWithKilled.txt","pcs-text":"#Used for getting specific results out from a target wrapper\\n#Some invalid values are specified so that we can test code\\n\\nsolved { SAT, UNSAT, TIMEOUT, CRASHED, ABORT, INVALID, KILLED } [SAT]\\nruntime [0,1000] [0]\\nrunlength [0,1000000][0]\\nquality [0, 1000000] [0]\\nseed [ -1,4294967295][1]i\\n","pcs-subspace":{}},"algo-cutoff":500.0,"algo-deterministic":false,"algo-tae-context":{}}', '2014-09-27 16:21:31');

-- --------------------------------------------------------

--
-- Table structure for table `junit_tae_test_idlebadexec_commandTable`
--

CREATE TABLE IF NOT EXISTS `junit_tae_test_idlebadexec_commandTable` (
  `commandID` int(11) NOT NULL AUTO_INCREMENT,
  `commandString` text NOT NULL,
  PRIMARY KEY (`commandID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `junit_tae_test_idlebadexec_commandTable`
--

INSERT INTO `junit_tae_test_idlebadexec_commandTable` (`commandID`, `commandString`) VALUES
(1, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite');

-- --------------------------------------------------------

--
-- Table structure for table `junit_tae_test_idlebadexec_execConfig`
--

CREATE TABLE IF NOT EXISTS `junit_tae_test_idlebadexec_execConfig` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `junit_tae_test_idlebadexec_execConfig`
--

INSERT INTO `junit_tae_test_idlebadexec_execConfig` (`algorithmExecutionConfigID`, `algorithmExecutionConfigHashCode`, `algorithmExecutable`, `algorithmExecutableDirectory`, `parameterFile`, `executeOnCluster`, `deterministicAlgorithm`, `cutoffTime`, `algorithmExecutionConfigurationJSON`, `lastModified`) VALUES
(1, '8fb237294588c00f8f019430b753ade1bb3f4bf2', 'ignore', '/home/sjr/git/MySQLDBTAE', '/tmp/junittests718769147255409301badexec', 0, 0, 500, '{"@algo-exec-config-id":2,"algo-exec":"ignore","algo-exec-dir":"/home/sjr/git/MySQLDBTAE","algo-pcs":{"@pcs-id":2,"pcs-filename":"/tmp/junittests718769147255409301badexec","pcs-text":"boo { yay, hoo, yeah } [hoo]\\n","pcs-subspace":{}},"algo-cutoff":500.0,"algo-deterministic":false,"algo-tae-context":{}}', '2014-09-27 16:18:08');

-- --------------------------------------------------------

--
-- Table structure for table `junit_tae_test_idlebadexec_runConfigs`
--

CREATE TABLE IF NOT EXISTS `junit_tae_test_idlebadexec_runConfigs` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;

-- --------------------------------------------------------

--
-- Table structure for table `junit_tae_test_idlebadexec_version`
--

CREATE TABLE IF NOT EXISTS `junit_tae_test_idlebadexec_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` varchar(255) NOT NULL,
  `hash` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash` (`hash`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `junit_tae_test_idlebadexec_version`
--

INSERT INTO `junit_tae_test_idlebadexec_version` (`id`, `version`, `hash`) VALUES
(1, 'v0.92.00dev-development-112 (8a5109587631)', '84ed1641e09245a2869dfe555e546f1bfc369c1c');

-- --------------------------------------------------------

--
-- Table structure for table `junit_tae_test_idlebadexec_workers`
--

CREATE TABLE IF NOT EXISTS `junit_tae_test_idlebadexec_workers` (
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

-- --------------------------------------------------------

--
-- Table structure for table `junit_tae_test_idle_commandTable`
--

CREATE TABLE IF NOT EXISTS `junit_tae_test_idle_commandTable` (
  `commandID` int(11) NOT NULL AUTO_INCREMENT,
  `commandString` text NOT NULL,
  PRIMARY KEY (`commandID`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `junit_tae_test_idle_commandTable`
--

INSERT INTO `junit_tae_test_idle_commandTable` (`commandID`, `commandString`) VALUES
(1, 'org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 59407 -testLoaderClass org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames ca.ubc.cpsc.beta.mysqldbtae.MySQLDBTestSuite');

-- --------------------------------------------------------

--
-- Table structure for table `junit_tae_test_idle_execConfig`
--

CREATE TABLE IF NOT EXISTS `junit_tae_test_idle_execConfig` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `junit_tae_test_idle_execConfig`
--

INSERT INTO `junit_tae_test_idle_execConfig` (`algorithmExecutionConfigID`, `algorithmExecutionConfigHashCode`, `algorithmExecutable`, `algorithmExecutableDirectory`, `parameterFile`, `executeOnCluster`, `deterministicAlgorithm`, `cutoffTime`, `algorithmExecutionConfigurationJSON`, `lastModified`) VALUES
(1, 'ab7f44ad86b35e0d29489a964abfed9cff31fceb', 'ignore', '/home/sjr/git/MySQLDBTAE', '/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFileWithKilled.txt', 0, 0, 500, '{"@algo-exec-config-id":2,"algo-exec":"ignore","algo-exec-dir":"/home/sjr/git/MySQLDBTAE","algo-pcs":{"@pcs-id":2,"pcs-filename":"/home/sjr/git/AEATK/bin/paramFiles/paramEchoParamFileWithKilled.txt","pcs-text":"#Used for getting specific results out from a target wrapper\\n#Some invalid values are specified so that we can test code\\n\\nsolved { SAT, UNSAT, TIMEOUT, CRASHED, ABORT, INVALID, KILLED } [SAT]\\nruntime [0,1000] [0]\\nrunlength [0,1000000][0]\\nquality [0, 1000000] [0]\\nseed [ -1,4294967295][1]i\\n","pcs-subspace":{}},"algo-cutoff":500.0,"algo-deterministic":false,"algo-tae-context":{}}', '2014-09-27 16:17:54');

-- --------------------------------------------------------

--
-- Table structure for table `junit_tae_test_idle_runConfigs`
--

CREATE TABLE IF NOT EXISTS `junit_tae_test_idle_runConfigs` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `junit_tae_test_idle_runConfigs`
--

INSERT INTO `junit_tae_test_idle_runConfigs` (`runConfigID`, `runConfigUUID`, `execConfigID`, `problemInstance`, `instanceSpecificInformation`, `seed`, `cutoffTime`, `paramConfiguration`, `cutoffLessThanMax`, `status`, `priority`, `workerUUID`, `killJob`, `retryAttempts`, `runPartition`, `noop`, `runResult`, `runLength`, `quality`, `resultSeed`, `runtime`, `walltime`, `additionalRunData`, `worstCaseEndtime`, `worstCaseNextUpdateWhenAssigned`, `lastModified`) VALUES
(1, '16b08fe271c3e09c98e29c0cb02a9acf3606c6ec', 1, 'TestInstance', '0', 1681395156, 1001, '0.7268160800678318,0.9732869992869586,0.8187146073086693,0.3914803166660759,3.0', 0, 'COMPLETE', 'HIGH', 'b47f8ade-faa4-44a0-a050-3df48e54ee3b', 0, 1, 0, 0, 'TIMEOUT', 973286.9992869586, 726816.0800678318, 1681395156, 818.7146073086693, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:41:15', '2014-09-27 16:17:55');

-- --------------------------------------------------------

--
-- Table structure for table `junit_tae_test_idle_version`
--

CREATE TABLE IF NOT EXISTS `junit_tae_test_idle_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` varchar(255) NOT NULL,
  `hash` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash` (`hash`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Dumping data for table `junit_tae_test_idle_version`
--

INSERT INTO `junit_tae_test_idle_version` (`id`, `version`, `hash`) VALUES
(1, 'v0.92.00dev-development-112 (8a5109587631)', '84ed1641e09245a2869dfe555e546f1bfc369c1c');

-- --------------------------------------------------------

--
-- Table structure for table `junit_tae_test_idle_workers`
--

CREATE TABLE IF NOT EXISTS `junit_tae_test_idle_workers` (
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

--
-- Dumping data for table `junit_tae_test_idle_workers`
--

INSERT INTO `junit_tae_test_idle_workers` (`workerUUID`, `hostname`, `username`, `jobID`, `status`, `version`, `crashInfo`, `startTime`, `startWeekYear`, `originalEndTime`, `currentlyIdle`, `endTime_UPDATEABLE`, `runsToBatch_UPDATEABLE`, `minRunsToBatch_UPDATEABLE`, `maxRunsToBatch_UPDATEABLE`, `autoAdjustBatchSize_UPDATEABLE`, `delayBetweenRequests_UPDATEABLE`, `pool_UPDATEABLE`, `poolIdleTimeLimit_UPDATEABLE`, `workerIdleTime_UPDATEABLE`, `concurrencyFactor_UPDATEABLE`, `upToDate`, `worstCaseNextUpdateWhenRunning`, `lastModified`) VALUES
('b47f8ade-faa4-44a0-a050-3df48e54ee3b', 'hilbert', 'sjr', 'CLI/20404@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Idle limit reached: 11.0 versus limit: 10', '2014-09-27 09:17:49', 392014, '2014-09-28 09:16:48', 1, '2014-09-28 09:16:48', 1, 1, 100, 1, 1, 'junit_tae_test_idle', 14400000, 16, 4, 1, '2014-09-27 09:18:07', '2014-09-27 16:18:07');

-- --------------------------------------------------------

--
-- Table structure for table `junit_tae_test_runConfigs`
--

CREATE TABLE IF NOT EXISTS `junit_tae_test_runConfigs` (
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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1447 ;

--
-- Dumping data for table `junit_tae_test_runConfigs`
--

INSERT INTO `junit_tae_test_runConfigs` (`runConfigID`, `runConfigUUID`, `execConfigID`, `problemInstance`, `instanceSpecificInformation`, `seed`, `cutoffTime`, `paramConfiguration`, `cutoffLessThanMax`, `status`, `priority`, `workerUUID`, `killJob`, `retryAttempts`, `runPartition`, `noop`, `runResult`, `runLength`, `quality`, `resultSeed`, `runtime`, `walltime`, `additionalRunData`, `worstCaseEndtime`, `worstCaseNextUpdateWhenAssigned`, `lastModified`) VALUES
(1, '6b275971059d6ef4a951bfab892dbe49ee0b3d69', 1, 'RunPartition', '0', 0, 1001, '0.0,0.0,0.001,3.492459653994893E-10,1.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 0, -10, 0, 'SAT', 0, 0, 0, 1, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:20', '2014-09-27 16:21:01'),
(2, '892b9bcea196dd5f0c0d49cdd52912963df416ce', 1, 'RunPartition', '0', 1, 1001, '0.0,0.0,0.003,5.820766089991488E-10,1.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 0, -10, 0, 'SAT', 0, 0, 1, 3, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:20', '2014-09-27 16:21:01'),
(3, '6d327e80a57240dd9c28f72403a284d89e6d2a1d', 1, 'RunPartition', '0', 2, 1001, '0.0,0.0,0.005,8.149072525988083E-10,1.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 0, -10, 0, 'SAT', 0, 0, 2, 5, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:20', '2014-09-27 16:21:01'),
(4, '2e6ecde34fb7bfebd4746a8cf0b826ade94a6105', 1, 'RunPartition', '0', 3, 1001, '0.0,0.0,0.007,1.0477378961984678E-9,1.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 0, -10, 0, 'SAT', 0, 0, 3, 7, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:20', '2014-09-27 16:21:01'),
(5, '401736020aba0866f40455ce025522f15e5f376c', 1, 'RunPartition', '0', 4, 1001, '0.0,0.0,0.009,1.2805685397981274E-9,1.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 0, -10, 0, 'SAT', 0, 0, 4, 9, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:20', '2014-09-27 16:21:01'),
(11, '42834ceddf965eea9dae64cf406be53483fea811', 1, 'RunPartition', '0', 0, 1001, '0.0,0.0,0.001,3.492459653994893E-10,1.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 0, -9, 0, 'SAT', 0, 0, 0, 0, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:21', '2014-09-27 16:21:03'),
(12, '9c506a6b787493491c89d4b43e3b6daffd93f6a3', 1, 'RunPartition', '0', 1, 1001, '0.0,0.0,0.003,5.820766089991488E-10,1.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 0, -9, 0, 'SAT', 0, 0, 1, 2, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:21', '2014-09-27 16:21:03'),
(13, 'ae8ec588c2abd3c39235ff32ba84229f6d59db2c', 1, 'RunPartition', '0', 2, 1001, '0.0,0.0,0.005,8.149072525988083E-10,1.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 0, -9, 0, 'SAT', 0, 0, 2, 4, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:21', '2014-09-27 16:21:03'),
(14, '34cf716543a91a823efc685787cc26fa7f2c80e0', 1, 'RunPartition', '0', 3, 1001, '0.0,0.0,0.007,1.0477378961984678E-9,1.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 0, -9, 0, 'SAT', 0, 0, 3, 6, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:21', '2014-09-27 16:21:03'),
(15, '2048b7d32f65fe0c5d9ee9ce026546407b04fb4c', 1, 'RunPartition', '0', 4, 1001, '0.0,0.0,0.009,1.2805685397981274E-9,1.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 0, -9, 0, 'SAT', 0, 0, 4, 8, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:21', '2014-09-27 16:21:03'),
(1347, '14ec3430cde6157cf3ff3ff8ce7695451a08d3cf', 1, 'TestInstance', '0', 3630782829, 1001, '0.7894190538076044,0.48478233007569416,0.9210071884556118,0.8453575031959085,1.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'SAT', 484782.33007569413, 789419.0538076044, 3630782829, 921.0071884556118, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:46', '2014-09-27 16:21:26'),
(1348, 'f10a901530753c5ceca7d0faf7bfc92a10eabf46', 1, 'TestInstance', '0', 1787145520, 1001, '0.08292589803299177,0.5171921705780883,0.42337476338568203,0.41610224197709417,7.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'KILLED', 517192.1705780883, 82925.89803299177, 1787145520, 423.374763385682, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:47', '2014-09-27 16:21:27'),
(1349, 'abe48067ec9d37217a1ebf7d649c808520a43979', 1, 'TestInstance', '0', 2139355888, 1001, '0.2223311427652476,0.20234938462415453,0.3476922842856005,0.49810760864100706,1.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'SAT', 202349.38462415454, 222331.14276524758, 2139355888, 347.6922842856005, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:47', '2014-09-27 16:21:27'),
(1350, '31159e9c6a4adb1448b643a6fc47282ab122e91b', 1, 'TestInstance', '0', 3820686668, 1001, '0.9035841731726574,0.95052238643012,0.12073480193617814,0.8895729362523246,3.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'TIMEOUT', 950522.38643012, 903584.1731726574, 3820686668, 120.73480193617814, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:48', '2014-09-27 16:21:28'),
(1351, '611a8ba622952bf4cef7d314a5a8d2323c712ed8', 1, 'TestInstance', '0', 91821223, 1001, '0.1495749843335974,0.40813158263097926,0.6032423938167171,0.021378794796443824,7.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'KILLED', 408131.58263097925, 149574.9843335974, 91821223, 603.242393816717, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:48', '2014-09-27 16:21:28'),
(1352, '83bdc39bd8687268269bb6316da4bb2fc8b4b9c6', 1, 'TestInstance', '0', 1884369399, 1001, '0.07755341434131369,0.6137005305788182,0.4268131684418599,0.4387389402979196,3.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'TIMEOUT', 613700.5305788183, 77553.41434131369, 1884369399, 426.8131684418599, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:48', '2014-09-27 16:21:28'),
(1353, 'e15db19b6fa099212dbaf5736c4d43e619987a2c', 1, 'TestInstance', '0', 1150954756, 1001, '0.9348723212790578,0.4153195507282663,0.9140622097064405,0.26797753694281506,1.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'SAT', 415319.5507282663, 934872.3212790579, 1150954756, 914.0622097064405, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:48', '2014-09-27 16:21:28'),
(1354, '2b60203ec863f195a62c772ad96911f148e7f794', 1, 'TestInstance', '0', 1037384621, 1001, '0.2333471802813848,0.7049914946041956,0.9738221707349122,0.24153492931706483,3.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'TIMEOUT', 704991.4946041956, 233347.1802813848, 1037384621, 973.8221707349122, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:48', '2014-09-27 16:21:28'),
(1355, '16716f25c287a21fdf05e873828ea70da5d91b4a', 1, 'TestInstance', '0', 3105382752, 1001, '0.8988343348624921,0.12453234214122466,0.6543743530250847,0.7230282651206879,2.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'UNSAT', 124532.34214122467, 898834.334862492, 3105382752, 654.3743530250847, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:48', '2014-09-27 16:21:28'),
(1356, '507cc9e2c2337aa090f1b02545151d449c611584', 1, 'TestInstance', '0', 3624227959, 1001, '0.9090757115483051,0.4826042869943673,0.7685827213950579,0.8438313285950964,2.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'UNSAT', 482604.2869943673, 909075.7115483051, 3624227959, 768.5827213950579, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:48', '2014-09-27 16:21:28'),
(1357, 'a4b84799a145453c139ab15c194c1c0ff37e14e5', 1, 'TestInstance', '0', 4237449716, 1001, '0.4427762661023933,0.8351148335412382,0.39841648079278014,0.9866081449467204,7.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'KILLED', 835114.8335412382, 442776.2661023933, 4237449716, 398.41648079278013, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:48', '2014-09-27 16:21:28'),
(1358, 'd8bfa96302a46ef51f8da30d4e487acdd19974dd', 1, 'TestInstance', '0', 3759892482, 1001, '0.8943646210924777,0.49208951450858285,0.39723901765452496,0.8754181867988272,7.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'KILLED', 492089.5145085828, 894364.6210924777, 3759892482, 397.23901765452496, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:48', '2014-09-27 16:21:28'),
(1359, '7b30a4111229fe09225df00bbba98427c145dccd', 1, 'TestInstance', '0', 262324280, 1001, '0.23058621812524505,0.8531074586520498,0.7629978383270831,0.06107713129346326,3.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'TIMEOUT', 853107.4586520499, 230586.21812524504, 262324280, 762.9978383270832, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:48', '2014-09-27 16:21:28'),
(1360, 'f6e01ff958013a307c64b2c7d8dfbacffb0ab0aa', 1, 'TestInstance', '0', 1132552363, 1001, '0.050312802303186355,0.669250339173059,0.5761210409604317,0.2636928959368512,7.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'KILLED', 669250.339173059, 50312.802303186356, 1132552363, 576.1210409604317, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:48', '2014-09-27 16:21:28'),
(1361, 'b7fb37d63111e559f703b251e11b16241b0e0c49', 1, 'TestInstance', '0', 953774962, 1001, '0.5374262216882114,0.18320679575774035,0.6547100746106718,0.22206803860094676,3.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'TIMEOUT', 183206.79575774036, 537426.2216882114, 953774962, 654.7100746106718, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:48', '2014-09-27 16:21:28'),
(1362, '78f120002019aa3868597a765a99041089756747', 1, 'TestInstance', '0', 3980594701, 1001, '0.5389535345239908,0.4915212473971773,0.5806430799285766,0.9268044264924702,3.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'TIMEOUT', 491521.24739717727, 538953.5345239907, 3980594701, 580.6430799285765, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:49', '2014-09-27 16:21:29'),
(1363, 'cd3056c4a821a18a193748170babda63d66e1ed5', 1, 'TestInstance', '0', 690938077, 1001, '0.958201131771746,0.6595127981358185,0.814998689386325,0.16087155750466708,7.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'KILLED', 659512.7981358186, 958201.1317717461, 690938077, 814.998689386325, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:49', '2014-09-27 16:21:29'),
(1364, '88a3c11642340d964cc56404898d0bd5ad91903f', 1, 'TestInstance', '0', 2716735787, 1001, '0.4136538034338957,0.7178748275180269,0.3370552149421242,0.6325393421266835,7.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'KILLED', 717874.8275180269, 413653.8034338957, 2716735787, 337.0552149421242, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:49', '2014-09-27 16:21:29'),
(1365, '572652a2cdee8d2c596c0acb768fac12299e0d0b', 1, 'TestInstance', '0', 1396440038, 1001, '0.3067484575288155,0.49058231247240613,0.6036882049650187,0.32513403314511896,7.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'KILLED', 490582.31247240613, 306748.45752881555, 1396440038, 603.6882049650187, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:49', '2014-09-27 16:21:29'),
(1366, '1fe894f5c93612b7fd273913d054ddc328fa38b9', 1, 'TestInstance', '0', 1811892159, 1001, '0.5018874235867788,0.8799894559131948,0.2679321544815814,0.4218640178623926,3.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'TIMEOUT', 879989.4559131948, 501887.42358677887, 1811892159, 267.9321544815814, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:49', '2014-09-27 16:21:29'),
(1367, '33337409f82189e9f32b8831b58109257350dadf', 1, 'TestInstance', '0', 2303846229, 1001, '0.275126368969981,0.3597478915926119,0.8128036857051358,0.5364060006019645,2.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'UNSAT', 359747.8915926119, 275126.368969981, 2303846229, 812.8036857051359, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:49', '2014-09-27 16:21:29'),
(1368, '2da72c8f40b364e936a036a5902794ad989d98cf', 1, 'TestInstance', '0', 2830360663, 1001, '0.49113253530993173,0.9034900666777647,0.06475340751706704,0.658994695134695,7.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'KILLED', 903490.0666777646, 491132.5353099317, 2830360663, 64.75340751706705, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:49', '2014-09-27 16:21:29'),
(1369, 'a82f3f0de741c737d1469236ebed28ba823c2269', 1, 'TestInstance', '0', 4008159317, 1001, '0.7409476721957479,0.08885427472942153,0.7854567184264143,0.9332223137763277,7.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'KILLED', 88854.27472942154, 740947.6721957478, 4008159317, 785.4567184264143, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:49', '2014-09-27 16:21:29'),
(1370, 'a955352ad3d516ab68f0c35e29827fe78b893a84', 1, 'TestInstance', '0', 653965062, 1001, '0.10270660771503959,0.15520986759067934,0.08218098685024522,0.1522631066263972,2.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'UNSAT', 155209.86759067935, 102706.60771503959, 653965062, 82.18098685024522, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:49', '2014-09-27 16:21:29'),
(1371, '16d426ac731370b6472699257ceb8c7695a2f84d', 1, 'TestInstance', '0', 2126676653, 1001, '0.6827569136543504,0.9145106477845614,0.0749666048773342,0.4951554941956057,3.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'TIMEOUT', 914510.6477845614, 682756.9136543504, 2126676653, 74.96660487733419, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:49', '2014-09-27 16:21:29'),
(1372, 'c9aca4dcca9e9c786ae982ccf8dcdba131f08f29', 1, 'TestInstance', '0', 1931890789, 1001, '0.27109807605589276,0.10796137874194212,0.05251885646401489,0.44980337611637,1.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'SAT', 107961.37874194211, 271098.07605589274, 1931890789, 52.51885646401489, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:49', '2014-09-27 16:21:29'),
(1373, '82475fdccb8e6c9f66732641b51d6349418af906', 1, 'TestInstance', '0', 732947173, 1001, '0.5996580099830894,0.9653644066184053,0.38100558159051845,0.17065256236338697,2.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'UNSAT', 965364.4066184052, 599658.0099830894, 732947173, 381.00558159051843, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:49', '2014-09-27 16:21:29'),
(1374, 'dbe36062d7b86f2aed299e8a1d326d2a24ae855e', 1, 'TestInstance', '0', 2543148852, 1001, '0.03390436160622923,0.5195714967873835,0.6794397450706845,0.5921229843301412,2.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'UNSAT', 519571.49678738345, 33904.36160622923, 2543148852, 679.4397450706845, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:49', '2014-09-27 16:21:29'),
(1375, '573e8a68ff7920198bcd8a6c1f5deb488cef1483', 1, 'TestInstance', '0', 1457601104, 1001, '0.9596553358405326,0.895982575952728,0.39276522326359753,0.3393742035051402,3.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'TIMEOUT', 895982.575952728, 959655.3358405327, 1457601104, 392.76522326359753, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:49', '2014-09-27 16:21:29'),
(1376, '0520bf8f45ee8b475a5cfa584da7d8a8ecc82740', 1, 'TestInstance', '0', 714606828, 1001, '0.685019090965952,0.8400640855641608,0.03453809054899348,0.16638236803319717,1.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'SAT', 840064.0855641608, 685019.090965952, 714606828, 34.53809054899348, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:49', '2014-09-27 16:21:29'),
(1377, '97d3d026ece40e52df3341e1d4188c8e599fe8d2', 1, 'TestInstance', '0', 3954887394, 1001, '0.6994254315654808,0.04090994245460211,0.13610526992815264,0.9208189776584462,3.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'TIMEOUT', 40909.94245460211, 699425.4315654808, 3954887394, 136.10526992815264, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:49', '2014-09-27 16:21:29'),
(1378, 'f97949f860e536356e59292a62e53b1d04ea0a8a', 1, 'TestInstance', '0', 2499605878, 1001, '0.6841551642233327,0.41723020784438825,0.09932612226433635,0.581984845669478,1.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'SAT', 417230.20784438826, 684155.1642233327, 2499605878, 99.32612226433635, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:49', '2014-09-27 16:21:29'),
(1379, '633f6bf6bcb91cabc9e2cb950e614bff154efe7b', 1, 'TestInstance', '0', 1333554406, 1001, '0.8677532878888078,0.7480422452542441,0.026900985266624655,0.31049233097338763,7.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'KILLED', 748042.2452542441, 867753.2878888078, 1333554406, 26.900985266624655, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:50', '2014-09-27 16:21:30'),
(1380, 'e6e9615eaf906b4e4beb8046a5f596b016fa2629', 1, 'TestInstance', '0', 1246534729, 1001, '0.3630110808722864,0.5869399132705095,0.3906006068031974,0.29023148357164313,1.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'SAT', 586939.9132705096, 363011.0808722864, 1246534729, 390.6006068031974, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:50', '2014-09-27 16:21:30'),
(1381, '3f1ddbc43137ddbc8fcd6fcfec6b7e1c7f09fba5', 1, 'TestInstance', '0', 3403962651, 1001, '0.7501986308383916,0.2798323104550552,0.9517755373528238,0.7925468151707792,3.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'TIMEOUT', 279832.3104550552, 750198.6308383916, 3403962651, 951.7755373528238, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:50', '2014-09-27 16:21:30'),
(1382, 'eb26fea82c10c0246eed2c7a128854638252b89a', 1, 'TestInstance', '0', 345345184, 1001, '0.4439831166336192,0.4721696843557476,0.17088138008864562,0.0804069418040088,2.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'UNSAT', 472169.6843557476, 443983.11663361924, 345345184, 170.8813800886456, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:50', '2014-09-27 16:21:30'),
(1383, '4d8f893fec3d7d05df6b02f25a099a306d9d70f9', 1, 'TestInstance', '0', 3125468765, 1001, '0.956496133517027,0.12059782451335033,0.5061949672825286,0.7277049044548289,2.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'UNSAT', 120597.82451335032, 956496.133517027, 3125468765, 506.1949672825285, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:50', '2014-09-27 16:21:30'),
(1384, 'c069529ea225a1b07e5e806d2030d91d0bd35f28', 1, 'TestInstance', '0', 463396614, 1001, '0.17607311436769568,0.44948580129955673,0.10716986286484831,0.10789293222876896,7.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'KILLED', 449485.80129955674, 176073.11436769567, 463396614, 107.16986286484831, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:50', '2014-09-27 16:21:30'),
(1385, '58a7581fe6e8df653df167fcf14520f085ab3c6c', 1, 'TestInstance', '0', 1601456048, 1001, '0.5738797758783847,0.7713531724525575,0.372196750625455,0.3728680427016532,7.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'KILLED', 771353.1724525575, 573879.7758783847, 1601456048, 372.19675062545497, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:50', '2014-09-27 16:21:30'),
(1386, '039a7cc3a1904e35119d3d4e028c9c712d9f9107', 1, 'TestInstance', '0', 3542271854, 1001, '0.8544683893247405,0.26632107014855533,0.8030901428812977,0.8247494359210251,7.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'KILLED', 266321.0701485553, 854468.3893247405, 3542271854, 803.0901428812977, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:50', '2014-09-27 16:21:30'),
(1387, 'c322a60978301d339af29173a69c1ee4c3c7885d', 1, 'TestInstance', '0', 1086823091, 1001, '0.7875105650164537,0.1130990566518839,0.0012141874625895,0.2530457201057473,3.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'TIMEOUT', 113099.0566518839, 787510.5650164536, 1086823091, 1.2141874625895, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:50', '2014-09-27 16:21:30'),
(1388, 'af8f2fc7d78c839adbb5bd4fc30ae1e8b9b54ef3', 1, 'TestInstance', '0', 4104097725, 1001, '0.5612144298017926,0.5828296535012631,0.9589061631023252,0.9555597150568944,3.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'TIMEOUT', 582829.6535012631, 561214.4298017926, 4104097725, 958.9061631023252, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:50', '2014-09-27 16:21:30'),
(1389, 'c45fedcf65e51214b7b1ca81074d2ea372cfa99f', 1, 'TestInstance', '0', 3037354051, 1001, '0.6257350155506421,0.8053363544859818,0.4774522303084112,0.707189098883609,1.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'SAT', 805336.3544859819, 625735.0155506422, 3037354051, 477.45223030841123, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:50', '2014-09-27 16:21:30'),
(1390, '3b93967e0b9eb5a2d31be509a04c12881f937742', 1, 'TestInstance', '0', 1240922507, 1001, '0.8868696033589151,0.10265781367408644,0.9261673014597466,0.288924786311359,1.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'SAT', 102657.81367408644, 886869.6033589151, 1240922507, 926.1673014597466, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:50', '2014-09-27 16:21:30'),
(1391, '638ccbb96ca7d61c1fb4f90f034ce8ca2fa61dbf', 1, 'TestInstance', '0', 1581406094, 1001, '0.6267618732929264,0.004903192276216828,0.44390673060935204,0.3681997990076896,3.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'TIMEOUT', 4903.192276216828, 626761.8732929265, 1581406094, 443.90673060935205, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:50', '2014-09-27 16:21:30'),
(1392, '3cf74392d49c2e3559ba58ddcb3662a276525b67', 1, 'TestInstance', '0', 470591147, 1001, '0.2305592720169709,0.7860690799124032,0.31029336218976766,0.10956803997755794,2.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'UNSAT', 786069.0799124031, 230559.2720169709, 470591147, 310.29336218976766, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:50', '2014-09-27 16:21:30'),
(1393, 'e413122911f44437c5afef5a523abdb5c2b35467', 1, 'TestInstance', '0', 3291007652, 1001, '0.44296151152601504,0.5146514193532804,0.606908012262442,0.7662474300558103,3.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'TIMEOUT', 514651.4193532804, 442961.51152601506, 3291007652, 606.908012262442, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:50', '2014-09-27 16:21:30'),
(1394, '3410a811c36dcfa9a2b4e816d66a76b571436427', 1, 'TestInstance', '0', 271904036, 1001, '0.36844182078770304,0.07279264123750218,0.043025623958277914,0.06330759204847096,1.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'SAT', 72792.64123750218, 368441.82078770304, 271904036, 43.02562395827791, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:50', '2014-09-27 16:21:30'),
(1395, 'fb6f8c8cb6028fdfe10b2419e7319528706c7280', 1, 'TestInstance', '0', 3008306417, 1001, '0.842064955715113,0.9441773928109825,0.0812312512443063,0.7004259195643416,1.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'SAT', 944177.3928109824, 842064.955715113, 3008306417, 81.2312512443063, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:50', '2014-09-27 16:21:30'),
(1396, 'ecfbf30caa0a2667a79d2a2fb4b5b6c558e1e056', 1, 'TestInstance', '0', 1543636952, 1001, '0.75705994102748,0.21620522530504083,0.8279300929222656,0.3594059853676227,3.0', 0, 'COMPLETE', 'HIGH', 'a034f10b-ab73-4f89-bf27-13ff5c852655', 0, 1, -10, 0, 'TIMEOUT', 216205.22530504083, 757059.94102748, 1543636952, 827.9300929222657, 0, '', '1900-01-01 00:00:00', '2014-09-27 10:44:50', '2014-09-27 16:21:30');

-- --------------------------------------------------------

--
-- Table structure for table `junit_tae_test_version`
--

CREATE TABLE IF NOT EXISTS `junit_tae_test_version` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` varchar(255) NOT NULL,
  `hash` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash` (`hash`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=9 ;

--
-- Dumping data for table `junit_tae_test_version`
--

INSERT INTO `junit_tae_test_version` (`id`, `version`, `hash`) VALUES
(1, 'v0.92.00dev-development-112 (8a5109587631)', '84ed1641e09245a2869dfe555e546f1bfc369c1c');

-- --------------------------------------------------------

--
-- Table structure for table `junit_tae_test_workers`
--

CREATE TABLE IF NOT EXISTS `junit_tae_test_workers` (
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

--
-- Dumping data for table `junit_tae_test_workers`
--

INSERT INTO `junit_tae_test_workers` (`workerUUID`, `hostname`, `username`, `jobID`, `status`, `version`, `crashInfo`, `startTime`, `startWeekYear`, `originalEndTime`, `currentlyIdle`, `endTime_UPDATEABLE`, `runsToBatch_UPDATEABLE`, `minRunsToBatch_UPDATEABLE`, `maxRunsToBatch_UPDATEABLE`, `autoAdjustBatchSize_UPDATEABLE`, `delayBetweenRequests_UPDATEABLE`, `pool_UPDATEABLE`, `poolIdleTimeLimit_UPDATEABLE`, `workerIdleTime_UPDATEABLE`, `concurrencyFactor_UPDATEABLE`, `upToDate`, `worstCaseNextUpdateWhenRunning`, `lastModified`) VALUES
('a034f10b-ab73-4f89-bf27-13ff5c852655', 'hilbert', 'sjr', 'CLI/23045@hilbert', 'DONE', 'v0.92.00dev-development-112 (8a5109587631)', 'Triggered By Shutdown Hook (probably shutting down due to SIGTERM or SIGINT)', '2014-09-27 09:20:59', 392014, '2014-09-28 09:19:59', 1, '2014-09-28 09:19:59', 6, 1, 100, 1, 1, 'junit_tae_test', 14400000, 7, 4, 1, '2014-09-27 09:21:43', '2014-09-27 16:21:43');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
