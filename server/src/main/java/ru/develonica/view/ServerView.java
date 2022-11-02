package ru.develonica.view;

import org.apache.commons.logging.Log;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import static java.lang.String.format;
import static org.apache.commons.logging.LogFactory.getLog;

/**
 * Отображение ответов от клиентов.
 */
public class ServerView {

    /** Logger для отображения логирования. */
    private static final Log LOG = getLog(ServerView.class);

    /** Формат ответа от клиента. */
    private static final String CLIENT_RESPONSE = "Consumer #%s sent response to request #%s:%s\n";

    /**
     * Отображение ответа от клиента с текущими значениями использования ресурсов
     *
     * @param textMessage текст сообщения клиента
     * @throws JMSException если произошла ошибка работы с брокером
     */
    public void printResponseInfo(TextMessage textMessage) throws JMSException {
        String responseText = textMessage.getText();
        String clientId = textMessage.getJMSCorrelationID();
        String jmsReplyTo = textMessage.getJMSReplyTo().toString();
        String responseNumber = jmsReplyTo.substring(jmsReplyTo.lastIndexOf(":") + 1);

        LOG.info(format(CLIENT_RESPONSE, clientId, responseNumber, responseText));
    }
}
