/*-
 * =================================LICENSE_START==================================
 * hermes-core
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
package com.sigpwned.hermes.core;

import static java.util.Objects.requireNonNull;
import java.math.BigDecimal;
import java.util.Objects;
import com.sigpwned.hermes.core.header.NumberMessageHeaderValue;
import com.sigpwned.hermes.core.header.StringMessageHeaderValue;

public abstract class MessageHeaderValue {
  public static enum Type {
    STRING, NUMBER;
  }

  public static NumberMessageHeaderValue of(BigDecimal value) {
    return new NumberMessageHeaderValue(value);
  }

  public static StringMessageHeaderValue of(String value) {
    return new StringMessageHeaderValue(value);
  }

  private final Type type;

  public MessageHeaderValue(Type type) {
    this.type = requireNonNull(type);
  }

  /**
   * @return the type
   */
  public Type getType() {
    return type;
  }

  public StringMessageHeaderValue asString() {
    return (StringMessageHeaderValue) this;
  }

  public NumberMessageHeaderValue asNumber() {
    return (NumberMessageHeaderValue) this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(type);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MessageHeaderValue other = (MessageHeaderValue) obj;
    return type == other.type;
  }

  @Override
  public String toString() {
    return "MessageHeaderValue [type=" + type + "]";
  }
}
