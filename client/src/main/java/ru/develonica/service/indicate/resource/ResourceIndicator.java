package ru.develonica.service.indicate.resource;

import ru.develonica.domain.ResourceInfo;

import java.util.List;

/**
 * Ресурсы железа.
 */
public interface ResourceIndicator {
    List<ResourceInfo> getResourceInfo();
}
