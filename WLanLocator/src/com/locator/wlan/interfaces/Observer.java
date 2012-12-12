package com.locator.wlan.interfaces;

import java.util.List;
import android.net.wifi.ScanResult;

/**
 * Interface to discribe a observer in a observer pattern.
 *
 * @author Nico Bleh
 * @version 1.0
 */
public interface Observer {

	/**
	 * 
	 * @param scanlist the List of ScanResults to update
	 */
	void update(List<ScanResult> scanlist);
}
