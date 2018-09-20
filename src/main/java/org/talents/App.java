package org.talents;


import java.util.List;
import org.talents.clients.Github;
import org.talents.model.github.Contributor;

public class App {

  public static void main(String[] args) {


    List<Contributor> contributors = Github.connect().contributors("falenn","k8sPlayground");

    for (Contributor contributor : contributors) {
      System.out.println(contributor.displayString());
    }

  }

}
