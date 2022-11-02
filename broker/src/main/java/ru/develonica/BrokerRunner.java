package ru.develonica;

import ru.develonica.controller.Broker;

/**
 * Запускает брокера сообщений.
 */
public class BrokerRunner {

    public static void main(String[] args) {
        Broker broker = new Broker();
        broker.run();
    }
}
