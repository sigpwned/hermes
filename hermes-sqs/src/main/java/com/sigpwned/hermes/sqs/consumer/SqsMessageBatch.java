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
package com.sigpwned.hermes.sqs.consumer;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import com.sigpwned.hermes.sqs.SqsDestination;


public interface SqsMessageBatch extends Iterable<SqsMessage>, AutoCloseable {
  /**
   * @return the messages
   */
  public List<SqsMessage> getMessages();

  @Override
  public Iterator<SqsMessage> iterator();

  public Stream<SqsMessage> stream();

  public int size();

  public boolean isEmpty();

  @Override
  public void close();

  /**
   * @return the destination
   */
  public SqsDestination getDestination();
}
