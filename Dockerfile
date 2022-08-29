# syntax=docker/dockerfile:1

FROM ubuntu:latest

WORKDIR /app

COPY be/push ./
COPY fe/dist ./fe-dist

CMD ["./push"]