package com.sigpwned.hermes.aws.lambda.jackson.sqs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.sigpwned.hermes.aws.lambda.sqs.BeanSqsConsumerLambdaFunctionBase;
import com.sigpwned.hermes.jackson.serialization.JacksonBeanMessageDeserializer;

public abstract class JacksonSerializedBeanSqsConsumerLambdaFunctionBase<T>
    extends BeanSqsConsumerLambdaFunctionBase<T> {

  protected JacksonSerializedBeanSqsConsumerLambdaFunctionBase(Class<T> type) {
    super(new JacksonBeanMessageDeserializer<>(type));
  }

  protected JacksonSerializedBeanSqsConsumerLambdaFunctionBase(TypeReference<T> type) {
    super(new JacksonBeanMessageDeserializer<>(type));
  }

  protected JacksonSerializedBeanSqsConsumerLambdaFunctionBase(JavaType type) {
    super(new JacksonBeanMessageDeserializer<>(type));
  }
}
