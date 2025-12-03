package com.ynthm.common.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import java.io.IOException;

/**
 * @author Ethan Wang
 * @version 1.0
 */
@JacksonStdImpl
public class MyStringDeserializer extends StdScalarDeserializer<String> {
  public static final MyStringDeserializer instance = new MyStringDeserializer();
  private static final long serialVersionUID = 1L;

  public MyStringDeserializer() {
    super(String.class);
  }

  @Override
  public LogicalType logicalType() {
    return LogicalType.Textual;
  }

  @Override
  public boolean isCachable() {
    return true;
  }

  @Override
  public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
    return getNullValue(ctxt);
  }

  public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    if (p.hasToken(JsonToken.VALUE_STRING)) {
      return p.getText().isEmpty() ? null : p.getText();
    } else {
      return p.hasToken(JsonToken.START_ARRAY)
          ? this._deserializeFromArray(p, ctxt)
          : this._parseString(p, ctxt);
    }
  }

  @Override
  public String deserializeWithType(
      JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
      throws IOException {
    return this.deserialize(p, ctxt);
  }
}
