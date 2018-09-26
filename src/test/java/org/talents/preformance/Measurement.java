package org.talents.preformance;

import com.google.auto.value.AutoValue;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@AutoValue
public abstract class Measurement {

  public abstract Instant startTime();
  public abstract Instant endTime();


  public static Measurement create(Instant startTime, Instant endTime) {
    return new org.talents.preformance.AutoValue_Measurement(startTime, endTime);
  }

  /**
   * Compute the difference between the two measurement times, preserving accuracy.
   * @return
   */
  public long durationInNanos() {
    return Duration.between(startTime(), endTime()).toNanos();
  }

  public long testStartTimeOffsetInNanos(Instant testStartTime) {
    return Duration.between(startTime(), testStartTime).toNanos();
  }

  public String toString() {
    return "{ startTime: " + startTime() + ", endTime: " + endTime() + ", delta: " + durationInNanos() + "}";
  }

  public String toDatString() {
    return (startTime().toEpochMilli() + "." + startTime().getNano() + "\t" + durationInNanos());
  }
}
