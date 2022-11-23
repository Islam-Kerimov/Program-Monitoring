package ru.develonica;

import ru.develonica.runner.BrokerApplicationRunner;

/**
 * Точка запуска брокера сообщений.
 */
public class BrokerApplication {
    public static void main(String[] args) {
        BrokerApplicationRunner runner = new BrokerApplicationRunner();
        runner.run();
    }

}
