# Simple Key value Store
This project aims to be a simple and generic key-value store. It can 
be used as a Java library, or as a small, REST-based webservice. It 
supports in-memory storage, auditfiles and SQLite databases. It also
features with caching when used with SQLite.

## REST-based webservice
The REST-based webservice only supports plaintext, string data.

### Starting
Command to start the application:
```bash
java -cp kvstore_0.7.jar kvstore.web.WebStore -dbFile [file] -serverHost [hostname] -serverPort [port] -backlog [value] -listenUrl [path]
```
Where the parameters are as follows:
* dbfile: the file that should be used by the key-value store. If it doesn't exists, it will be created.
* serverHost: the hostname or IP of the server to be created.
* serverPort: the listener port of the server.
* backlog: [optional]: this is the backlog value as documented for the Java HttpServer class.
* listenUrl: the URL part for which the server should listen.

For example:
```bash
java -cp kvstore_0.7.jar kvstore.web.WebStore -dbFile /home/user/dbfile.db -serverHost 127.0.0.1 -serverPort 8081 -listenUrl /store
```
will end up with a service on `http://127.0.0.1:8081/store` URL. The logs will be written to the console.

### Usage
The server supports four main HTTP methods: GET, POST, PUT, DELETE.
The HTTP return codes corresponds to the state of the processing
of the request. The provided test.sh can be used to test the newly
started instance. Example usages follow:

```bash
# Create new value myKey=myData.
curl -X POST 'myData' http://localhost:8081/store/myKey

# Read the newly created data.
curl http://locahost:8081/store/myKey

# Update the newly created data to myData2.
curl -X PUT 'myData2' http://locahost:8081/store/myKey

# Delete it.
curl -X DELETE http://localhost:8081/store/myKey
```

Every data is returned and expected to be received with the content
type of `text/plain`. Listing every data or wildcarding is not currently
supported.

## Usasge as an API
There are two main parts of the key-value store, the `Store` itself, which
expects a `Persister` instance upon creating the object. The key-value store
itself is a generic object, which accepts both as key and value a serializable
type, which is then converted to Base64. 

### Persisters
The currently available persisters are:
* Audit
* Empty
* SQLite

The Audit persister creates an audit file with one of the following operations:
* ADD
* REMOVE

The Empty persister is basically a 'null object', which can be used to start
the key-value store in the in-memory mode. It doesn't perform any real
persisting operation.

The SQLite persister is used to create a small, one file database to be
used to store the data added. By default, it caches the keys to speed up
some operations. It also returns a lazy hash map, which feches data only
when it is accessed thus eliminating the need of storing the entire database
in the memory.


