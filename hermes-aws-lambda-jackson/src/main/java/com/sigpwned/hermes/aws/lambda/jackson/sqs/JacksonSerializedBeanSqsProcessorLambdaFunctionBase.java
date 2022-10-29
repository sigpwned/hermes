/*-
 * =================================LICENSE_START==================================
 * hermes-aws-lambda-jackson
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
package com.sigpwned.hermes.aws.lambda.jackson.sqs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigpwned.hermes.aws.lambda.sqs.BeanSqsProcessorLambdaFunctionBase;
import com.sigpwned.hermes.core.MessageProducer;
import com.sigpwned.hermes.jackson.serialization.JacksonBeanMessageDeserializer;
import com.sigpwned.hermes.jackson.serialization.JacksonBeanMessageSerializer;

public abstract class JacksonSerializedBeanSqsProcessorLambdaFunctionBase<I, O>
    extends BeanSqsProcessorLambdaFunctionBase<I, O> {

  protected JacksonSerializedBeanSqsProcessorLambdaFunctionBase(MessageProducer producer,
      Class<I> type) {
    super(producer, new JacksonBeanMessageDeserializer<>(type),
        new JacksonBeanMessageSerializer<>());
  }

  protected JacksonSerializedBeanSqsProcessorLambdaFunctionBase(MessageProducer producer,
      ObjectMapper mapper, Class<I> type) {
    super(producer, new JacksonBeanMessageDeserializer<>(mapper, type),
        new JacksonBeanMessageSerializer<>(mapper));
  }

  protected JacksonSerializedBeanSqsProcessorLambdaFunctionBase(MessageProducer producer,
      TypeReference<I> type) {
    super(producer, new JacksonBeanMessageDeserializer<>(type),
        new JacksonBeanMessageSerializer<>());
  }

  protected JacksonSerializedBeanSqsProcessorLambdaFunctionBase(MessageProducer producer,
      ObjectMapper mapper, TypeReference<I> type) {
    super(producer, new JacksonBeanMessageDeserializer<>(mapper, type),
        new JacksonBeanMessageSerializer<>(mapper));
  }

  protected JacksonSerializedBeanSqsProcessorLambdaFunctionBase(MessageProducer producer,
      JavaType type) {
    super(producer, new JacksonBeanMessageDeserializer<>(type),
        new JacksonBeanMessageSerializer<>());
  }

  protected JacksonSerializedBeanSqsProcessorLambdaFunctionBase(MessageProducer producer,
      ObjectMapper mapper, JavaType type) {
    super(producer, new JacksonBeanMessageDeserializer<>(mapper, type),
        new JacksonBeanMessageSerializer<>(mapper));
  }
}
