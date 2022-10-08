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
package com.sigpwned.hermes.aws.sqs;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import java.util.Objects;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import software.amazon.awssdk.regions.Region;

public class SqsDestination {
  // https://sqs.us-east-2.amazonaws.com/123456789012/mystack-myqueue-15PG5C2FC1CW8
  private static final Pattern SQS_QUEUE_URL_PATTERN =
      Pattern.compile("^https://sqs.([-a-z0-9]+).amazonaws.com/([0-9]+)/([-_a-zA-Z0-9]+(?:[.]fifo)?)$");

  // arn:aws:sqs:us-east-2:123456789012:mystack-myqueue-15PG5C2FC1CW8
  private static final Pattern SQS_QUEUE_ARN_PATTERN =
      Pattern.compile("^sqs:([-a-z0-9]+):([0-9]+):([-_a-zA-Z0-9]+)$");

  @JsonCreator
  public static SqsDestination fromString(String s) {
    Matcher m1 = SQS_QUEUE_URL_PATTERN.matcher(s);
    if (m1.matches())
      return fromMatchResult(m1.toMatchResult());

    Matcher m2 = SQS_QUEUE_ARN_PATTERN.matcher(s);
    if (m2.matches())
      return fromMatchResult(m2.toMatchResult());

    throw new IllegalArgumentException("s must be a valid SQS Queue ARN or Queue URL");
  }

  public static SqsDestination fromQueueArn(String s) {
    final Matcher m = SQS_QUEUE_ARN_PATTERN.matcher(s);
    if (!m.matches())
      throw new IllegalArgumentException("s must be a valid SQS Queue ARN");
    return fromMatchResult(m);
  }

  public static SqsDestination fromQueueUrl(String s) {
    final Matcher m = SQS_QUEUE_URL_PATTERN.matcher(s);
    if (!m.matches())
      throw new IllegalArgumentException("s must be a valid SQS Queue URL");
    return fromMatchResult(m);
  }

  private static SqsDestination fromMatchResult(MatchResult m) {
    final Region region = Region.of(m.group(1));
    final String accountId = m.group(2);
    final String queueName = m.group(3);
    return of(region, accountId, queueName);
  }

  public static SqsDestination of(Region region, String accountId, String queueName) {
    return new SqsDestination(region, accountId, queueName);
  }

  private final Region region;
  private final String accountId;
  private final String queueName;

  public SqsDestination(Region region, String accountId, String queueName) {
    this.region = requireNonNull(region);
    this.accountId = requireNonNull(accountId);
    this.queueName = requireNonNull(queueName);
  }

  /**
   * @return the region
   */
  public Region getRegion() {
    return region;
  }

  /**
   * @return the accountId
   */
  public String getAccountId() {
    return accountId;
  }

  /**
   * @return the topicName
   */
  public String getQueueName() {
    return queueName;
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountId, region, queueName);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SqsDestination other = (SqsDestination) obj;
    return Objects.equals(accountId, other.accountId) && Objects.equals(region, other.region)
        && Objects.equals(queueName, other.queueName);
  }

  public String toQueueArn() {
    return format("arn:aws:sqs:%s:%s:%s", getRegion(), getAccountId(), getQueueName());
  }

  public String toQueueUrl() {
    return format("https://sqs.%s.amazonaws.com/%s/%s", getRegion(), getAccountId(),
        getQueueName());
  }

  @Override
  @JsonValue
  public String toString() {
    return toQueueArn();
  }
}
