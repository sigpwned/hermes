/*-
 * =================================LICENSE_START==================================
 * hermes-sns
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
package com.sigpwned.hermes.sns;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import software.amazon.awssdk.regions.Region;

public class SnsDestination {
  // arn:aws:sns:us-east-2:123456789012:MyTopic
  private static final Pattern SNS_TOPIC_ARN_PATTERN =
      Pattern.compile("^arn:aws:sns:([-a-z0-9]+):([0-9]+):([-_a-zA-Z0-9]+(?:[.]fifo)?)$");

  @JsonCreator
  public static SnsDestination fromString(String s) {
    return fromTopicArn(s);
  }

  public static SnsDestination fromTopicArn(String s) {
    final Matcher m = SNS_TOPIC_ARN_PATTERN.matcher(s);
    if (!m.matches())
      throw new IllegalArgumentException("s must be a valid SNS Topic ARN");

    final Region region = Region.of(m.group(1));
    final String accountId = m.group(2);
    final String topicName = m.group(3);

    return of(region, accountId, topicName);
  }

  public static SnsDestination of(Region region, String accountId, String topicName) {
    return new SnsDestination(region, accountId, topicName);
  }

  private final Region region;
  private final String accountId;
  private final String topicName;

  public SnsDestination(Region region, String accountId, String topicName) {
    this.region = requireNonNull(region);
    this.accountId = requireNonNull(accountId);
    this.topicName = requireNonNull(topicName);
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
  public String getTopicName() {
    return topicName;
  }
  
  public boolean isFifo() {
    return getTopicName().endsWith(".fifo");
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountId, region, topicName);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SnsDestination other = (SnsDestination) obj;
    return Objects.equals(accountId, other.accountId) && Objects.equals(region, other.region)
        && Objects.equals(topicName, other.topicName);
  }

  public String toTopicArn() {
    return format("arn:aws:sns:%s:%s:%s", getRegion(), getAccountId(), getTopicName());
  }

  @Override
  @JsonValue
  public String toString() {
    return toTopicArn();
  }
}
