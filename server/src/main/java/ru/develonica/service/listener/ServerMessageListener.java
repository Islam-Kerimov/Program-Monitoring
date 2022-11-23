package ru.develonica.service.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.logging.Log;
import ru.develonica.domain.MonitorMessage;
import ru.develonica.service.MessageConverterService;
import ru.develonica.view.Receiver;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

import static org.apache.commons.logging.LogFactory.getLog;

/**
 * Сервер реализует интерфейс слушателя сообщений. В тот момент,
 * когда ответ от клиентов попадает в очередь, брокер передает сообщение
 * серверу.
 */
public class ServerMessageListener implements MessageListener {

    /** Logger для отображения логирования. */
    private static final Log LOG = getLog(MessageConverterService.class);

    /** Список объектов для вывода сообщений. */
    private final List<Receiver> receivers;

    /** Сервис конвертации сообщений. */
    private final MessageConverterService messageConverterService;

    public ServerMessageListener(List<Receiver> receivers, MessageConverterService messageConverterService) {
        this.receivers = receivers;
        this.messageConverterService = messageConverterService;
    }

    /**
     * Слушает и обрабатывает ответы от клиента.
     *
     * @param message ответ от клиента.
     */
    @Override
    public void onMessage(Message message) {
        try {
            MonitorMessage monitorMessage = messageConverterService.convertToObject((TextMessage) message);

            for (Receiver receiver : receivers) {
                receiver.write((TextMessage) message, monitorMessage);
            }
        } catch (JMSException jmse) {
            LOG.error("JMS provider fails to get the data due to some internal error.", jmse);
        } catch (JsonProcessingException jpe) {
            LOG.error("JSON deserialization error.", jpe);
        }
    }
}
