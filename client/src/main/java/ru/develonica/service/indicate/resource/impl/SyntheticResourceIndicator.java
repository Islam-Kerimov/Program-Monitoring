package ru.develonica.service.indicate.resource.impl;

import ru.develonica.domain.ClientProperties;
import ru.develonica.domain.Resource;
import ru.develonica.domain.ResourceInfo;
import ru.develonica.domain.Status;
import ru.develonica.service.indicate.resource.ResourceIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static ru.develonica.domain.Resource.FREE_SPACE;
import static ru.develonica.domain.Resource.FREQUENCY;
import static ru.develonica.domain.Resource.RAM;
import static ru.develonica.domain.Status.LIMIT_EXCEEDED;
import static ru.develonica.domain.Status.STABLE;

/**
 * Синтетические показатели железа.
 */
public class SyntheticResourceIndicator implements ResourceIndicator {

    /** Количество параметров. */
    private static final int AMOUNT_RESOURCE = 3;

    /** Максимальное значение параметра (%). */
    private static final int MAX_PERCENT = 100;

    /** Пороговое значение оперативной памяти (%). */
    private final double alertRam;

    /** Пороговое значение состояния процессора (%). */
    private final double alertFrequency;

    /** Пороговое значение свободного места на диске (%). */
    private final double alertFreeSpace;

    public SyntheticResourceIndicator(ClientProperties clientProperties) {
        this.alertRam = clientProperties.getRam();
        this.alertFrequency = clientProperties.getFrequency();
        this.alertFreeSpace = clientProperties.getFreeSpace();
    }

    /**
     * Генерация синтетических показателей ресурсов.
     *
     * @return показатели ресурсов.
     */
    @Override
    public List<ResourceInfo> getResourceInfo() {
        List<ResourceInfo> result = new ArrayList<>(AMOUNT_RESOURCE);

        Random random = new Random();
        double actualRam = getRandomNum(random);
        ResourceInfo ram = getResource(RAM, actualRam, alertRam);
        result.add(ram);

        double actualFrequency = getRandomNum(random);
        ResourceInfo frequency = getResource(FREQUENCY, actualFrequency, alertFrequency);
        result.add(frequency);

        double actualFreeSpace = getRandomNum(random);
        ResourceInfo freeSpace = getResource(FREE_SPACE, actualFreeSpace, alertFreeSpace);
        result.add(freeSpace);

        return result;
    }

    private ResourceInfo getResource(Resource resource, double actualValue, double alertValue) {

        return new ResourceInfo(resource, getStatus(actualValue, alertValue), actualValue, alertValue);
    }

    private static int getRandomNum(Random random) {
        return random.nextInt(MAX_PERCENT + 1);
    }

    private Status getStatus(double actualValue, double alertValue) {
        return actualValue > alertValue
                ? LIMIT_EXCEEDED
                : STABLE;
    }
}
