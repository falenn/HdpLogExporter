package org.talents.rql;


import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import net.jazdw.rql.parser.RQLParser;
import okhttp3.internal.http.HttpHeaders;
import org.junit.Before;
import org.junit.Test;
import org.talents.rql.filters.HttpRequestHeaderFilter;

import static org.mockito.Mockito.*;

//https://www.baeldung.com/rest-api-search-language-rsql-fiql
//https://www.programcreek.com/java-api-examples/index.php?source_dir=rql-parser-master/src/main/java/net/jazdw/rql/parser/RQLParser.java#
public class RQLTest {

  protected HttpHeaders headers = null;
  protected HttpServletRequest request = null;

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
    Enumeration headerNames = new Enumeration<String>() {
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
  }

  @Test
  public void testRQLStartup() {

    RQLParser p = new RQLParser();
    HttpRequestHeaderFilter<HttpServletRequest> filter = new HttpRequestHeaderFilter<HttpServletRequest>();



  }




}
