package ru.develonica.listener;

import org.apache.commons.logging.Log;
import ru.develonica.service.ServerService;
import ru.develonica.view.ServerView;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import static org.apache.commons.logging.LogFactory.getLog;

/**
 * Сервер реализует интерфейс слушателя сообщений. В тот момент,
 * когда ответ от клиентов попадает в очередь, брокер передает сообщение
 * серверу.
 */
public class ServerMessageListener implements MessageListener {

    /** Logger для отображения логирования. */
    private static final Log LOG = getLog(ServerMessageListener.class);

    /** Отображение в консоли сервера сообщений от клиентов. */
    private final ServerView view;

    /** Запись в csv файл сообщений от клиентов с типом ошибка. */
    private final ServerService service;

    public ServerMessageListener() {
        this.view = new ServerView();
        this.service = new ServerService();
    }

    /**
     * Слушает ответы от клиентов и выводит их в консоль, так же сообщения
     * с типом ошибка записывает в csv файл.
     *
     * @param message ответ от клиентов.
     */
    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            view.printResponseInfo(textMessage);
            service.writeDataForCsvFile(textMessage);
        } catch (JMSException jmse) {
            LOG.error(jmse);
        }
    }
}
