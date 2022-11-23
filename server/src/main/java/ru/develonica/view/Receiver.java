package ru.develonica.view;

import ru.develonica.domain.MonitorMessage;

import javax.jms.JMSException;
import javax.jms.TextMessage;

/**
 * Интерфейс вывода сообщений.
 */
public interface Receiver {
    void write(TextMessage textMessage, MonitorMessage monitorMessage) throws JMSException;
}
