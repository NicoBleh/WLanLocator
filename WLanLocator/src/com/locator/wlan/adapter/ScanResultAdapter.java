/**
 * 
 */
package com.locator.wlan.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.locator.wlan.R;
import com.locator.wlan.WLanResult;

/**
 * Class to add ScanResults in an Adapter to display them in a ListView
 * 
 * @author Nico Bleh
 *
 */
public class ScanResultAdapter extends ArrayAdapter<WLanResult> {

	int textViewResourceId;
	Context context;
	private LayoutInflater inflater;
	ArrayList<WLanResult> scanlist;
	
	/**
	 * Constructor
	 * 
	 * @param context
	 * @param textViewResourceId
	 * @param scanmap
	 */
	public ScanResultAdapter(Context context, int textViewResourceId, HashMap<String, WLanResult> scanmap) {
		super(context, textViewResourceId);
		this.textViewResourceId = textViewResourceId;
		this.context = context;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //Get the Layout
		scanlist = new ArrayList<WLanResult>();
		for(WLanResult res : scanmap.values()) { //Add all ScanResults
			scanlist.add(res);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		WLanResult scanres = scanlist.get(position);
		
		if( convertView == null ){
	        convertView = inflater.inflate(textViewResourceId, null);
	    }
		
		TextView ssid = (TextView) convertView.findViewById(R.id.ssid);
		ssid.setText(scanres.getSSID());
		TextView bssid = (TextView) convertView.findViewById(R.id.mac);
		bssid.setText(scanres.getBSSID());
		TextView level = (TextView) convertView.findViewById(R.id.level);
		level.setText(Integer.toString(scanres.getLevel()));
		return convertView;
	}
	
	/**
	 * Update the Adapter with new data
	 * 
	 * @param scanmap
	 */
	public void update(HashMap<String, WLanResult> scanmap) {
		this.clear();
		this.scanlist.clear();
		for(WLanResult res : scanmap.values()) {
			this.scanlist.add(res);
			this.insert(res, 0);
		}
	}
}
