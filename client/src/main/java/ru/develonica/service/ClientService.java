package ru.develonica.service;

import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import ru.develonica.domain.ResourceInfo;

import java.io.File;

import static java.lang.Math.random;
import static java.util.Arrays.stream;

/**
 *
 */
public class ClientService {

    public ResourceInfo getCurrentResource() {
        ResourceInfo totalInfo = getTotalInfo();

        // текущие показатели клиента
        double randomRam = totalInfo.getRam() * random();
        double randomHertz = totalInfo.getHertz() * random();
        double randomDiskSpace = totalInfo.getDiskSpace() * random();

        return new ResourceInfo(randomRam, randomHertz, randomDiskSpace);
    }

    public ResourceInfo getTotalInfo() {
        HardwareAbstractionLayer hardware = new SystemInfo().getHardware();

        // общие показатели важных ресурсов
        long totalRam = hardware.getMemory().getTotal();
        long totalHertz = hardware.getProcessor().getProcessorIdentifier().getVendorFreq();
        long totalDiskSpace = stream(File.listRoots())
                .map(File::getTotalSpace)
                .mapToLong(Long::longValue)
                .sum();

        return new ResourceInfo(totalRam, totalHertz, totalDiskSpace);
    }
}
