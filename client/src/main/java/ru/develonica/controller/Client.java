package ru.develonica.controller;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import ru.develonica.ConnectionParameter;
import ru.develonica.listener.ClientMessageListener;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import static javax.jms.DeliveryMode.NON_PERSISTENT;
import static javax.jms.Session.AUTO_ACKNOWLEDGE;
import static org.apache.commons.logging.LogFactory.getLog;
import static ru.develonica.Mapper.readMapper;


/**
 * Клиент мониторинга сообщений.
 */
public class Client {

    /** Logger для отображения логирования. */
    private static final Log LOG = getLog(Client.class);

    /** Параметры подключения к ActiveMQ */
    private static final ConnectionParameter PARAMETER;

    /** URL подключения. */
    private static final String MESSAGE_BROKER_URL;

    /** Имя топика из которой это приложение будет прослушивать сообщения. */
    private static final String MESSAGE_TOPIC_NAME;

    /** Объекты, используемые при создании session. */
    private static final int ACK_MODE;
    private static final boolean TRANSACTED;

    static {
        PARAMETER = readMapper(
                Client.class.getClassLoader().getResource("application.yml"),
                ConnectionParameter.class);
        MESSAGE_BROKER_URL = PARAMETER.url();
        MESSAGE_TOPIC_NAME = PARAMETER.topicName();
        ACK_MODE = AUTO_ACKNOWLEDGE;
        TRANSACTED = false;
    }

    /**
     * Подключается к ActiveMQ, а затем настраивает объекта Consumer для получения данных
     */
    public void run() {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(MESSAGE_BROKER_URL);
        Connection connection = null;
        Session session = null;
        try {
            connection = connectionFactory.createConnection();
            connection.start();

            session = connection.createSession(TRANSACTED, ACK_MODE);

            Destination destination = session.createTopic(MESSAGE_TOPIC_NAME);

            // настройка Producer-а сообщений для ответа на сообщения от клиентов.
            MessageProducer replyProducer = session.createProducer(null);
            replyProducer.setDeliveryMode(NON_PERSISTENT);

            // настройка клиента для получения запросов от сервера
            MessageConsumer consumer = session.createConsumer(destination);

            // установка клиента в качестве слушателя запросов от сервера
            consumer.setMessageListener(new ClientMessageListener(replyProducer, session));
        } catch (JMSException jmse) {
            LOG.error(jmse);
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException jmsException) {
                    LOG.error(jmsException);
                }
            }
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException jmsException) {
                    LOG.error(jmsException);
                }
            }
        }
    }
}
