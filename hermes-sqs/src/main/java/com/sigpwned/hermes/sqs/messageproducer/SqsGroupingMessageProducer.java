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
package com.sigpwned.hermes.sqs.messageproducer;

import com.sigpwned.hermes.aws.util.Sizeof;
import com.sigpwned.hermes.core.messageproducer.GroupingMessageProducer;
import com.sigpwned.hermes.core.model.MessageContent;
import com.sigpwned.hermes.sqs.SqsDestination;
import com.sigpwned.hermes.sqs.util.Sqs;

public class SqsGroupingMessageProducer extends GroupingMessageProducer
    implements SqsMessageProducer {
  public SqsGroupingMessageProducer(SqsMessageProducer delegate) {
    super(delegate, Sqs.MAX_SEND_PAYLOAD_SIZE, Sqs.MAX_SEND_BATCH_LENGTH);
  }

  @Override
  protected long size(MessageContent message) {
    return Sizeof.messageContent(message);
  }

  @Override
  public SqsMessageProducer getDelegate() {
    return (SqsMessageProducer) super.getDelegate();
  }

  @Override
  public SqsDestination getDestination() {
    return getDelegate().getDestination();
  }
}
