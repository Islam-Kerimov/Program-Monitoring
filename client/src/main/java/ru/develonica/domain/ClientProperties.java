package ru.develonica.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Класс с параметрами клиента мониторинга сообщений.
 */
@Getter
public class ClientProperties {

    /** URL брокера. */
    private String url;

    /** Название топика в брокере сообщений. */
    @JsonProperty("topic-name")
    private String topicName;

    /** Название очереди в брокере сообщений. */
    @JsonProperty("queue-name")
    private String queueName;

    /** Порог показателя оперативно памяти в процентах. */
    private double ram;

    /** Порог показателя состояния процессора в процентах. */
    private double frequency;

    /** Порог показателя свободного места на диске в процентах. */
    @JsonProperty("free-space")
    private double freeSpace;

    @JsonProperty("client-id")
    private String clientId;
}
