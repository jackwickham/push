# syntax=docker/dockerfile:1

FROM ubuntu:latest

RUN sudo apt install ca-certificates

WORKDIR /app

COPY be/push ./
COPY fe/dist ./fe-dist

CMD ["./push"]