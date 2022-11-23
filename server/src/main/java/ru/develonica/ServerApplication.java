package ru.develonica;

import ru.develonica.runner.ServerApplicationRunner;

/**
 * Точка запуска сервера мониторинга сообщений.
 */
public class ServerApplication {

    public static void main(String[] args) {
        ServerApplicationRunner runner = new ServerApplicationRunner();
        runner.run();
    }
}
