package org.talents.model.github;

public class Contributor {

  protected String login;
  protected int contributions;

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public int getContributions() {
    return contributions;
  }

  public void setContributions(int contributions) {
    this.contributions = contributions;
  }

  public String displayString() {
    return getLogin() + ": " + getContributions();
  }
}
