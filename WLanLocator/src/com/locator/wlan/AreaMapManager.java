package com.locator.wlan;

import java.util.ArrayList;

/**
 * Manages the AreaMaps of the Application
 * 
 * @author Nico Bleh
 */
public class AreaMapManager {

	/**
	 * List of AreaMaps
	 */
	private ArrayList<AreaMap> areaMaps;

	/**
	 * Constructor
	 */
	public AreaMapManager() {
		this.areaMaps = new ArrayList<AreaMap>();
	}
	
	/**
	 * @param amap the AreaMap to be insterted into the list of maps
	 */
	public void addAreaMap(AreaMap amap) {
		this.areaMaps.add(amap);
	}
	
	/**
	 * @param location the location to get the map from
	 * @return the AreaMap from the desired Location
	 */
	public AreaMap getAreaMapFromLocation(Location location) {
		for(AreaMap areaMap : areaMaps) {
			if(areaMap.checkIfCoordinatesAreInMap(location)) {
				return areaMap;
			}
		}
		return null;
	}

	/**
	 * @param aMapName the name of the map to get
	 * @return AreaMap from the name of the map
	 */
	public AreaMap getAreaMapFromString(String aMapName) {
		for(AreaMap areaMap : areaMaps) {
			if(areaMap.getName() == aMapName) {
				return areaMap;
			}
		}
		return null;
	}

	/**
	 * @param aMap the AreaMap to be removed from the list of maps
	 */
	public void deleteAreaMap(AreaMap aMap) {
		areaMaps.remove(aMap);
	}

	/**
	 * @param aMap the AreaMap to be changed
	 */
	public void changeAreaMap(AreaMap aMap) {
		for(int i = 0; i < areaMaps.size(); i++) {
			if(areaMaps.get(i).getMapOfArea() == aMap.getMapOfArea()) {
				areaMaps.set(i, aMap);
				break;
			}
		}
		
	}

	/**
	 * @return a String array of all the mapnames
	 */
	public String[] getNameList() {
		ArrayList<String> names = new ArrayList<String>();
		for(AreaMap amap : areaMaps) {
			names.add(amap.getName());
		}
		return  names.toArray(new String[0]);
	}
}
