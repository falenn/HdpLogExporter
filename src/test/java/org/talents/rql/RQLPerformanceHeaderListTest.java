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

public class RQLPerformanceHeaderListTest {

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
    String queryString = "and((Host=like=*.tutsplus.com|Host=like=*.outsiders.com|Host=like=*.friend.com),User-Agent=like=*windows*)";

    RQLParser parser = new RQLParser();
    ASTNode node = parser.parse(queryString);

    ExecutorService executor = Executors.newFixedThreadPool(5);
    for (int i = 0; i < 1000; i++) {
      Runnable worker = new fixedMatcherTask(5,
          node,
          new HeaderFilter<Entry>(),
          HttpRequestGenerator.create().generateRequest());
      executor.execute(worker);
    }
    executor.shutdown();
    while (!executor.isTerminated()) {
    }
    System.out.println("Finished all threads");

    analyzeTiming();

  }

  class fixedMatcherTask implements Runnable {
    private ASTNode node = null;
    private HeaderFilter<Entry> filter = null;
    private HttpServletRequest request = null;
    private int thread;
    public fixedMatcherTask(int thread, ASTNode node, HeaderFilter<Entry> filter, HttpServletRequest request) {
      this.node = node;
      this.filter = filter;
      this.request = request;
      this.thread = thread;
    }

    @Override public void run() {
      Instant starttime = Instant.now();
      List<Entry> results = node.accept(filter, HeaderToList.convert(request));
      profiler.addMetric(Measurement.create(starttime, Instant.now()));
    }
  }

  public static void analyzeTiming() {
    for (Measurement m : profiler.getMetrics()) {
      System.out.println(m);
    }
  }
}
