package ru.develonica.runner;

import org.apache.commons.logging.Log;
import ru.develonica.controller.ServerController;
import ru.develonica.domain.ServerProperties;
import ru.develonica.service.BrokerConnectionService;
import ru.develonica.service.MessageConverterService;
import ru.develonica.service.MessageService;
import ru.develonica.service.PropertyReader;
import ru.develonica.service.listener.ServerMessageListener;
import ru.develonica.view.Receiver;
import ru.develonica.view.impl.ReceiverFile;
import ru.develonica.view.impl.ReceiverLogger;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.logging.LogFactory.getLog;

/**
 * Запускает сервер мониторинга сообщений.
 */
public class ServerApplicationRunner {

    /** Logger для отображения логирования. */
    private static final Log LOG = getLog(ServerApplicationRunner.class);

    /** Имя файла с properties сервера. */
    private static final String APPLICATION_PROPERTY = "properties.yaml";

    public void run() {
        try {
            PropertyReader reader = new PropertyReader();

            ServerProperties serverProperties = reader.read(
                    ServerApplicationRunner.class.getClassLoader().getResource(APPLICATION_PROPERTY),
                    ServerProperties.class);

            runServerController(serverProperties);
        } catch (IOException ioe) {
            LOG.error("Read yaml file error", ioe);
        }

    }

    /**
     * Запускает сервер мониторинга сообщений и контроллер приложения.
     *
     * @param serverProperties параметры сервера мониторинга.
     */
    private void runServerController(ServerProperties serverProperties) {
        try (BrokerConnectionService serverConnectionService = new BrokerConnectionService(serverProperties)) {

            // параметры для соединения и работы с брокером сообщений.
            Session session = serverConnectionService.getSession();
            MessageProducer producer = serverConnectionService.createProducer();
            MessageConsumer consumer = serverConnectionService.createConsumer();

            // сервис для отправки сообщений клиенту.
            MessageService messageService = new MessageService(producer, session);

            // объекты вывода сообщений
            List<Receiver> receivers = new ArrayList<>();
            receivers.add(new ReceiverLogger());
            receivers.add(new ReceiverFile(new File(serverProperties.getFileName())));

            MessageConverterService messageConverterService = new MessageConverterService();

            // слушатель сообщений от клиента.
            ServerMessageListener serverMessageListener = new ServerMessageListener(receivers, messageConverterService);

            // контроллер приложения
            ServerController serverController = new ServerController(
                    serverProperties,
                    consumer,
                    serverMessageListener,
                    messageService,
                    serverConnectionService);

            serverController.run();
        } catch (JMSException jmse) {
            LOG.error("Work JMS error.", jmse);
        }
    }
}
