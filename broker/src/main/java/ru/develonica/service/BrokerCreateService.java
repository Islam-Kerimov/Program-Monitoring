package ru.develonica.service;

import org.apache.activemq.broker.BrokerService;
import org.apache.commons.logging.Log;
import ru.develonica.exception.BrokerInitException;
import ru.develonica.exception.BrokerStartException;

import java.io.Closeable;
import java.net.URI;

import static org.apache.activemq.broker.BrokerFactory.createBroker;
import static org.apache.commons.logging.LogFactory.getLog;

/**
 * Сервис инициализации и запуска брокера сообщений.
 */
public class BrokerCreateService implements Closeable {

    /** Logger для отображения логирования. */
    private static final Log LOG = getLog(BrokerCreateService.class);

    /** URI брокера. */
    private final String brokerUri;

    /** Сервис для работы с брокером. */
    private final BrokerService broker;

    public BrokerCreateService(String brokerUri) throws BrokerStartException, BrokerInitException {
        this.brokerUri = brokerUri;
        this.broker = init();
    }

    /**
     * Инициализация брокера сообщений.
     *
     * @return брокер сообщений.
     * @throws BrokerInitException если произошла ошибка при создании брокера.
     */
    private BrokerService init() throws BrokerInitException {
        try {
            return createBroker(new URI(brokerUri));
        } catch (Exception e) {
            throw new BrokerInitException(e);
        }
    }

    /**
     * Стартует брокер сообщений.
     *
     * @throws BrokerStartException если произошла ошибка при старте брокера.
     */
    public void start() throws BrokerStartException {
        try {
            broker.start();
        } catch (Exception e) {
            throw new BrokerStartException(e);
        }
    }

    @Override
    public void close() {
        if (broker != null) {
            try {
                broker.stop();
            } catch (Exception e) {
                LOG.error("Stop broker error");
            }
        }
    }
}
