package com.sigpwned.hermes.aws.lambda.jackson.sns;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.sigpwned.hermes.aws.lambda.sns.BeanSnsConsumerLambdaFunctionBase;
import com.sigpwned.hermes.jackson.serialization.JacksonBeanMessageDeserializer;

public abstract class JacksonSerializedBeanSnsConsumerLambdaFunctionBase<T>
    extends BeanSnsConsumerLambdaFunctionBase<T> {

  protected JacksonSerializedBeanSnsConsumerLambdaFunctionBase(Class<T> type) {
    super(new JacksonBeanMessageDeserializer<>(type));
  }

  protected JacksonSerializedBeanSnsConsumerLambdaFunctionBase(TypeReference<T> type) {
    super(new JacksonBeanMessageDeserializer<>(type));
  }

  protected JacksonSerializedBeanSnsConsumerLambdaFunctionBase(JavaType type) {
    super(new JacksonBeanMessageDeserializer<>(type));
  }
}
