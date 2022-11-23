package ru.develonica.service;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import ru.develonica.domain.ServerProperties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import java.io.Closeable;

import static javax.jms.DeliveryMode.NON_PERSISTENT;
import static javax.jms.Session.AUTO_ACKNOWLEDGE;
import static org.apache.commons.logging.LogFactory.getLog;

/**
 * Сервис для работы с брокером сообщений.
 */
public class BrokerConnectionService implements Closeable {

    /** Logger для отображения логирования. */
    private static final Log LOG = getLog(BrokerConnectionService.class);

    /** Параметры клиента мониторинга сообщений. */
    private final ServerProperties properties;

    /** Объект создания подключения к брокеру. */
    private final Connection connection;

    /** Контекст для создания и потребления сообщений. */
    private final Session session;

    public BrokerConnectionService(ServerProperties properties) throws JMSException {
        this.properties = properties;
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(properties.getUrl());
        this.connection = connectionFactory.createConnection();
        this.session = connection.createSession(false, AUTO_ACKNOWLEDGE);
    }

    public Session getSession() {
        return session;
    }

    /**
     * Запускает подключение к брокеру.
     *
     * @throws JMSException если произошла ошибка при работе с брокером.
     */
    public void start() throws JMSException {
        connection.start();
    }

    /**
     * Создание потребителя сообщений.
     *
     * @return потребителя сообщений
     * @throws JMSException если произошла ошибка при работе с брокером.
     */
    public MessageConsumer createConsumer() throws JMSException {
        Destination queue = session.createQueue(properties.getQueueName());
        return session.createConsumer(queue);
    }

    /**
     * Создание производителя сообщений.
     *
     * @return производителя сообщений.
     * @throws JMSException если произошла ошибка при работе с брокером.
     */
    public MessageProducer createProducer() throws JMSException {
        Destination topic = session.createTopic(properties.getTopicName());
        MessageProducer replyProducer = session.createProducer(topic);
        replyProducer.setDeliveryMode(NON_PERSISTENT);

        return replyProducer;
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException e) {
                LOG.error("Error close connection");
            }
        }
        if (session != null) {
            try {
                session.close();
            } catch (JMSException e) {
                LOG.error("Error close session");
            }
        }
    }
}
