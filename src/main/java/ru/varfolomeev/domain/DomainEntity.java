package ru.varfolomeev.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;

public abstract class DomainEntity {
    public Document toDocument() {
        return MapperWrapper.oMapper.convertValue(this, Document.class);
    }

    public static <T extends DomainEntity> T fromDocument(Document document, Class<T> clazz) {
        return MapperWrapper.fromDocument(document, clazz);
    }

    private static class MapperWrapper {
        private static final ObjectMapper oMapper = new ObjectMapper();

        static <T extends DomainEntity> T fromDocument(Document document, Class<T> clazz) {
            return MapperWrapper.oMapper.convertValue(document, clazz);
        }
    }
}
