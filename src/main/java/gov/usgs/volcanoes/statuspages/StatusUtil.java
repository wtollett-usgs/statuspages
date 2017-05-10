package gov.usgs.volcanoes.statuspages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Util functions for StatusPageGenerator program.
 * 
 * @author Nathan Ducasse, Bill Tollett
 *
 */
public class StatusUtil {

  /**
   * Convert string to j2ksec value.
   * 
   * @param str - String representing amount of time
   * @return j2ksec value for the passed-in string
   */
  public static long strTime(String str) {

    if (str.endsWith("h")) {
      return Long.parseLong(str.substring(0, str.length() - 1)) * 3600 * -1000;
    } else if (str.endsWith("d")) {
      return Long.parseLong(str.substring(0, str.length() - 1)) * 86400 * -1000;
    } else if (str.endsWith("w")) {
      return Long.parseLong(str.substring(0, str.length() - 1)) * 604800 * -1000;
    } else if (str.endsWith("m")) {
      return Long.parseLong(str.substring(0, str.length() - 1)) * 2592000 * -1000;
    } else { // if ends with 'y'
      return Long.parseLong(str.substring(0, str.length() - 1)) * 31536000 * -1000;
    }
  }

  /**
   * Replace iterative substrings with a given string, or a single substring with a given string if
   * no iterative substring are found.
   * 
   * @param from - Original string
   * @param toReplace - Substring to replace
   * @param value - Replacement string(s)
   * @return String with values replaced
   */
  public static String replaceSubstr(String from, String toReplace, String[] value) {
    if (from.contains(toReplace + "1")) {
      int channelNumber = 1;
      while (from.contains(toReplace + String.valueOf(channelNumber))) {
        from = from.replaceAll(toReplace + String.valueOf(channelNumber), value[channelNumber - 1]);
        channelNumber++;
      }
    } else if (from.contains(toReplace)) {
      from = from.replaceAll(toReplace, value[0]);
    }

    return from;
  }

  /**
   * Convert json object to map.
   */
  public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
    Map<String, Object> retMap = new HashMap<String, Object>();

    if (json != JSONObject.NULL) {
      retMap = toMap(json);
    }

    return retMap;
  }

  private static Map<String, Object> toMap(JSONObject object) throws JSONException {
    Map<String, Object> map = new HashMap<String, Object>();

    Iterator<String> keysItr = object.keys();
    while (keysItr.hasNext()) {
      String key = keysItr.next();
      Object value = object.get(key);

      if (value instanceof JSONArray) {
        value = toList((JSONArray) value);
      } else if (value instanceof JSONObject) {
        value = toMap((JSONObject) value);
      }

      map.put(key, value);
    }
    return map;
  }

  private static List<Object> toList(JSONArray array) throws JSONException {
    List<Object> list = new ArrayList<Object>();
    for (int i = 0; i < array.length(); i++) {
      Object value = array.get(i);

      if (value instanceof JSONArray) {
        value = toList((JSONArray) value);
      } else if (value instanceof JSONObject) {
        value = toMap((JSONObject) value);
      }

      list.add(value);
    }
    return list;
  }
}
