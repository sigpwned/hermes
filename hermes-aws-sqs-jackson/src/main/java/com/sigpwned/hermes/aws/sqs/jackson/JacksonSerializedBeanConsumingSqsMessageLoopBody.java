/*-
 * =================================LICENSE_START==================================
 * hermes-aws-sqs-jackson
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
package com.sigpwned.hermes.aws.sqs.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigpwned.hermes.aws.sqs.messageloop.body.BeanConsumingSqsMessageLoopBody;
import com.sigpwned.hermes.jackson.serialization.JacksonBeanMessageDeserializer;
import com.sigpwned.hermes.jackson.util.Jackson;

public abstract class JacksonSerializedBeanConsumingSqsMessageLoopBody<T>
    extends BeanConsumingSqsMessageLoopBody<T> {
  public JacksonSerializedBeanConsumingSqsMessageLoopBody(Class<T> type) {
    super(new JacksonBeanMessageDeserializer<>(type));
  }

  public JacksonSerializedBeanConsumingSqsMessageLoopBody(ObjectMapper mapper, Class<T> type) {
    super(new JacksonBeanMessageDeserializer<>(mapper, type));
  }

  public JacksonSerializedBeanConsumingSqsMessageLoopBody(TypeReference<T> type) {
    super(new JacksonBeanMessageDeserializer<>(Jackson.DEFAULT_OBJECT_MAPPER, type));
  }

  public JacksonSerializedBeanConsumingSqsMessageLoopBody(ObjectMapper mapper,
      TypeReference<T> type) {
    super(new JacksonBeanMessageDeserializer<>(mapper, type));
  }

  public JacksonSerializedBeanConsumingSqsMessageLoopBody(JavaType type) {
    super(new JacksonBeanMessageDeserializer<>(Jackson.DEFAULT_OBJECT_MAPPER, type));
  }

  public JacksonSerializedBeanConsumingSqsMessageLoopBody(ObjectMapper mapper, JavaType type) {
    super(new JacksonBeanMessageDeserializer<>(mapper, type));
  }
}
