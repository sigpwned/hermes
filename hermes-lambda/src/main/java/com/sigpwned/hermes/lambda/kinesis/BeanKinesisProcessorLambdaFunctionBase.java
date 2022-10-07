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

import static java.util.Objects.requireNonNull;
import java.util.List;
import com.amazonaws.services.lambda.runtime.Context;
import com.sigpwned.hermes.core.BeanMessageDeserializer;
import com.sigpwned.hermes.core.BeanMessageProducer;

public abstract class BeanKinesisProcessorLambdaFunctionBase<I, O>
    extends BeanKinesisConsumerLambdaFunctionBase<I> {
  private final BeanMessageProducer<O> producer;

  public BeanKinesisProcessorLambdaFunctionBase(BeanMessageDeserializer<I> deserializer,
      BeanMessageProducer<O> producer) {
    super(deserializer);
    this.producer = requireNonNull(producer);
  }

  @Override
  public void handleBeans(List<I> inputBeans, Context context) {
    List<O> outputBeans = processBeans(inputBeans, context);
    getProducer().send(outputBeans);
  }

  public abstract List<O> processBeans(List<I> inputMessages, Context context);

  /**
   * @return the producer
   */
  protected BeanMessageProducer<O> getProducer() {
    return producer;
  }
}
