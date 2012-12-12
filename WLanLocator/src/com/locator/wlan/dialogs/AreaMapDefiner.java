package com.locator.wlan.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.locator.wlan.R;
import com.locator.wlan.AreaMap;
import com.locator.wlan.Location;

/**
 * A Dialog to define a areamap
 * 
 * @author Nico Bleh
 */
public class AreaMapDefiner extends Dialog{
	
	Uri map;
	Context context;
	OnMapChangeListener onMapChangeListener;
	
	/**
	 * Inner interface to store a areamap in the UI-Thread
	 */
	public interface OnMapChangeListener {
        void mapChanged(AreaMap aMap);
    }
	
	/**
	 * Constructor
	 * 
	 * @param context
	 * @param map
	 * @param onMapChangeListener
	 */
	public AreaMapDefiner(Context context, Uri map, OnMapChangeListener onMapChangeListener) {
		super(context);
		this.context = context;
		this.map = map;
		this.onMapChangeListener = onMapChangeListener;
	}
	
	/**
	 * Called when the Dialog should be displayed
	 */
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
			setContentView(R.layout.areamapdefinedialog_layout); //set the dialog layout
			setTitle(R.string.newMap);
			
			final EditText editName = (EditText) findViewById(R.id.editName);
			final EditText editLatitude = (EditText) findViewById(R.id.editLatitude);
			final EditText editLongitude = (EditText) findViewById(R.id.editLongitude);
			final EditText editHeight = (EditText) findViewById(R.id.editHeight);
			final EditText editScale = (EditText) findViewById(R.id.editScaleFactor);
			
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
					AreaMap aMap = new AreaMap();
					aMap.setMapOfArea(map);
					aMap.setName(editName.getText().toString());
					aMap.setLocation(new Location(Double.valueOf(editLatitude.getText().toString()),
							Double.valueOf(editLongitude.getText().toString()),
							Double.valueOf(editHeight.getText().toString())));
					aMap.setOneMeterScaleFactor(Double.valueOf(editScale.getText().toString()));
					
					onMapChangeListener.mapChanged(aMap);
					dismiss();
				}
			});
	        
	 }

}
