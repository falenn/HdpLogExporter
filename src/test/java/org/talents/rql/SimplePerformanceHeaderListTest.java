package org.talents.rql;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.servlet.http.HttpServletRequest;
import net.jazdw.rql.parser.ASTNode;
import net.jazdw.rql.parser.RQLParser;
import org.junit.Before;
import org.junit.Test;
import org.talents.preformance.HttpRequestGenerator;
import org.talents.preformance.Measurement;
import org.talents.preformance.Profiler;
import org.talents.rql.filters.HeaderFilter;
import org.talents.rql.model.Entry;
import org.talents.util.HeaderToList;

public class SimplePerformanceHeaderListTest {

  protected RQLParser parser = null;
  protected HeaderFilter<Entry> filter = null;
  protected List<Entry> entries = null;

  public static Profiler profiler = Profiler.create();

  @Before
  public void setup() {}

  @Test
  public void startupTest() {

    Profiler.pause(5);

    // Parse and prepare the query
    String header = "Host";
    String queryString = "*.tutsplus.com";

    ExecutorService executor = Executors.newFixedThreadPool(5);
    for (int i = 0; i < 1000000; i++) {
      Runnable worker = new simpleHeaderMatcherTask(
          5,
          header,
          queryString,
          HttpRequestGenerator.create().generateRequest());
      executor.execute(worker);
    }
    executor.shutdown();
    while (!executor.isTerminated()) {
    }
    System.out.println("Finished all threads");

    analyzeTiming();

  }

  class simpleHeaderMatcherTask implements Runnable {
    private HttpServletRequest request = null;
    private int thread;
    private String header = "";
    private String matchCriteria = "";

    public simpleHeaderMatcherTask(int thread, String header, String matchCriteria, HttpServletRequest request) {
      this.header = header;
      this.matchCriteria = matchCriteria;
      this.request = request;
      this.thread = thread;
    }

    @Override public void run() {
      Instant start = Instant.now(profiler.getClock());
      request.getHeader(this.header).compareToIgnoreCase(this.matchCriteria);
      profiler.addMetric(Measurement.create(start, Instant.now(profiler.getClock())));
    }
  }

  public static void analyzeTiming() {
    profiler.generateReport();
    profiler.generateDat("simpleHeaderTest.dat");
  }
}
