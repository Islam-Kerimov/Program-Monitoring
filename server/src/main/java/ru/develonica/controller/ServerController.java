package ru.develonica.controller;

import org.apache.commons.logging.Log;
import ru.develonica.domain.ServerProperties;
import ru.develonica.runner.ServerApplicationRunner;
import ru.develonica.service.BrokerConnectionService;
import ru.develonica.service.MessageService;
import ru.develonica.service.listener.ServerMessageListener;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;

import static java.lang.Thread.sleep;
import static org.apache.commons.logging.LogFactory.getLog;

/**
 * Контроллер сервера мониторинга сообщений.
 */
public class ServerController {

    /** Logger для отображения логирования. */
    private static final Log LOG = getLog(ServerApplicationRunner.class);

    /** Параметры сервера. */
    private final ServerProperties serverProperties;

    /** Потребитель сообщений. */
    private final MessageConsumer consumer;

    /** Слушатель сообщений от клиента. */
    private final ServerMessageListener serverMessageListener;

    /** Сервис для работы с сообщениями. */
    private final MessageService messageService;

    /** Сервис для работы с брокером сообщений. */
    private final BrokerConnectionService serverConnectionService;

    public ServerController(
            ServerProperties serverProperties,
            MessageConsumer consumer,
            ServerMessageListener serverMessageListener,
            MessageService messageService,
            BrokerConnectionService serverConnectionService) {

        this.serverProperties = serverProperties;
        this.consumer = consumer;
        this.serverMessageListener = serverMessageListener;
        this.messageService = messageService;
        this.serverConnectionService = serverConnectionService;
    }

    /**
     * Запускает контроллер приложения.
     * Отправляет запроса брокеру сообщений,
     * не чаще 1 раза в <code>serverProperties.getWaitTime()</code>.
     *
     * @throws JMSException если произошла ошибка работы с брокером.
     */
    public void run() throws JMSException {
        serverConnectionService.start();
        consumer.setMessageListener(serverMessageListener);
        while (true) {
            try {
                messageService.send();
                sleep(serverProperties.getWaitTime());
            } catch (InterruptedException ie) {
                LOG.error(ie);
            }
        }
    }
}
