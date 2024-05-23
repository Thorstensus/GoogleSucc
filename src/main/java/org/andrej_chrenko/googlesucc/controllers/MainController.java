package org.andrej_chrenko.googlesucc.controllers;

import org.andrej_chrenko.googlesucc.utils.GoogleSuccUtils;
import org.andrej_chrenko.googlesucc.models.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.util.List;

@Controller
public class MainController {

  private final GoogleSuccUtils googleSuccUtils;

  @Autowired
  public MainController(GoogleSuccUtils googleSuccUtils) {
    this.googleSuccUtils = googleSuccUtils;
  }

  @GetMapping("/")
  public String loadMainPage() {
    return "main";
  }

  @GetMapping("/succ")
  public String search(@RequestParam String query, Model model) {
    List<SearchResult> results = googleSuccUtils.fetchSearchResults(query);
    model.addAttribute("query", query);
    model.addAttribute("results", results);
    return "main";
  }

  @GetMapping("/download")
  @ResponseBody
  public ResponseEntity<FileSystemResource> download(@RequestParam String query) {
    List<SearchResult> results = googleSuccUtils.fetchSearchResults(query);
    File file = googleSuccUtils.createJsonTextFile(results, "search_results.txt");

    FileSystemResource fileSystemResource = new FileSystemResource(file);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=search_results.txt");

    return ResponseEntity.ok().headers(httpHeaders).body(fileSystemResource);
  }

}
