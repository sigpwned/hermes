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
 * Partitions messages into groups that can be sent together to reduce the number of send calls, and
 * therefore the cost of sending.
 */
public abstract class GroupingMessageProducer implements MessageProducer {
  private final MessageProducer delegate;
  private final int maxPayloadSize;
  private final int maxGroupLength;

  public GroupingMessageProducer(MessageProducer delegate, int maxPayloadSize, int maxGroupLength) {
    this.delegate = delegate;
    this.maxPayloadSize = maxPayloadSize;
    this.maxGroupLength = maxGroupLength;
  }

  @Override
  public void send(List<MessageContent> messages) {
    List<List<MessageContent>> grouped;
    if (messages.isEmpty()) {
      grouped = emptyList();
    } else {
      grouped = group(messages);
    }
    for (List<MessageContent> group : grouped)
      getDelegate().send(group);
  }

  private List<List<MessageContent>> group(List<MessageContent> messages) {
    List<List<MessageContent>> result = new ArrayList<>();

    // How big is each message?
    List<SizedMessageContent> sizeds =
        messages.stream().map(m -> SizedMessageContent.of(m, Math.toIntExact(size(m))))
            .collect(toCollection(ArrayList::new));

    // Sort messages by size descending
    Collections.sort(sizeds, Comparator.comparingInt(SizedMessageContent::getSize).reversed());

    // Collect the messages into batches of allowed size
    while (!sizeds.isEmpty()) {
      List<MessageContent> group = new ArrayList<>();

      Iterator<SizedMessageContent> iterator = sizeds.iterator();
      while (iterator.hasNext()) {
        int currentPayloadSize = 0;
        SizedMessageContent smc = iterator.next();
        if (currentPayloadSize + smc.getSize() <= getMaxPayloadSize()) {
          currentPayloadSize = currentPayloadSize + smc.getSize();
          group.add(smc.getMessage());
          iterator.remove();
        }
        if (group.size() >= getMaxGroupLength())
          break;
      }

      if (group.isEmpty())
        throw new IllegalArgumentException(format("%d messages are too big", sizeds.size()));

      result.add(group);
    }

    return result;
  }

  protected abstract long size(MessageContent message);

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

  /**
   * @return the maxPayloadSize
   */
  public int getMaxGroupLength() {
    return maxGroupLength;
  }
}
