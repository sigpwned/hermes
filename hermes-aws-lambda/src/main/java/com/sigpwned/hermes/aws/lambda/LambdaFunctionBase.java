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
package com.sigpwned.hermes.aws.lambda;

import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.sigpwned.hermes.core.OptionalEnvironmentVariable;

public abstract class LambdaFunctionBase<I, O> implements RequestHandler<I, O> {
  /**
   * Equivalent to {@code OptionalEnvironmentVariable.getenv(name))}.
   * 
   * @see System#getenv(String)
   */
  protected static OptionalEnvironmentVariable<String> getenv(String name) {
    return OptionalEnvironmentVariable.getenv(name);
  }
}
