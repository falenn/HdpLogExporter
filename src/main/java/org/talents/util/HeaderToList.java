package org.talents.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import okhttp3.internal.http.HttpHeaders;
import org.talents.rql.model.Entry;

public class HeaderToList {

  public static List<Entry> convert(HttpServletRequest request) {

    List<Entry> entries = new ArrayList<Entry>();

    Enumeration<String> keys = request.getHeaderNames();
    while(keys.hasMoreElements()) {

      String key = keys.nextElement();

      String value = request.getHeader(key);
      Entry entry = Entry.create(key,value);

      Entry.create(key,value);
      entries.add(entry);
    }
    return entries;
  }
}
