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

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import com.sigpwned.hermes.aws.sqs.util.Sqs;

public class SqsReceivePlan {
  public static final int DEFAULT_VISIBILITY_TIMEOUT_SECONDS = 30;

  public static SqsReceivePlan of(int maxBatchSize, int batchCompleteWait, int visibilityTimeout) {
    return new SqsReceivePlan(maxBatchSize, batchCompleteWait, visibilityTimeout);
  }

  /**
   * Receive one message with a visibility timeout of 30 seconds.
   * 
   * @see #ofOne(int)
   */
  public static SqsReceivePlan ofBatchSizeOne() {
    return ofBatchSizeOne(DEFAULT_VISIBILITY_TIMEOUT_SECONDS);
  }

  /**
   * Receive one message with the given visibility timeout.
   */
  public static SqsReceivePlan ofBatchSizeOne(int visibilityTimeout) {
    return new SqsReceivePlan(1, 0, visibilityTimeout);
  }

  /**
   * Receive up to the SQS maximum batch size with a visibility timeout of 30 seconds. Process
   * received messages immediately.
   * 
   * @see #ofSmallEfficientBatches(int)
   */
  public static SqsReceivePlan ofSmallEfficientBatches() {
    return ofSmallEfficientBatches(DEFAULT_VISIBILITY_TIMEOUT_SECONDS);
  }

  /**
   * Receive up to the SQS maximum batch size with the given visibility timeout. Process received
   * messages immediately.
   */
  public static SqsReceivePlan ofSmallEfficientBatches(int visibilityTimeout) {
    // Do not wait for any particular batch size. Process messages immediately on receipt.
    int batchCompleteWait = 0;
    return of(Sqs.MAX_MAX_NUMBER_OF_MESSAGES, batchCompleteWait, visibilityTimeout);
  }

  /**
   * https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-visibility-timeout.html#configuring-visibility-timeout
   */
  private static final int MAX_VISIBILITY_TIMEOUT_SECONDS = (int) TimeUnit.HOURS.toSeconds(12L);

  /**
   * Maximum number of messages to receive.
   */
  private final int maxBatchSize;

  /**
   * If at least one message is received in the first {@link #batchStartWait} seconds of the receive
   * but fewer than {@link #maxBatchSize} messages, then continue attempting to receive messages to
   * complete the batch up to this many additional seconds. The batch is returned when it is
   * completed or this much time has passed, whichever happens first.
   */
  private final int batchCompleteWait;

  /**
   * The visibility timeout to set for received messages, in seconds. The visibility timeout must
   * cover {@link #batchCompleteWait} time, processing time, and delete time or risk processing
   * duplicate messages.
   */
  private final int visibilityTimeout;

  public SqsReceivePlan(int maxBatchSize, int batchCompleteWait, int visibilityTimeout) {
    if (maxBatchSize <= 0)
      throw new IllegalArgumentException("maxBatchSize must be at least 1");
    if (batchCompleteWait < 0)
      throw new IllegalArgumentException("batchCompleteWait must be at least 0");
    if (visibilityTimeout < 0)
      throw new IllegalArgumentException("visibilityTimeout must be at least 0");
    if (visibilityTimeout > MAX_VISIBILITY_TIMEOUT_SECONDS)
      throw new IllegalArgumentException(
          "visibilityTimeout must be no more than " + MAX_VISIBILITY_TIMEOUT_SECONDS);
    this.maxBatchSize = maxBatchSize;
    this.batchCompleteWait = batchCompleteWait;
    this.visibilityTimeout = visibilityTimeout;
  }

  /**
   * @return the maxBatchSize
   */
  public int getMaxBatchSize() {
    return maxBatchSize;
  }

  /**
   * @return the batchCompleteWait
   */
  public int getBatchCompleteWait() {
    return batchCompleteWait;
  }

  /**
   * @return the visibilityTimeout
   */
  public int getVisibilityTimeout() {
    return visibilityTimeout;
  }

  @Override
  public int hashCode() {
    return Objects.hash(batchCompleteWait, maxBatchSize, visibilityTimeout);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SqsReceivePlan other = (SqsReceivePlan) obj;
    return batchCompleteWait == other.batchCompleteWait && maxBatchSize == other.maxBatchSize
        && visibilityTimeout == other.visibilityTimeout;
  }

  @Override
  public String toString() {
    return "SqsReceivePlan [maxBatchSize=" + maxBatchSize + ", batchCompleteWait="
        + batchCompleteWait + ", visibilityTimeout=" + visibilityTimeout + "]";
  }
}
