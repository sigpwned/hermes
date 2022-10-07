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
package com.sigpwned.hermes.sqs.messageconsumer;

import java.util.List;
import com.sigpwned.hermes.sqs.SqsDestination;
import com.sigpwned.hermes.sqs.messageconsumer.batch.DefaultSqsMessageBatch;
import com.sigpwned.hermes.sqs.util.Sqs;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

public class SqsMessageConsumer {
  private final SqsClient client;
  private final SqsDestination destination;

  public SqsMessageConsumer(SqsClient client, SqsDestination destination) {
    this.client = client;
    this.destination = destination;
  }

  public SqsMessageBatch receive(int maxNumberOfMessages, int waitTimeSeconds,
      int visibilityTimeout) {
    if (maxNumberOfMessages < Sqs.MIN_MAX_NUMBER_OF_MESSAGES)
      throw new IllegalArgumentException(
          "maxNumberOfMessages must be no less than " + Sqs.MIN_MAX_NUMBER_OF_MESSAGES);
    if (maxNumberOfMessages > Sqs.MAX_MAX_NUMBER_OF_MESSAGES)
      throw new IllegalArgumentException(
          "maxNumberOfMessages must be no more than " + Sqs.MAX_MAX_NUMBER_OF_MESSAGES);
    if (waitTimeSeconds < Sqs.MIN_WAIT_TIME_SECONDS)
      throw new IllegalArgumentException(
          "waitTimeSeconds must be no less than " + Sqs.MIN_WAIT_TIME_SECONDS);
    if (waitTimeSeconds > Sqs.MAX_WAIT_TIME_SECONDS)
      throw new IllegalArgumentException(
          "waitTimeSeconds must be no more than " + Sqs.MAX_WAIT_TIME_SECONDS);
    if (visibilityTimeout < Sqs.MIN_VISIBILITY_TIMEOUT_SECONDS)
      throw new IllegalArgumentException(
          "visibilityTimeout must be no less than " + Sqs.MIN_VISIBILITY_TIMEOUT_SECONDS);
    if (visibilityTimeout > Sqs.MAX_VISIBILITY_TIMEOUT_SECONDS)
      throw new IllegalArgumentException(
          "visibilityTimeout must be no more than " + Sqs.MAX_VISIBILITY_TIMEOUT_SECONDS);

    ReceiveMessageResponse response = getClient().receiveMessage(ReceiveMessageRequest.builder()
        .queueUrl(getDestination().toQueueUrl()).maxNumberOfMessages(maxNumberOfMessages)
        .visibilityTimeout(visibilityTimeout).waitTimeSeconds(waitTimeSeconds).build());

    return new DefaultSqsMessageBatch(getClient(), getDestination(),
        response.hasMessages() && !response.messages().isEmpty()
            ? response.messages().stream().map(m -> (SqsMessage) null).toList()
            : List.of());
  }

  private SqsClient getClient() {
    return client;
  }

  public SqsDestination getDestination() {
    return destination;
  }
}
