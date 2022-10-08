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
package com.sigpwned.hermes.aws.sqs.util;

import static java.util.stream.Collectors.toMap;
import java.util.concurrent.TimeUnit;
import com.sigpwned.hermes.aws.sqs.SqsDestination;
import com.sigpwned.hermes.aws.sqs.messageconsumer.SqsMessage;
import com.sigpwned.hermes.aws.sqs.messageproducer.DefaultSqsMessageProducer;
import com.sigpwned.hermes.aws.sqs.messageproducer.SqsGroupingMessageProducer;
import com.sigpwned.hermes.aws.sqs.messageproducer.SqsMessageProducer;
import com.sigpwned.hermes.aws.util.Messaging;
import com.sigpwned.hermes.core.header.StringMessageHeaderValue;
import com.sigpwned.hermes.core.model.Message;
import com.sigpwned.hermes.core.model.MessageHeader;
import com.sigpwned.hermes.core.model.MessageHeaderValue;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequestEntry;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry;

public final class Sqs {
  private Sqs() {}

  public static final int MIN_MAX_NUMBER_OF_MESSAGES = 1;

  public static final int MAX_MAX_NUMBER_OF_MESSAGES = 10;

  public static final int MIN_WAIT_TIME_SECONDS = 0;

  public static final int MAX_WAIT_TIME_SECONDS = 20;

  public static final int MIN_VISIBILITY_TIMEOUT_SECONDS = 1;

  /**
   * https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-visibility-timeout.html#configuring-visibility-timeout
   */
  public static final int MAX_VISIBILITY_TIMEOUT_SECONDS = (int) TimeUnit.HOURS.toSeconds(12L);

  public static final int MAX_SEND_BATCH_LENGTH = 10;

  public static final int MAX_SEND_PAYLOAD_SIZE = Messaging.MAX_PAYLOAD_SIZE;

  /**
   * Must be a {@link StringMessageHeaderValue}
   */
  public static final String MESSAGE_GROUP_ID_HEADER_NAME =
      Messaging.HERMES_HEADER_PREFIX + "MessageGroupId";

  /**
   * Must be a {@link StringMessageHeaderValue}
   */
  public static final String MESSAGE_DEDUPLICATION_ID_HEADER_NAME =
      Messaging.HERMES_HEADER_PREFIX + "MessageDeduplicationId";

  /**
   * Must be a {@link StringMessageHeaderValue}
   */
  public static final String RECEIPT_HANDLE_HEADER_NAME =
      Messaging.AWS_HEADER_PREFIX + "ReceiptHandle";

  public static SqsClient defaultClient() {
    return SqsClient.builder().httpClient(UrlConnectionHttpClient.create()).build();
  }

  public static SqsMessageProducer optimizedProducer(SqsDestination destination) {
    return new SqsGroupingMessageProducer(new DefaultSqsMessageProducer(destination));
  }

  public static DeleteMessageBatchRequestEntry toDeleteMessageBatchRequestEntry(SqsMessage m) {
    return DeleteMessageBatchRequestEntry.builder().id(m.getId())
        .receiptHandle(m.getReceiptHandle()).build();
  }

  public static SendMessageBatchRequestEntry toSendMessageBatchRequestEntry(Message m) {
    SendMessageBatchRequestEntry.Builder b =
        SendMessageBatchRequestEntry.builder().id(m.getId()).messageBody(m.getBody());

    b.messageAttributes(m.getHeaders().stream()
        .filter(hi -> !hi.getName().startsWith(Messaging.HERMES_HEADER_PREFIX)
            && !hi.getName().startsWith(Messaging.AWS_HEADER_PREFIX))
        .collect(toMap(hi -> hi.getName(), hi -> Sqs.toMessageAttributeValue(hi.getValue()))));

    m.getHeaders().findMessageHeaderByName(MESSAGE_GROUP_ID_HEADER_NAME)
        .map(MessageHeader::getValue).map(MessageHeaderValue::asString)
        .map(StringMessageHeaderValue::getValue).ifPresent(b::messageGroupId);

    m.getHeaders().findMessageHeaderByName(MESSAGE_DEDUPLICATION_ID_HEADER_NAME)
        .map(MessageHeader::getValue).map(MessageHeaderValue::asString)
        .map(StringMessageHeaderValue::getValue).ifPresent(b::messageDeduplicationId);

    return b.build();
  }

  public static MessageAttributeValue toMessageAttributeValue(MessageHeaderValue v) {
    MessageAttributeValue result;

    switch (v.getType()) {
      case NUMBER:
        result = MessageAttributeValue.builder().dataType("Number")
            .stringValue(v.asNumber().getValue().toString()).build();
        break;
      case STRING:
        result = MessageAttributeValue.builder().dataType("String")
            .stringValue(v.asString().getValue().toString()).build();
        break;
      default:
        throw new AssertionError(v.getType());
    }

    return result;
  }
}
