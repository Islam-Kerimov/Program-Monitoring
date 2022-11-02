package ru.develonica;

/**
 * Параметры подключения к брокеру.
 *
 * @param url URL
 * @param topicName название топика в брокере сообщений.
 */
public record ConnectionParameter(String url, String topicName) {
}
