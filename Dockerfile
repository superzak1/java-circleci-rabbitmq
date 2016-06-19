FROM ubuntu:16.04
MAINTAINER RevOf11.com
ENV DEBIAN_FRONTEND noninteractive

RUN \
  apt-get update && \
  apt-get dist-upgrade -y && \
  apt-get update && \
  apt-get install -y software-properties-common wget curl sudo vim

RUN groupadd -r rabbitmq && useradd -ms /bin/bash -g rabbitmq rabbitmq

RUN \
  apt-get update && \
  apt-get -y install supervisor && \
  mkdir -p /var/log/supervisor && \
  mkdir -p /etc/supervisor/conf.d

ADD .docker/supervisor.conf /etc/supervisor.conf

RUN \
  apt-get update && \
  apt-get install -y rabbitmq-server && \
  rabbitmq-plugins --offline enable rabbitmq_management

ADD .docker/rabbitmq.config /etc/rabbitmq/rabbitmq.config
ADD .docker/rabbitmq.sv.conf /etc/supervisor/conf.d/rabbitmq.sv.conf

# execution
USER root
CMD ["supervisord", "-c", "/etc/supervisor.conf"]
