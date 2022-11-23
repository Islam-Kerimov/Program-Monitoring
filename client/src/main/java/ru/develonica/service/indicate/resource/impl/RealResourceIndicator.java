package ru.develonica.service.indicate.resource.impl;

import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import ru.develonica.domain.ClientProperties;
import ru.develonica.domain.Resource;
import ru.develonica.domain.ResourceInfo;
import ru.develonica.domain.Status;
import ru.develonica.service.indicate.resource.ResourceIndicator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.stream;
import static ru.develonica.domain.Resource.FREE_SPACE;
import static ru.develonica.domain.Resource.FREQUENCY;
import static ru.develonica.domain.Resource.RAM;
import static ru.develonica.domain.Status.LIMIT_EXCEEDED;
import static ru.develonica.domain.Status.STABLE;

/**
 * Реальные показатели ресурсов.
 */
public class RealResourceIndicator implements ResourceIndicator {

    /** Количество параметров. */
    private static final int AMOUNT_RESOURCE = 3;

    /** Знаменатель для получения актуальных значений в %. */
    private static final int DIVIDER = 100;

    /** Делитель для показателя состояния процессора. */
    private static final double FREQUENCY_DIVIDER = 12.0;

    /** Пороговое значение оперативной памяти (%). */
    private final double alertRam;

    /** Пороговое значение состояния процессора (%). */
    private final double alertFrequency;

    /** Пороговое значение свободного места на диске (%). */
    private final double alertFreeSpace;

    public RealResourceIndicator(ClientProperties clientProperties) {
        this.alertRam = clientProperties.getRam();
        this.alertFrequency = clientProperties.getFrequency();
        this.alertFreeSpace = clientProperties.getFreeSpace();
    }

    /**
     * Получения реальных показателей ресурсов.
     *
     * @return показатели ресурсов.
     */
    @Override
    public List<ResourceInfo> getResourceInfo() {
        HardwareAbstractionLayer hardware = new SystemInfo().getHardware();
        List<ResourceInfo> result = new ArrayList<>(AMOUNT_RESOURCE);

        // оперативная память
        double totalRam = hardware.getMemory().getTotal();
        double freeRam = hardware.getMemory().getAvailable();
        ResourceInfo ram = getResource(freeRam, totalRam, alertRam, RAM);
        result.add(ram);

        // состояния процессора
        double totalFrequency = hardware.getProcessor().getProcessorIdentifier().getVendorFreq();
        double freeFrequency = hardware.getProcessor().getProcessorIdentifier().getVendorFreq() / FREQUENCY_DIVIDER;
        ResourceInfo frequency = getResource(freeFrequency, totalFrequency, alertFrequency, FREQUENCY);
        result.add(frequency);

        // свободное место на диске
        double totalDiskSpace = stream(File.listRoots())
                .map(File::getTotalSpace)
                .mapToLong(Long::longValue)
                .sum();
        double freeDiskSpace = stream(File.listRoots())
                .map(File::getFreeSpace)
                .mapToLong(Long::longValue)
                .sum();
        ResourceInfo freeSpace = getResource(freeDiskSpace, totalDiskSpace, alertFreeSpace, FREE_SPACE);
        result.add(freeSpace);

        return result;
    }

    private ResourceInfo getResource(double actualValue, double totalValue, double alertValue, Resource resource) {
        double percentActualValue = (long) (actualValue / totalValue * DIVIDER);
        Status status = percentActualValue > alertValue
                ? LIMIT_EXCEEDED
                : STABLE;
        return new ResourceInfo(resource, status, percentActualValue, alertValue);
    }
}
