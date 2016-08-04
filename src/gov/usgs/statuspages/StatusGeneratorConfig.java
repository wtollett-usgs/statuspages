package gov.usgs.statuspages;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;
import org.json.*;

public class StatusGeneratorConfig {
	
	private final Map<String, PageConfig> pageConfigMap;
	
	/**
	 * Constructor
	 * @param fileName - File to parse
	 */
	public StatusGeneratorConfig(String fileName) {
		this.pageConfigMap = createPageConfigMap(loadFromFile(fileName));
	}
	
	/**
	 * Open and load the JSON-formatted config file into a JSONObject to be parsed.
	 * @param fileName
	 * @return
	 */
	private JSONObject loadFromFile(String fileName) {
	    String JSFile = "";
	    try {
    		File inFile   = new File(fileName);
    		Scanner in    = new Scanner(inFile);
    		JSFile = new String();
    		
    		while (in.hasNext()) {
    			JSFile += in.next();
    		}
    		in.close();
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
		
		JSONObject JSObject = new JSONObject(JSFile);
		return JSObject;
	}
	
	/**
	 * Parse the config file, creating a PageConfig object for each entry in the JSON dictionary
	 * @param JSObject
	 * @return
	 */
	private Map<String, PageConfig> createPageConfigMap(JSONObject JSObject) {
		Map<String, PageConfig> pages = new Hashtable<String, PageConfig>();
		
		JSONArray jObj = JSObject.getJSONArray("pages");
		
		for (int objIndex = 0; objIndex< jObj.length(); objIndex++) {
			pages.put(jObj.getJSONObject(objIndex).getString("name"), new PageConfig(jObj.getJSONObject(objIndex)));
		}
		
		return pages;
		
	}
	
	/**
	 * Getter for Map of PageConfig objects
	 */
	public Map<String, PageConfig> getPageConfigMap() {
		return this.pageConfigMap;
	}
}
