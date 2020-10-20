/*
 * MIT License
 *
 * Copyright (c) [2020] [He Zhang]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished
 * to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.saturn.odata.processors;

import com.github.saturn.odata.annotations.ODataEntityType;
import com.github.saturn.odata.annotations.ODataEntitySet;
import com.github.saturn.odata.annotations.ODataEnumType;
import com.github.saturn.odata.annotations.ODataComplexType;
import com.github.saturn.odata.annotations.ODataProperty;
import com.github.saturn.odata.annotations.ODataNavigationProperty;
import com.github.saturn.odata.enums.PrimitiveType;
import com.github.saturn.odata.enums.SelfDefinedType;
import com.github.saturn.odata.exceptions.SaturnODataException;
import com.github.saturn.odata.interfaces.EntityOperation;
import com.github.saturn.odata.metadata.SaturnEdmContext;
import com.github.saturn.odata.utils.ClassUtils;

import com.github.saturn.odata.utils.ExceptionUtils;
import com.github.saturn.odata.utils.ODataUtils;
import com.github.saturn.odata.utils.StringUtils;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.processor.Processor;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceKind;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.Comparator;

public class SaturnProcessor implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(SaturnProcessor.class);

    protected OData odata;
    protected ServiceMetadata serviceMetadata;
    protected SaturnEdmContext saturnEdmContext;

    @Override
    public void init(final OData odata, final ServiceMetadata serviceMetadata) {
        this.odata = odata;
        this.serviceMetadata = serviceMetadata;
    }

    public SaturnProcessor initialize(final SaturnEdmContext saturnEdmContext) {
        // todo
        // only use the entityTypes and entitySets
        this.saturnEdmContext = saturnEdmContext;
        return this;
    }

    protected Entity fromObject2Entity(final Object object) throws SaturnODataException, IllegalAccessException {
        return fromObject2Entity(object, null);
    }

    /**
     * This method takes an object to extract data and create an entityType defined in schema.
     *
     * @param object An instance of a class annotated with <code>@EdmEntity</code> or <code>@EdmComplex</code>
     * @param expandOption ..
     * @return ..
     */
    protected Entity fromObject2Entity(final Object object, final ExpandOption expandOption) throws SaturnODataException, IllegalAccessException {
        ExceptionUtils.assertNotNull(object, SelfDefinedType.ENTITY.getMessage());

        Entity entity = new Entity();
        Class<?> clazz = object.getClass();

        ODataEntityType oDataEntityType = clazz.getAnnotation(ODataEntityType.class);
        ODataEntitySet oDataEntitySet = clazz.getAnnotation(ODataEntitySet.class);
        ODataComplexType oDataComplexType = clazz.getAnnotation(ODataComplexType.class);

        if (oDataEntityType == null && oDataComplexType == null) {
            throw new SaturnODataException(
                    HttpStatusCode.INTERNAL_SERVER_ERROR, "Class %s should have annotation @ODataEntityType or @ODataComplexType.", clazz);
        } else if (oDataEntityType != null && oDataEntitySet == null) {
            throw new SaturnODataException(
                    HttpStatusCode.INTERNAL_SERVER_ERROR, "Class %s should have annotation @ODataEntitySet.", clazz);
        } else if (oDataEntityType != null && oDataEntityType.name().trim().isEmpty()) {
            throw new SaturnODataException(
                    HttpStatusCode.INTERNAL_SERVER_ERROR, "Class %s with annotation @ODataEntityType should have name field.", clazz);
        } else if (oDataEntitySet != null && oDataEntitySet.name().trim().isEmpty()) {
            throw new SaturnODataException(
                    HttpStatusCode.INTERNAL_SERVER_ERROR, "Class %s with annotation @ODataEntitySet should have name field.", clazz);
        }

        String entityName = oDataEntityType != null ? oDataEntityType.name() : oDataComplexType.name();
        List<Field> fields = ClassUtils.getFields(clazz);
        LOG.debug("{} fields loaded in class {}", fields.size(), clazz);

        for (Field field : fields) {
            LOG.debug("Start processing {} field of type {}.", field.getName(), field.getType());

            if (field.isAnnotationPresent(ODataProperty.class)) {
                Property property = generateEntityProperty(field, object, expandOption);
                entity.addProperty(property);
                LOG.debug("Load property {} into entity {} ", property, entityName);

            } else if (field.isAnnotationPresent(ODataNavigationProperty.class)) {
                Link link = generateEntityLink(field, object, expandOption);
                if (link != null) {
                    entity.getNavigationLinks().add(link);
                    LOG.debug("Load navigation property {} into entity {} ", link, entityName);
                }
            }
        }

        // entity should have a key array if it is not a complex type
        if (oDataEntityType != null) {

            String[] keys = oDataEntityType.keys();
            ExceptionUtils.assertLengthGreaterThanZero(keys, oDataEntityType.name() + " -> keys");
            Set<String> keySet = new HashSet<>(Arrays.asList(keys));
            Map<String, Object> keyValues = entity
                    .getProperties()
                    .stream()
                    .filter(k -> keySet.contains(k.getName()))
                    .collect(Collectors.toMap(Property::getName, Property::getValue));
            String entityId = ODataUtils.generateFormatedEntityId(keyValues);

            if (entityId != null) {
                try {
                    entity.setId(new URI(oDataEntitySet.name() + entityId));
                } catch (URISyntaxException e) {
                    throw new SaturnODataException(HttpStatusCode.INTERNAL_SERVER_ERROR, e.getMessage());
                }
            }
            entity.setType(String.format(StringUtils.FQN, oDataEntityType.namespace(), oDataEntityType.name()));

        } else {
            entity.setType(String.format(StringUtils.FQN, oDataComplexType.namespace(), oDataComplexType.name()));
        }

        return entity;
    }

    private Property generateEntityProperty(final Field field, final Object object, final ExpandOption expandOption) throws IllegalAccessException, SaturnODataException {

        field.setAccessible(true);
        Object actualValue = field.get(object);
        ODataProperty oDataProperty = field.getAnnotation(ODataProperty.class);
        String propertyName = oDataProperty.name().trim().isEmpty() ? field.getName() : oDataProperty.name();

        LOG.debug("Load property {} for edm entity {} from field {} of class {} with value: {}",
                propertyName, object.getClass(), field.getName(), field.getDeclaringClass(), actualValue);

        Class<?> fieldType = field.getType();
        String type = null;
        ValueType valueType = null;

        PrimitiveType primitiveType = ODataUtils.getPrimitiveType(fieldType);

        // for primitiveType condition
        if (primitiveType != null) {
            type = primitiveType.getType();
            valueType = ValueType.PRIMITIVE;

            if (primitiveType.equals(PrimitiveType.EDM_DATE)) {
                if (actualValue != null) {
                    LocalDate localDate = (LocalDate) actualValue;
                    actualValue = GregorianCalendar.from(localDate.atStartOfDay(ZoneId.systemDefault()));
                }
            } else if (primitiveType.equals(PrimitiveType.EDM_DATE_TIME)) {
                if (actualValue != null) {
                    LocalDateTime localDateTime = (LocalDateTime) actualValue;
                    actualValue = GregorianCalendar.from(localDateTime.atZone(ZoneId.systemDefault()));
                }
            }
        } else if (fieldType.isEnum()) {
            valueType = ValueType.ENUM;

            if (actualValue instanceof Enum) {
                Enum<?> odataEnum = (Enum<?>) actualValue;
                actualValue = odataEnum.ordinal();
            } else if (actualValue != null) {
                throw new SaturnODataException("%s is not an enum type", actualValue);
            }
        } else if (Collection.class.isAssignableFrom(fieldType)) {
            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
            Class<?> argType = (Class<?>) (parameterizedType.getActualTypeArguments()[0]);
            PrimitiveType pt = ODataUtils.getPrimitiveType(argType);

            // for condition List<Integer>, basic type of collections.
            if (pt != null) {
                type = String.format(StringUtils.COLLECTION_QUALIFIED_FULL_NAME, pt.getType());
                valueType = ValueType.COLLECTION_PRIMITIVE;
            } else {

                // todo: can be referenced by getFunction with collection parameters.
                // for condition List<Entity>
                FullQualifiedName fullQualifiedName =
                        ODataUtils.getFullQualifiedNameFromClassType(argType, saturnEdmContext.getNameSpace());
                if (fullQualifiedName != null) {
                    // todo, should clear whether should use fullQualifiedName.name().
                    type = String.format(StringUtils.COLLECTION_QUALIFIED_FULL_NAME, fullQualifiedName.toString());

                    if (argType.isAnnotationPresent(ODataEnumType.class)) {
                        valueType = ValueType.COLLECTION_ENUM;
                    } else if (argType.isAnnotationPresent(ODataComplexType.class)) {
                        valueType = ValueType.COLLECTION_COMPLEX;
                    }
                }
            }
        } else {

            if (!fieldType.isAnnotationPresent(ODataComplexType.class)) {
                throw new SaturnODataException(
                        HttpStatusCode.INTERNAL_SERVER_ERROR,
                        "Unrecognized type found: %s in field %s [%s].", fieldType, field.getName(), object.getClass().getName());
            }
            ODataComplexType oDataComplexType = fieldType.getAnnotation(ODataComplexType.class);
            Object complexObj = field.get(object);

            if (complexObj != null) {
                Entity complexEntity = fromObject2Entity(complexObj, expandOption);
                ComplexValue complexValue = new ComplexValue();
                complexValue.getValue().addAll(complexEntity.getProperties());
                actualValue = complexValue;
            }

            type = String.format(StringUtils.FQN, oDataComplexType.namespace(), oDataComplexType.name());
            valueType = ValueType.COMPLEX;
        }
        return new Property(type, propertyName, valueType, actualValue);
    }


    private Link generateEntityLink(final Field field, final Object object, final ExpandOption expandOption) {

        if (expandOption == null || expandOption.getExpandItems().isEmpty()) {
            return null;
        }

        ODataNavigationProperty oDataNavigationProperty = field.getAnnotation(ODataNavigationProperty.class);
        String linkName = oDataNavigationProperty.name().trim().isEmpty()
                ? field.getName() : oDataNavigationProperty.name();
        List<Entity> entities = new ArrayList<>();
        boolean collectionType = Collection.class.isAssignableFrom(field.getType());

        Optional<List<Entity>> optionalEntities = expandOption
                .getExpandItems()
                .parallelStream()
                .filter(expandItem -> expandItem
                        .getResourcePath()
                        .getUriResourceParts()
                        .parallelStream()
                        .anyMatch(uriResource -> uriResource
                                .getKind().equals(UriResourceKind.navigationProperty)
                                && uriResource.getSegmentValue().equals(linkName)))
                .findFirst()
                .map(expandItem -> {
                    ExpandOption expandNestedOption = expandItem.getExpandOption();

                    try {
                        field.setAccessible(true);
                        Object expandNestedObject = field.get(object);

                        if (expandNestedObject != null) {

                            if (collectionType) {
                                List<?> expandNestedObjects = (List<?>) expandNestedObject;
                                for (Object obj : expandNestedObjects) {
                                    Entity expandEntity = fromObject2Entity(obj, expandNestedOption);
                                    entities.add(expandEntity);
                                }
                            } else {
                                Entity expandEntity = fromObject2Entity(expandNestedObject, expandNestedOption);
                                entities.add(expandEntity);
                            }
                        }
                        return entities;
                    } catch (IllegalAccessException | SaturnODataException e) {
                        LOG.error(e.getMessage(), e);
                        return null;
                    }
                });

        if (optionalEntities.isPresent()) {
            Link link = new Link();
            link.setTitle(linkName);

            if (collectionType) {
                EntityCollection collection = new EntityCollection();
                collection.getEntities().addAll(entities);
                link.setInlineEntitySet(collection);
            } else {

                if (optionalEntities.get().isEmpty()) {
                    return null;
                }
                Entity entity = optionalEntities.get().get(0);
                link.setInlineEntity(entity);
                link.setType(entity.getType());
            }
            return link;
        }
        return null;
    }

    protected Object fromEntity2Object(final Entity entity, final Class<?> clazz) throws IllegalAccessException, InstantiationException {

        if (entity == null || clazz == null) {
            return null;
        }
        Object object = clazz.newInstance();
        List<Field> fields = ClassUtils.getFields(clazz);
        LOG.debug("{} class loaded in fields {}", clazz, fields.size());

        for (Field field : fields) {

            if (field.isAnnotationPresent(ODataProperty.class)) {

                ODataProperty oDataProperty = field.getAnnotation(ODataProperty.class);
                String propertyName = oDataProperty.name().trim().isEmpty() ? field.getName() : oDataProperty.name();
                Property property = entity.getProperty(propertyName);

                if (property != null) {
                    Class<?> fieldClass = field.getType();

                    if (fieldClass.isAnnotationPresent(ODataComplexType.class)) {
                        Entity complexEntity = new Entity();
                        ComplexValue complexValue = (ComplexValue) property.getValue();
                        complexEntity.getProperties().addAll(complexValue.getValue());
                        Object complexObject = fromEntity2Object(complexEntity, fieldClass);
                        field.setAccessible(true);
                        field.set(object, complexObject);

                    } else if (fieldClass.isAnnotationPresent(ODataEnumType.class) && property.asEnum() != null) {
                        Enum<?>[] constants = (Enum<?>[]) fieldClass.getEnumConstants();
                        Arrays.sort(constants, Comparator.comparingInt(Enum::ordinal));
                        // todo: need to test here
                        Object actualValue = constants[((Enum<?>) property.asEnum()).ordinal()];
                        field.setAccessible(true);
                        field.set(object, actualValue);

                    } else if (Collection.class.isAssignableFrom(fieldClass)) {
                        field.setAccessible(true);
                        field.set(object, property.getValue());

                    } else {
                        if (field.getType().isAssignableFrom(LocalDate.class)
                                && property.getValue() instanceof GregorianCalendar) {
                            field.setAccessible(true);
                            field.set(object, ((GregorianCalendar) property.getValue()).toZonedDateTime().toLocalDate());

                        } else if (field.getType().isAssignableFrom(LocalDateTime.class)
                                && property.getValue() instanceof Timestamp) {
                            field.setAccessible(true);
                            field.set(object, ((Timestamp) property.getValue()).toLocalDateTime());

                        } else {
                            field.setAccessible(true);
                            field.set(object, property.getValue());
                        }
                    }
                }

            } else if (field.isAnnotationPresent(ODataNavigationProperty.class)) {
                ODataNavigationProperty oDataNavigationProperty = field.getAnnotation(ODataNavigationProperty.class);
                String propertyName = oDataNavigationProperty.name().isEmpty() ? field.getName() : oDataNavigationProperty.name();
                Link link = entity.getNavigationLink(propertyName);

                if (link != null) {
                    Class<?> fieldClass = field.getType();

                    if (Collection.class.isAssignableFrom(fieldClass)) {
                        EntityCollection entityCollection = link.getInlineEntitySet();

                        if (entityCollection != null) {
                            List<Entity> entities = entityCollection.getEntities();
                            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                            Class<?> argType = (Class<?>) (parameterizedType.getActualTypeArguments()[0]);
                            List<Object> inlineObjects = new ArrayList<>();

                            for (Entity e : entities) {
                                Object inlineObject = fromEntity2Object(e, argType);
                                if (inlineObject != null) {
                                    inlineObjects.add(inlineObject);
                                }
                            }

                            field.setAccessible(true);
                            field.set(object, inlineObjects);
                        }

                    } else {
                        field.setAccessible(true);
                        ODataEntityType oDataEntityType = fieldClass.getAnnotation(ODataEntityType.class);
                        Class<?> entityClazz = saturnEdmContext.getEntityTypes().get(oDataEntityType.name());
                        Object entityObject = fromEntity2Object(link.getInlineEntity(), entityClazz);
                        field.set(object, entityObject);
                    }
                }
            }
        }
        return object;
    }

    protected void fromNaviBindings2NaviLinks(Entity reqEntity, Map<String, EntityOperation> entityOperationMap, String uri) throws SaturnODataException {
        List<Link> naviBindings = reqEntity.getNavigationBindings();

        if (naviBindings != null && naviBindings.size() > 0) {

            for (Link link : naviBindings) {
                Link naviLink = new Link();
                naviLink.setTitle(link.getTitle());
                reqEntity.getNavigationLinks().add(naviLink);

                if (link.getBindingLinks().isEmpty()) {
                    String bindingLink = link.getBindingLink();
                    Entity entity = generateEntityFromBindingLink(bindingLink, entityOperationMap, uri);
                    naviLink.setInlineEntity(entity);

                } else {
                    EntityCollection entityCollection = naviLink.getInlineEntitySet();

                    if (entityCollection == null) {
                        entityCollection = new EntityCollection();
                        naviLink.setInlineEntitySet(entityCollection);
                    }

                    for (String bindingLink : link.getBindingLinks()) {
                        Entity entity = generateEntityFromBindingLink(bindingLink, entityOperationMap, uri);
                        entityCollection.getEntities().add(entity);
                    }
                }
            }
        }
    }

    private Entity generateEntityFromBindingLink(String bindingLink, Map<String, EntityOperation> entityOperationMap, String uri) throws SaturnODataException {
        try {
            UriResourceEntitySet uriResourceEntitySet = odata.createUriHelper().parseEntityId(serviceMetadata.getEdm(), bindingLink, uri);
            EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();
            List<UriParameter> parameters = uriResourceEntitySet.getKeyPredicates();
            Map<String, UriParameter> parameterMap = parameters
                    .stream()
                    .collect(Collectors.toMap(UriParameter::getName, p -> p));

            // services defined by self implement interface SaturnODataService.
            EntityOperation entityOperation = entityOperationMap.get(edmEntitySet.getName());
            ExceptionUtils.assertNotNull(entityOperation, SelfDefinedType.SERVICE.getMessage(), edmEntitySet.getName());

            // the object is springEntity defined by self.
            Object object = entityOperation.retrieveByKey(parameterMap);
            ExceptionUtils.assertNotNull(object, SelfDefinedType.ENTITY.getMessage(), edmEntitySet.getName());

            return fromObject2Entity(object, null);

        } catch (DeserializerException | SaturnODataException | IllegalAccessException e) {
            throw new SaturnODataException(HttpStatusCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    protected EdmEntitySet getNavigationEntitySet(EdmEntitySet fromEdmEntitySet, EdmNavigationProperty edmNavigationProperty) throws SaturnODataException {
        EdmEntitySet navigationEntitySet;
        EdmBindingTarget edmBindingTarget = fromEdmEntitySet.getRelatedBindingTarget(edmNavigationProperty.getName());
        ExceptionUtils.assertNotNull(edmBindingTarget, EdmBindingTarget.class.getSimpleName());

        if (edmBindingTarget instanceof EdmEntitySet) {
            navigationEntitySet = (EdmEntitySet) edmBindingTarget;
        } else {
            throw new SaturnODataException(HttpStatusCode.NOT_IMPLEMENTED, "Haven't implemented yet.");
        }
        return navigationEntitySet;
    }

    public OData getOData() {
        return odata;
    }

    public ServiceMetadata getServiceMetadata() {
        return serviceMetadata;
    }

    public SaturnEdmContext getSaturnEdmContext() {
        return saturnEdmContext;
    }
}
