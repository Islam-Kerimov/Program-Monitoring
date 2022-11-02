package ru.develonica.view;

import org.apache.commons.logging.Log;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import static java.lang.String.format;
import static org.apache.commons.logging.LogFactory.getLog;

/**
 * Отображение запросов от сервера.
 */
public class ClientView {

    /** Logger для отображения логирования. */
    private static final Log LOG = getLog(ClientView.class);

    /** Формат запроса от сервера. */
    private static final String SERVER_REQUEST = "Producer #%s sent request: %s #%s";

    /**
     * Отображение запроса от сервера.
     *
     * @param textMessage текст запроса сервера
     * @throws JMSException если произошла ошибка работы с брокером
     */
    public void printRequestInfo(TextMessage textMessage) throws JMSException {
        String requestText = textMessage.getText();
        String jmsReplyTo = textMessage.getJMSReplyTo().toString();
        String requestNumber = jmsReplyTo.substring(jmsReplyTo.lastIndexOf(":") + 1);
        String serverId = textMessage.getJMSCorrelationID();

        LOG.info(format(SERVER_REQUEST, serverId, requestText, requestNumber));
    }
}
