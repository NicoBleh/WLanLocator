package com.locator.wlan;

import java.util.HashMap;
import java.util.List;

import com.locator.wlan.calculator.DistanceCalculator;

import android.net.wifi.ScanResult;


/**
 * Manages the stored and mapped accesspoints
 * 
 * @author Nico Bleh
 *
 */
public class WLanManager {
	
	/**
	* HashMap to store all accesspoints
	**/
	private HashMap<String, WLanAccessPoint> wLanAccessPoints;

	/**
	 *  Constructor
	 */
	public WLanManager() {
		wLanAccessPoints = new HashMap<String, WLanAccessPoint>();
	}

	/**
	 * Is called when a location needs to be stored. All accesspoint which have a set location
	 * are getting updatet with signalstrength and distance in meter from the current location. 
	 */
	public void captureLocation(Location location, List<ScanResult> scanlist, DistanceCalculator distCalc) {
		for(ScanResult scanres : scanlist) {
			WLanAccessPoint ap = wLanAccessPoints.get(scanres.BSSID);
			if(ap != null) {
				ap.setSignalDistance(scanres.level, distCalc.calculateDistance(location, ap.getLocation()));
			}
		}
		
	}
	
	/**
	 *	Gets the desired AccessPoint
	 * 
	 * @param bssid the MAC-Adress
	 * @return the accesspoint
	 */
	public WLanAccessPoint getWLanAccesspoint(String bssid) {
		return wLanAccessPoints.get(bssid);
	}
	
	/**
	 * Adds a AccessPoint to the List
	 * 
	 * @param ap the AcessPoint to be stored
	 */
	public void addWLanAccesspoint(WLanAccessPoint ap) {
		wLanAccessPoints.put(ap.getMAC(), ap);
	}

	/**
	 * Get a String array of all MAC-Adresses
	 * 
	 * @return String array with BSSID's
	 */
	public String[] getWLanAccesspointList() {
		String[] names = new String[wLanAccessPoints.size()];
		int i = 0;
		for(String set : wLanAccessPoints.keySet()) {
			names[i] = set;
			i++;
		}
		return names;
	}
	
	/**
	 * Counts the stored Accesspoints
	 * 
	 * @return count of acesspoints
	 */
	public int countWLanAccesspoints() {
		return wLanAccessPoints.size();
	}
}

