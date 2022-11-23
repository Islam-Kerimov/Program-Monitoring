package ru.develonica.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.logging.Log;
import ru.develonica.domain.MonitorMessage;
import ru.develonica.domain.ResourceInfo;
import ru.develonica.service.indicate.resource.ResourceIndicator;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.List;

import static java.util.UUID.randomUUID;
import static org.apache.commons.logging.LogFactory.getLog;

/**
 * Сервис для работы с сообщениями.
 */
public class MessageService {

    /** Logger для отображения логирования. */
    private static final Log LOG = getLog(MessageService.class);

    /** Производитель сообщений. */
    private final MessageProducer replyProducer;

    /** Ресурсы железа. */
    private final ResourceIndicator resourceIndicator;

    /** Контекст для создания и потребления сообщений. */
    private final Session session;

    /** Сервис конвертации сообщений. */
    private final MessageConverterService messageConverterService;

    /** Уникальный идентификатор клиента. */
    private String clientId;

    public MessageService(MessageProducer replyProducer,
                          ResourceIndicator resourceIndicator,
                          Session session,
                          MessageConverterService messageConverterService,
                          String clientId) {
        this.replyProducer = replyProducer;
        this.resourceIndicator = resourceIndicator;
        this.session = session;
        this.clientId = clientId;
        this.messageConverterService = messageConverterService;
    }

    /**
     * Отправить сообщение серверу.
     *
     * @throws JMSException если произошла ошибка при работе с брокером.
     */
    public void send() throws JMSException {
        List<ResourceInfo> resultInfo = resourceIndicator.getResourceInfo();

        if (clientId == null) {
            clientId = randomUUID().toString();
        }

        try {
            TextMessage message = messageConverterService.convertToMessage(
                    new MonitorMessage(clientId, resultInfo), session);
            replyProducer.send(message);

            LOG.info("Message added in queue");
        } catch (JsonProcessingException jpe) {
            LOG.error("JSON serialization error.", jpe);
        }
    }
}
