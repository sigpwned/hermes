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

import static java.util.Objects.requireNonNull;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import com.sigpwned.hermes.aws.sqs.messageloop.SqsReceivePlan;
import com.sigpwned.hermes.aws.sqs.messageloop.SqsReceivePlanner;

public class ThrottlingSqsReceivePlanner implements SqsReceivePlanner {
  public static ThrottlingSqsReceivePlanner wrap(SqsReceivePlanner delegate,
      Duration minimumDuration) {
    return new ThrottlingSqsReceivePlanner(delegate, minimumDuration);
  }

  private final SqsReceivePlanner delegate;
  private final Duration minimumDuration;
  private OffsetDateTime last;

  public ThrottlingSqsReceivePlanner(SqsReceivePlanner delegate, Duration minimumDuration) {
    if (minimumDuration.isNegative())
      throw new IllegalArgumentException("minimumDuration must not be negative");
    this.delegate = requireNonNull(delegate);
    this.minimumDuration = requireNonNull(minimumDuration);
    this.last =
        OffsetDateTime.of(LocalDate.of(1970, Month.JANUARY, 1), LocalTime.MIN, ZoneOffset.UTC);
  }

  @Override
  public SqsReceivePlan plan() throws InterruptedException {
    final OffsetDateTime now = now();
    final Duration elapsed = Duration.between(last, now);
    if (elapsed.compareTo(getMinimumDuration()) < 0) {
      Thread.sleep(getMinimumDuration().minus(elapsed).toMillis());
      last = now();
    } else {
      last = now;
    }
    return getDelegate().plan();
  }

  /**
   * @return the delegate
   */
  private SqsReceivePlanner getDelegate() {
    return delegate;
  }

  /**
   * @return the minimumDuration
   */
  private Duration getMinimumDuration() {
    return minimumDuration;
  }

  protected OffsetDateTime now() {
    return OffsetDateTime.now(ZoneOffset.UTC);
  }
}
