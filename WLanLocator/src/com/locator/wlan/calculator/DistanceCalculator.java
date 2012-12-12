package com.locator.wlan.calculator;

import com.locator.wlan.Location;

/**
 * Calculates the Distance in meter between two locations
 * 
 * @author Nico Bleh
 * @version 1.0
 */
public class DistanceCalculator {
	
	/**
	 *  calculates the distance beween 2 locations
	 *  
	 *  @return double distance in m
	 */
	public double calculateDistance(Location a, Location b) {
		double distance = 0.0; //init
		
		//Pytagoras c²= a² + b²
		distance = Math.sqrt( Math.pow((b.getLatitude()-a.getLatitude()), 2)
					+ Math.pow((b.getLongitude()-a.getLongitude()), 2)
					+ Math.pow((b.getHeight()-a.getHeight()), 2) );
		
		return distance;
	}

}
