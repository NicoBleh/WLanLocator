package com.locator.wlan;

import java.util.HashMap;
import java.util.TreeMap;

import com.locator.wlan.interfaces.Applicable;

/**
 * Describes an Accesspoint
 * 
 * @author Nico Bleh
 * @version 1.0
 */
public class WLanAccessPoint implements Applicable {
	
	private String ssid;
	private String mac;
	
	/**
	 * The location of the AcessPoint
	 */
	private Location location;
	
	/**
	 * TreeMap to hold pairs of signalstrenth an distances
	 */
	private TreeMap<Double, Double> signaldistancemap;
	
	/**
	 * If a distance has been calculated before it is stored in a HashMap to increase performance
	 */
	private HashMap<Double, Double> calculateddistancemap;
	
	/**
	 * Holds the coefficient-matrix for calculating distances
	 */
	private double[] coefficients;
	
	/**
	 * will be set if new values arrive
	 */
	private boolean newValues;
	
	/**
	 * Constructor
	 * 
	 * @param ssid the ssid to set
	 * @param mac the mac-Adress to set
	 */
	public WLanAccessPoint(String ssid, String mac) {
		this.ssid = ssid;
		this.mac = mac;
		signaldistancemap = new TreeMap<Double, Double>();
		calculateddistancemap = new HashMap<Double, Double>();
		newValues = false;
		location = null;
	}

	/**
	 * 
	 * @return the ssid
	 */
	public String getSSID() {
		return ssid;
	}
	
	/**
	 * 
	 * @return the mac
	 */
	public String getMAC() {
		return mac;
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * 
	 * @param location
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * Adds a new pair of signalstrenth and distance into the Accesspoint which invokes a new calculation of values
	 * 
	 * @param signalstrength
	 * @param distance
	 */
	public void setSignalDistance(double signalstrength, double distance) {
		this.signaldistancemap.put(signalstrength, distance); //Save the new signal-distance pair
		this.newValues = true;	//forces new coeffients to be build
		this.calculateddistancemap.clear(); //clears the saved distanes
	}
	
	/**
	 * 
	 * @param signalstrength
	 * @return a double value of the calculated distance to the accesspoint
	 */
	public double getDistanceFromSignalstrength(Double signalstrength) {
		
		int power = 3;
			double answer = 0;
			if(calculateddistancemap.get(signalstrength) == null) { //If no distance is saved calculate it
				//make Matrix if new signaldistancevalues are saved
				if(newValues) {
					int xIndex = 0;
					while (xIndex < signaldistancemap.size() - (1 + power + (signaldistancemap.size() - 1) % power) && (Double) signaldistancemap.keySet().toArray()[xIndex + power] < signalstrength)
					{
						xIndex += power;
					}
			 
					double matrix[][] = new double[power + 1][power + 2];
					for (int i = 0; i < power + 1; ++i)
					{
						for (int j = 0; j < power; ++j)
						{
							matrix[i][j] = Math.pow((Double) signaldistancemap.keySet().toArray()[xIndex + i], (power - j));
						}
						matrix[i][power] = 1;
						matrix[i][power + 1] = (Double) signaldistancemap.values().toArray()[xIndex + i];
					}
					coefficients = lin_solve(matrix);
					newValues = false;
				}
				
				for (int i = 0; i < coefficients.length; ++i)
				{
					answer += coefficients[i] * Math.pow(signalstrength, (power - i));
				}
				
				//Round to two digits behind point
				answer = answer * 100;
				answer = Math.rint(answer);
				answer = answer / 100;
				calculateddistancemap.put(signalstrength, answer);
			}
			else {
				answer = calculateddistancemap.get(signalstrength);
			}
			return answer;
	}

	/**
	 * Solves the matrix
	 * 
	 * @param matrix
	 * @return double array of coefficients
	 */
	private static double[] lin_solve(double[][] matrix)
	{
		double[] results = new double[matrix.length];
		int[] order = new int[matrix.length];
		for (int i = 0; i < order.length; ++i)
		{
			order[i] = i;
		}
		for (int i = 0; i < matrix.length; ++i)
		{
			// partial pivot
			int maxIndex = i;
			for (int j = i + 1; j < matrix.length; ++j)
			{
				if (Math.abs(matrix[maxIndex][i]) < Math.abs(matrix[j][i]))
				{
					maxIndex = j;
				}
			}
			if (maxIndex != i)
			{
				// swap order
				{
					int temp = order[i];
					order[i] = order[maxIndex];
					order[maxIndex] = temp;
				}
				// swap matrix
				for (int j = 0; j < matrix[0].length; ++j)
				{
					double temp = matrix[i][j];
					matrix[i][j] = matrix[maxIndex][j];
					matrix[maxIndex][j] = temp;
				}
			}
			if (Math.abs(matrix[i][i]) < 1e-15)
			{
				throw new RuntimeException("Singularity detected");
			}
			for (int j = i + 1; j < matrix.length; ++j)
			{
				double factor = matrix[j][i] / matrix[i][i];
				for (int k = i; k < matrix[0].length; ++k)
				{
					matrix[j][k] -= matrix[i][k] * factor;
				}
			}
		}
		for (int i = matrix.length - 1; i >= 0; --i)
		{
			// back substitute
			results[i] = matrix[i][matrix.length];
			for (int j = i + 1; j < matrix.length; ++j)
			{
				results[i] -= results[j] * matrix[i][j];
			}
			results[i] /= matrix[i][i];
		}
		double[] correctResults = new double[results.length];
		for (int i = 0; i < order.length; ++i)
		{
			// switch the order around back to the original order
			correctResults[order[i]] = results[i];
		}
		return results;
	}
	
	/**
	 * 
	 * @see com.locator.wlan.interfaces.Applicable#drawSelfOn(com.locator.wlan.AreaMap)
	 */
	@Override
	public AreaMap drawSelfOn(AreaMap aMap) {
		//TODO
		
		
		return aMap;
	}
}
