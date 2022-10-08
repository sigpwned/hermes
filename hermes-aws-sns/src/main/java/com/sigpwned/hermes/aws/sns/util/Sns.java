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
package com.sigpwned.hermes.aws.sns.util;

import static java.util.stream.Collectors.toMap;
import com.sigpwned.hermes.aws.sns.SnsDestination;
import com.sigpwned.hermes.aws.sns.messageproducer.DefaultSnsMessageProducer;
import com.sigpwned.hermes.aws.sns.messageproducer.SnsGroupingMessageProducer;
import com.sigpwned.hermes.aws.sns.messageproducer.SnsMessageProducer;
import com.sigpwned.hermes.aws.util.Messaging;
import com.sigpwned.hermes.core.header.StringMessageHeaderValue;
import com.sigpwned.hermes.core.model.Message;
import com.sigpwned.hermes.core.model.MessageHeader;
import com.sigpwned.hermes.core.model.MessageHeaderValue;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishBatchRequestEntry;

public final class Sns {
  private Sns() {}

  public static final int MAX_BATCH_LENGTH = 10;

  public static final int MAX_PAYLOAD_SIZE = Messaging.MAX_PAYLOAD_SIZE;

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

  public static SnsClient defaultClient() {
    return SnsClient.builder().httpClient(UrlConnectionHttpClient.create()).build();
  }

  public static SnsMessageProducer optimizedProducer(SnsDestination destination) {
    return new SnsGroupingMessageProducer(new DefaultSnsMessageProducer(destination));
  }

  public static PublishBatchRequestEntry toPublishBatchRequestEntry(Message m) {
    PublishBatchRequestEntry.Builder b =
        PublishBatchRequestEntry.builder().id(m.getId()).message(m.getBody());

    b.messageAttributes(m.getHeaders().stream()
        .filter(hi -> !hi.getName().startsWith(Messaging.HERMES_HEADER_PREFIX)
            && !hi.getName().startsWith(Messaging.AWS_HEADER_PREFIX))
        .collect(toMap(hi -> hi.getName(), hi -> Sns.toMessageAttributeValue(hi.getValue()))));

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
