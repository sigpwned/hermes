/*-
 * =================================LICENSE_START==================================
 * hermes-aws-lambda
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
package com.sigpwned.hermes.aws.lambda.serialization;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.amazonaws.services.lambda.runtime.serialization.PojoSerializer;
import com.amazonaws.services.lambda.runtime.serialization.events.LambdaEventSerializers;
import com.sigpwned.hermes.core.model.MessageContent;
import com.sigpwned.hermes.core.model.MessageHeaders;
import com.sigpwned.hermes.core.serialization.BeanMessageSerializer;

/**
 * Deserializes AWS Events.
 */
public class AwsPojoBeanMessageSerializer<T> implements BeanMessageSerializer<T> {
  private final PojoSerializer<T> serializer;

  public AwsPojoBeanMessageSerializer(Class<T> clazz) {
    this(LambdaEventSerializers.serializerFor(clazz,
        Thread.currentThread().getContextClassLoader()));
  }

  public AwsPojoBeanMessageSerializer(PojoSerializer<T> serializer) {
    this.serializer = serializer;
  }

  @Override
  public List<MessageContent> serializeBeans(List<T> values) {
    return values.stream().map(v -> MessageContent.of(MessageHeaders.EMPTY, toJson(v))).toList();
  }

  private String toJson(T value) {
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    try {
      try {
        getSerializer().toJson(value, buf);
      } finally {
        buf.close();
      }
    } catch (IOException e) {
      throw new UncheckedIOException("unexpected I/O exception from in-memory stream", e);
    }
    return new String(buf.toByteArray(), StandardCharsets.UTF_8);
  }

  /**
   * @return the serializer
   */
  private PojoSerializer<T> getSerializer() {
    return serializer;
  }
}
