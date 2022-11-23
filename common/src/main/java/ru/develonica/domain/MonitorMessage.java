package ru.develonica.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Объект сообщение для отправки серверу.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MonitorMessage {

    /** Уникальный идентификатор клиента. */
    private String clientId;

    /** Список ресурсов с актуальными показателями. */
    private List<ResourceInfo> resourceInfo = new ArrayList<>();
}
