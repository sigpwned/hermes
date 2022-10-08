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
import com.sigpwned.hermes.core.serialization.BeanMessageSerializer;

public class BeanMessageProducer<T> {
  private final MessageProducer producer;
  private final BeanMessageSerializer<T> serializer;

  public BeanMessageProducer(MessageProducer producer, BeanMessageSerializer<T> serializer) {
    this.producer = requireNonNull(producer);
    this.serializer = requireNonNull(serializer);
  }

  public void send(List<T> beans) {
    getProducer().send(getSerializer().serializeBeans(beans));
  }

  /**
   * @return the producer
   */
  private MessageProducer getProducer() {
    return producer;
  }

  /**
   * @return the serializer
   */
  private BeanMessageSerializer<T> getSerializer() {
    return serializer;
  }

  @Override
  public String toString() {
    return "BeanMessageProducer [producer=" + producer + ", serializer=" + serializer + "]";
  }
}
