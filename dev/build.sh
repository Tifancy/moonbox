#!/usr/bin/env bash


cd ../external/calcite && mvn install
cd ../calcite-elasticsearch5-adapter && mvn install
cd ../spark-hbase-connector && mvn install -Pspark
cd ../../ && mvn package -Pdist -DskipTest