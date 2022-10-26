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
import com.sigpwned.hermes.aws.sqs.messageloop.body.BeanProcessingSqsMessageLoopBody;
import com.sigpwned.hermes.core.MessageProducer;
import com.sigpwned.hermes.jackson.serialization.JacksonBeanMessageDeserializer;
import com.sigpwned.hermes.jackson.serialization.JacksonBeanMessageSerializer;

public abstract class JacksonSerializedBeanProcessingSqsMessageLoopBody<I, O>
    extends BeanProcessingSqsMessageLoopBody<I, O> {
  public JacksonSerializedBeanProcessingSqsMessageLoopBody(MessageProducer producer,
      Class<I> inputType) {
    super(producer, new JacksonBeanMessageDeserializer<>(inputType),
        new JacksonBeanMessageSerializer<>());
  }

  public JacksonSerializedBeanProcessingSqsMessageLoopBody(MessageProducer producer,
      ObjectMapper mapper, Class<I> inputType) {
    super(producer, new JacksonBeanMessageDeserializer<>(mapper, inputType),
        new JacksonBeanMessageSerializer<>(mapper));
  }

  public JacksonSerializedBeanProcessingSqsMessageLoopBody(MessageProducer producer,
      TypeReference<I> inputType) {
    super(producer, new JacksonBeanMessageDeserializer<>(inputType),
        new JacksonBeanMessageSerializer<>());
  }

  public JacksonSerializedBeanProcessingSqsMessageLoopBody(MessageProducer producer,
      ObjectMapper mapper, TypeReference<I> inputType) {
    super(producer, new JacksonBeanMessageDeserializer<>(mapper, inputType),
        new JacksonBeanMessageSerializer<>(mapper));
  }

  public JacksonSerializedBeanProcessingSqsMessageLoopBody(MessageProducer producer,
      JavaType inputType) {
    super(producer, new JacksonBeanMessageDeserializer<>(inputType),
        new JacksonBeanMessageSerializer<>());
  }

  public JacksonSerializedBeanProcessingSqsMessageLoopBody(MessageProducer producer,
      ObjectMapper mapper, JavaType inputType) {
    super(producer, new JacksonBeanMessageDeserializer<>(mapper, inputType),
        new JacksonBeanMessageSerializer<>(mapper));
  }
}
