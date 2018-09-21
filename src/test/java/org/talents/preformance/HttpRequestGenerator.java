package org.talents.preformance;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpRequestGenerator {

  protected HttpServletRequest request = null;
  protected Map<String, String> headers = null;
  private static HttpRequestGenerator instance = null;

  //public instance constructor
  public static HttpRequestGenerator create(){
    if(instance == null)
      instance = new HttpRequestGenerator();
    return instance;
  }

  //private constuctor
  private HttpRequestGenerator() {

  }

  public HttpServletRequest generateRequest() {
    if(request == null) {

      Iterator<String> iterator = generateHeaders().keySet().iterator();
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
    }
    return request;
  }



  public Map<String, String> generateHeaders() {

    if(this.headers == null) {
        headers = new HashMap<>();
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
    }
    return headers;

  }
}
