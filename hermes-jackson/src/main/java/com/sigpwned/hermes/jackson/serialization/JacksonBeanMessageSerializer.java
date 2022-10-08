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
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigpwned.hermes.core.model.MessageContent;
import com.sigpwned.hermes.core.model.MessageHeaders;
import com.sigpwned.hermes.core.serialization.BeanMessageSerializer;
import com.sigpwned.hermes.jackson.util.Jackson;

public class JacksonBeanMessageSerializer<T> implements BeanMessageSerializer<T> {
  private final ObjectMapper mapper;

  public JacksonBeanMessageSerializer() {
    this(Jackson.DEFAULT_OBJECT_MAPPER);
  }

  public JacksonBeanMessageSerializer(ObjectMapper mapper) {
    this.mapper = requireNonNull(mapper);
  }

  @Override
  public List<MessageContent> serializeBeans(List<T> values) {
    return preprocess(values).stream().map(v -> {
      String body;
      try {
        body = getMapper().writeValueAsString(v);
      } catch (IOException e) {
        throw new UncheckedIOException("Failed to serialize value " + v, e);
      }
      return MessageContent.of(MessageHeaders.EMPTY, body);
    }).toList();
  }

  /**
   * customization hook for batching, etc.
   */
  protected List<T> preprocess(List<T> values) {
    return values;
  }

  /**
   * @return the mapper
   */
  private ObjectMapper getMapper() {
    return mapper;
  }
}
