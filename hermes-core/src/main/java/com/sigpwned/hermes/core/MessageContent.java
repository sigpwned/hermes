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
import java.util.Objects;

public class MessageContent {
  public static MessageContent of(MessageHeaders headers, String body) {
    return new MessageContent(headers, body);
  }

  private final MessageHeaders headers;
  private final String body;

  public MessageContent(MessageHeaders headers, String content) {
    this.headers = requireNonNull(headers);
    this.body = requireNonNull(content);
  }

  /**
   * @return the headers
   */
  public MessageHeaders getHeaders() {
    return headers;
  }

  /**
   * @return the content
   */
  public String getBody() {
    return body;
  }

  @Override
  public int hashCode() {
    return Objects.hash(body, headers);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MessageContent other = (MessageContent) obj;
    return Objects.equals(body, other.body) && Objects.equals(headers, other.headers);
  }

  @Override
  public String toString() {
    return "MessageBody [headers=" + headers + ", body=" + body + "]";
  }
}
