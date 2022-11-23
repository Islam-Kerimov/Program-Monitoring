package ru.develonica.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Класс с параметрами сервера мониторинга сообщений.
 */
@Getter
public class ServerProperties {

    /** URL брокера. */
    private String url;

    /** Название топика в брокере сообщений. */
    @JsonProperty("topic-name")
    private String topicName;

    /** Название очереди в брокере сообщений. */
    @JsonProperty("queue-name")
    private String queueName;

    /** Время ожидания между запросами. */
    @JsonProperty("wait-time")
    private long waitTime;

    /** Имя файла отчета. */
    @JsonProperty("file-name")
    private String fileName;
}
