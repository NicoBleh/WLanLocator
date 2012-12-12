package com.locator.wlan;

/**
 * Class to hold the latitude, longitude and height 
 * 
 * @author Nico Bleh
 * @version 1.0
 */
public class Location {
	
	private double latitude, longitude, height;
	
	/**
	 * Constructor
	 * 
	 * @param latitude
	 * @param longitude
	 * @param height
	 */
	public Location(double latitude, double longitude, double height) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.height = height;
	}
	
	/**
	 * 
	 * @return latitude
	 */
	public double getLatitude() {
		return this.latitude;
	}
	
	/**
	 * 
	 * @return longitude
	 */
	public double getLongitude() {
		return this.longitude;
	}
	
	/**
	 * 
	 * @return height
	 */
	public double getHeight() {
		return this.height;
	}
}
