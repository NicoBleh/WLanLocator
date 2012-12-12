package com.locator.wlan;

/**
 * Due that ScanReult is not generatable for testing a suitable class has been implemented
 * It only holds basic ScanResult data
 * 
 * @author Nico Bleh
 *
 */
public class WLanResult {
	private String SSID;
	private String BSSID;
	private int level;
	
	/**
	 * Constructor
	 * 
	 * @param SSID
	 * @param BSSID
	 * @param level
	 */
	public WLanResult(String SSID, String BSSID, int level) {
		this.SSID = SSID;
		this.BSSID = BSSID;
		this.level = level;
	}

	/**
	 * 
	 * @return SSID
	 */
	public String getSSID() {
		return SSID;
	}

	/**
	 * 
	 * @return BSSID or MAC
	 */
	public String getBSSID() {
		return BSSID;
	}

	/**
	 * 
	 * @return level
	 */
	public int getLevel() {
		return level;
	}
}
