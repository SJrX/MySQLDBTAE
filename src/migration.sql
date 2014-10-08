---This file should migrate old versions of the database to the new versions

RENAME TABLE `ACLIB_POOL_NAME_execConfig` TO  `ACLIB_POOL_NAME_algoExecConfig` ;

ALTER TABLE  `ACLIB_POOL_NAME_algoExecConfig` DROP  `executeOnCluster` ;


RENAME TABLE `ACLIB_POOL_NAME_runConfigs` TO  `ACLIB_POOL_NAME_runs` ;


ALTER TABLE  `ACLIB_POOL_NAME_runs` CHANGE  `runConfigID`  `runID` INT( 11 ) NOT NULL AUTO_INCREMENT ,
CHANGE  `runConfigUUID`  `runHashCode` CHAR( 48 ) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL ,
CHANGE  `runResult`  `result_status` ENUM(  'TIMEOUT',  'SAT',  'UNSAT',  'CRASHED',  'ABORT',  'KILLED' ) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT  'ABORT',
CHANGE  `runLength`  `result_runLength` DOUBLE NOT NULL DEFAULT  '0',
CHANGE  `quality`  `result_quality` DOUBLE NOT NULL DEFAULT  '0',
CHANGE  `resultSeed`  `result_seed` BIGINT( 20 ) NOT NULL DEFAULT  '1',
CHANGE  `runtime`  `result_runtime` DOUBLE NOT NULL DEFAULT  '0',
CHANGE  `walltime`  `result_walltime` DOUBLE NOT NULL DEFAULT  '0',
CHANGE  `execConfigID`  `algorithmExecutionConfigID` INT( 11 ) NOT NULL,
DROP `noop`,
CHANGE  `additionalRunData`  `result_additionalRunData` LONGTEXT CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL ,
ADD COLUMN `lastInsertionTime` datetime NOT NULL DEFAULT '1900-01-01 00:00:00' AFTER  `worstCaseNextUpdateWhenAssigned`,
ADD COLUMN `lastAssignedTime` datetime NOT NULL DEFAULT '1900-01-01 00:00:00' AFTER `lastInsertionTime`,
ADD COLUMN `lastCompletedTime` datetime NOT NULL DEFAULT '1900-01-01 00:00:00' AFTER `lastAssignedTime`;

