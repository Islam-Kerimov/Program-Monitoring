package ru.develonica;

import com.opencsv.CSVWriter;
import org.apache.commons.logging.Log;
import ru.develonica.controller.Server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.apache.commons.logging.LogFactory.getLog;

/**
 * Запускает сервер мониторинга сообщений.
 */
public class ServerRunner {

    /** Logger для отображения логирования. */
    private static final Log LOG = getLog(ServerRunner.class);

    /** Заголовки csv файла. */
    private static final String[] HEADER_CSV_FILE = {"consumerId", "time", "resource"};

    public static void main(String[] args) {
        createCsvFile();

        Server server = new Server();
        server.run();
    }

    /** Создает пустой csv файл с заголовками. */
    private static void createCsvFile() {
        try (CSVWriter writer = new CSVWriter(
                new FileWriter(
                        new File("report.csv").getAbsolutePath()))) {
            writer.writeNext(HEADER_CSV_FILE);
        } catch (IOException ioe) {
            LOG.error(ioe);
        }
    }
}
