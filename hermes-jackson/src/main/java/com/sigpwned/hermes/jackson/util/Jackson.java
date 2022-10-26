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
package com.sigpwned.hermes.jackson.util;

import java.lang.reflect.Type;
import java.util.Optional;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

public final class Jackson {
  private Jackson() {}

  public static final ObjectMapper DEFAULT_OBJECT_MAPPER =
      new ObjectMapper().registerModule(new JavaTimeModule()).registerModule(new Jdk8Module())
          .registerModule(new ParameterNamesModule());

  /**
   * Returns the erased class for the given type.
   *
   * <p>
   * Example: if type is <code>List&lt;String&gt;</code>, returns <code>List.class</code>
   * </p>
   * parameter
   *
   * @param type the type
   * @return the erased class
   */
  public static Class<?> getErasedType(Type type) {
    return getErasedType(Jackson.DEFAULT_OBJECT_MAPPER.getTypeFactory(), type);
  }

  public static Class<?> getErasedType(TypeFactory typeFactory, Type type) {
    return typeFactory.constructType(type).getRawClass();
  }

  /**
   * Same as {@link #findGenericParameter(Type, Class, int)} with n = 0.
   *
   * @param type the type
   * @param parameterizedSupertype the parameterized supertype
   * @return the first parameter type
   * @see #findGenericParameter(Type, Class, int)
   */
  public static Optional<JavaType> findGenericParameter(Type type,
      Class<?> parameterizedSupertype) {
    return findGenericParameter(type, parameterizedSupertype, 0);
  }

  /**
   * For the given type which extends parameterizedSupertype, returns the nth generic parameter for
   * the parameterized supertype, if concretely expressed.
   *
   * <p>
   * Example:
   * </p>
   * <ul>
   * <li>if {@code type} is {@code ArrayList<String>}, {@code parameterizedSupertype} is
   * {@code List.class}, and {@code n} is {@code 0}, returns {@code Optional.of(String.class)}.</li>
   *
   * <li>if {@code type} is {@code Map<String, Integer>}, {@code parameterizedSupertype} is
   * {@code Map.class}, and {@code n} is {@code 1}, returns {@code Optional.of(Integer.class)}.</li>
   *
   * <li>if {@code type} is {@code ArrayList.class} (raw), {@code parameterizedSupertype} is
   * {@code List.class}, and {@code n} is {@code 0}, returns {@code Optional.empty()}.</li>
   * </ul>
   *
   * @param type the subtype of parameterizedSupertype
   * @param parameterizedSupertype the parameterized supertype from which we want the generic
   *        parameter
   * @param n the index in {@code Foo<X, Y, Z, ...>}
   * @return the parameter on the supertype, if it is concretely defined.
   * @throws ArrayIndexOutOfBoundsException if n &gt; the number of type variables the type has
   */
  public static Optional<JavaType> findGenericParameter(Type type, Class<?> parameterizedSupertype,
      int n) {
    return findGenericParameter(Jackson.DEFAULT_OBJECT_MAPPER.getTypeFactory(), type,
        parameterizedSupertype, n);
  }

  public static Optional<JavaType> findGenericParameter(TypeFactory typeFactory, Type type,
      Class<?> parameterizedSupertype, int n) {
    JavaType[] parameters =
        typeFactory.findTypeParameters(typeFactory.constructType(type), parameterizedSupertype);
    return Optional.ofNullable(n < parameters.length ? parameters[n] : null);
  }
}
