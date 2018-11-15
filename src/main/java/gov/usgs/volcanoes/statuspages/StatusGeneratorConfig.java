package gov.usgs.volcanoes.statuspages;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Representation of the entire config.
 * 
 * @author Nathan Ducasse, Bill Tollett
 *
 */
public class StatusGeneratorConfig {

  private final Map<String, PageConfig> pageConfigMap;

  /**
   * Constructor taking a filename to be parsed.
   * 
   * @param fileName File to parse
   */
  public StatusGeneratorConfig(String fileName) {
    this.pageConfigMap = createPageConfigMap(loadFromFile(fileName));
  }

  /**
   * Open and load the JSON-formatted config file into a JSONObject to be parsed.
   * 
   * @param fileName file to be parsed
   * @return json representation of parsed config file
   */
  private JSONObject loadFromFile(String fileName) {
    String jsonFile = "";
    try {
      File inFile = new File(fileName);
      Scanner in = new Scanner(inFile);
      jsonFile = new String();

      while (in.hasNext()) {
        jsonFile += in.next();
      }
      in.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    JSONObject jsonObject = new JSONObject(jsonFile);
    return jsonObject;
  }

  /**
   * Parse the config file, creating a PageConfig object for each entry in the JSON dictionary.
   * 
   * @param jsonObject json representation of the parsed config file
   * @return Map of PageConfig objects
   */
  private Map<String, PageConfig> createPageConfigMap(JSONObject jsonObject) {
    Map<String, PageConfig> pages = new Hashtable<String, PageConfig>();

    JSONArray jsonArray = jsonObject.getJSONArray("pages");

    for (int objIndex = 0; objIndex < jsonArray.length(); objIndex++) {
      pages.put(jsonArray.getJSONObject(objIndex).getString("name"),
          new PageConfig(jsonArray.getJSONObject(objIndex)));
    }

    return pages;

  }

  /**
   * Get the map of PageConfig objects.
   */
  public Map<String, PageConfig> getPageConfigMap() {
    return this.pageConfigMap;
  }
}
