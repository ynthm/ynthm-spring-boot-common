package com.ynthm.common.web.core.enums.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.ynthm.common.web.core.enums.EnumBase;
import java.io.IOException;
import java.io.Serializable;

/**
 * @author Ethan Wang
 * @version 1.0
 */
public class EnumeratorJsonSerializer extends JsonSerializer<EnumBase<?>> {
  @Override
  public void serialize(EnumBase<?> value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    Serializable valueObject = value.value();
    if (valueObject instanceof Integer) {
      gen.writeNumber((Integer) valueObject);
    } else if (valueObject instanceof String) {
      gen.writeString(valueObject.toString());
    }

    JsonStreamContext outputContext = gen.getOutputContext();
    if (outputContext.inObject()) {
      String currentName = outputContext.getCurrentName();
      gen.writeObjectField(currentName + "Label", value.label());
    }
  }
}
