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

import static java.util.Collections.unmodifiableList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class MessageHeaders implements Iterable<MessageHeader> {
  public static final MessageHeaders EMPTY = of(List.of());

  public static MessageHeaders of() {
    return EMPTY;
  }

  public static MessageHeaders of(List<MessageHeader> headers) {
    return new MessageHeaders(headers);
  }

  private final List<MessageHeader> headers;

  public MessageHeaders(List<MessageHeader> headers) {
    // TODO Check for duplicates
    this.headers = unmodifiableList(headers);
  }

  public List<MessageHeader> getHeaders() {
    return headers;
  }

  public Optional<MessageHeader> findMessageHeaderByName(String name) {
    return getHeaders().stream().filter(h -> h.getName().equals(name)).findFirst();
  }

  @Override
  public Iterator<MessageHeader> iterator() {
    return getHeaders().iterator();
  }

  public Stream<MessageHeader> stream() {
    return getHeaders().stream();
  }

  @Override
  public int hashCode() {
    return Objects.hash(headers);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MessageHeaders other = (MessageHeaders) obj;
    return Objects.equals(headers, other.headers);
  }

  @Override
  public String toString() {
    return "MessageHeaders [headers=" + headers + "]";
  }
}
