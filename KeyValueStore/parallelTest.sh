#!/bin/sh

bigPreDate=$(date +%s)

./perfTest.sh a & ./perfTest.sh b & ./perfTest.sh c


bigPostDate=$(date +%s)

echo "Big dates: $((bigPostDate - bigPreDate))"
