package ru.develonica;

import ru.develonica.runner.ClientApplicationRunner;

/**
 * Точка запуска клиента мониторинга сообщений.
 */
public class ClientApplication {

    public static void main(String[] args) {
        ClientApplicationRunner runner = new ClientApplicationRunner();
        runner.run();
    }
}
