package ru.develonica.controller;

import ru.develonica.service.BrokerConnectionService;
import ru.develonica.service.listener.ClientMessageListener;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;

/**
 * Контроллер клиента мониторинга сообщений.
 */
public class ClientController {

    /** Сервис для работы с брокером сообщений. */
    private final BrokerConnectionService clientConnectionService;

    /** Потребитель сообщений. */
    private final MessageConsumer consumer;

    /** Слушатель сообщений от сервера. */
    private final ClientMessageListener clientMessageListener;

    public ClientController(MessageConsumer consumer,
                            ClientMessageListener clientMessageListener,
                            BrokerConnectionService clientConnectionService) {

        this.clientConnectionService = clientConnectionService;
        this.consumer = consumer;
        this.clientMessageListener = clientMessageListener;
    }

    /**
     * Запускает контроллер приложения.
     *
     * @throws JMSException если произошла ошибка работы с брокером.
     */
    public void run() throws JMSException {
        clientConnectionService.start();
        consumer.setMessageListener(clientMessageListener);
        while (true) ;
    }
}
