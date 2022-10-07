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
package com.sigpwned.hermes.sqs.loop;

import static java.util.Objects.requireNonNull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sigpwned.hermes.sqs.consumer.SqsMessageBatch;
import com.sigpwned.hermes.sqs.consumer.SqsMessageConsumer;
import com.sigpwned.hermes.sqs.consumer.batch.CombinedSqsMessageBatch;
import com.sigpwned.hermes.sqs.util.Sqs;

public class SqsMessageLoop implements Runnable {
  private static final Logger LOGGER = LoggerFactory.getLogger(SqsMessageLoop.class);

  private final SqsReceivePlanner planner;
  private final SqsMessageConsumer consumer;
  private final SqsMessageLoopBody body;

  public SqsMessageLoop(SqsReceivePlanner planner, SqsMessageConsumer consumer,
      SqsMessageLoopBody body) {
    this.planner = requireNonNull(planner);
    this.consumer = requireNonNull(consumer);
    this.body = requireNonNull(body);
  }

  public void run() {
    try {
      while (!Thread.interrupted()) {
        try {
          SqsReceivePlan plan = plan();
          try (SqsMessageBatch messages = receive(plan)) {
            accept(messages);
          }
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          throw e;
        } catch (Exception e) {
          if (LOGGER.isErrorEnabled())
            LOGGER.error("Received exception. Ignoring...", e);
        }
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      if (LOGGER.isInfoEnabled())
        LOGGER.info("Interrupted. Exiting...", e);
    } catch (Exception e) {
      if (LOGGER.isErrorEnabled())
        LOGGER.error("Fatal error. Exiting...", e);
    }
  }

  protected SqsReceivePlan plan() throws InterruptedException {
    return getPlanner().plan();
  }

  protected SqsMessageBatch receive(SqsReceivePlan plan) {
    SqsMessageBatch batch0;
    do {
      int maxNumberOfMessages = Math.min(plan.getMaxBatchSize(), Sqs.MAX_MAX_NUMBER_OF_MESSAGES);
      int waitTimeSeconds = Sqs.MAX_WAIT_TIME_SECONDS;
      int visibilityTimeout = plan.getVisibilityTimeout();
      batch0 = getConsumer().receive(maxNumberOfMessages, waitTimeSeconds, visibilityTimeout);
    } while (batch0.isEmpty());

    SqsMessageBatch result;
    if (batch0.size() < plan.getMaxBatchSize()) {
      List<SqsMessageBatch> batches = new ArrayList<>();
      batches.add(batch0);

      int currentBatchSize = batch0.size();

      long batchCompleteBegin = now();
      long batchCompleteExpiration =
          batchCompleteBegin + TimeUnit.SECONDS.toNanos(plan.getBatchCompleteWait());
      long now = batchCompleteBegin;
      while (currentBatchSize < plan.getMaxBatchSize() && now < batchCompleteExpiration) {
        int maxNumberOfMessages =
            Math.min(plan.getMaxBatchSize() - currentBatchSize, Sqs.MAX_MAX_NUMBER_OF_MESSAGES);
        int waitTimeSeconds =
            (int) Math.min(TimeUnit.NANOSECONDS.toSeconds(batchCompleteExpiration - now),
                Sqs.MAX_WAIT_TIME_SECONDS);
        int visibilityTimeout = plan.getVisibilityTimeout();
        SqsMessageBatch batchi =
            getConsumer().receive(maxNumberOfMessages, waitTimeSeconds, visibilityTimeout);

        if (!batchi.isEmpty()) {
          batches.add(batchi);
          currentBatchSize = currentBatchSize + batchi.size();
        }

        now = now();
      }

      result = batches.size() == 1 ? batches.get(0) : new CombinedSqsMessageBatch(batches);
    } else {
      result = batch0;
    }

    return result;
  }

  protected void accept(SqsMessageBatch messages) {
    getBody().acceptMessages(messages.getMessages());
  }

  /**
   * @return the planner
   */
  private SqsReceivePlanner getPlanner() {
    return planner;
  }

  /**
   * @return the consumer
   */
  private SqsMessageConsumer getConsumer() {
    return consumer;
  }

  /**
   * @return the body
   */
  private SqsMessageLoopBody getBody() {
    return body;
  }

  /**
   * test hook. nanoseconds.
   */
  protected long now() {
    return System.nanoTime();
  }
}
