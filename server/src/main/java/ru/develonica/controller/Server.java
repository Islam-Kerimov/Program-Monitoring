package ru.develonica.controller;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import ru.develonica.ConnectionParameter;
import ru.develonica.ServerRunner;
import ru.develonica.listener.ServerMessageListener;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import static java.lang.Long.MAX_VALUE;
import static java.lang.Thread.sleep;
import static java.util.UUID.randomUUID;
import static javax.jms.DeliveryMode.NON_PERSISTENT;
import static javax.jms.Session.AUTO_ACKNOWLEDGE;
import static org.apache.commons.logging.LogFactory.getLog;
import static ru.develonica.Mapper.readMapper;

/**
 * Сервер мониторинга сообщений.
 */
public class Server {

    /** Logger для отображения логирования. */
    private static final Log LOG = getLog(ServerRunner.class);

    /** Параметры подключения к ActiveMQ */
    private static final ConnectionParameter PARAMETER;

    /** URL подключения. */
    private static final String MESSAGE_BROKER_URL;

    /** Имя топика из которой это приложение будет прослушивать сообщения. */
    private static final String MESSAGE_TOPIC_NAME;

    /** Объекты, используемые при создании session. */
    private static final int ACK_MODE;
    private static final boolean TRANSACTED;

    /** Текст запроса сервера клиенту. */
    private static final String REQUEST = "Send me the status of resource settings";

    /** Объект, который используется для создания подключений. */
    private final ConnectionFactory connectionFactory;

    /** Уникальный идентификатор сервера. */
    private final long serverId;

    /** Объект, который используется для создания подключений. */
    private Connection connection;

    /** Контекст для создания и потребления сообщений. */
    private Session session;

    static {
        PARAMETER = readMapper(
                Server.class.getClassLoader().getResource("application.yml"),
                ConnectionParameter.class);
        MESSAGE_BROKER_URL = PARAMETER.url();
        MESSAGE_TOPIC_NAME = PARAMETER.topicName();
        ACK_MODE = AUTO_ACKNOWLEDGE;
        TRANSACTED = false;
    }

    public Server() {
        this.connectionFactory = new ActiveMQConnectionFactory(MESSAGE_BROKER_URL);
        this.serverId = randomUUID().getMostSignificantBits() & MAX_VALUE;
    }

    /**
     * Отправляет запроса в ActiveMQ, не чаще 1 раза в минуту.
     * В случае отключения брокера, завершить работу.
     */
    public void run() {
        while (true) {
            try {
                sendRequest();
                sleep(60000);
            } catch (InterruptedException ie) {
                LOG.error(ie);
            } catch (JMSException jmse) {
                LOG.error(jmse);
                if (connection != null) {
                    try {
                        connection.close();
                        session.close();
                    } catch (JMSException jmsException) {
                        LOG.error(jmsException);
                    }
                }
                return;
            }
        }
    }

    /**
     * Подключается к ActiveMQ и отправляет ему данные.
     *
     * @throws JMSException если произошла ошибка работы с брокером
     */
    private void sendRequest() throws JMSException {
        if (connection == null) {
            connection = connectionFactory.createConnection();
            connection.start();
        }

        if (session == null) {
            session = connection.createSession(TRANSACTED, ACK_MODE);
        }

        Destination destination = session.createTopic(MESSAGE_TOPIC_NAME);

        MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(NON_PERSISTENT);

        // временная очередь, в которой сервер будет прослушивать ответы от клиентов
        Destination temporaryQueue = session.createTemporaryQueue();
        MessageConsumer responseConsumer = session.createConsumer(temporaryQueue);

        // установка сервера в качестве слушателя ответов от клиентов
        responseConsumer.setMessageListener(new ServerMessageListener());

        // создать объект текстового сообщения с данными для отправки
        TextMessage requestMessage = session.createTextMessage();
        requestMessage.setText(REQUEST);
        requestMessage.setJMSCorrelationID(String.valueOf(serverId));
        requestMessage.setJMSReplyTo(temporaryQueue);

        // отправить данные клиенту
        LOG.info("Send the request Message");
        producer.send(requestMessage);
    }
}
