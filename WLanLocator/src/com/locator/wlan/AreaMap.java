package com.locator.wlan;

import android.net.Uri;

/**
 * Describes a Areamap
 * 
 * @author Nico Bleh
 */
public class AreaMap{

	/**
	 * The location of the map is set by a Location variable. x/y 0/0 is supposed to be the location point
	 */
	private Location location;
	
	/**
	 * Needs to be set if the map is not orientated to north by 0°
	 */
	private double degreToNorth;
	
	/**
	 * 	The Factor needs to be set to determine the distance between points on the map.
	 */
	private double oneMeterScaleFactor;
	
	/**
	 * Path of the Bitmap on the Device
	 */
	private Uri mapOfArea;
	
	/**
	 * Name of the Map
	 */
	private String name;
	
	/**
	 * The maximum x value
	 */
	private int maxx;
	
	/**
	 * The maximum y value
	 */
	private int maxy;
	
	/**
	 * The maximum x/y koordinates
	 */
	private double[] maxkoordinates;

	/**
	 * Constructor
	 */
	public AreaMap() {
		//for savety, set standard values
		location = null;
		degreToNorth = 0;
		oneMeterScaleFactor = 39.4;
		this.name = "";
		maxkoordinates = null;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(Location location)
	{
		this.location = location;
	}

	/**
	 * @return the location
	 */
	public Location getLocation()
	{
		return location;
	}

	/**
	 * @param location the location to check if in this map
	 * @return true if location is in this map else false
	 */
	public boolean checkIfCoordinatesAreInMap(Location location)
	{
		if(maxkoordinates == null) {
			maxkoordinates = this.getKoordinatesFromXYPosition(maxx, maxy);
		}
		if(this.location.getLatitude() < location.getLatitude() && location.getLatitude() < maxkoordinates[0] 
				&& this.location.getLongitude() < location.getLongitude() && location.getLongitude() < maxkoordinates[1] 
						&& this.location.getHeight() < location.getHeight() && location.getHeight() < (this.location.getHeight() + 2.5))
			return true;
		return false;
	}

	/**
	 * @return the degreToNorth
	 */
	public double getDegreToNorth() {
		return degreToNorth;
	}

	/**
	 * @param degreToNorth the degreToNorth to set
	 */
	public void setDegreToNorth(double degreToNorth) {
		this.degreToNorth = degreToNorth;
	}

	/**
	 * @return the oneMeterScaleFactor
	 */
	public double getOneMeterScaleFactor() {
		return oneMeterScaleFactor;
	}

	/**
	 * Defines how many pixels one meter is.
	 * 
	 * @param oneMeterScaleFactor the oneMeterScaleFactor to set
	 */
	public void setOneMeterScaleFactor(double oneMeterScaleFactor) {
		this.oneMeterScaleFactor = oneMeterScaleFactor;
	}

	/**
	 * @return the mapOfArea
	 */
	public Uri getMapOfArea() {
		return mapOfArea;
	}

	/**
	 * @param mapOfArea the mapOfArea to set
	 */
	public void setMapOfArea(Uri mapOfArea) {
		this.mapOfArea = mapOfArea;
	}

	/**
	 * @return the koordinates of the specified x an y values on the areamap. Returns null if the location of map have not been set.
	 */
	public double [] getKoordinatesFromXYPosition(double x, double y) {
		double lati;
		double longi;
		double [] ret = null;
		
		if(this.location.getLatitude() > 0 && this.location.getLongitude() > 0 ) {
			
			lati = this.location.getLatitude() + (180 / Math.PI) * (y / oneMeterScaleFactor / 6378137);
			
			
			//longi = this.location.getLongitude() + (180 / Math.PI) * (x / oneMeterScaleFactor / 6378137) / Math.cos(this.location.getLatitude());
			longi = (x * (180 / Math.PI) / oneMeterScaleFactor / 6378137 / Math.cos(this.location.getLatitude())) + this.location.getLongitude();
			ret = new double[2];
			ret[0] = lati;
			ret[1] = longi;
			
		}
		return ret;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param location the location to get the x/y pair for
	 * @return the x/y pair of the desired location
	 */
	public float[] getxyFromLocation(Location location) {
		float[] xy = new float[2];
		xy[0] = 0;
		xy[1] = 0;
		
		xy[0] = (float) ((-location.getLongitude() + this.location.getLongitude()) / (180 / Math.PI) * oneMeterScaleFactor * 6378137 * Math.cos(this.location.getLatitude()));
		xy[1] = (float) ((-location.getLatitude() + this.location.getLatitude()) / (180 / Math.PI) * oneMeterScaleFactor * 6378137); 
		
		return xy;
	}
	
	/**
	 * @param maxx the maxx to set
	 */
	public void setMaxx(int maxx) {
		this.maxx = maxx;
	}
	
	/**
	 * @param maxy the maxy to set
	 */
	public void setMaxy(int maxy) {
		this.maxy = maxy;
	}
}
