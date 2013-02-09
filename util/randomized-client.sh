#!/bin/bash
while true
do
    NAME=$(head -c 10 /dev/urandom | base64 | sed 's/[\\=\/+]//g')
    MESSAGE=$(
	cat <<EOT
{"message":"connect", "revision":1, "name":"$NAME"}\n{"message":"loadout", "primary-weapon":"laser", "secondary-weapon":"mortar"}
EOT
    )

    echo -e $MESSAGE | nc localhost 54321
    echo "###############################"
    sleep 10s
done
