/*-
 * =================================LICENSE_START==================================
 * hermes-aws-lambda
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
package com.sigpwned.hermes.aws.lambda.serialization;

import com.amazonaws.services.lambda.runtime.serialization.PojoSerializer;
import com.amazonaws.services.lambda.runtime.serialization.events.LambdaEventSerializers;
import com.sigpwned.hermes.core.model.Message;
import com.sigpwned.hermes.core.serialization.BeanMessageDeserializer;

/**
 * Deserializes AWS Events.
 */
public class AwsPojoBeanMessageDeserializer<T> implements BeanMessageDeserializer<T> {
  private final PojoSerializer<T> serializer;

  public AwsPojoBeanMessageDeserializer(Class<T> clazz) {
    this(LambdaEventSerializers.serializerFor(clazz,
        Thread.currentThread().getContextClassLoader()));
  }

  public AwsPojoBeanMessageDeserializer(PojoSerializer<T> serializer) {
    this.serializer = serializer;
  }

  @Override
  public T deserializeBean(Message m) {
    return getSerializer().fromJson(m.getBody());
  }

  /**
   * @return the serializer
   */
  private PojoSerializer<T> getSerializer() {
    return serializer;
  }
}
