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
package com.sigpwned.hermes.sqs.messageconsumer.batch;

import static java.util.stream.Collectors.toUnmodifiableList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import com.sigpwned.hermes.sqs.SqsDestination;
import com.sigpwned.hermes.sqs.messageconsumer.SqsMessage;
import com.sigpwned.hermes.sqs.messageconsumer.SqsMessageBatch;

public class CombinedSqsMessageBatch implements SqsMessageBatch {
  private final List<SqsMessageBatch> batches;
  private final SqsDestination destination;
  private final List<SqsMessage> messages;

  public CombinedSqsMessageBatch(List<SqsMessageBatch> batches) {
    if (batches.isEmpty())
      throw new IllegalArgumentException("no batches");
    List<SqsDestination> destinations =
        batches.stream().map(SqsMessageBatch::getDestination).distinct().toList();
    if (destinations.size() > 1)
      throw new IllegalArgumentException("multiple destinations " + destinations);
    this.batches = batches;
    this.destination = destinations.get(0);
    this.messages =
        getBatches().stream().flatMap(b -> b.getMessages().stream()).collect(toUnmodifiableList());
  }

  @Override
  public List<SqsMessage> getMessages() {
    return messages;
  }

  @Override
  public Iterator<SqsMessage> iterator() {
    return getMessages().iterator();
  }

  @Override
  public Stream<SqsMessage> stream() {
    return getMessages().stream();
  }

  @Override
  public int size() {
    return getMessages().size();
  }

  @Override
  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
  public void close() {
    RuntimeException problem = null;
    for (SqsMessageBatch batch : getBatches()) {
      try {
        batch.close();
      } catch (RuntimeException e) {
        problem = e;
      }
    }
    if (problem != null)
      throw problem;
  }

  @Override
  public SqsDestination getDestination() {
    return destination;
  }

  /**
   * @return the batches
   */
  private List<SqsMessageBatch> getBatches() {
    return batches;
  }
}
