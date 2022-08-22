#!/bin/sh

readonly address=http://localhost:9998/store/
readonly method=${1}
readonly key=${2}
readonly val=${3}

if [ "${method}" = "GET" ]
then
  curl ${address}${key}
fi

if [ "${method}" = "POST" -o "${method}" = "PUT" ]
then
  curl -X ${method} --data-binary "${val}" ${address}${key}
fi

if [ "${method}" = "DELETE" ]
then
  curl -X ${method} ${address}${key}
fi

