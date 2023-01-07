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
package com.sigpwned.hermes.aws.sqs.messageproducer;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import com.sigpwned.hermes.aws.sqs.SqsDestination;
import com.sigpwned.hermes.aws.sqs.util.Sqs;
import com.sigpwned.hermes.core.model.Message;
import com.sigpwned.hermes.core.model.MessageContent;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchResponse;

/**
 * Sends the given messages to SNS.
 */
public class DefaultSqsMessageProducer implements SqsMessageProducer {
  private final SqsClient client;
  private final SqsDestination destination;

  public DefaultSqsMessageProducer(String topicArn) {
    this(SqsDestination.fromQueueArn(topicArn));
  }

  public DefaultSqsMessageProducer(SqsDestination destination) {
    this(Sqs.defaultClient(), destination);
  }

  public DefaultSqsMessageProducer(SqsClient client, SqsDestination destination) {
    this.client = requireNonNull(client);
    this.destination = requireNonNull(destination);
  }

  @Override
  public void send(List<MessageContent> messages) {
    if (messages.size() > Sqs.MAX_SEND_BATCH_LENGTH)
      throw new IllegalArgumentException(
          "messages must have length no more than " + Sqs.MAX_SEND_BATCH_LENGTH);
    final AtomicInteger idseq = new AtomicInteger(1);
    List<Message> batch = messages.stream()
        .map(m -> Message.of(Integer.toString(idseq.getAndIncrement()), m)).collect(toList());
    while (!batch.isEmpty()) {
      SendMessageBatchResponse response = getClient().sendMessageBatch(
          SendMessageBatchRequest.builder().queueUrl(getDestination().toQueueUrl())
              .entries(batch.stream().map(Sqs::toSendMessageBatchRequestEntry).collect(toList()))
              .build());
      batch = batch.stream().filter(m -> response.failed().stream().filter(f -> !f.senderFault())
          .anyMatch(f -> m.getId().equals(f.id()))).collect(toList());
    }
  }

  /**
   * @return the client
   */
  private SqsClient getClient() {
    return client;
  }

  /**
   * @return the destination
   */
  @Override
  public SqsDestination getDestination() {
    return destination;
  }
}
