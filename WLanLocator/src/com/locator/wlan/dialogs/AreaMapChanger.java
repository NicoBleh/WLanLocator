package com.locator.wlan.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.locator.wlan.AreaMap;
import com.locator.wlan.Location;
import com.locator.wlan.R;

/**
 * A Dialog to change a areamap
 * 
 * @author Nico Bleh
 */
public class AreaMapChanger extends Dialog {

	Context context;
	AreaMap aMap;
	OnMapChangeListener onMapChangeListener;
	
	/**
	 * Inner interface to store a areamap in the UI-Thread
	 */
	public interface OnMapChangeListener {
        void mapChanged(AreaMap aMap, String command);
    }
	
	/**
	 * Constructor
	 * 
	 * @param context
	 * @param aMap
	 * @param onMapChangeListener
	 */
	public AreaMapChanger(Context context, AreaMap aMap, OnMapChangeListener onMapChangeListener) {
		super(context);
		this.context = context;
		this.aMap = aMap;
		this.onMapChangeListener = onMapChangeListener;
	}
	
	/**
	 * Called when the Dialog should be displayed
	 */
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
			setContentView(R.layout.areamapchangerdialog_layout); //set the dialog layout
			setTitle(R.string.changeMap);
			
			final EditText editName = (EditText) findViewById(R.id.editName);
			final EditText editLatitude = (EditText) findViewById(R.id.editLatitude);
			final EditText editLongitude = (EditText) findViewById(R.id.editLongitude);
			final EditText editHeight = (EditText) findViewById(R.id.editHeight);
			final EditText editScale = (EditText) findViewById(R.id.editScaleFactor);
			
			editName.setText(aMap.getName());
			editLatitude.setText(Double.toString(aMap.getLocation().getLatitude()));
			editLongitude.setText(Double.toString(aMap.getLocation().getLongitude()));
			editHeight.setText(Double.toString(aMap.getLocation().getHeight()));
			editScale.setText(Double.toString(aMap.getOneMeterScaleFactor()));
			
			
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
					AreaMap newMap = new AreaMap();
					newMap.setMapOfArea(aMap.getMapOfArea());
					newMap.setName(editName.getText().toString());
					newMap.setLocation(new Location(Double.valueOf(editLatitude.getText().toString()),
							Double.valueOf(editLongitude.getText().toString()),
							Double.valueOf(editHeight.getText().toString())));
					newMap.setOneMeterScaleFactor(Double.valueOf(editScale.getText().toString()));
					
					onMapChangeListener.mapChanged(newMap, "change");
					dismiss();
				}
			});
			
			Button deleteButton = (Button) findViewById(R.id.delete);
			deleteButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onMapChangeListener.mapChanged(aMap, "delete");
					dismiss();
				}
			});
	        
	 }

}
