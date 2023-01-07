/*-
 * =================================LICENSE_START==================================
 * hermes-core
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
package com.sigpwned.hermes.core.messageproducer;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toCollection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import com.sigpwned.hermes.core.MessageProducer;
import com.sigpwned.hermes.core.model.MessageContent;
import com.sigpwned.hermes.core.util.SizedMessageContent;

/**
 * Combines multiple smaller messages into fewer larger, batched messages to reduce the number of
 * messages sent, and therefore the cost of sending.
 */
public abstract class BatchingMessageProducer implements MessageProducer {
  private final MessageProducer delegate;
  private final int maxPayloadSize;

  public BatchingMessageProducer(MessageProducer delegate, int maxPayloadSize) {
    this.delegate = delegate;
    this.maxPayloadSize = maxPayloadSize;
  }

  @Override
  public void send(List<MessageContent> messages) {
    List<MessageContent> combined;
    if (messages.isEmpty()) {
      combined = emptyList();
    } else {
      combined = combine(messages);
    }
    getDelegate().send(combined);
  }

  private List<MessageContent> combine(List<MessageContent> messages) {
    List<MessageContent> result = new ArrayList<>();

    // How big is each message?
    List<SizedMessageContent> sizeds =
        messages.stream().map(m -> SizedMessageContent.of(m, Math.toIntExact(size(m))))
            .collect(toCollection(ArrayList::new));

    // Sort messages by size descending
    Collections.sort(sizeds, Comparator.comparingInt(SizedMessageContent::getSize).reversed());

    // Collect the messages into batches of allowed size
    while (!sizeds.isEmpty()) {
      int currentPayloadSize = 0;
      List<MessageContent> payload = new ArrayList<>();

      Iterator<SizedMessageContent> iterator = sizeds.iterator();
      while (iterator.hasNext()) {
        SizedMessageContent smc = iterator.next();
        if (currentPayloadSize + smc.getSize() <= getMaxPayloadSize()) {
          currentPayloadSize = currentPayloadSize + smc.getSize();
          payload.add(smc.getMessage());
          iterator.remove();
        }
      }

      if (payload.isEmpty())
        throw new IllegalArgumentException(format("%d messages are too big", sizeds.size()));

      result.add(batch(payload));
    }

    return result;
  }

  protected abstract long size(MessageContent message);

  protected abstract MessageContent batch(List<MessageContent> messages);

  /**
   * @return the delegate
   */
  public MessageProducer getDelegate() {
    return delegate;
  }

  /**
   * @return the maxPayloadSize
   */
  public int getMaxPayloadSize() {
    return maxPayloadSize;
  }
}
