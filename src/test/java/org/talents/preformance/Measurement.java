package org.talents.preformance;

import com.google.auto.value.AutoValue;
import java.time.Instant;

@AutoValue
public abstract class Measurement {

  public abstract Instant startTime();
  public abstract Instant endTime();


  public static Measurement create(Instant startTime, Instant endTime) {
    return new org.talents.preformance.AutoValue_Measurement(startTime, endTime);
  }

  public long delta() {
    return endTime().compareTo(startTime());
  }

  public String toString() {
    return "{ startTime: " + startTime() + ", endTime: " + endTime() + ", delta: " + delta() + "}";
  }

}
