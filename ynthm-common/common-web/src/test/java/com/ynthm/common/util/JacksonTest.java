package com.ynthm.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.Data;
import org.junit.jupiter.api.Test;

/**
 * @author Ethan Wang
 * @version 1.0
 */
class JacksonTest {
  @Data
  public static class Person {
    private String name;
    private int age;
  }
  @Test
  void test() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    // 将JSON中的空字符串（""）作为null值绑定到一个POJO或者Map或者Collection集合对象
//    mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    mapper.coercionConfigFor(Person.class).setCoercion(CoercionInputShape.EmptyString, CoercionAction.Fail);
    SimpleModule simpleModule = new SimpleModule();
    simpleModule.addDeserializer(String.class, MyStringDeserializer.instance);
    mapper.registerModule(simpleModule);
    Person person = new Person();
    person.setName("");
    String personJson = mapper.writeValueAsString(person);
    Person s = mapper.readValue(personJson, Person.class);
    System.out.println(s);
  }
}
