package com.sigpwned.hermes.aws.lambda.jackson.sns;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.sigpwned.hermes.aws.lambda.sns.BeanSnsProcessorLambdaFunctionBase;
import com.sigpwned.hermes.core.MessageProducer;
import com.sigpwned.hermes.jackson.serialization.JacksonBeanMessageDeserializer;
import com.sigpwned.hermes.jackson.serialization.JacksonBeanMessageSerializer;

public abstract class JacksonSerializedBeanSnsProcessorLambdaFunctionBase<I, O>
    extends BeanSnsProcessorLambdaFunctionBase<I, O> {

  protected JacksonSerializedBeanSnsProcessorLambdaFunctionBase(MessageProducer producer,
      Class<I> type) {
    super(producer, new JacksonBeanMessageDeserializer<>(type),
        new JacksonBeanMessageSerializer<>());
  }

  protected JacksonSerializedBeanSnsProcessorLambdaFunctionBase(MessageProducer producer,
      TypeReference<I> type) {
    super(producer, new JacksonBeanMessageDeserializer<>(type),
        new JacksonBeanMessageSerializer<>());
  }

  protected JacksonSerializedBeanSnsProcessorLambdaFunctionBase(MessageProducer producer,
      JavaType type) {
    super(producer, new JacksonBeanMessageDeserializer<>(type),
        new JacksonBeanMessageSerializer<>());
  }
}
