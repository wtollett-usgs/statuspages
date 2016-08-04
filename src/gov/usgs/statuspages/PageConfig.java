package gov.usgs.statuspages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.*;

public class PageConfig {
	
	private String name;
	private String url;
	private String filename;
	private Map<String,Integer> timeperiods = new HashMap<String, Integer>();
	private List<List<String>> channelList  = new ArrayList<List<String>>();
	
	private boolean multiChannelFlag    = false;
	
	/**
	 * Constructor used to build config from passed-in json object
	 * @param passedJSObject
	 */
	public PageConfig(JSONObject passedJSObject) {
		this.name     = passedJSObject.getString("name");
		this.url      = passedJSObject.getString("url");
		this.filename = passedJSObject.getString("filename");
		
		//parses JSON object for time periods for given data set.
		JSONObject timePeriodJSObject = passedJSObject.getJSONObject("timeperiods");
		this.timeperiods              = (Map)Util.jsonToMap(timePeriodJSObject);
		
		if (this.url.contains("CHANNEL")) {			
			//throws an exception if JSON file is in format [[...]] and routes to special case.
			JSONArray channelJSArray = passedJSObject.getJSONArray("channels");
			try{
				List<String> channels = new ArrayList<String>();
				
				for(int channelIndex = 0; channelIndex < channelJSArray.length(); channelIndex++) {
					channels.add(channelJSArray.getString(channelIndex));
				}
				
				this.channelList.add(channels);
			}catch(Exception e) {
				this.multiChannelFlag = true;
				for(int listOfChannel = 0; listOfChannel < channelJSArray.length(); listOfChannel++) {
					this.channelList.add(new ArrayList<String>());
					
					for(int channelIndex = 0; channelIndex < channelJSArray.getJSONArray(listOfChannel).length(); channelIndex++) {
						this.channelList.get(listOfChannel).add(channelJSArray.getJSONArray(listOfChannel).getString(channelIndex));
					}
				}
			}
		}
	}
	
	/**
	 * @return Return the page name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return Return the Valve3 url template for this set of images
	 */
	public String getURL() {
		return this.url;
	}
	
	/**
	 * @return Return the filename template for this set of images
	 */
	public String getFileName() {
		return this.filename;
	}
	
	/**
	 * @return Return the chart generation time periods for this set of images
	 */
	public Map<String,Integer> getTimePeriods() {
		return this.timeperiods;
	}
	
	/**
	 * @return Return the list of (lists of...) channels for this set of images
	 */
	public List<List<String>> getChannels() {
		return this.channelList;
	}
	
	/**
	 * @return Returns true of channels are in the format [[channel1, channel2, ...]] and false otherwise
	 */
	public boolean getMultiChannelFlag() {
		return this.multiChannelFlag;
	}
	
	/**
	 * @return Returns true if this set of charts is generated for a defined set of channels
	 */
	public boolean getContainsChannelFlag() {
		return !(this.channelList.isEmpty());
	}
}
