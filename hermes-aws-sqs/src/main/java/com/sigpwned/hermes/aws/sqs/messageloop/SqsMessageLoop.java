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

import static java.util.Objects.requireNonNull;
import java.io.InterruptedIOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sigpwned.hermes.aws.sqs.SqsDestination;
import com.sigpwned.hermes.aws.sqs.messageconsumer.SqsMessageBatch;
import com.sigpwned.hermes.aws.sqs.messageconsumer.SqsMessageConsumer;
import com.sigpwned.hermes.aws.sqs.messageconsumer.batch.CombinedSqsMessageBatch;
import com.sigpwned.hermes.aws.sqs.util.Sqs;
import software.amazon.awssdk.services.sqs.model.OverLimitException;

public class SqsMessageLoop implements Runnable {
  private static final Logger LOGGER = LoggerFactory.getLogger(SqsMessageLoop.class);

  private final SqsMessageConsumer consumer;
  private final SqsReceivePlanner planner;
  private final SqsMessageLoopBody body;

  public SqsMessageLoop(SqsDestination destination, SqsMessageLoopBody body) {
    this(destination, SqsReceivePlanner.ofFastProcessingImmediateSmallBatchPlan(), body);
  }

  public SqsMessageLoop(SqsDestination destination, int visibilityTimeout,
      SqsMessageLoopBody body) {
    this(destination, SqsReceivePlanner.ofImmediateSmallBatchPlan(visibilityTimeout), body);
  }

  public SqsMessageLoop(SqsDestination destination, SqsReceivePlan plan, SqsMessageLoopBody body) {
    this(destination, SqsReceivePlanner.ofPlan(plan), body);
  }

  public SqsMessageLoop(SqsDestination destination, SqsReceivePlanner planner,
      SqsMessageLoopBody body) {
    this(new SqsMessageConsumer(destination), planner, body);
  }

  public SqsMessageLoop(SqsMessageConsumer consumer, SqsReceivePlanner planner,
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
            acceptMessages(messages);
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
    SqsMessageBatch result;

    SqsMessageBatch firstBatch =
        startReceive(Math.min(plan.getMaxBatchSize(), Sqs.MAX_MAX_NUMBER_OF_MESSAGES),
            plan.getVisibilityTimeout());
    if (plan.getMaxBatchSize() > firstBatch.size() && plan.getBatchCompleteWait() > 0) {
      List<SqsMessageBatch> lastBatches =
          completeReceive(plan.getMaxBatchSize() - firstBatch.size(), plan.getBatchCompleteWait(),
              plan.getVisibilityTimeout());
      if (lastBatches.isEmpty()) {
        result = firstBatch;
      } else {
        result = new CombinedSqsMessageBatch(
            Stream.concat(Stream.of(firstBatch), lastBatches.stream()).toList());
      }
    } else {
      result = firstBatch;
    }

    return result;
  }


  protected SqsMessageBatch startReceive(int maxNumberOfMessages, int visibilityTimeout) {
    int consecutiveErrors = 0;
    SqsMessageBatch result = null;
    do {
      if (consecutiveErrors > 0) {
        Duration backoff = startReceiveBackoff(consecutiveErrors);
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("SQS destination {} backing off for {} millis due to errors",
              getConsumer().getDestination(), backoff.toMillis());
        try {
          Thread.sleep(backoff.toMillis());
        } catch (InterruptedException e) {
          if (LOGGER.isWarnEnabled())
            LOGGER.warn("SQS destination {} was interrupted. Bailing out...",
                getConsumer().getDestination());
          Thread.currentThread().interrupt();
          throw new UncheckedIOException(new InterruptedIOException());
        }
      }
      try {
        result = getConsumer().receive(maxNumberOfMessages, Sqs.MAX_WAIT_TIME_SECONDS,
            visibilityTimeout);
        consecutiveErrors = 0;
      } catch (OverLimitException e) {
        if (LOGGER.isWarnEnabled())
          LOGGER.warn("SQS destination {} is over limit. Backing off...",
              getConsumer().getDestination());
        consecutiveErrors = consecutiveErrors + 1;
      }
    } while (result == null || result.isEmpty());
    return result;
  }

  /**
   * Hook for determining exponential backoff from errors
   */
  protected Duration startReceiveBackoff(int consecutiveErrors) {
    if (consecutiveErrors < 0)
      throw new IllegalArgumentException("consecutiveErrors must not be negative");
    return consecutiveErrors > 0 ? Duration.ofSeconds(1 << Math.min(consecutiveErrors - 1, 6))
        : Duration.ZERO;
  }

  protected List<SqsMessageBatch> completeReceive(int maxTotalMessages, int batchCompleteWait,
      int visibilityTimeout) {
    List<SqsMessageBatch> result = new ArrayList<>();

    int currentTotalMessages = 0;
    long batchCompleteBegin = now();
    long batchCompleteExpiration = batchCompleteBegin + TimeUnit.SECONDS.toNanos(batchCompleteWait);
    long now = batchCompleteBegin;
    while (currentTotalMessages < maxTotalMessages && now < batchCompleteExpiration) {
      int maxNumberOfMessages =
          Math.min(maxTotalMessages - currentTotalMessages, Sqs.MAX_MAX_NUMBER_OF_MESSAGES);
      int waitTimeSeconds =
          (int) Math.min(TimeUnit.NANOSECONDS.toSeconds(batchCompleteExpiration - now),
              Sqs.MAX_WAIT_TIME_SECONDS);
      SqsMessageBatch batchi;
      try {
        batchi = getConsumer().receive(maxNumberOfMessages, waitTimeSeconds, visibilityTimeout);
      } catch (OverLimitException e) {
        // Let's just end the batch right here.
        if (LOGGER.isWarnEnabled())
          LOGGER.warn("SQS destination {} is over limit. Bailing out of current batch...",
              getConsumer().getDestination(), e);
        break;
      }

      if (!batchi.isEmpty()) {
        result.add(batchi);
        currentTotalMessages = currentTotalMessages + batchi.size();
      }

      now = now();
    }

    return result;
  }


  protected void acceptMessages(SqsMessageBatch batch) throws InterruptedException {
    getBody().acceptBatch(batch);
  }

  /**
   * @return the consumer
   */
  private SqsMessageConsumer getConsumer() {
    return consumer;
  }

  /**
   * @return the planner
   */
  private SqsReceivePlanner getPlanner() {
    return planner;
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
