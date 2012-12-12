package com.locator.wlan.dialogs;

import java.util.HashMap;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.locator.wlan.Location;
import com.locator.wlan.R;
import com.locator.wlan.WLanAccessPoint;
import com.locator.wlan.WLanResult;
import com.locator.wlan.adapter.ScanResultAdapter;

/**
 * A Dialog to define a accesspoint
 * 
 * @author Nico Bleh
 */
public class AcesspointDefiner extends Dialog{
	
	OnAccesspointChangeListener oApCL;
	boolean changeAp;
	Location location;
	HashMap<String, WLanResult> scanmap;
	Context context;
	WLanResult selectedResult;
	private ScanResultAdapter apadapter;
	
	/**
	 * Inner interface to store a acesspoint in the UI-Thread
	 */
	public interface OnAccesspointChangeListener {
        void apChanged(WLanAccessPoint ap); //calles the method on the UI-Tread
    }
	
	/**
	 * Constructor
	 * 
	 * @param context
	 * @param oApCL
	 * @param apadapter
	 * @param location
	 * @param changeAp
	 */
	public AcesspointDefiner(Context context, OnAccesspointChangeListener oApCL, ScanResultAdapter apadapter, Location location, boolean changeAp) {
		super(context);
		this.context = context;
		this.oApCL = oApCL;
		this.changeAp = changeAp;
		this.location = location;
		this.apadapter = apadapter;
	}
	
	/**
	 * Called when the Dialog should be displayed
	 */
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
			setContentView(R.layout.accesspointdialog_layout); //set the dialog layout
			setTitle(R.string.newAcesspoint);
			
			ListView apList = (ListView) findViewById(R.id.aplist);
			apList.setAdapter((ListAdapter) apadapter);
			
			apList.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> aview, View arg1, int position, long arg3) {
						selectedResult = ((WLanResult) aview.getItemAtPosition(position));
					}
			});
			
			final EditText editLatitude = (EditText) findViewById(R.id.editLatitude);
			final EditText editLongitude = (EditText) findViewById(R.id.editLongitude);
			final EditText editHeight = (EditText) findViewById(R.id.editHeight);
			
			editLatitude.setText(Double.toString(location.getLatitude()));
			editLongitude.setText(Double.toString(location.getLongitude()));
			editHeight.setText("1.5");
			
			Button backButton = (Button) findViewById(R.id.buttonBack);
			backButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
			
			Button saveButton = (Button) findViewById(R.id.buttonSave);
			saveButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(selectedResult != null) {
						Location newLocation = new Location(Double.valueOf(editLatitude.getText().toString()),
							Double.valueOf(editLongitude.getText().toString()),
							location.getHeight() + Double.valueOf(editHeight.getText().toString()));
						
						WLanAccessPoint ap = new WLanAccessPoint(selectedResult.getSSID(), selectedResult.getBSSID());
						ap.setLocation(newLocation);
						oApCL.apChanged(ap);
						dismiss();
					}
				}
			});    
	 }
}
