package ru.develonica.controller;

import org.apache.commons.logging.Log;
import ru.develonica.exception.BrokerStartException;
import ru.develonica.service.BrokerCreateService;

import java.util.Scanner;

import static java.lang.System.in;
import static org.apache.commons.logging.LogFactory.getLog;

/**
 * Контроллер приложения.
 */
public class BrokerController {

    /** Logger для отображения логирования. */
    private static final Log LOG = getLog(BrokerController.class);

    /** Сервис инициализации и запуска брокера сообщений. */
    private final BrokerCreateService brokerCreateService;

    public BrokerController(BrokerCreateService brokerCreateService) {
        this.brokerCreateService = brokerCreateService;
    }

    /**
     * Запускает брокер сообщений и ожидает его завершения.
     *
     * @throws BrokerStartException если произошла ошибка при создании брокера.
     */
    public void run() throws BrokerStartException {
        brokerCreateService.start();

        LOG.info("Broker start");
        LOG.info("Press any key to stop the broker");

        Scanner scanner = new Scanner(in);
        scanner.nextLine();
    }
}
