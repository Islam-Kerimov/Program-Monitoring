package ru.develonica;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.logging.Log;

import java.io.IOException;
import java.net.URL;

import static org.apache.commons.logging.LogFactory.getLog;

/**
 * Класс для чтения файлов YAML.
 */
public class Mapper {

    /** Logger для отображения логирования. */
    private static final Log LOG = getLog(Mapper.class);

    /** Реализация чтения и записи файлов YAML. */
    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper(new YAMLFactory());
    }

    /**
     * Читает из файла YAML.
     *
     * @param url   URL файла
     * @param clazz тип класса
     * @param <T>   тип возвращаемого объекта
     * @return объект переданного класса
     */
    public static <T> T readMapper(URL url, Class<T> clazz) {
        T newMapper = null;
        try {
            newMapper = MAPPER.readValue(url, clazz);
        } catch (IOException ioe) {
            LOG.error(ioe);
        }

        return newMapper;
    }
}
