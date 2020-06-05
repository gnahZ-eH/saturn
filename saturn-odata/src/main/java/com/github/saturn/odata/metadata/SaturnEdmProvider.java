package com.github.saturn.odata.metadata;

import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SaturnEdmProvider extends CsdlAbstractEdmProvider {

    private static final Logger LOG = LoggerFactory.getLogger(SaturnEdmProvider.class);

    private Map<String, Class<?>> entitiesMap        = new HashMap<>();
    private Map<String, Class<?>> enumsMap           = new HashMap<>();
    private Map<String, Class<?>> actionsMap         = new HashMap<>();
    private Map<String, Class<?>> actionImportsMap   = new HashMap<>();
    private Map<String, Class<?>> functionsMap       = new HashMap<>();
    private Map<String, Class<?>> functionImportsMap = new HashMap<>();
    private Map<String, Class<?>> complexTypesMap    = new HashMap<>();
    private Map<String, String>   entityTypesMap     = new HashMap<>();

    private String NAME_SPACE = null;
    private String DEFAULT_EDM_PKG = null;



}
