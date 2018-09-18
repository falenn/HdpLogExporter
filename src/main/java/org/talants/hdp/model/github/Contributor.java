package org.talants.hdp.model.github;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Contributor {

  abstract String login();
  abstract int contributions();
}
