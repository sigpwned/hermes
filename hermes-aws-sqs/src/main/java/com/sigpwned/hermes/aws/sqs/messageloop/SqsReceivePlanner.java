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
package com.sigpwned.hermes.aws.sqs.messageloop;

import com.sigpwned.hermes.aws.sqs.util.Sqs;

@FunctionalInterface
public interface SqsReceivePlanner {
  public static SqsReceivePlanner ofPlan(SqsReceivePlan plan) {
    return () -> plan;
  }

  /**
   * Creates a receive planner that receives and sends messages to the processor immediately as they
   * come in with small batches that's suitable for a processor that runs in the given number of
   * seconds. User is responsible for padding the timeout to account for message deletion; in
   * practice, 5 seconds is more than sufficient.
   */
  public static SqsReceivePlanner ofImmediateSmallBatchPlan(int visibilityTimeout) {
    // Do not wait for any particular batch size. Process messages immediately on receipt.
    int batchCompleteWait = 0;
    return ofPlan(
        SqsReceivePlan.of(Sqs.MAX_MAX_NUMBER_OF_MESSAGES, batchCompleteWait, visibilityTimeout));
  }

  public static final int DEFAULT_FAST_PROCESSING_VISIBILITY_TIMEOUT_SECONDS = 30;

  /**
   * Creates a receive planner that receives and sends messages to the processor immediately as they
   * come in with small batches that's suitable for a processor that runs quickly.
   */
  public static SqsReceivePlanner ofFastProcessingImmediateSmallBatchPlan() {
    return ofImmediateSmallBatchPlan(DEFAULT_FAST_PROCESSING_VISIBILITY_TIMEOUT_SECONDS);
  }

  public SqsReceivePlan plan() throws InterruptedException;
}
