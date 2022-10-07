/*-
 * =================================LICENSE_START==================================
 * hermes-aws
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
package com.sigpwned.hermes.aws.util;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sigpwned.hermes.core.MessageContent;
import com.sigpwned.hermes.core.MessageHeader;
import com.sigpwned.hermes.core.MessageHeaderValue;
import com.sigpwned.hermes.core.MessageHeaders;
import com.sigpwned.hermes.core.util.CountingOutputStream;

public final class Sizeof {
  private Sizeof() {}

  private static final Logger LOGGER = LoggerFactory.getLogger(Sizeof.class);

  public static long messageContent(MessageContent b) {
    return messageHeaders(b.getHeaders()) + string(b.getBody());
  }

  public static long messageHeaders(MessageHeaders hs) {
    return hs.getHeaders().stream().filter(h -> {
      // These are never sent, so don't count towards message size.
      return !h.getName().startsWith(Messaging.AWS_HEADER_PREFIX);
    }).mapToLong(Sizeof::messageHeader).sum();
  }

  public static long messageHeader(MessageHeader h) {
    long result = messageHeaderValue(h.getValue());
    if (h.getName().startsWith(Messaging.HERMES_HEADER_PREFIX)) {
      // These are used for internal purposes. The name and type are never sent. The body may or
      // may not be sent, so this is technically an upper bound as opposed to an exact size.
    } else {
      result = result + string(h.getName()) + messageHeaderValueType(h.getValue().getType());
    }
    return result;
  }

  public static long messageHeaderValueType(MessageHeaderValue.Type t) {
    long result;
    switch (t) {
      case NUMBER:
        result = string(t.name());
        break;
      case STRING:
        result = string(t.name());
        break;
      default:
        throw new AssertionError(t);
    }
    return result;
  }

  public static long messageHeaderValue(MessageHeaderValue v) {
    long result;
    switch (v.getType()) {
      case NUMBER:
        result = string(v.asNumber().getValue().toString());
        break;
      case STRING:
        result = string(v.asString().getValue());
        break;
      default:
        throw new AssertionError(v.getType());
    }
    return result;
  }

  public static long string(String s) {
    CountingOutputStream result = new CountingOutputStream();
    try (Writer w = new OutputStreamWriter(result, StandardCharsets.UTF_8)) {
      w.write(s);

      // Very superstitious... If a flush is needed, it should happen as part of close.
      w.flush();
    } catch (IOException e) {
      // The underlying OutputStream never generates an IOException, so this should never happen.
      if (LOGGER.isErrorEnabled())
        LOGGER.error("Failed to determine length of string", e);
      throw new UncheckedIOException(e);
    }
    return result.count();
  }
}
