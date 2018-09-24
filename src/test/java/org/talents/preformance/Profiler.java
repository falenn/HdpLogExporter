package org.talents.preformance;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.rmi.server.ExportException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Profiler {

  public String filename = "output.dat";

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

  public int getCount() {
    return this.getMetrics().size();
  }

  public double getSumDurationMS(){
    double total = 0.0d;
    Iterator<Measurement> i = getMetrics().iterator();
    while(((Iterator) i).hasNext()) {
      Measurement m = i.next();
      total += m.delta();
    }
    return total/1000;
  }

  public double getAvgDurationMS(){
    double total = 0.0d;
    Iterator<Measurement> i = getMetrics().iterator();
    while(((Iterator) i).hasNext()) {
        Measurement m = i.next();
        total += m.delta();
    }
    return total/this.getCount()/1000;
  }

  public long getLongestDurationMS() {
    this.getMetrics().sort((Measurement o1, Measurement o2)->Long.compare(o2.delta(),o1.delta()));
    return ((Measurement) this.getMetrics().get(0)).delta()/1000;
  }

  public Measurement dropLongestDuration() {
    this.getMetrics().sort((Measurement o1, Measurement o2)->Long.compare(o2.delta(),o1.delta()));
    return this.getMetrics().remove(0);
  }

  public long getShorestDurationMS() {
    this.getMetrics().sort((Measurement o1, Measurement o2)->Long.compare(o1.delta(),o2.delta()));
    return ((Measurement) this.getMetrics().get(0)).delta()/1000;
  }

  public Measurement dropShortestDuration() {
    this.getMetrics().sort((Measurement o1, Measurement o2)->Long.compare(o1.delta(),o2.delta()));
    return this.getMetrics().remove(0);
  }

  public void sortByStartTime() {
    this.getMetrics().sort((Measurement o1, Measurement o2)-> Long.compare(o1.startTime().toEpochMilli(),o2.startTime().toEpochMilli()));
  }

  public double getMeanDurationMS (){
    double mean = 0;
    mean = this.getSumDurationMS() / (this.getMetrics().size() * 1.0);
    return mean;
  }

  public double medianDurationMS (){
    int middle = this.getMetrics().size()/2;

    if (this.getMetrics().size() % 2 == 1) {
      return ((Measurement) this.getMetrics().get(middle)).delta()/1000;
    } else {
      return (((Measurement) this.getMetrics().get(middle-1)).delta()/1000 +
              ((Measurement) this.getMetrics().get(middle)).delta()/1000) / 2.0;
    }
  }

  public double getStandardDeviationDurationMS (){
    int sum = 0;
    double mean = getMeanDurationMS();
    Iterator<Measurement> i = this.getMetrics().iterator();
    while (i.hasNext()) {
      Measurement m = i.next();
      sum += Math.pow((m.delta()/1000 - mean), 2);
    }
    return Math.sqrt( sum / ( this.getMetrics().size() - 1 ) ); // sample
  }

  public double getStandardDeviationDurationMS (int multiple){
    int sum = 0;
    double mean = getMeanDurationMS();
    Iterator<Measurement> i = this.getMetrics().iterator();
    return mean + multiple * getStandardDeviationDurationMS();
  }

  public void cleanup() {
    List<Measurement> outliers = new ArrayList<Measurement>();
    double sd2 = getStandardDeviationDurationMS(2);
    Iterator<Measurement> i = this.getMetrics().iterator();
    while (i.hasNext()) {
      Measurement m = i.next();
      if(m.delta()/1000 > sd2)
        outliers.add(m);
    }

    Iterator<Measurement> j = outliers.iterator();
    while (j.hasNext()) {
      Measurement m = j.next();
      this.metrics.remove(m);
    }
    outliers = null;
  }

  public void generateReport() {

    System.out.println("Drop fastest and slowest times:" + "\n" +
        "Longest: " + dropLongestDuration() + "\n" +
        "Shortest: " + dropShortestDuration());

    cleanup();

    System.out.println("\n------------ Profiler Report ---------------\n" +
        "Total measurements: " + this.getCount() + "\n" +
        "Average Time: \t\t\t" + this.getAvgDurationMS() + "ms\n" +
        "Longest duration: \t" + this.getLongestDurationMS() + "ms\n" +
        "Shortest duration: \t" + this.getShorestDurationMS() + "ms\n" +
        "Mean Duration: \t\t" + this.getMeanDurationMS() + "ms\n" +
        "Standard Dev Dur:\t" + this.getStandardDeviationDurationMS() + "ms");
  }

  public void generateDat() {
    try {
      cleanup();
      sortByStartTime();
      FileWriter fileWriter = new FileWriter(this.filename,false);
      PrintWriter printWriter = new PrintWriter(fileWriter);
      printWriter.printf("# This file is called   " + filename + "\n");
      printWriter.printf("# Performance of RQL query on HTTP Headers\n");
      printWriter.printf("# StartTime    EndTime       Duration \n");
      Iterator<Measurement> i = this.getMetrics().iterator();
      while(i.hasNext()) {
        Measurement m = i.next();
        printWriter.printf("%d\t%d\t%d\n",m.startTime().getNano(), m.endTime().getNano(), m.delta());
      }
      printWriter.close();
    } catch (Exception e) {

    }
  }
}
