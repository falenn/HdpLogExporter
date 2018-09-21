package org.talents.rql;


import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import net.jazdw.rql.parser.ASTNode;
import net.jazdw.rql.parser.RQLParser;
import okhttp3.internal.http.HttpHeaders;
import org.junit.Before;
import org.junit.Test;
import org.talents.rql.filters.HeaderFilter;
import org.talents.rql.filters.ListFilter;
import org.talents.rql.model.Entry;
import org.talents.util.HeaderToList;
import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

//https://www.baeldung.com/rest-api-search-language-rsql-fiql
//https://www.programcreek.com/java-api-examples/index.php?source_dir=rql-parser-master/src/main/java/net/jazdw/rql/parser/RQLParser.java#
public class RQLHeaderFilterTest {

  protected HttpHeaders headers = null;
  protected HttpServletRequest request = null;
  protected String HOSTNAME="net.tutsplus.com";
  protected String MIXED_CASE_HOSTNAME="net.TutsPlus.com";

  protected RQLParser parser = null;
  protected HeaderFilter<Entry> filter = null;
  protected List<Entry> entries = null;

  @Before
  public void setup() {

    Map<String, String> headers = new HashMap<>();
    headers.put("Host","net.tutsplus.com");
    headers.put("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5 (.NET CLR 3.5.30729)");
    headers.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
    headers.put("Accept-Language","en-us,en;q=0.5");
    headers.put("Accept-Encoding","gzip,deflate");
    headers.put("Accept-Charset","ISO-8859-1,utf-8;q=0.7,*;q=0.7");
    headers.put("Keep-Alive","300");
    headers.put("Connection","keep-alive");
    headers.put("Cookie","PHPSESSID=r2t5uvjq435r4q7ib3vtdjq120");
    headers.put("Pragma","no-cache");
    headers.put("Cache-Control","no-cache");

    // create an Enumeration over the header keys
    Iterator<String> iterator = headers.keySet().iterator();
    Enumeration<String> headerNames = new Enumeration<String>() {
      @Override
      public boolean hasMoreElements() {
        return iterator.hasNext();
      }

      @Override
      public String nextElement() {
        return iterator.next();
      }
    };

    //https://stackoverflow.com/questions/12945907/how-to-mock-the-httpservletrequest

    // mock HttpServletRequest
    request = mock(HttpServletRequest.class);

    // mock the returned value of request.getHeaderNames()
    when(request.getHeaderNames()).thenReturn(headerNames);
    // When the mock request for a header is made, invoke the lamda function to return the field of the
    // supplied argument from the mocked Hashmap's matching key.
    when(request.getHeader(anyString())).thenAnswer(invocation -> headers.get((String) invocation.getArgument(0)));

    parser = new RQLParser();
    filter = new HeaderFilter<Entry>();
    entries = HeaderToList.convert(request);
  }

  @Test
  public void testCreateFilter() {
    RQLParser myParser = new RQLParser();
    HeaderFilter<Entry> myfilter =
        new HeaderFilter<Entry>();
    assertNotNull(filter);
    ASTNode node = myParser.parse("Host=sadfJKL");
    assertNotNull(node );
  }


  @Test
  public void testEquals() {
    String queryString = "Host=net.tutsplus.com";

    ASTNode node = parser.parse(queryString);
    List<Entry> results = node.accept(filter, entries);
    assertNotEquals(0, results.size());
    debugResults(results, queryString);
  }


  @Test
  public void testHostMatch() {
    String queryString = "Host=like=*tutsplus*";

    ASTNode node = parser.parse(queryString);
    List<Entry> results = node.accept(filter, entries);
    //assertEquals(1, results.size());
    debugResults(results, queryString);
  }

  @Test
  public void testAndMatch() {
    String queryString = "Host=like=*tutsplus*&User-Agent=like=*Windows*";

    ASTNode node = parser.parse(queryString);
    List<Entry> results = node.accept(filter, entries);
    assertNotEquals(0, results.size());
    debugResults(results, queryString);
  }

  @Test
  public void testAndMatchFail() {
    String queryString = "Host=like=*tutsplus*&User-Agent=like=*Linux*";

    ASTNode node = parser.parse(queryString);
    List<Entry> results = node.accept(filter, entries);
    assertEquals(0, results.size());
    debugResults(results, queryString);
  }

  @Test
  public void testOrMatch() {
    String queryString = "or(Host=like=*tutsplus*,User-Agent=like=*Linux*)";

    ASTNode node = parser.parse(queryString);
    List<Entry> results = node.accept(filter, entries);
    assertNotEquals(0, results.size());
    debugResults(results, queryString);
  }

  @Test
  public void testOrLogicalMatch() {
    String queryString = "Host=like=*tutsplus*|Host=like=*google.com";

    ASTNode node = parser.parse(queryString);
    List<Entry> results = node.accept(filter, entries);
    assertNotEquals(0, results.size());
    debugResults(results, queryString);
  }

  @Test
  public void testOrLogicalEq() {
    String queryString = "Host=net.tutsplus.com|Host=mail.google.com";

    ASTNode node = parser.parse(queryString);
    List<Entry> results = node.accept(filter, entries);
    assertNotEquals(0, results.size());
    debugResults(results, queryString);
  }

  @Test
  public void testMatchCaseInsensitive() {
    String queryString = "Host=like=" + MIXED_CASE_HOSTNAME;

    ASTNode node = parser.parse(queryString);
    List<Entry> results = node.accept(filter, entries);
    assertNotEquals(0, results.size());
    debugResults(results, queryString);
  }

  @Test
  public void testAndOrMatch() {
    String queryString = "and((Host=like=*.tutsplus.com|Host=like=*.outsiders.com|Host=like=*.friend.com),User-Agent=like=*windows*)";

    ASTNode node = parser.parse(queryString);
    List<Entry> results = node.accept(filter, entries);
    assertNotEquals(0, results.size());
    debugResults(results, queryString);
  }

  //------------- helpers -----------

  public static void debugResults(List<Entry> results, String queryString) {
    System.out.println(getMethodNameUsingCurrentThread() + ": records found[" + results.size() + "] for query: " + queryString);
    //for (Entry entry : results) {
    // System.out.println("Entry matched: " + entry.displayString());
    //}
  }

  public static String getMethodNameUsingCurrentThread() {
    return Thread.currentThread().getStackTrace()[3].getMethodName();
  }
}
