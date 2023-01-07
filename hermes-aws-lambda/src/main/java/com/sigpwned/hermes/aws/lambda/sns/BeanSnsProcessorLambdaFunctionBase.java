/*-
 * =================================LICENSE_START==================================
 * hermes-lambda
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
package com.sigpwned.hermes.aws.lambda.sns;

import static java.util.stream.Collectors.toList;
import java.util.List;
import com.amazonaws.services.lambda.runtime.Context;
import com.sigpwned.hermes.core.MessageProducer;
import com.sigpwned.hermes.core.model.Message;
import com.sigpwned.hermes.core.model.MessageContent;
import com.sigpwned.hermes.core.serialization.BeanMessageDeserializer;
import com.sigpwned.hermes.core.serialization.BeanMessageSerializer;

public abstract class BeanSnsProcessorLambdaFunctionBase<I, O>
    extends SnsProcessorLambdaFunctionBase {
  private final BeanMessageDeserializer<I> deserializer;
  private final BeanMessageSerializer<O> serializer;

  protected BeanSnsProcessorLambdaFunctionBase(MessageProducer producer,
      BeanMessageDeserializer<I> deserializer, BeanMessageSerializer<O> serializer) {
    super(producer);
    this.deserializer = deserializer;
    this.serializer = serializer;
  }

  @Override
  public List<MessageContent> processMessages(List<Message> inputMessages, Context context) {
    List<I> inputBeans =
        inputMessages.stream().map(getDeserializer()::deserializeBean).collect(toList());
    List<O> outputBeans = processBeans(inputBeans, context);
    return getSerializer().serializeBeans(outputBeans);
  }

  public abstract List<O> processBeans(List<I> inputBeans, Context context);

  /**
   * @return the deserializer
   */
  private BeanMessageDeserializer<I> getDeserializer() {
    return deserializer;
  }

  /**
   * @return the serializer
   */
  private BeanMessageSerializer<O> getSerializer() {
    return serializer;
  }
}
