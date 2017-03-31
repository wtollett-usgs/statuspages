package gov.usgs.volcanoes.statuspages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class PageConfig {

  private String name;
  private String url;
  private String filename;
  private Map<String, Integer> timeperiods = new HashMap<String, Integer>();
  private List<List<String>> channelList = new ArrayList<List<String>>();

  private boolean multiChannelFlag = false;

  /**
   * Constructor used to build config from passed-in json object.
   * 
   * @param passedJsonObj JSON object holding config file contents
   */
  public PageConfig(JSONObject passedJsonObj) {
    this.name = passedJsonObj.getString("name");
    this.url = passedJsonObj.getString("url");
    this.filename = passedJsonObj.getString("filename");

    // parses JSON object for time periods for given data set.
    JSONObject timePeriodJsonObject = passedJsonObj.getJSONObject("timeperiods");
    this.timeperiods = (Map) Util.jsonToMap(timePeriodJsonObject);

    if (this.url.contains("CHANNEL")) {
      // throws an exception if JSON file is in format [[...]] and routes to special case.
      JSONArray channelJsonArray = passedJsonObj.getJSONArray("channels");
      try {
        List<String> channels = new ArrayList<String>();

        for (int channelIndex = 0; channelIndex < channelJsonArray.length(); channelIndex++) {
          channels.add(channelJsonArray.getString(channelIndex));
        }

        this.channelList.add(channels);
      } catch (Exception e) {
        this.multiChannelFlag = true;
        for (int listOfChannel = 0; listOfChannel < channelJsonArray.length(); listOfChannel++) {
          this.channelList.add(new ArrayList<String>());

          for (int channelIndex = 0; channelIndex < channelJsonArray.getJSONArray(listOfChannel)
              .length(); channelIndex++) {
            this.channelList.get(listOfChannel)
                .add(channelJsonArray.getJSONArray(listOfChannel).getString(channelIndex));
          }
        }
      }
    }
  }

  /**
   * Get the page name.
   * 
   * @return Return the page name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Get the valve3 url.
   * 
   * @return Return the Valve3 url template for this set of images
   */
  public String getUrl() {
    return this.url;
  }

  /**
   * Get the filename template for this set of images.
   * 
   * @return Return the filename template for this set of images
   */
  public String getFileName() {
    return this.filename;
  }

  /**
   * Get the time periods used for this set of images.
   * 
   * @return Return the chart generation time periods for this set of images
   */
  public Map<String, Integer> getTimePeriods() {
    return this.timeperiods;
  }

  /**
   * Get the channels used for this set of images.
   * 
   * @return Return the list of (lists of...) channels for this set of images
   */
  public List<List<String>> getChannels() {
    return this.channelList;
  }

  /**
   * Find out if this set of images uses multiple channels
   * 
   * @return Returns true if channels are in the format [[channel1, channel2, ...]] and false
   *         otherwise
   */
  public boolean getMultiChannelFlag() {
    return this.multiChannelFlag;
  }

  /**
   * Find out if this set of images is generated for a defined set of channels.
   * 
   * @return Returns true if this set of charts is generated for a defined set of channels
   */
  public boolean getContainsChannelFlag() {
    return !(this.channelList.isEmpty());
  }
}
