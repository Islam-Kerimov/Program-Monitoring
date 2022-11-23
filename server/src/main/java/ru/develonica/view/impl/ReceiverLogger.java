package ru.develonica.view.impl;

import org.apache.commons.logging.Log;
import ru.develonica.domain.MonitorMessage;
import ru.develonica.domain.ResourceInfo;
import ru.develonica.view.Receiver;

import javax.jms.TextMessage;
import java.util.List;

import static java.lang.String.format;
import static org.apache.commons.logging.LogFactory.getLog;

/**
 * Вывод сообщений в консоль.
 */
public class ReceiverLogger implements Receiver {

    /** Logger для отображения логирования. */
    private static final Log LOG = getLog(ReceiverLogger.class);

    /** Формат вывода сообщений в консоль. */
    private static final String LOGGER_FORMAT = "%10s - %s/%s [%s]%n";

    /** Формат вывода идентификатора клиента. */
    private static final String TITLE_FORMAT = "CLIENT_ID = %s";

    /** Символ разделительной линии. */
    private static final String END_LINE_SYMBOL = "=";

    /** Количество символа разделителя. */
    private static final int COUNT_SYMBOL = 64;

    /**
     * Преобразовывает TextMessage в объект Java и выводит данные в консоль.
     *
     * @param textMessage    объект сообщения от клиента.
     * @param monitorMessage объект сообщение для отправки серверу.
     */
    @Override
    public void write(TextMessage textMessage, MonitorMessage monitorMessage) {
        String clientId = monitorMessage.getClientId();
        List<ResourceInfo> resourceInfo = monitorMessage.getResourceInfo();

        LOG.info("Message received from client");
        LOG.info(format(TITLE_FORMAT, clientId));
        for (ResourceInfo res : resourceInfo) {
            System.out.printf(
                    LOGGER_FORMAT,
                    res.getResource(),
                    res.getActualValue(),
                    res.getAlertValue(),
                    res.getStatus());
        }
        System.out.println(END_LINE_SYMBOL.repeat(COUNT_SYMBOL));
    }
}
