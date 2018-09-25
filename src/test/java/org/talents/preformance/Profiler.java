package org.talents.preformance;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.rmi.server.ExportException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Profiler {



  protected List<Measurement> metrics = null;
  protected Clock clock = null;

  public static Profiler create() {
    return new Profiler();
  }

  private Profiler() {
    metrics = new ArrayList<Measurement>();
    clock = new NanoClock();
  }

  public void addMetric(Measurement m) {
    this.metrics.add(m);
  }

  public List<Measurement> getMetrics() {
    return metrics;
  }

  public Clock getClock() {
    if(this.clock == null)
      this.clock = new NanoClock();
    return this.clock;
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

  public int getCount() {
    return this.getMetrics().size();
  }

  /**
   * get sum of all time spent.  This is a summation of the "delta" fields in the Measurement objects
   * (StopTime - StartTime). Units are NS (NanoSeconds)
   * @return
   */
  public double getSumDuration(){
    double total = 0.0d;
    Iterator<Measurement> i = getMetrics().iterator();
    while(((Iterator) i).hasNext()) {
      Measurement m = i.next();
      total += m.durationInNanos();
    }
    return total;
  }

  /**
   * Average of all Measurement.delta().  Units in NanoSeconds.
   * @return
   */
  public double getAvgDuration(){
    long total = 0;
    Iterator<Measurement> i = getMetrics().iterator();
    while(((Iterator) i).hasNext()) {
        Measurement m = i.next();
        total += m.durationInNanos();
    }
    return total/this.getCount();
  }

  public Measurement dropLongestDuration() {
    this.sotByDuration();
    return this.getMetrics().remove(this.metrics.size()-1);
  }

  public long getShortestDuration() {
    this.sotByDuration();
    return ((Measurement) this.getMetrics().get(0)).durationInNanos();
  }

  public long getLongestDuration() {
    this.sotByDuration();
    return ((Measurement) this.getMetrics().get(this.getMetrics().size()-1)).durationInNanos();
  }

  public Measurement dropShortestDuration() {
    this.sortByStartTime();
    return this.getMetrics().remove(0);
  }

  public void sotByDuration() {
    this.getMetrics().sort((Measurement o1, Measurement o2)-> Long.compare(o1.durationInNanos(),o2.durationInNanos()));
  }

  public void sortByStartTime() {
    this.getMetrics().sort((Measurement o1, Measurement o2)-> o1.startTime().compareTo(o2.startTime()));
  }

  public double getMeanDuration (){
    double mean = 0;
    mean = this.getSumDuration() / (this.getMetrics().size() * 1.0);
    return mean;
  }

  public double medianDuration (){
    int middle = this.getMetrics().size()/2;

    if (this.getMetrics().size() % 2 == 1) {
      return ((Measurement) this.getMetrics().get(middle)).durationInNanos();
    } else {
      return (((Measurement) this.getMetrics().get(middle-1)).durationInNanos() +
              ((Measurement) this.getMetrics().get(middle)).durationInNanos()) / 2.0;
    }
  }

  public double getStandardDeviationDuration (){
    int sum = 0;
    double mean = getMeanDuration();
    Iterator<Measurement> i = this.getMetrics().iterator();
    while (i.hasNext()) {
      Measurement m = i.next();
      sum += Math.pow((m.durationInNanos() - mean), 2);
    }
    return Math.sqrt( sum / ( this.getMetrics().size() - 1 ) ); // sample
  }

  public double getStandardDeviationDuration (int multiple){
    int sum = 0;
    double mean = getMeanDuration();
    Iterator<Measurement> i = this.getMetrics().iterator();
    return mean + multiple * getStandardDeviationDuration();
  }

  public void cleanup() {
    List<Measurement> outliers = new ArrayList<Measurement>();
    double sd2 = getStandardDeviationDuration(2);
    Iterator<Measurement> i = this.getMetrics().iterator();
    while (i.hasNext()) {
      Measurement m = i.next();
      if(m.durationInNanos() > sd2)
        outliers.add(m);
    }

    Iterator<Measurement> j = outliers.iterator();
    while (j.hasNext()) {
      Measurement m = j.next();
      this.metrics.remove(m);
    }
    outliers = null;
  }

  public boolean greaterThanSD2(Measurement m) {
    double sd2 = getStandardDeviationDuration(2);
    if (Double.compare(sd2, m.durationInNanos()) < 0)
        return true;
    return false;
  }

  public void generateReport() {

    //System.out.println("Drop fastest and slowest times:" + "\n" +
    //    "Longest: " + dropLongestDuration() + "\n" +
    //    "Shortest: " + dropShortestDuration());

    //cleanup();
    //cleanup();
    sortByStartTime();

    System.out.println("\n------------ Profiler Report ---------------\n" +
        "Total measurements: " + this.getCount() + "\n" +
        "Average Time: \t\t\t" + this.getAvgDuration()/1000000.0 + " ms\n" +
        "Longest duration: \t" + this.getLongestDuration()/1000000.0 + " ms\n" +
        "Shortest duration: \t" + this.getShortestDuration()/1000000.0 + " ms\n" +
        "Mean Duration: \t\t" + this.getMeanDuration()/1000000.0 + " ms\n" +
        "Test start time: " + this.getMetrics().get(0).startTime() + "\n" +
        "Test end time: " + this.getMetrics().get(this.getMetrics().size()-1).endTime() + "\n" +
        "Total test time: " + Duration.between(
          this.getMetrics().get(this.getMetrics().size()-1).endTime(),
          this.getMetrics().get(0).startTime()) + "\n" +
        "Standard Dev Dur:\t" + this.getStandardDeviationDuration()/1000000.0 + " ms");
  }

  public void generateDat(String datFileName) {
    try {

      // drop points outside 2 deviations

      sortByStartTime();
      //cleanup();

      FileWriter fileWriter = new FileWriter(datFileName,false);
      PrintWriter printWriter = new PrintWriter(fileWriter);
      printWriter.printf("# This file is called   " + datFileName + "\n");
      printWriter.printf("# Performance of RQL query on HTTP Headers\n");
      printWriter.printf("# StartTime    EndTime       Duration \n");
      Iterator<Measurement> i = this.getMetrics().iterator();
      while(i.hasNext()) {
        Measurement m = i.next();
        if(!greaterThanSD2(m))
          printWriter.printf("%d\t%d\t%d\n",m.startTime().getNano(), m.endTime().getNano(), m.durationInNanos());
      }
      printWriter.close();
    } catch (Exception e) {

    }
  }
}
