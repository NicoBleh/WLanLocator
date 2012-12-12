package com.locator.wlan;

import com.locator.wlan.interfaces.Applicable;

/**
 * Holds the current location of the calculated position
 * 
 * @author Nico Bleh
 */
public class CurrentPosition implements Applicable {
	
	/**
	 * The location to hold
	 */
	private Location location;
	
	/**
	 * Constructor
	 */
	public CurrentPosition() {
		location = null;
	}


	/**
	 * @return the Location
	 */
	public Location getLocation() {
		return location;
	}


	/**
	 * @param location the location to set
	 */
	public void setLocation(Location location) {
		this.location = location;
	}


	/**
	 * @param aMap the AreaMap to draw the current position onto
	 * @return the AreaMap
	 */
	@Override
	public AreaMap drawSelfOn(AreaMap aMap) {
		//TODO Draw something on the map with the current location
		
		return aMap;
	}
}
