package ru.develonica.listener;

import org.apache.commons.logging.Log;
import ru.develonica.domain.ResourceInfo;
import ru.develonica.domain.Status;
import ru.develonica.service.ClientService;
import ru.develonica.view.ClientView;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

import static java.lang.Long.MAX_VALUE;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.time.Instant.ofEpochMilli;
import static java.time.LocalDateTime.ofInstant;
import static java.time.ZoneId.systemDefault;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.UUID.randomUUID;
import static org.apache.commons.logging.LogFactory.getLog;
import static oshi.util.FormatUtil.formatBytes;
import static oshi.util.FormatUtil.formatHertz;
import static ru.develonica.Mapper.readMapper;

/**
 * Клиент реализует интерфейс слушателя сообщений. В тот момент,
 * когда запрос от сервера попадает в topic сообщений, брокер передает сообщение
 * клиентам.
 */
public class ClientMessageListener implements MessageListener {

    /** Logger для отображения логирования. */
    private static final Log LOG = getLog(ClientMessageListener.class);

    /** Формат отображения времени получения запроса от сервера. */
    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /** Формат для отображения ресурсов клиента. */
    private static final String LINE_TEXT_FORMAT = "%s - %s/%s [%s]";

    /** Формат для отображения всех показателей клиента. */
    private static final String ALL_TEXT_FORMAT = "\n%s\n%s\n%s\n%s";

    /** Текстовое наименование памяти клиента. */
    private static final String MEMORY = "ram";

    /** Текстовое наименование процессора клиента. */
    private static final String HERTZ = "hertz";

    /** Текстовое наименование свободного места на диске клиента. */
    private static final String SPACE = "disk space";

    /** Контекст для создания и потребления сообщений. */
    private final Session session;

    /** Сервер для отправки ответов. */
    private final MessageProducer replyProducer;

    /** Уникальный идентификатор клиента. */
    private final long clientId;

    /** Отображение в консоли клиента запроса от сервера. */
    private final ClientView view;

    /** Получения значение важных для использования ресурсов. */
    private final ClientService clientService;

    public ClientMessageListener(MessageProducer replyProducer, Session session) {
        this.replyProducer = replyProducer;
        this.session = session;
        this.clientId = randomUUID().getMostSignificantBits() & MAX_VALUE;
        this.view = new ClientView();
        this.clientService = new ClientService();
    }

    /**
     * Слушает запросы от сервера и отсылает обратно показатели
     * значений ресурсов с типом состояния, датой запроса и
     * уникальным идентификатором клиента.
     *
     * @param message запрос сервера
     */
    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;

            // вывести в консоли клиента запрос сервера
            view.printRequestInfo(textMessage);

            // текущие синтетические показатели ресурсов клиента.
            ResourceInfo resourceInfo = clientService.getCurrentResource();
            Instant instant = ofEpochMilli(textMessage.getJMSTimestamp());
            DateTimeFormatter formatter = ofPattern(TIME_FORMAT);
            String localDateTime = ofInstant(instant, systemDefault()).format(formatter);

            String text = convertToTextMessage(resourceInfo, localDateTime);

            // создание объекта текстового сообщения для отправки обратно серверу
            TextMessage responseMessage = createResponseMessage(session, text, textMessage);

            // отправить данные серверу
            LOG.info("Send the response Message\n");
            replyProducer.send(textMessage.getJMSReplyTo(), responseMessage);
        } catch (JMSException jmse) {
            LOG.error(jmse);
        }
    }

    /**
     * Получение строки с показателями ресурсов клиента и времени получения запроса.
     *
     * @param resourceInfo  текущие показатели ресурсов клиента
     * @param localDateTime время получения запроса от сервера
     * @return преобразованную строку со всеми параметрами клиента
     */
    private String convertToTextMessage(ResourceInfo resourceInfo, String localDateTime) {
        ResourceInfo totalInfo = clientService.getTotalInfo();

        ResourceInfo alertInfo = readMapper(
                ClientMessageListener.class.getClassLoader().getResource("alert-resource.yaml"),
                ResourceInfo.class);
        alertInfo.setRam(alertInfo.getRam() * totalInfo.getRam() / 100);
        alertInfo.setHertz(alertInfo.getHertz() * totalInfo.getHertz() / 100);
        alertInfo.setDiskSpace(alertInfo.getDiskSpace() * totalInfo.getDiskSpace() / 100);

        String ram = format(LINE_TEXT_FORMAT, MEMORY,
                formatBytes((long) resourceInfo.getRam()),
                formatBytes((long) totalInfo.getRam()),
                resourceInfo.getRam() >= alertInfo.getRam()
                        ? Status.PROBLEM
                        : Status.NORM);

        String hertz = format(LINE_TEXT_FORMAT, HERTZ,
                formatHertz((long) resourceInfo.getHertz()),
                formatHertz((long) totalInfo.getHertz()),
                resourceInfo.getHertz() >= alertInfo.getHertz()
                        ? Status.PROBLEM
                        : Status.NORM);

        String diskSpace = format(LINE_TEXT_FORMAT, SPACE,
                formatBytes((long) resourceInfo.getDiskSpace()),
                formatBytes((long) totalInfo.getDiskSpace()),
                resourceInfo.getDiskSpace() >= alertInfo.getDiskSpace()
                        ? Status.PROBLEM
                        : Status.NORM);

        return format(ALL_TEXT_FORMAT, localDateTime, ram, hertz, diskSpace);
    }

    /**
     * Создание объекта текстового сообщения для отправки обратно серверу
     *
     * @param session     текущая сессия
     * @param text        ответ клиента
     * @param textMessage текстовое сообщение полученное от сервера
     * @return объекта текстового сообщения
     */
    private TextMessage createResponseMessage(Session session, String text, TextMessage textMessage)
            throws JMSException {
        TextMessage responseMessage = session.createTextMessage();
        responseMessage.setText(text);
        responseMessage.setJMSCorrelationID(valueOf(clientId));
        responseMessage.setJMSReplyTo(textMessage.getJMSReplyTo());

        return responseMessage;
    }
}
