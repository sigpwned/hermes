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

import java.io.OutputStream;

/**
 * This class is used for checking the size of a string without copying the whole string into memory
 * and converting it to bytes array. Compared to String.getBytes().length, it is more efficient and
 * reliable for large strings.
 * 
 * @see <a href=
 *      "https://github.com/awslabs/payload-offloading-java-common-lib-for-aws/blob/master/src/main/java/software/amazon/payloadoffloading/CountingOutputStream.java">awslabs/payload-offloading-java-common-lib-for-aws</a>
 */
public class CountingOutputStream extends OutputStream {
  private long count;

  public CountingOutputStream() {
    count = 0L;
  }

  @Override
  public void write(int b) {
    count = count + 1;
  }

  @Override
  public void write(byte[] b) {
    count = count + b.length;
  }

  @Override
  public void write(byte[] b, int offset, int len) {
    count = count + len;
  }

  @Override
  public void flush() {
    // NOP
  }

  @Override
  public void close() {
    // NOP
  }

  public long count() {
    return count;
  }
}
