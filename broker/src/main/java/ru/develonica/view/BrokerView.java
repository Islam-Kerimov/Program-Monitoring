package ru.develonica.view;

import static java.lang.System.out;

/**
 * Отображение в консоли о запуске/остановке брокера.
 */
public class BrokerView {

    /** Сообщение о начале работы брокера. */
    private static final String BROKER_START = "Broker start";

    /** Сообщение об остановке брокера. */
    private static final String BROKER_STOP = "Press any key to stop the broker";

    public void startBroker() {
        out.println(BROKER_START);
    }

    public void stopBroker() {
        out.println(BROKER_STOP);
    }
}
