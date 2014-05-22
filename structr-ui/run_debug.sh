#!/bin/bash
java -Djava.net.useSystemProxies=true -Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=n -Dfile.encoding=utf-8 -Xms1g -Xmx1g -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+UseNUMA -cp target/lib/*:target/structr-ui-1.0-SNAPSHOT.jar -Djava.util.logging.config.file=logging.properties org.structr.Server
