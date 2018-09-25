package org.talents.preformance;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MeasurementTest {

  public Measurement measurement = null;
  public Profiler profiler = Profiler.create();

  @Before
  public void setup() {

  }

  @Test
  public void testMeasurementDuration() {

    Instant start = Instant.now(profiler.getClock());
    try {
      Thread.sleep(5);
    } catch (Exception e) {
      ;
    }
    Instant end = Instant.now(profiler.getClock());

    Measurement m = Measurement.create(start,end);

    System.out.println("Measurement record: " + m.toString());

    long duration = Duration.between(start, end).toNanos();
    System.out.println("duration: " + duration);
  }

  @Test
  public void testMeasurementDurationNoClock() {

    Instant start = Instant.now();
    try {
      Thread.sleep(5);
    } catch (Exception e) {
      ;
    }
    Instant end = Instant.now();

    Measurement m = Measurement.create(start,end);

    System.out.println("Measurement record: " + m.toString());
    long duration = Duration.between(start, end).toNanos();
    System.out.println("duration: " + duration);
  }

}
