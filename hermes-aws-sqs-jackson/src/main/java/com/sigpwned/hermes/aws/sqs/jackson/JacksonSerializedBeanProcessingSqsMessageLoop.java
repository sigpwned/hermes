/*-
 * =================================LICENSE_START==================================
 * hermes-aws-sqs-jackson
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
package com.sigpwned.hermes.aws.sqs.jackson;

import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sigpwned.hermes.aws.sqs.SqsDestination;
import com.sigpwned.hermes.aws.sqs.messageloop.SqsMessageLoop;
import com.sigpwned.hermes.aws.sqs.messageloop.SqsMessageLoopBody;
import com.sigpwned.hermes.aws.sqs.messageloop.SqsReceivePlan;
import com.sigpwned.hermes.aws.sqs.messageloop.SqsReceivePlanner;
import com.sigpwned.hermes.core.MessageProducer;
import com.sigpwned.hermes.jackson.util.Jackson;

public abstract class JacksonSerializedBeanProcessingSqsMessageLoop<I, O> implements Runnable {
  private final ObjectMapper mapper;
  private final SqsMessageLoop loop;

  public JacksonSerializedBeanProcessingSqsMessageLoop(SqsDestination destination,
      int visibilityTimeout, MessageProducer producer, Class<I> inputType) {
    this(destination, SqsReceivePlanner.ofImmediateSmallBatchPlan(visibilityTimeout), producer,
        inputType);
  }

  public JacksonSerializedBeanProcessingSqsMessageLoop(SqsDestination destination,
      MessageProducer producer, Class<I> inputType) {
    this(destination, SqsReceivePlanner.ofFastProcessingImmediateSmallBatchPlan(), producer,
        inputType);
  }

  public JacksonSerializedBeanProcessingSqsMessageLoop(SqsDestination destination,
      SqsReceivePlan plan, MessageProducer producer, Class<I> inputType) {
    this(destination, SqsReceivePlanner.ofPlan(plan), producer, inputType);
  }

  public JacksonSerializedBeanProcessingSqsMessageLoop(SqsDestination destination,
      SqsReceivePlanner planner, MessageProducer producer, Class<I> inputType) {
    this(destination, planner, producer, Jackson.DEFAULT_OBJECT_MAPPER, inputType);
  }

  public JacksonSerializedBeanProcessingSqsMessageLoop(SqsDestination destination,
      SqsReceivePlanner planner, MessageProducer producer, ObjectMapper mapper,
      Class<I> inputType) {
    this(destination, planner, producer, mapper.getTypeFactory().constructType(inputType));
  }

  public JacksonSerializedBeanProcessingSqsMessageLoop(SqsDestination destination,
      SqsReceivePlanner planner, MessageProducer producer, TypeReference<I> inputType) {
    this(destination, planner, producer, Jackson.DEFAULT_OBJECT_MAPPER, inputType);
  }

  public JacksonSerializedBeanProcessingSqsMessageLoop(SqsDestination destination,
      SqsReceivePlanner planner, MessageProducer producer, ObjectMapper mapper,
      TypeReference<I> inputType) {
    this(destination, planner, producer, mapper.getTypeFactory().constructType(inputType));
  }

  public JacksonSerializedBeanProcessingSqsMessageLoop(SqsDestination destination,
      SqsReceivePlanner planner, MessageProducer producer, JavaType inputType) {
    this(destination, planner, producer, Jackson.DEFAULT_OBJECT_MAPPER, inputType);
  }

  public JacksonSerializedBeanProcessingSqsMessageLoop(SqsDestination destination,
      SqsReceivePlanner planner, MessageProducer producer, ObjectMapper mapper,
      JavaType inputType) {
    this.mapper = mapper;
    this.loop = new SqsMessageLoop(destination, planner, asSqsMessageLoopBody(producer, inputType));
  }

  @Override
  public void run() {
    getLoop().run();
  }

  protected abstract List<O> processBeans(List<I> beans);

  /**
   * Hook
   */
  protected SqsMessageLoopBody asSqsMessageLoopBody(MessageProducer producer, JavaType inputType) {
    return new JacksonSerializedBeanProcessingSqsMessageLoopBody<I, O>(producer, getMapper(),
        inputType) {
      @Override
      public List<O> processBeans(List<I> inputBeans) {
        return JacksonSerializedBeanProcessingSqsMessageLoop.this.processBeans(inputBeans);
      }
    };
  }

  /**
   * Available for use in {@link #asSqsMessageLoopBody(JavaType)}.
   */
  protected ObjectMapper getMapper() {
    return mapper;
  }

  private SqsMessageLoop getLoop() {
    return loop;
  }
}
