# SimpleChat

Simple chat is a simple example how to make a simple chat with a TLS encryption.

## What does it need to get it to work?

You need to generate certifcate pair for the encryption.
You can generate it with `openssl req -x509 -newkey rsa:4096 -keyout private.pem -out csr.pem`
