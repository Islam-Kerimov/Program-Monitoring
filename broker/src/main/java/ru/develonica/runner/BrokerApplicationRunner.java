package ru.develonica.runner;

import org.apache.commons.logging.Log;
import ru.develonica.controller.BrokerController;
import ru.develonica.domain.BrokerProperties;
import ru.develonica.exception.BrokerInitException;
import ru.develonica.exception.BrokerStartException;
import ru.develonica.service.BrokerCreateService;
import ru.develonica.service.PropertyReader;

import java.io.IOException;

import static org.apache.commons.logging.LogFactory.getLog;

/**
 * Запускает брокера сообщений.
 */
public class BrokerApplicationRunner {

    /** Logger для отображения логирования. */
    private static final Log LOG = getLog(BrokerApplicationRunner.class);

    /** Имя файла с properties брокера. */
    private static final String APPLICATION_PROPERTY = "properties.yaml";

    public void run() {
        try {
            PropertyReader reader = new PropertyReader();

            BrokerProperties brokerProperties = reader.read(
                    BrokerApplicationRunner.class.getClassLoader().getResource(APPLICATION_PROPERTY),
                    BrokerProperties.class);

            runBrokerController(brokerProperties);
        } catch (IOException ioe) {
            LOG.error("Read yaml file error", ioe);
        }
    }

    /**
     * Запускает брокера и контроллер приложения.
     *
     * @param brokerProperties параметры брокера сообщений.
     */
    private void runBrokerController(BrokerProperties brokerProperties) {
        try (BrokerCreateService brokerCreateService = new BrokerCreateService(brokerProperties.getUrl())) {

            BrokerController brokerController = new BrokerController(brokerCreateService);
            brokerController.run();
        } catch (BrokerStartException bse) {
            LOG.error("Start broker error", bse);
        } catch (BrokerInitException bie) {
            LOG.error("Init broker error", bie);
        }
    }
}
