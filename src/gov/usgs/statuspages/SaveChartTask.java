package gov.usgs.statuspages;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.codec.binary.Base64;

public class SaveChartTask implements Runnable{

    private static String BASEURL  = "https://hvointernal.wr.usgs.gov/valve3/valve3.jsp?";
    private static String USERPASS = "***REMOVED***:***REMOVED***";
    
    private int interval;
	private String sectionName;
    private String[] channels;
	private String filename;
	private String valveUrl;
	private String fileLocation;
	private String authString;
	
	/**
	 * Constructor.
	 * @param config - Entire config for this 'section'
	 * @param timeperiod - String version of the time period for this chart. E.G. '3d'
	 * @param channels - Channel(s) for this chart
	 * @param loc - Location to save file to
	 */
	public SaveChartTask(PageConfig config, String timeperiod, String[] channels, String loc) {
				
	    this.sectionName  = config.getName();
	    this.channels     = channels;
		this.interval     = config.getTimePeriods().get(timeperiod);
		this.valveUrl     = config.getURL();
		this.filename     = config.getFileName();
		this.fileLocation = loc;
		
		// Setup the filename and url
		createFilenameAndUrl(timeperiod);
		
		// Setup encoded auth string
		byte[] authEncBytes = Base64.encodeBase64(USERPASS.getBytes());
        authString          = new String(authEncBytes);
	}
	
	/**
	 * Method that is run for each task execution. Connects to the url, downloads and saves the
	 * resulting chart.
	 */
	public void run() {
		try {
			URL url = new URL(BASEURL + this.valveUrl);
			URLConnection urlConnection = url.openConnection();
			urlConnection.setRequestProperty("Authorization", "Basic " + this.authString);
			
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			OutputStream out = new BufferedOutputStream(new FileOutputStream(this.fileLocation + this.filename));
			
			int i;
			while ((i = in.read()) != -1) {
			    out.write(i);
			}
			
			in.close();
			out.close();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}
	
	/**
	 * Generate filename and url for use by this task.
	 * @param timePeriod - String representation of time period
	 */
	private void createFilenameAndUrl(String timePeriod) {
	    // RSAM channels behave differently
	    if (this.sectionName.equalsIgnoreCase("RSAM")) {
	        this.valveUrl = this.valveUrl.replace("CHANNEL", this.channels[0]);
            String[] chSplit = this.channels[0].split("\\$");
            String replacement = "";
            if (chSplit[0].length() == 3)
                replacement = chSplit[0] + "__" + chSplit[1];
            else
                replacement = chSplit[0] + "_" + chSplit[1];
            this.filename = this.filename.replace("CHANNEL_", replacement);
        } else {
            this.valveUrl = Util.replaceSubstr(this.valveUrl, "CHANNEL", this.channels);
            this.filename = Util.replaceSubstr(this.filename, "CHANNEL", this.channels);
        }
	    this.valveUrl = this.valveUrl.replace("STARTTIME", "" + Util.strTime(timePeriod));
        this.filename = this.filename.replace(".png", "-" + timePeriod + ".png");
	}
	
	/**
	 * Getter for interval between checks
	 */
	public int getInterval() {
	    return this.interval;
	}
	
	/**
	 * Getter for filename to save image to
	 */
	public String getFilename() {
	    return this.filename;
	}
	
	/**
	 * Getter for Valve3 url
	 */
	public String getURL() {
	    return this.valveUrl;
	}
}
