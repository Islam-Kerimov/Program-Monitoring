package ru.develonica.exception;

/**
 * Ошибка при запуске брокера.
 */
public class BrokerStartException extends Exception {
    public BrokerStartException(Exception e) {
        super(e);
    }
}
