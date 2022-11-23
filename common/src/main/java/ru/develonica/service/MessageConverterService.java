package ru.develonica.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.develonica.domain.MonitorMessage;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * Сервис сериализации и десериализации сообщений.
 */
public class MessageConverterService {

    /** Класс маппер сообщений. */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Преобразование объекта Java в TextMessage.
     *
     * @param text    объект Java.
     * @param session сессия брокера сообщения.
     * @return объект текстового сообщения
     * @throws JMSException            если произошла ошибка при работе с брокером.
     * @throws JsonProcessingException если произошла ошибка при работе c jackson.
     */
    public TextMessage convertToMessage(MonitorMessage text, Session session) throws JMSException, JsonProcessingException {
        String textMessage = objectMapper.writeValueAsString(text);
        return session.createTextMessage(textMessage);
    }

    /**
     * Преобразование TextMessage в объект Java.
     *
     * @param text объект текстового сообщения
     * @return объект Java.
     * @throws JMSException            если произошла ошибка при работе с брокером.
     * @throws JsonProcessingException если произошла ошибка при работе c jackson.
     */
    public MonitorMessage convertToObject(TextMessage text) throws JMSException, JsonProcessingException {
        return objectMapper.readValue(text.getText(), MonitorMessage.class);
    }
}
