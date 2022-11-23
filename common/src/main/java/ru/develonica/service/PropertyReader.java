package ru.develonica.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.net.URL;

/**
 * Чтение файлов с помощью маппера.
 */
public class PropertyReader {

    /** Маппер. */
    private final ObjectMapper propertyMapper = new ObjectMapper(new YAMLFactory());

    /**
     * Чтение yaml файлов.
     *
     * @param url    путь до файла.
     * @param tClass класс передаваемого параметра
     * @param <T>    тип возвращаемого значения.
     * @return объект типа переданного параметром
     * @throws IOException если произошла ошибка с чтением файла.
     */
    public <T> T read(URL url, Class<T> tClass) throws IOException {
        return propertyMapper.readValue(url, tClass);
    }
}
