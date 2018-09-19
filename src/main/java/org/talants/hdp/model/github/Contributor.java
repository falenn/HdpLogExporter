package org.talants.hdp.model.github;



import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import org.talants.hdp.model.github.AutoValue_Contributor;

@AutoValue
public abstract class Contributor {

  public abstract String login();
  public abstract int contributions();

  public static Contributor create(String login, int contributions) {
    return new AutoValue_Contributor(login, contributions);
  }

  // The public static method returning a TypeAdapter<Foo> is what
  // tells auto-value-gson to create a TypeAdapter for Foo.
  public static TypeAdapter<Contributor> typeAdapter(Gson gson) {
    return new AutoValue_Contributor.typeAdapter(gson;
  }

  public String displayString() {
    return login() + ": " + contributions();
  }
}
