#!/usr/bin/env bash
EXEC=ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorker
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

exec java -cp "$DIR/commons-math-2.2.jar:$DIR/slf4j-api-1.6.4.jar:$DIR/commons-collections-3.2.1.jar:$DIR/commons-io-2.1.jar:$DIR/fastrf.jar:$DIR/jcommander.jar:$DIR/rundispatcher.jar:$DIR/opencsv-2.3.jar:$DIR/aclib.jar:$DIR/sm.jar:$DIR/truezip-samples-7.4.3-jar-with-dependencies.jar:$DIR/mysql-connector-java-5.1.18-bin.jar:$DIR/logback-access-1.0.0.jar:$DIR/logback-core-1.0.0.jar:$DIR/logback-classic-1.0.0.jar:$DIR/conf/:$DIR/commons-codec-1.7.jar::$DIR/mysqldbtae.jar" $EXEC "$@"