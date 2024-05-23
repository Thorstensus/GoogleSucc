package org.andrej_chrenko.googlesucc.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.andrej_chrenko.googlesucc.models.SearchResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class GoogleSuccUtils {

  private static final Dotenv dotenv = Dotenv.load();
  private final String API_KEY = dotenv.get("GOOGLE_API_KEY");
  private final String CX = dotenv.get("CX");
  private final String GOOGLE_SEARCH_URL = "https://www.googleapis.com/customsearch/v1";

  private final OkHttpClient client;

  private final ObjectMapper objectMapper;

  public GoogleSuccUtils() {
    this.client = new OkHttpClient();
    this.objectMapper = new ObjectMapper();
  }

  public List<SearchResult> fetchSearchResults(String query) {
    String searchUrl = GOOGLE_SEARCH_URL + "?key=" + API_KEY + "&cx=" + CX + "&q=" + query;
    Request request = new Request.Builder().url(searchUrl).build();

    List<SearchResult> results = new ArrayList<>();

    try (Response response = client.newCall(request).execute()) {
      if (response.isSuccessful() && response.body() != null) {
        String jsonResponse = response.body().string();
        results = parseResults(jsonResponse);
      } else {
        throw new IOException("Failed to fetch search results");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return results;
  }

  public List<SearchResult> parseResults(String jsonResponse) {
    List<SearchResult> results = new ArrayList<>();

    try {
      JsonNode rootNode = objectMapper.readTree(jsonResponse);
      JsonNode itemsNode = rootNode.path("items");

      for (JsonNode itemNode : itemsNode) {
        String title = itemNode.path("title").asText();
        String link = itemNode.path("link").asText();
        results.add(new SearchResult(title, link));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return results;
  }

  public File createJsonTextFile(List<SearchResult> results, String fileName) {
    File file = new File(fileName);
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
      objectMapper.writeValue(writer, results);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return file;
  }


}
