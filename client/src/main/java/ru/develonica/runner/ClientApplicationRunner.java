package ru.develonica.runner;

import org.apache.commons.logging.Log;
import ru.develonica.controller.ClientController;
import ru.develonica.domain.ClientProperties;
import ru.develonica.service.BrokerConnectionService;
import ru.develonica.service.MessageConverterService;
import ru.develonica.service.MessageService;
import ru.develonica.service.PropertyReader;
import ru.develonica.service.indicate.resource.ResourceIndicator;
import ru.develonica.service.indicate.resource.impl.SyntheticResourceIndicator;
import ru.develonica.service.listener.ClientMessageListener;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import java.io.IOException;

import static org.apache.commons.logging.LogFactory.getLog;

/**
 * Запускает клиента мониторинга сообщений.
 */
public class ClientApplicationRunner {

    /** Logger для отображения логирования. */
    private static final Log LOG = getLog(ClientApplicationRunner.class);

    /** Имя файла с properties клиента. */
    private static final String APPLICATION_PROPERTY = "properties.yaml";

    public void run() {
        try {
            PropertyReader reader = new PropertyReader();

            ClientProperties clientProperties = reader.read(
                    ClientApplicationRunner.class.getClassLoader().getResource(APPLICATION_PROPERTY),
                    ClientProperties.class);

            runClientController(clientProperties);
        } catch (IOException ioe) {
            LOG.error("Read yaml file error", ioe);
        }
    }

    /**
     * Запускает клиента мониторинга сообщений и контроллер приложения.
     *
     * @param clientProperties параметры клиента мониторинга.
     */
    private void runClientController(ClientProperties clientProperties) {
        try (BrokerConnectionService clientConnectionService = new BrokerConnectionService(clientProperties)) {

            // параметры для соединения и работы с брокером сообщений.
            Session session = clientConnectionService.getSession();
            MessageConsumer consumer = clientConnectionService.createConsumer();
            MessageProducer producer = clientConnectionService.createProducer();

            // показатели ресурсов железа (синтетические и реальные)
            ResourceIndicator resourceIndicator = new SyntheticResourceIndicator(clientProperties);

            MessageConverterService messageConverterService = new MessageConverterService();

            // сервис для отправки сообщений серверу.
            MessageService messageService = new MessageService(producer, resourceIndicator, session, messageConverterService, clientProperties.getClientId());

            // слушатель сообщений от сервера.
            ClientMessageListener clientMessageListener = new ClientMessageListener(messageService);

            // контроллер приложения
            ClientController clientController = new ClientController(
                    consumer, clientMessageListener, clientConnectionService);
            clientController.run();
        } catch (JMSException jmse) {
            LOG.error("Work JMS error.", jmse);
        }
    }
}
