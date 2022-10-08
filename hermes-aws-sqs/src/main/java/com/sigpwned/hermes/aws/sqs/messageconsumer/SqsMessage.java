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
package com.sigpwned.hermes.aws.sqs.messageconsumer;

import static java.util.Objects.requireNonNull;
import java.util.Objects;
import com.sigpwned.hermes.core.model.Message;
import com.sigpwned.hermes.core.model.MessageContent;

public class SqsMessage extends Message {
  private final String receiptHandle;
  private boolean retired;

  public SqsMessage(String id, MessageContent content, String receiptHandle) {
    super(id, content);
    this.receiptHandle = requireNonNull(receiptHandle);
    this.retired = false;
  }

  /**
   * @return the receiptHandle
   */
  public String getReceiptHandle() {
    return receiptHandle;
  }

  public void retire() {
    retired = true;
  }

  /**
   * @return the retired
   */
  public boolean retired() {
    return retired;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(receiptHandle, retired);
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
    SqsMessage other = (SqsMessage) obj;
    return Objects.equals(receiptHandle, other.receiptHandle) && retired == other.retired;
  }

  @Override
  public String toString() {
    return "SqsMessage [receiptHandle=" + receiptHandle + ", retired=" + retired + "]";
  }
}
