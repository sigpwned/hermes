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
package com.sigpwned.hermes.core.model;

import static java.util.Objects.requireNonNull;
import java.util.Objects;

public class Message extends MessageContent {
  public static Message of(String id, MessageHeaders headers, String body) {
    return new Message(id, headers, body);
  }

  public static Message of(String id, MessageContent content) {
    return of(id, content.getHeaders(), content.getBody());
  }

  private final String id;

  public Message(String id, MessageContent content) {
    this(id, content.getHeaders(), content.getBody());
  }

  public Message(String id, MessageHeaders headers, String body) {
    super(headers, body);
    this.id = requireNonNull(id);
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(id);
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
    Message other = (Message) obj;
    return Objects.equals(id, other.id);
  }

  @Override
  public String toString() {
    return "Message [id=" + id + "]";
  }
}
