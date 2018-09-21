package org.talents.rql.model;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Entry {

  public abstract String getKey();
  public abstract String getValue();

  public static Entry create(String key, String value) {
    return new org.talents.rql.model.AutoValue_Entry(key, value);
  }

  public String displayString() {
    return getKey() + ": " + getValue();
  }

}
