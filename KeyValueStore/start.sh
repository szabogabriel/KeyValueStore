#!/bin/sh

rm -f test.db

java -Xmx4m -cp ./lib/hsu_1.0.0.jar:./lib/sqlite-jdbc-3.14.2.1.jar:./kvstore_0.9.jar kvstore.web.simple.WebStore -dbFile test.db -serverHost localhost -serverPort 9998 -backlog 3 -listenUrl "/store"

