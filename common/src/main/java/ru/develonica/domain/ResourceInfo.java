package ru.develonica.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Значения важных для использования ресурсов.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ResourceInfo {

    /** Ресурсы железа. */
    private Resource resource;

    /** Статус состояния ресурсов. */
    private Status status;

    /** Показатель реальных значений ресурсов. */
    private double actualValue;

    /** Показатель пороговых значений ресурсов. */
    private double alertValue;
}
