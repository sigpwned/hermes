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
package com.sigpwned.hermes.lambda.sqs;

import static java.util.Objects.requireNonNull;
import java.util.List;
import com.amazonaws.services.lambda.runtime.Context;
import com.sigpwned.hermes.core.Message;
import com.sigpwned.hermes.core.MessageContent;
import com.sigpwned.hermes.core.MessageProducer;

public abstract class SqsProcessorLambdaFunctionBase extends SqsConsumerLambdaFunctionBase {
  private final MessageProducer producer;

  public SqsProcessorLambdaFunctionBase(MessageProducer producer) {
    this.producer = requireNonNull(producer);
  }

  public void handleMessages(List<Message> inputMessages, Context context) {
    List<MessageContent> outputMessages = processMessages(inputMessages, context);
    getProducer().send(outputMessages);
  }

  public abstract List<MessageContent> processMessages(List<Message> inputMessages,
      Context context);

  /**
   * @return the producer
   */
  protected MessageProducer getProducer() {
    return producer;
  }
}
