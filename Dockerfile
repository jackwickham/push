# syntax=docker/dockerfile:1

FROM ubuntu:latest

RUN apt-get update && apt-get install -y ca-certificates

WORKDIR /app

COPY be/push ./
COPY fe/dist ./fe-dist

CMD ["./push"]