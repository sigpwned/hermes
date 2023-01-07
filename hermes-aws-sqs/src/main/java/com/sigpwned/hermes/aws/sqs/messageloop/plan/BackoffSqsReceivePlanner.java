/*-
 * =================================LICENSE_START==================================
 * hermes-aws-sqs
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
package com.sigpwned.hermes.aws.sqs.messageloop.plan;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import java.time.Duration;
import java.util.Collections;
import com.sigpwned.hermes.aws.sqs.messageconsumer.SqsMessageBatch;
import com.sigpwned.hermes.aws.sqs.messageloop.SqsMessageLoopBody;
import com.sigpwned.hermes.aws.sqs.messageloop.SqsReceivePlan;
import com.sigpwned.hermes.aws.sqs.messageloop.SqsReceivePlanner;

/**
 * Used to track errors during processing and back off appropriately. Must wrap the corresponding
 * message loop's body.
 */
public class BackoffSqsReceivePlanner implements SqsReceivePlanner {
  public static BackoffSqsReceivePlanner wrap(SqsReceivePlanner delegate, Duration maximumBackoff) {
    return new BackoffSqsReceivePlanner(delegate, maximumBackoff);
  }

  public static final Duration DEFAULT_MAXIMUM_BACKOFF = Duration.ofSeconds(60);

  private final SqsReceivePlanner delegate;
  private final Duration maximumBackoff;
  private int consecutiveErrors;
  private boolean errored;

  public BackoffSqsReceivePlanner(SqsReceivePlanner delegate) {
    this(delegate, DEFAULT_MAXIMUM_BACKOFF);
  }

  public BackoffSqsReceivePlanner(SqsReceivePlanner delegate, Duration maximumBackoff) {
    this.delegate = requireNonNull(delegate);
    this.maximumBackoff = requireNonNull(maximumBackoff);
    this.consecutiveErrors = 0;
    this.errored = false;
  }

  @Override
  public SqsReceivePlan plan() throws InterruptedException {
    if (errored)
      consecutiveErrors = consecutiveErrors + 1;
    else
      consecutiveErrors = 0;

    errored = false;

    if (consecutiveErrors > 0)
      Thread.sleep(
          Collections.min(asList(backoff(consecutiveErrors), getMaximumBackoff())).toMillis());

    return getDelegate().plan();
  }

  public SqsMessageLoopBody wrap(SqsMessageLoopBody body) {
    return new SqsMessageLoopBody() {
      @Override
      public void acceptBatch(SqsMessageBatch batch) throws InterruptedException {
        boolean success = false;
        try {
          body.acceptBatch(batch);
          success = true;
        } finally {
          if (success == false)
            errored = true;
        }
      }
    };
  }

  /**
   * @return the delegate
   */
  private SqsReceivePlanner getDelegate() {
    return delegate;
  }

  /**
   * @return the maximumBackoff
   */
  private Duration getMaximumBackoff() {
    return maximumBackoff;
  }

  /**
   * hook
   */
  protected Duration backoff(int consecutiveErrors) {
    return Duration
        .ofSeconds(consecutiveErrors <= 32 ? 1 << (consecutiveErrors - 1) : Integer.MAX_VALUE);
  }
}
