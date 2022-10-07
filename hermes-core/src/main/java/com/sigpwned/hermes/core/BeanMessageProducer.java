/*-
 * =================================LICENSE_START==================================
 * hermes-core
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
package com.sigpwned.hermes.core;

import static java.util.Objects.requireNonNull;
import java.util.List;

public class BeanMessageProducer<T> {
  private final MessageProducer delegate;
  private final BeanMessageSerializer<T> serializer;

  public BeanMessageProducer(MessageProducer delegate, BeanMessageSerializer<T> serializer) {
    this.delegate = requireNonNull(delegate);
    this.serializer = serializer;
  }

  public void send(List<T> values) {
    getDelegate().send(getSerializer().serializeBeans(values));
  }

  /**
   * @return the producer
   */
  private MessageProducer getDelegate() {
    return delegate;
  }

  /**
   * @return the serializer
   */
  private BeanMessageSerializer<T> getSerializer() {
    return serializer;
  }
}
