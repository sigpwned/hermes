/*-
 * =================================LICENSE_START==================================
 * hermes-sns
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
package com.sigpwned.hermes.sns.messageproducer;

import com.sigpwned.hermes.aws.util.Sizeof;
import com.sigpwned.hermes.core.messageproducer.GroupingMessageProducer;
import com.sigpwned.hermes.core.model.MessageContent;
import com.sigpwned.hermes.sns.SnsDestination;
import com.sigpwned.hermes.sns.util.Sns;

public class SnsGroupingMessageProducer extends GroupingMessageProducer
    implements SnsMessageProducer {
  public SnsGroupingMessageProducer(SnsMessageProducer delegate) {
    super(delegate, Sns.MAX_PAYLOAD_SIZE, Sns.MAX_BATCH_LENGTH);
  }

  @Override
  protected long size(MessageContent message) {
    return Sizeof.messageContent(message);
  }

  @Override
  public SnsMessageProducer getDelegate() {
    return (SnsMessageProducer) super.getDelegate();
  }

  @Override
  public SnsDestination getDestination() {
    return getDelegate().getDestination();
  }
}
