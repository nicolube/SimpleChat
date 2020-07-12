# SimpleChat

Simple chat is a simple example how to make a simple chat with a TLS encryption.

## What does it need to get it to work?

You need to generate certifcate pair for the encryption.
You can generate it with `openssl req -x509 -nodes -newkey rsa:4096 -keyout privkey.pem -out csr.pem` and copy them into `Server/src/main/resources`,
also copy the `csr.pem` into `Client/src/main/resources`

## How to disable TLS?

Simple comment `.addLast("ssl", sslContext.newHandler(ch.alloc()))` both main classes.

[Client.java](Client/src/main/java/de/nicolube/simplechat/client/Client.java)

[Server.java](Server/src/main/java/de/nicolube/simplechat/server/Server.java)

## How to change the host?

You can change the hostname and port inside the Config source files

[Client Config.java](Client/src/main/java/de/nicolube/simplechat/client/Config.java)

[Server Config.java](Server/src/main/java/de/nicolube/simplechat/server/Config.java)

