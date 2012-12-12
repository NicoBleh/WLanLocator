package com.locator.wlan;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

import com.locator.wlan.interfaces.Observer;
import com.locator.wlan.interfaces.Subject;

/**
 * Class to pull ScanResults from the devices wifi and notify its observers.
 * The Subject is started as a Async Task to no get in interference with the ui Thread.
 * 
 * @author Nico Bleh
 * @version 1.0
 *
 */
public class WLanData extends AsyncTask<Void, Void, Void> implements Subject {

	/**
	 * Fields
	 */
	private ArrayList<Observer> observerList;	//List of registered observers
	private ConnectivityManager conManager;
	private boolean run;	//Variable to control the lifecicle of the AsyncTask
	private int updateintervall;	//Time which the AsyncTask waits before pulling new scans
	private WifiManager wifiService;
	private List<ScanResult> scanlist; //Temporal list with found AccessPoints
	private List<ScanResult> oldscanlist; //Temporal list with found AccessPoints

	/**
	 * Constructor
	 */
	public WLanData(Context context, int updateintervall) {
		this.updateintervall = updateintervall;
		run = true;
		
		conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		NetworkInfo netInfo;
    	netInfo = conManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    	netInfo.getState();
    	
    	wifiService = (WifiManager) context.getSystemService(Context.WIFI_SERVICE); //Instanz des WifiManagers holen
    	oldscanlist = null;
    	
    	observerList = new ArrayList<Observer>();
	}

	/**
	 * Registers a observer at the subject
	 * 
	 * @return void
	 * @param Observer the observer to be notified
	 */
	@Override
	public void registerObserver(Observer o) {
		observerList.add(o);
	}

	/**
	 * Registers a observer at the subject
	 * 
	 * @return void
	 * @param Observer the observer who not wants to be notified any more
	 */
	@Override
	public void removeObserver(Observer o) {
		observerList.remove(o);
	}


	/**
	 * 	Notifies all the observers registered by passing the latest List of Accesspoints
	 */
	@Override
	public void notifyObservers() {
		for(Observer o : observerList) {
			o.update(scanlist);
		}
	}

	public void endTask() {
		this.run = false;
	}

	/**
	 * Gets the scan results and notifies his observers if results have changed
	 */
	@Override
	protected Void doInBackground(Void... params) {
		while(run) {
	    	//WLan-Netze scannen
	    	wifiService.startScan();
	    	//Ergebnisse holen
	    	scanlist = wifiService.getScanResults();
	    	
	    	if(!equalLists(scanlist, oldscanlist) && scanlist != null) {				
				oldscanlist = scanlist;
				this.notifyObservers();
	    	}
			try {
				Thread.sleep(updateintervall);
			} catch (InterruptedException e) {
			}
		}
		return null;
	}

	/**
	 * Determines the equalness of two schan results
	 * 
	 * @param list1
	 * @param list2
	 * @return true if the two scan results a equal. Else false
	 */
	private boolean equalLists(List<ScanResult> list1, List<ScanResult> list2) {
		if(list1 == null || list2 == null) return false;
		if(list1.size() != list2.size()) {
			return false;
		}
		else {
			for(int i=0; i < list1.size(); i++) {
				if(!list1.get(i).BSSID.equals(list2.get(i).BSSID) || list1.get(i).level != list2.get(i).level)
					return false;
			}
			return true;
		}
	}
	

}
