package org.talants.hdp.model.github;

import com.google.auto.value.AutoValue;
import org.talants.hdp.model.github.AutoValue_Contributor;

@AutoValue
public abstract class Contributor {

  abstract String login();
  abstract int contributions();
}
