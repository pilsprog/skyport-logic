#! /usr/bin/env bash

if [ ! -f target/skyport-1.0-SNAPSHOT.jar ]; then
    mvn package
fi

java -cp target/skyport-1.0-SNAPSHOT.jar skyport.Skyport 54321 0 48000 assets/balanced/high-noon-2p.skyportmap 1000
