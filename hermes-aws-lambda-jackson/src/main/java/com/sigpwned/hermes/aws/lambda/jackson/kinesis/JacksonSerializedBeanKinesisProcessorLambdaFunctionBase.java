package com.sigpwned.hermes.aws.lambda.jackson.kinesis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.sigpwned.hermes.aws.lambda.kinesis.BeanKinesisProcessorLambdaFunctionBase;
import com.sigpwned.hermes.core.MessageProducer;
import com.sigpwned.hermes.jackson.serialization.JacksonBeanMessageDeserializer;
import com.sigpwned.hermes.jackson.serialization.JacksonBeanMessageSerializer;

public abstract class JacksonSerializedBeanKinesisProcessorLambdaFunctionBase<I, O>
    extends BeanKinesisProcessorLambdaFunctionBase<I, O> {

  protected JacksonSerializedBeanKinesisProcessorLambdaFunctionBase(MessageProducer producer,
      Class<I> type) {
    super(producer, new JacksonBeanMessageDeserializer<>(type),
        new JacksonBeanMessageSerializer<>());
  }

  protected JacksonSerializedBeanKinesisProcessorLambdaFunctionBase(MessageProducer producer,
      TypeReference<I> type) {
    super(producer, new JacksonBeanMessageDeserializer<>(type),
        new JacksonBeanMessageSerializer<>());
  }

  protected JacksonSerializedBeanKinesisProcessorLambdaFunctionBase(MessageProducer producer,
      JavaType type) {
    super(producer, new JacksonBeanMessageDeserializer<>(type),
        new JacksonBeanMessageSerializer<>());
  }
}
