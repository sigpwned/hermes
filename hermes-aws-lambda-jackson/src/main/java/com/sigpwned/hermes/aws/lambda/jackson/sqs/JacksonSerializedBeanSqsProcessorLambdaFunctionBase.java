package com.sigpwned.hermes.aws.lambda.jackson.sqs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
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
      TypeReference<I> type) {
    super(producer, new JacksonBeanMessageDeserializer<>(type),
        new JacksonBeanMessageSerializer<>());
  }

  protected JacksonSerializedBeanSqsProcessorLambdaFunctionBase(MessageProducer producer,
      JavaType type) {
    super(producer, new JacksonBeanMessageDeserializer<>(type),
        new JacksonBeanMessageSerializer<>());
  }
}
