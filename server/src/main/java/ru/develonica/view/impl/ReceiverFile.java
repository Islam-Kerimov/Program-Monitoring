package ru.develonica.view.impl;

import com.opencsv.CSVWriter;
import org.apache.commons.logging.Log;
import ru.develonica.domain.MonitorMessage;
import ru.develonica.view.Receiver;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static java.lang.String.valueOf;
import static java.time.Instant.ofEpochMilli;
import static java.time.LocalDateTime.ofInstant;
import static java.util.TimeZone.getDefault;
import static org.apache.commons.logging.LogFactory.getLog;
import static ru.develonica.domain.Status.LIMIT_EXCEEDED;

/**
 * Вывод сообщений в файл.
 */
public class ReceiverFile implements Receiver {

    /** Logger для отображения логирования. */
    private static final Log LOG = getLog(ReceiverFile.class);

    /** Заголовки csv файла. */
    private static final String[] HEADER_CSV_FILE = {"TIME", "CLIENT_ID", "RESOURCE", "ACTUAL_VALUE", "ALERT_VALUE"};

    /** csv файл для записи отчетов. */
    private final File file;

    public ReceiverFile(File file) {
        this.file = file.getAbsoluteFile();
    }

    /**
     * Преобразовывает TextMessage в объект Java и записывает необходимые данные в файл.
     *
     * @param textMessage    объект сообщения от клиента.
     * @param monitorMessage объект сообщение для отправки серверу.
     */
    @Override
    public void write(TextMessage textMessage, MonitorMessage monitorMessage) throws JMSException {
        try (FileWriter fileWriter = new FileWriter(file, true);
             CSVWriter writer = new CSVWriter(fileWriter)) {

            // список показателей {TIME, CLIENT_ID, RESOURCE, ACTUAL_VALUE, ALERT_VALUE}
            LocalDateTime localDateTime = ofInstant(
                    ofEpochMilli(textMessage.getJMSTimestamp()),
                    getDefault().toZoneId());
            List<String[]> csvData = createCsvData(localDateTime, monitorMessage);

            if (file.length() == 0) {
                writer.writeNext(HEADER_CSV_FILE);
            }

            writer.writeAll(csvData);
        } catch (IOException ioe) {
            LOG.error(ioe);
        }
    }

    private List<String[]> createCsvData(LocalDateTime localDateTime, MonitorMessage monitorMessage) {
        return monitorMessage.getResourceInfo().stream()
                .filter(status -> LIMIT_EXCEEDED.equals(status.getStatus()))
                .map(info -> new String[]{
                        valueOf(localDateTime),
                        monitorMessage.getClientId(),
                        valueOf(info.getResource()),
                        valueOf(info.getActualValue()),
                        valueOf(info.getAlertValue())})
                .toList();
    }
}
