#! /usr/bin/env bash

if [ ! -f target/skyport-1.0-SNAPSHOT.jar ]; then
    mvn package
fi

java -cp target/skyport-1.0-SNAPSHOT.jar skyport.Skyport --map high-noon-2p.skyportmap
