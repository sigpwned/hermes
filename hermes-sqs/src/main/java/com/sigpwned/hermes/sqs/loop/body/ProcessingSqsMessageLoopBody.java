/*-
 * =================================LICENSE_START==================================
 * hermes-sqs
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
package com.sigpwned.hermes.sqs.loop.body;

import static java.util.Objects.requireNonNull;
import java.util.List;
import com.sigpwned.hermes.core.MessageContent;
import com.sigpwned.hermes.core.MessageProducer;
import com.sigpwned.hermes.sqs.consumer.SqsMessage;
import com.sigpwned.hermes.sqs.loop.SqsMessageLoopBody;

public abstract class ProcessingSqsMessageLoopBody implements SqsMessageLoopBody {
  private final MessageProducer producer;

  public ProcessingSqsMessageLoopBody(MessageProducer producer) {
    this.producer = requireNonNull(producer);
  }

  @Override
  public void acceptMessages(List<SqsMessage> inputMessages) {
    List<MessageContent> outputMessages = processMessages(inputMessages);
    if (!outputMessages.isEmpty())
      getProducer().send(outputMessages);
  }

  public abstract List<MessageContent> processMessages(List<SqsMessage> inputMessages);

  /**
   * @return the producer
   */
  private MessageProducer getProducer() {
    return producer;
  }
}
