package ru.develonica.service;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * Сервис для работы с сообщениями.
 */
public class MessageService {

    /** Текст запроса сервера клиенту. */
    private static final String REQUEST = "Send me the status of resource settings";

    /** Производитель сообщений. */
    private final MessageProducer producer;

    /** Контекст для создания и потребления сообщений. */
    private final Session session;

    public MessageService(MessageProducer producer, Session session) {
        this.producer = producer;
        this.session = session;
    }

    /**
     * Отправить сообщение клиенту.
     *
     * @throws JMSException если произошла ошибка при работе с брокером.
     */
    public void send() throws JMSException {
        TextMessage textMessage = session.createTextMessage(REQUEST);
        producer.send(textMessage);
    }
}
