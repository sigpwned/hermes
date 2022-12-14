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
package com.sigpwned.hermes.aws.sqs.messageconsumer.batch;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import java.util.List;
import com.sigpwned.hermes.aws.sqs.SqsDestination;
import com.sigpwned.hermes.aws.sqs.messageconsumer.SqsMessage;
import com.sigpwned.hermes.aws.sqs.messageconsumer.SqsMessageBatch;
import com.sigpwned.hermes.aws.sqs.util.Sqs;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequestEntry;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchResponse;

public class DefaultSqsMessageBatch implements SqsMessageBatch {
  private final SqsClient client;
  private final SqsDestination destination;
  private final List<SqsMessage> messages;

  public DefaultSqsMessageBatch(SqsClient client, SqsDestination destination,
      List<SqsMessage> messages) {
    if (messages.size() > Sqs.MAX_MAX_NUMBER_OF_MESSAGES)
      throw new IllegalArgumentException("messages size must be no more than " + messages.size());
    this.client = requireNonNull(client);
    this.destination = requireNonNull(destination);
    this.messages = unmodifiableList(messages);
  }

  /**
   * @return the messages
   */
  @Override
  public List<SqsMessage> getMessages() {
    return messages;
  }

  @Override
  public void close() {
    List<DeleteMessageBatchRequestEntry> deletes = messages.stream().filter(SqsMessage::retired)
        .map(Sqs::toDeleteMessageBatchRequestEntry).collect(toList());
    while (!deletes.isEmpty()) {
      DeleteMessageBatchResponse response = getClient().deleteMessageBatch(DeleteMessageBatchRequest
          .builder().queueUrl(getDestination().toQueueUrl()).entries(deletes).build());
      deletes = deletes.stream().filter(m -> response.failed().stream()
          .filter(f -> !f.senderFault()).anyMatch(f -> m.id().equals(f.id()))).collect(toList());
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
