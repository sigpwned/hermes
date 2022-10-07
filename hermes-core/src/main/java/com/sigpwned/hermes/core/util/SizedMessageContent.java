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
package com.sigpwned.hermes.core.util;

import java.util.Objects;
import com.sigpwned.hermes.core.MessageContent;

public class SizedMessageContent {
  public static SizedMessageContent of(MessageContent message, int size) {
    return new SizedMessageContent(message, size);
  }

  private final MessageContent message;
  private final int size;

  public SizedMessageContent(MessageContent message, int size) {
    this.message = message;
    this.size = size;
  }

  /**
   * @return the message
   */
  public MessageContent getMessage() {
    return message;
  }

  /**
   * @return the size
   */
  public int getSize() {
    return size;
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, size);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SizedMessageContent other = (SizedMessageContent) obj;
    return Objects.equals(message, other.message) && size == other.size;
  }

  @Override
  public String toString() {
    return "SizedMessageContent [message=" + message + ", size=" + size + "]";
  }
}
