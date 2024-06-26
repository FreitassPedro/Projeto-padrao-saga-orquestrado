package br.com.microservices.orchestrated.inventoryservice.core.utils;

import br.com.microservices.orchestrated.inventoryservice.core.dto.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor @Slf4j
public class JsonUtil {
    private final ObjectMapper objectMapper; //Mapper para transformar Json em ObjectJava, e vice-versa

    public String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error("Exception inventory-service convertion toJson");
            return "";
        }
    }

    public Event toEvent(String json) {

        try {
            return objectMapper.readValue(json, Event.class);
        } catch (Exception e) {
            return null;
        }
    }
}
