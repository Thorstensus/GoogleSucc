package org.andrej_chrenko.googlesucc.models;

public class SearchResult {
  private final String title;

  private final String link;

  public SearchResult(String title, String link) {
    this.title = title;
    this.link = link;
  }

  public String getTitle() {
    return title;
  }

  public String getLink() {
    return link;
  }
}
