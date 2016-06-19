package com.revof11.javacircleci.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

/**
 * Basic data loading routine for RabbitMQ connection information loading.  We allow
 * for a few different configuration options here in order to be flexible and so you
 * have something you can steal and work with if you so desire.  The class itself
 * simply isolates the functionality for loading so that we can execute other things
 * however we want to without re-coding the logic over-and-over.
 *
 * <br /><br />
 *
 * We accept either an environment variable of {@code AMQP_URL}/{@code RABBITMQ_URL}
 * to hold the data or a Java property value of {@code env.AMQP_URL}/{@code env.RABBITMQ_URL}
 * (in that order of precedence).  If we do not find anything, then we throw a basic
 * {@code RuntimeException} with a basic error message that indicates we were unable
 * to find the configuration.  We then create a {@link Connection} using the
 * standard client setup.
 */
public class ConnectionLoader {

  /**
   * The {@code Logger} to use in cooperation with all class instances.
   */
  private static final Logger LOG = LoggerFactory.getLogger(ConnectionLoader.class);

  /**
   * Requests that we load the data for the environment variable configuration set.
   * @return the {@code Connection} configuration for the currently set information
   * @throws RuntimeException if anything goes wrong during data loading
   */
  public Connection getConnection() {
    LOG.info("Executing lookup of RabbitMQ configuration from environment");

    String uri = System.getenv("AMQP_URL");
    uri = StringUtils.isBlank(uri) ? System.getenv("RABBITMQ_URL") : uri;
    uri = StringUtils.isBlank(uri) ? System.getProperty("env.AMQP_URL") : uri;
    uri = StringUtils.isBlank(uri) ? System.getProperty("env.RABBITMQ_URL") : uri;
    if (StringUtils.isBlank(uri)) {
      throw new RuntimeException("Unable to find RabbitMQ configuration.");
    }

    // create and return the connection
    try {
      ConnectionFactory factory = new ConnectionFactory();
      factory.setUri(uri);
      return factory.newConnection();
    } catch (IOException | NoSuchAlgorithmException | URISyntaxException | TimeoutException | KeyManagementException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
