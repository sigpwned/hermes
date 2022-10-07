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
package com.sigpwned.hermes.lambda.kinesis;

import static java.util.stream.Collectors.toList;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent.KinesisEventRecord;
import com.sigpwned.hermes.core.model.Message;
import com.sigpwned.hermes.core.model.MessageHeaders;

public abstract class KinesisConsumerLambdaFunctionBase
    implements RequestHandler<KinesisEvent, Void> {
  @Override
  public Void handleRequest(KinesisEvent input, Context context) {
    List<Message> messages = input.getRecords().stream()
        .map(KinesisConsumerLambdaFunctionBase::toMessage).collect(toList());

    handleMessages(messages, context);

    return null;
  }

  public abstract void handleMessages(List<Message> messages, Context context);

  protected static Message toMessage(KinesisEventRecord m) {
    // Guaranteed globally unique
    String id = m.getKinesis().getPartitionKey() + "/" + m.getKinesis().getSequenceNumber();

    // Kinesis has no headers
    MessageHeaders headers = MessageHeaders.of();

    String body = StandardCharsets.UTF_8.decode(m.getKinesis().getData()).toString();

    return Message.of(id, headers, body);
  }
}
