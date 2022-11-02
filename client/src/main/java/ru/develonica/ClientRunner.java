package ru.develonica;

import ru.develonica.controller.Client;

/**
 * Создает клиента мониторинга сообщений
 */
public class ClientRunner {

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
}
