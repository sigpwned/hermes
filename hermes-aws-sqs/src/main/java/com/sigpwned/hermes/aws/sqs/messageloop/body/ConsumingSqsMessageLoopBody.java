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
package com.sigpwned.hermes.aws.sqs.messageloop.body;

import java.util.List;
import com.sigpwned.hermes.aws.sqs.messageconsumer.SqsMessage;
import com.sigpwned.hermes.aws.sqs.messageconsumer.SqsMessageBatch;
import com.sigpwned.hermes.aws.sqs.messageloop.SqsMessageLoopBody;

public abstract class ConsumingSqsMessageLoopBody implements SqsMessageLoopBody {
  @Override
  public final void acceptBatch(SqsMessageBatch batch) throws InterruptedException {
    acceptMessages(batch.getMessages());
    batch.retireAll();
  }

  protected abstract void acceptMessages(List<SqsMessage> messages);
}
