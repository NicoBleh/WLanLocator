/**
 * 
 */
package com.locator.wlan.calculator;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.linear.*;

import com.locator.wlan.Location;
import com.locator.wlan.WLanAccessPoint;
import com.locator.wlan.WLanManager;
import com.locator.wlan.WLanResult;

/**
 * Calculates the Position with the "Kugelschnittverfahren"
 * 
 * @author Nico Bleh
 */
public class LocationCalculator {
	
	/**
	 * WLanManager to get the AccessPoints
	 */
	WLanManager wLanManager;
	
	/**
	 * List to hold the old ssids
	 */
	ArrayList<String> oldSSIDList;
	
	/**
	 * boolean to set true if a calculation matrix needs to be created. Only if thre a new Accesspoints in reach
	 */
	boolean anew;
	
	/**
	 * the calculation matrix
	 */
	RealMatrix ar;
	
	/**
	 * Construktor
	 */
	public LocationCalculator(WLanManager wLanManager) {
		this.wLanManager = wLanManager;
		ar = null;
		anew = true;
		oldSSIDList = new ArrayList<String>();
	}

	/**
	 *  get the Location with the provided list of acesspoints and signal strength
	 *  
	 *  @param scanmap the scanmap of AccessPoints with values
	 *  @return location
	 */
	public Location getLocation(HashMap<String, WLanResult> scanmap) {
		
		//check the scanlist
		if(scanmap == null) return null; 
		
		//get the amount of accesspoints in range
		int apcount = scanmap.size();
		
		//needs to be at least 3 accesspoints in range to calculate a position
		if(apcount < 3) { //Not calculateble
			return null;
		}
		else {
			//get all known accesspoints
			ArrayList<WLanAccessPoint> apList = new ArrayList<WLanAccessPoint>();
			WLanAccessPoint apoint;
			for(String res : scanmap.keySet()) { 
				apoint = wLanManager.getWLanAccesspoint(res); //try to find the accesspoint
				if(apoint != null) apList.add(apoint);
			}
			//Check if there are still at least 3 accesspoints for calculation
			if(apList.size() < 3) return null;
			else {
				apcount = apList.size();
				double[] b = new double[apcount]; //Create b
				
				Location aploc = null;
			
				if(apcount == oldSSIDList.size()) {
					int i = 0;
					for(WLanAccessPoint ap : apList) {
						if(ap.getSSID() != oldSSIDList.get(i)) {
							anew = true;
							break;
						}
						i++;
					}
				}
				else {
					anew = true;
				}
				if(anew) { //Build all matrices
					double[][] a = new double[apcount][4]; //Create A
					oldSSIDList.clear();
					int i = 0;
				
					for(WLanAccessPoint ap : apList) { //set matrix values
						aploc = ap.getLocation();
						a[i][0] = 1;
						a[i][1] = aploc.getLatitude() * (-2);
						a[i][2] = aploc.getLongitude() * (-2);
						a[i][3] = aploc.getHeight() * (-2);
						b[i] = Math.pow(ap.getDistanceFromSignalstrength((double) scanmap.get(ap.getMAC()).getLevel()), 2)
								- Math.pow(aploc.getLatitude(), 2) - Math.pow(aploc.getLongitude(), 2) - Math.pow(aploc.getHeight(), 2);
						i++;
						oldSSIDList.add(ap.getSSID());
					}
					
					ar = new Array2DRowRealMatrix(a);
				} else {
					 //build only b
					int i = 0;
					for(WLanAccessPoint ap : apList) { //set matrix values
						aploc = ap.getLocation();
						b[i] = Math.pow(ap.getDistanceFromSignalstrength((double) scanmap.get(ap.getMAC()).getLevel()), 2) - Math.pow(aploc.getLatitude(), 2) - Math.pow(aploc.getLongitude(), 2) - Math.pow(aploc.getHeight(), 2);
						i++;
					}
				}
				
				anew = false;
				RealVector constants = new ArrayRealVector(b, false);
					
				RealVector solution = new QRDecomposition(ar).getSolver().solve(constants);
					
				Location location = new Location( solution.getEntry(1), solution.getEntry(2), solution.getEntry(3));
					
					
				return location;
			}
		}	
	}
}
