package org.talants.hdp;


import java.util.List;
import org.talants.hdp.clients.Github;
import org.talants.hdp.model.github.Contributor;

public class App {

  public static void main(String[] args) {

    List<Contributor> contributors = Github.connect().contributors("falenn","k8sPlayground");

    for (Contributor contributor : contributors) {
      System.out.println(contributor.displayString());
    }

  }

}
