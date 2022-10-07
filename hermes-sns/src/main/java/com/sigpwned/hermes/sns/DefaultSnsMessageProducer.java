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
package com.sigpwned.hermes.sns;

import static java.util.Objects.requireNonNull;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import com.sigpwned.hermes.core.Message;
import com.sigpwned.hermes.core.MessageContent;
import com.sigpwned.hermes.sns.util.Sns;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishBatchRequest;
import software.amazon.awssdk.services.sns.model.PublishBatchResponse;

/**
 * Sends the given messages to SNS.
 */
public class DefaultSnsMessageProducer implements SnsMessageProducer {
  private final SnsClient client;
  private final SnsDestination destination;

  public DefaultSnsMessageProducer(String topicArn) {
    this(SnsDestination.fromTopicArn(topicArn));
  }

  public DefaultSnsMessageProducer(SnsDestination destination) {
    this(Sns.defaultClient(), destination);
  }

  public DefaultSnsMessageProducer(SnsClient client, SnsDestination destination) {
    this.client = requireNonNull(client);
    this.destination = requireNonNull(destination);
  }

  @Override
  public void send(List<MessageContent> messages) {
    final AtomicInteger idseq = new AtomicInteger(1);
    List<Message> batch = messages.stream()
        .map(m -> Message.of(Integer.toString(idseq.getAndIncrement()), m)).toList();
    while (!batch.isEmpty()) {
      PublishBatchResponse response = getClient().publishBatch(PublishBatchRequest.builder()
          .topicArn(getDestination().toTopicArn())
          .publishBatchRequestEntries(batch.stream().map(Sns::toPublishBatchRequestEntry).toList())
          .build());
      batch = batch.stream().filter(m -> response.failed().stream().filter(f -> !f.senderFault())
          .anyMatch(f -> m.getId().equals(f.id()))).toList();
    }
  }

  /**
   * @return the client
   */
  private SnsClient getClient() {
    return client;
  }

  /**
   * @return the destination
   */
  @Override
  public SnsDestination getDestination() {
    return destination;
  }
}
