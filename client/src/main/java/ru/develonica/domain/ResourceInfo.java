package ru.develonica.domain;

/**
 * Значения важных для использования ресурсов.
 */
public class ResourceInfo {

    /** Показатель оперативной памяти. */
    private double ram;

    /** Показатель состояния процессора. */
    private double hertz;

    /** Показатель свободного места на диске. */
    private double diskSpace;

    public ResourceInfo(double ram, double hertz, double diskSpace) {
        this.ram = ram;
        this.hertz = hertz;
        this.diskSpace = diskSpace;
    }

    /** Конструктор без параметров, необходим для чтения и записи yaml файлов. */
    public ResourceInfo() {
    }

    public double getRam() {
        return ram;
    }

    public void setRam(double ram) {
        this.ram = ram;
    }

    public double getHertz() {
        return hertz;
    }

    public void setHertz(double hertz) {
        this.hertz = hertz;
    }

    public double getDiskSpace() {
        return diskSpace;
    }

    public void setDiskSpace(double diskSpace) {
        this.diskSpace = diskSpace;
    }
}
