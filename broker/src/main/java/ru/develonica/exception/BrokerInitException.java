package ru.develonica.exception;

/**
 * Ошибка при инициализации брокера.
 */
public class BrokerInitException extends Exception {
    public BrokerInitException(Exception e) {
        super(e);
    }
}
