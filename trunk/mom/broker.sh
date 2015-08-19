#!/usr/bin/env bash
lib="."
for temp in ./target/lib/*.jar
do
    lib+=":"$temp
done
java -cp $lib com.alibaba.middleware.race.mom.broker.BrokerStart ~/store test 3
