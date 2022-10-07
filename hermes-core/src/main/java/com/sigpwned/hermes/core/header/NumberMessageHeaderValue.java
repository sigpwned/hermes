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
package com.sigpwned.hermes.core.header;

import static java.util.Objects.requireNonNull;
import java.math.BigDecimal;
import java.util.Objects;
import com.sigpwned.hermes.core.model.MessageHeaderValue;

public class NumberMessageHeaderValue extends MessageHeaderValue {
  public static NumberMessageHeaderValue of(BigDecimal value) {
    return new NumberMessageHeaderValue(value);
  }

  private final BigDecimal value;

  public NumberMessageHeaderValue(BigDecimal value) {
    super(Type.NUMBER);
    this.value = requireNonNull(value);
  }

  /**
   * @return the value
   */
  public BigDecimal getValue() {
    return value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(value);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    NumberMessageHeaderValue other = (NumberMessageHeaderValue) obj;
    return Objects.equals(value, other.value);
  }

  @Override
  public String toString() {
    return "NumberMessageHeaderValue [value=" + value + "]";
  }
}
