package ru.develonica.controller;

import org.apache.activemq.broker.BrokerService;
import org.apache.commons.logging.Log;
import ru.develonica.ConnectionParameter;
import ru.develonica.view.BrokerView;

import java.net.URI;
import java.util.Scanner;

import static java.lang.String.format;
import static java.lang.System.in;
import static org.apache.activemq.broker.BrokerFactory.createBroker;
import static org.apache.commons.logging.LogFactory.getLog;
import static ru.develonica.Mapper.readMapper;

/**
 * Брокер сообщений.
 */
public class Broker {

    /** Logger для отображения логирования. */
    private static final Log LOG = getLog(Broker.class);

    /** URI брокера. */
    private final String brokerUri;

    /** Отображение сообщений. */
    private final BrokerView brokerView;

    public Broker() {
        ConnectionParameter connectionParameter = readMapper(
                Broker.class.getClassLoader().getResource("application.yml"),
                ConnectionParameter.class);
        this.brokerUri = format("broker:(%s)", connectionParameter.url());
        this.brokerView = new BrokerView();
    }

    /** Запуск и остановки брокера сообщений. */
    public void run() {
        BrokerService broker = null;
        try {
            broker = createBroker(new URI(brokerUri));
            broker.start();

            brokerView.startBroker();
            brokerView.stopBroker();

            Scanner scanner = new Scanner(in);
            scanner.nextLine();
        } catch (Exception e) {
            LOG.error(e);
        } finally {
            if (broker != null) {
                try {
                    broker.stop();
                } catch (Exception e) {
                    LOG.error(e);
                }
            }
        }
    }
}
