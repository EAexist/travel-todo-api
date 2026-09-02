#!/bin/sh
set -e

if [ "${SSL:-false}" = "true" ]; then
    cp /certs/travel-todo-api-455519588477.asia-northeast3.run.app.test+3.pem /tmp/server.crt
    cp /certs/travel-todo-api-455519588477.asia-northeast3.run.app.test+3-key.pem /tmp/server.key

    chown postgres:postgres /tmp/server.crt /tmp/server.key
    chmod 644 /tmp/server.crt
    chmod 600 /tmp/server.key

    exec docker-entrypoint.sh postgres \
        -c ssl=on \
        -c ssl_cert_file=/tmp/server.crt \
        -c ssl_key_file=/tmp/server.key
else
    exec docker-entrypoint.sh postgres
fi