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

import java.util.ArrayList;
import java.util.List;
import com.sigpwned.hermes.core.BeanMessageDeserializer;
import com.sigpwned.hermes.sqs.consumer.SqsMessage;

public abstract class BeanConsumingSqsMessageLoopBody<T> extends ConsumingSqsMessageLoopBody {
  private final BeanMessageDeserializer<T> deserializer;

  public BeanConsumingSqsMessageLoopBody(BeanMessageDeserializer<T> deserializer) {
    this.deserializer = deserializer;
  }

  @Override
  public void acceptMessages(List<SqsMessage> messages) {
    List<T> beans = new ArrayList<>();
    for (SqsMessage message : messages)
      beans.add(getDeserializer().deserializeBean(message));
  }

  public abstract void acceptBeans(List<T> beans);

  /**
   * @return the deserializer
   */
  private BeanMessageDeserializer<T> getDeserializer() {
    return deserializer;
  }
}
