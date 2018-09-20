package org.talents.model.github;



import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import org.talents.model.github.AutoValue_ContributorAV;

//https://www.programcreek.com/java-api-examples/index.php?api=feign.gson.GsonDecoder
//https://ryanharter.com/blog/2016/03/22/autovalue/
//https://medium.com/3xplore/autovalue-with-retrofit-2-0-61f9530787b1
//https://stackoverflow.com/questions/36529238/how-to-use-autovalue-with-retrofit-2/36583645#36583645
//https://medium.com/rocknnull/no-more-value-classes-boilerplate-the-power-of-autovalue-bbaf36cf8bbe

@JsonDeserialize(as = AutoValue_ContributorAV.class)
@AutoValue
public abstract class ContributorAV {

  public abstract String login();
  public abstract int contributions();

  public static ContributorAV create(String login, int contributions) {
    return new AutoValue_ContributorAV(login, contributions);
  }

  // The public static method returning a TypeAdapter<Foo> is what
  // tells auto-value-gson to create a TypeAdapter for Foo.
  //public static TypeAdapter<Contributor> typeAdapter(Gson gson) {
   // return new AutoValue_Contributor()
    //return null;
  //}

  public String displayString() {
    return login() + ": " + contributions();
  }
}
