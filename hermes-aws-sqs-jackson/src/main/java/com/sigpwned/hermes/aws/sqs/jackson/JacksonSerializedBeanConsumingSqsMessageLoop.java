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
import com.sigpwned.hermes.jackson.util.Jackson;

public abstract class JacksonSerializedBeanConsumingSqsMessageLoop<T> implements Runnable {
  private final ObjectMapper mapper;
  private final SqsMessageLoop loop;

  public JacksonSerializedBeanConsumingSqsMessageLoop(SqsDestination destination,
      Class<T> inputType) {
    this(destination, SqsReceivePlanner.ofFastProcessingImmediateSmallBatchPlan(), inputType);
  }

  public JacksonSerializedBeanConsumingSqsMessageLoop(SqsDestination destination,
      int visibilityTimeout, Class<T> inputType) {
    this(destination, SqsReceivePlanner.ofImmediateSmallBatchPlan(visibilityTimeout), inputType);
  }

  public JacksonSerializedBeanConsumingSqsMessageLoop(SqsDestination destination,
      SqsReceivePlan plan, Class<T> inputType) {
    this(destination, SqsReceivePlanner.ofPlan(plan), inputType);
  }

  public JacksonSerializedBeanConsumingSqsMessageLoop(SqsDestination destination,
      SqsReceivePlanner planner, Class<T> inputType) {
    this(destination, planner, Jackson.DEFAULT_OBJECT_MAPPER, inputType);
  }

  public JacksonSerializedBeanConsumingSqsMessageLoop(SqsDestination destination,
      SqsReceivePlanner planner, ObjectMapper mapper, Class<T> inputType) {
    this(destination, planner, mapper.getTypeFactory().constructType(inputType));
  }

  public JacksonSerializedBeanConsumingSqsMessageLoop(SqsDestination destination,
      SqsReceivePlanner planner, TypeReference<T> inputType) {
    this(destination, planner, Jackson.DEFAULT_OBJECT_MAPPER, inputType);
  }

  public JacksonSerializedBeanConsumingSqsMessageLoop(SqsDestination destination,
      SqsReceivePlanner planner, ObjectMapper mapper, TypeReference<T> inputType) {
    this(destination, planner, mapper.getTypeFactory().constructType(inputType));
  }

  public JacksonSerializedBeanConsumingSqsMessageLoop(SqsDestination destination,
      SqsReceivePlanner planner, JavaType inputType) {
    this(destination, planner, Jackson.DEFAULT_OBJECT_MAPPER, inputType);
  }

  public JacksonSerializedBeanConsumingSqsMessageLoop(SqsDestination destination,
      SqsReceivePlanner planner, ObjectMapper mapper, JavaType inputType) {
    this.mapper = mapper;
    this.loop = new SqsMessageLoop(destination, planner, asSqsMessageLoopBody(inputType));
  }

  @Override
  public void run() {
    getLoop().run();
  }

  protected abstract void acceptBeans(List<T> beans);

  /**
   * Hook
   */
  protected SqsMessageLoopBody asSqsMessageLoopBody(JavaType inputType) {
    return new JacksonSerializedBeanConsumingSqsMessageLoopBody<T>(getMapper(), inputType) {
      @Override
      public void acceptBeans(List<T> beans) {
        JacksonSerializedBeanConsumingSqsMessageLoop.this.acceptBeans(beans);
      }
    };
  }

  protected ObjectMapper getMapper() {
    return mapper;
  }

  private SqsMessageLoop getLoop() {
    return loop;
  }
}
