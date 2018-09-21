package org.talents.preformance;

import java.util.ArrayList;
import java.util.List;

public class Profiler {

  protected List<Measurement> metrics = null;

  public static Profiler create() {
    return new Profiler();
  }

  private Profiler() {
    metrics = new ArrayList<Measurement>();
  }

  public void addMetric(Measurement m) {
    this.metrics.add(m);
  }

  public List<Measurement> getMetrics() {
    return metrics;
  }



  public static void pause(int seconds) {
    //
    // wait to give user chance to connect profiler
    System.out.println("Timeout to allow attach profiler");
    try {
      for (int i = 0; i < seconds; ++i) {
        Thread.sleep( 1000);
        System.out.print(".");
      }
      System.out.println("");
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    System.out.println("Finished waiting for profiler");
  }



}
