#!/bin/sh

POSTEND=1000

GETEND=1000

prefix=${1:-def}

preDate=$(date +%s)

echo Post timinig
for i in $(seq 1 $POSTEND); do 
./test.sh POST "Key_${prefix}_$i" "Value_${prefix}_$i" > /dev/null 2>&1
done

postDate=$(date +%s)

echo Get timing
for i in $(seq 1 $GETEND); do
./test.sh GET "Key_${prefix}_$i" > /dev/null 2>&1
done

getDate=$(date +%s)

echo "Start date: $preDate"
echo "Post date: $postDate"
echo "Get date: $getDate"

echo "Post duration: $((postDate - preDate))"
echo "Get duration: $((getDate - postDate))"
