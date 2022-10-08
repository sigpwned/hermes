/*-
 * =================================LICENSE_START==================================
 * hermes-jackson
 * ====================================SECTION=====================================
 * Copyright (C) 2022 Andy Boothe
 * ====================================SECTION=====================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==================================LICENSE_END===================================
 */
package com.sigpwned.hermes.jackson.serialization;

import static java.util.Objects.requireNonNull;
import java.io.IOException;
import java.io.UncheckedIOException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.sigpwned.hermes.core.model.Message;
import com.sigpwned.hermes.core.serialization.BeanMessageDeserializer;
import com.sigpwned.hermes.jackson.util.Jackson;

public class JacksonBeanMessageDeserializer<T> implements BeanMessageDeserializer<T> {
  private final ObjectMapper mapper;
  private final JavaType type;

  public JacksonBeanMessageDeserializer(Class<T> type) {
    this(Jackson.DEFAULT_OBJECT_MAPPER, type);
  }

  public JacksonBeanMessageDeserializer(ObjectMapper mapper, Class<T> type) {
    this(mapper, TypeFactory.defaultInstance().constructType(type));
  }

  public JacksonBeanMessageDeserializer(TypeReference<T> type) {
    this(Jackson.DEFAULT_OBJECT_MAPPER, TypeFactory.defaultInstance().constructType(type));
  }

  public JacksonBeanMessageDeserializer(ObjectMapper mapper, TypeReference<T> type) {
    this(mapper, TypeFactory.defaultInstance().constructType(type));
  }

  public JacksonBeanMessageDeserializer(JavaType type) {
    this(Jackson.DEFAULT_OBJECT_MAPPER, type);
  }

  public JacksonBeanMessageDeserializer(ObjectMapper mapper, JavaType type) {
    this.mapper = requireNonNull(mapper);
    this.type = requireNonNull(type);
  }

  @Override
  public T deserializeBean(Message m) {
    T result;
    try {
      result = getMapper().readValue(m.getBody(), getType());
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to deserialize message", e);
    }
    return result;
  }

  /**
   * @return the mapper
   */
  public ObjectMapper getMapper() {
    return mapper;
  }

  /**
   * @return the type
   */
  public JavaType getType() {
    return type;
  }
}
