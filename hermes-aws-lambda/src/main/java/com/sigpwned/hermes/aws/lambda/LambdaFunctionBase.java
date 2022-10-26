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
