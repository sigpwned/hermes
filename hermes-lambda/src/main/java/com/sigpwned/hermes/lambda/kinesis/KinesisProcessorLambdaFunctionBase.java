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
package com.sigpwned.hermes.lambda.kinesis;

import static java.util.Objects.requireNonNull;
import java.util.List;
import com.amazonaws.services.lambda.runtime.Context;
import com.sigpwned.hermes.core.MessageProducer;
import com.sigpwned.hermes.core.model.Message;
import com.sigpwned.hermes.core.model.MessageContent;

public abstract class KinesisProcessorLambdaFunctionBase extends KinesisConsumerLambdaFunctionBase {
  private final MessageProducer producer;

  public KinesisProcessorLambdaFunctionBase(MessageProducer producer) {
    this.producer = requireNonNull(producer);
  }

  public void handleRequest(List<Message> inputMessages, Context context) {
    List<MessageContent> outputMessages = processRequest(inputMessages, context);
    getProducer().send(outputMessages);
  }

  public abstract List<MessageContent> processRequest(List<Message> inputMessages, Context context);

  /**
   * @return the producer
   */
  protected MessageProducer getProducer() {
    return producer;
  }
}
