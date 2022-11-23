package ru.develonica.service.listener;

import org.apache.commons.logging.Log;
import ru.develonica.service.MessageService;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import static org.apache.commons.logging.LogFactory.getLog;

/**
 * Клиент реализует интерфейс слушателя сообщений. В тот момент,
 * когда запрос от сервера попадает в topic сообщений, брокер передает сообщение
 * клиентам.
 */
public class ClientMessageListener implements MessageListener {

    /** Logger для отображения логирования. */
    private static final Log LOG = getLog(ClientMessageListener.class);

    /** Сервис для работы с сообщениями. */
    private final MessageService messageService;

    public ClientMessageListener(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Слушает и обрабатывает запросы от сервера.
     *
     * @param message запрос сервера
     */
    @Override
    public void onMessage(Message message) {
        try {
            messageService.send();
        } catch (JMSException jmse) {
            LOG.error("JMS provider fails to create message due to some internal error.", jmse);
        }
    }
}
