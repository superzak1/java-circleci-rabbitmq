package com.revof11.javacircleci.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

/**
 * Tests basic RabbitMQ communication using our custom loader.
 */
@Test (
  description = "Tests basic RabbitMQ communication using our custom loader."
)
public class TestRabbitConnection {

  /**
   * The {@code Logger} to use in cooperation with all testing.
   */
  private static final Logger LOG = LoggerFactory.getLogger(TestRabbitConnection.class);

  /**
   * Lists the environment variables for CircleCI.
   */
  @Test (
    description = "Lists the environment variables for CircleCI."
  )
  public void testEnvironmentVariables() {
    System.getenv().entrySet().forEach(entry -> {
      String key = entry.getKey();
      String value = entry.getValue();
      LOG.info(String.format("Environment Variable : %-24s : %-24s", key, value));
    });
  }

  /**
   * Tests the core of the processing.
   * @throws Exception if anything goes horribly wrong
   */
  @Test (
    dependsOnMethods = {"testEnvironmentVariables"},
    description = "Tests the core of the processing."
  )
  public void testCore() throws Exception {
    final String toPost = UUID.randomUUID().toString();

    final String exchange = "MyExchange";
    final String exchangeType = "topic";
    final String queue = "MyQueue";
    final String routingKey = "MyRoutingKey";

    final Charset charset = Charset.forName("UTF-8");

    // connect
    Connection connection = null;
    Channel channel = null;
    try {
      // connect
      LOG.info("AMQP Test : Connecting");
      connection = new ConnectionLoader().getConnection();

      // put something in a topic... cuz, why not?
      LOG.info("AMQP Test : Posting to queue for consumption");
      channel = connection.createChannel();
      channel.exchangeDeclare(exchange, exchangeType, true, false, new ConcurrentHashMap<>());
      channel.queueDeclare(queue, true, false, false, new ConcurrentHashMap<>());
      channel.queueBind(queue, exchange, routingKey);
      channel.basicPublish(exchange, routingKey, null, toPost.getBytes(charset));

      // well, that was nice... let's see if we can get it off the queue now
      LOG.info("AMQP Test : Retrieving what was posted to the queue");
      QueueingConsumer consumer = new QueueingConsumer(channel);
      channel.basicConsume(queue, true, consumer);    // I do not usually auto-ACK here, but it's OK for this
      QueueingConsumer.Delivery delivery = consumer.nextDelivery();
      byte[] deliveryBody = delivery.getBody();

      // make sure it's the same as what we put on there...
      LOG.info("AMQP Test : Making sure that the data on the queue is what we actually posted");
      Assert.assertEquals(new String(deliveryBody, charset), toPost, "Invalid data brought back from the queue.");
    } finally {
      // shut things down so we don't bleed resources
      try {
        if (channel != null) {
          channel.close();
        }
      } catch (IOException | TimeoutException e) {
        LOG.warn(String.format("Non-fatal error closing channel:  %s", e.getMessage()), e);
      }

      try {
        if (connection != null) {
          connection.close();
        }
      } catch (IOException e) {
        LOG.warn(String.format("Non-fatal error closing connection:  %s", e.getMessage()), e);
      }
    }
  }
}
