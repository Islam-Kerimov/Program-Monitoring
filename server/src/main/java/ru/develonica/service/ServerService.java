package ru.develonica.service;

import com.opencsv.CSVWriter;
import org.apache.commons.logging.Log;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.logging.LogFactory.getLog;

/**
 * Запись в файл отчет сообщений от клиентов с типом ошибка.
 */
public class ServerService {

    /** Logger для отображения логирования. */
    private static final Log LOG = getLog(ServerService.class);

    /** Сообщение о состоянии ресурса клиента. */
    private static final String PROBLEM = "PROBLEM";

    /**
     * Записывает в csv файл сообщения от клиентов с типом ошибка,
     * с указанием времени запроса и уникальным идентификатором клиента.
     *
     * @param textMessage текст сообщения клиента
     * @throws JMSException если произошла ошибка работы с брокером
     */
    public void writeDataForCsvFile(TextMessage textMessage) throws JMSException {
        try (CSVWriter writer = new CSVWriter(
                new FileWriter(
                        new File("report.csv").getAbsolutePath(), true))) {

            String responseText = textMessage.getText();
            String clientId = textMessage.getJMSCorrelationID();

            // список показателей {clientId, time, resource type}
            List<String[]> csvData = createCsvData(clientId, responseText);

            writer.writeAll(csvData);
        } catch (IOException ioe) {
            LOG.error(ioe);
        }
    }

    /**
     * Создает список показателей с типом ошибка,
     * с указанием времени запроса и идентификатором клиента
     *
     * @param clientId     уникальный идентификатор клиента
     * @param responseText текст значений показателей ресурсов клиента
     * @return список показателей с типом ошибка
     */
    private List<String[]> createCsvData(String clientId, String responseText) {
        String[] allDataInfo = responseText.split("\n");

        final String time = allDataInfo[1];

        return Arrays.stream(allDataInfo)
                .filter(res -> res.contains(PROBLEM))
                .map(data -> new String[]{clientId, time, data.split("\\[")[0].trim()})
                .toList();
    }
}
