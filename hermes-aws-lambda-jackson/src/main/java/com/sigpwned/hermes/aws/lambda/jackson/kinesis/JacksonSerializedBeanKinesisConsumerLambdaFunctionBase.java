package com.sigpwned.hermes.aws.lambda.jackson.kinesis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.sigpwned.hermes.aws.lambda.kinesis.BeanKinesisConsumerLambdaFunctionBase;
import com.sigpwned.hermes.jackson.serialization.JacksonBeanMessageDeserializer;

public abstract class JacksonSerializedBeanKinesisConsumerLambdaFunctionBase<T>
    extends BeanKinesisConsumerLambdaFunctionBase<T> {

  protected JacksonSerializedBeanKinesisConsumerLambdaFunctionBase(Class<T> type) {
    super(new JacksonBeanMessageDeserializer<>(type));
  }

  protected JacksonSerializedBeanKinesisConsumerLambdaFunctionBase(TypeReference<T> type) {
    super(new JacksonBeanMessageDeserializer<>(type));
  }

  protected JacksonSerializedBeanKinesisConsumerLambdaFunctionBase(JavaType type) {
    super(new JacksonBeanMessageDeserializer<>(type));
  }
}
