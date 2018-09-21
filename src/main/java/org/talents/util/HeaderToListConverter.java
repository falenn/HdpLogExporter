package org.talents.util;

public abstract class HeaderToListConverter {

  /**
   * Extracts the span context from upstream. For example, as http headers.
   *
   * @param carrier holds propagation fields. For example, an outgoing message or http request.
   * @param getter invoked for each propagation key to get.
   * @throws SpanContextParseException if the input is invalid
   * @since 0.11
   */
  //public abstract <C /*>>> extends @NonNull Object*/> SpanContext extract(
  //    C carrier, Getter<C> getter) throws SpanContextParseException;
}
