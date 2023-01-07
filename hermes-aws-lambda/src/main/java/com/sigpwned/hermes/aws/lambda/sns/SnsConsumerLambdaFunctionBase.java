/*-
 * =================================LICENSE_START==================================
 * hermes-lambda
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
package com.sigpwned.hermes.aws.lambda.sns;

import static java.util.stream.Collectors.toList;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.lambda.runtime.events.SNSEvent.MessageAttribute;
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNSRecord;
import com.sigpwned.hermes.aws.lambda.LambdaFunctionBase;
import com.sigpwned.hermes.core.header.NumberMessageHeaderValue;
import com.sigpwned.hermes.core.header.StringMessageHeaderValue;
import com.sigpwned.hermes.core.model.Message;
import com.sigpwned.hermes.core.model.MessageHeader;
import com.sigpwned.hermes.core.model.MessageHeaderValue;
import com.sigpwned.hermes.core.model.MessageHeaders;

public abstract class SnsConsumerLambdaFunctionBase extends LambdaFunctionBase<SNSEvent, Void> {
  private static final Logger LOGGER = LoggerFactory.getLogger(SnsConsumerLambdaFunctionBase.class);

  @Override
  public Void handleRequest(SNSEvent input, Context context) {
    List<Message> messages =
        input.getRecords().stream().map(SnsConsumerLambdaFunctionBase::toMessage).collect(toList());

    handleMessages(messages, context);

    return null;
  }

  public abstract void handleMessages(List<Message> messages, Context context);

  protected static Message toMessage(SNSRecord m) {
    String id = m.getSNS().getMessageId();

    MessageHeaders headers = MessageHeaders.of(m.getSNS().getMessageAttributes().entrySet().stream()
        .flatMap(e -> stream(
            toMessageAttributeValue(e.getValue()).map(v -> MessageHeader.of(e.getKey(), v))))
        .collect(toList()));

    String body = m.getSNS().getMessage();

    return Message.of(id, headers, body);
  }

  private static <T> Stream<T> stream(Optional<T> o) {
    return o.isPresent() ? Stream.of(o.get()) : Stream.empty();
  }

  protected static Optional<MessageHeaderValue> toMessageAttributeValue(MessageAttribute a) {
    MessageHeaderValue result;

    switch (a.getType()) {
      case "Number":
        result = NumberMessageHeaderValue.of(new BigDecimal(a.getValue()));
        break;
      case "String":
        result = StringMessageHeaderValue.of(a.getValue());
        break;
      default:
        if (LOGGER.isWarnEnabled())
          LOGGER.warn("Ignoring message attribute value with type {}", a.getType());
        result = null;
    }

    return Optional.ofNullable(result);
  }
}
