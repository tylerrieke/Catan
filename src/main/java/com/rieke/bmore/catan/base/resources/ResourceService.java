package com.rieke.bmore.catan.base.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ResourceService {

    private Map<String, Class<? extends Resource>> resourceMap;

    public ResourceService() {

    }

    @Autowired
    public void setResources(List<Resource> resources) {
        resourceMap = new HashMap<>();
        for(Resource resource:resources) {
            resourceMap.put(resource.getName(), resource.getClass());
        }
    }

    public Class<? extends Resource> getResourceByName(String name) {
        return resourceMap.get(name);
    }

    public Collection<Class<? extends Resource>> getResources() {
        return resourceMap.values();
    }
}
