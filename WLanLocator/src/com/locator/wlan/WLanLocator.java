package com.locator.wlan;

import com.locator.wlan.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.view.MotionEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.locator.wlan.adapter.ScanResultAdapter;
import com.locator.wlan.calculator.LocationCalculator;
import com.locator.wlan.data.UriDeserializer;
import com.locator.wlan.data.UriSerializer;
import com.locator.wlan.dialogs.AcesspointDefiner;
import com.locator.wlan.dialogs.AreaMapChanger;
import com.locator.wlan.dialogs.AreaMapDefiner;
import com.locator.wlan.graphics.TouchView;
import com.locator.wlan.interfaces.Observer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.*;


/**
 * Main Activity of the Application
 * 
 * @author Nico Bleh
 *
 */
public class WLanLocator <MyActivity> extends Activity implements Observer {
	
	//For intent to pick a picture
	private static final int SELECT_PICTURE = 1;
	
	private Context context;
	private HashMap<String, WLanResult> scanmap;
	
	//The view to hold the map
	private TouchView touchView;
	
	//the AsyncThread to pull ScanResults
	private WLanData wLanData;
	
	//Manages AccessPoints
	private WLanManager wLanManager;
	
	//Manages AreaMaps
	private AreaMapManager areaMapManager;
	
	//Calculates locations
	private LocationCalculator locationCalculator;
	
	//Holds the current position
	private CurrentPosition currentPosition;
	
	//Adapter to display ScanResults on a Listview
	ScanResultAdapter apadapter;
	
	//The ListView to show ScanResults
	ListView apList;

	//The name of the last selected map
	private String selectedMap;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    /** Called when the activity is first created. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	//Inflate menue
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    
    /** Called when the activity starts */
    @Override
    protected void onStart() {
        super.onStart();
        context = this;
        selectedMap = "";
        
        
        Gson gson = new GsonBuilder()
        .registerTypeAdapter(Uri.class, new UriSerializer())
        .registerTypeAdapter(Uri.class, new UriDeserializer())
        .create();
        
        //Restore data
        try {
	        ObjectInputStream in = new ObjectInputStream(openFileInput("saveData"));
	        wLanManager = gson.fromJson( (String) in.readObject(), WLanManager.class);
	        areaMapManager = gson.fromJson((String) in.readObject(), AreaMapManager.class);
	        in.close();
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    	
        if(wLanManager == null) wLanManager = new WLanManager();
        if(areaMapManager == null)  areaMapManager = new AreaMapManager();
        
        locationCalculator = new LocationCalculator(wLanManager);
		currentPosition = new CurrentPosition();
		
        this.position();
    }
    
    /** Called when the activity resumes. */
    @Override
    protected void onResume() {
        super.onResume();
        startWLanScanning();
		scanmap = new HashMap<String, WLanResult>();
    }
    
    /** Called when the activity is paused. */
    @Override
    protected void onPause() {
        super.onPause();
        stopWLanScanning();
        
        Gson gson = new GsonBuilder()
        .registerTypeAdapter(Uri.class, new UriSerializer())
        .registerTypeAdapter(Uri.class, new UriDeserializer())
        .create();
        
        String jsonWLanManager = gson.toJson(wLanManager);
        String jsonAreaMapManager = gson.toJson(areaMapManager);
        
        //Saving state with serializing and storeing objects
        // Save the JSONObject
        ObjectOutput out;
		try {
			out = new ObjectOutputStream(openFileOutput("saveData", Context.MODE_PRIVATE));
			out.writeObject( jsonWLanManager );
			out.writeObject( jsonAreaMapManager );
	        out.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    }
    
  
    /**
     * Called when menuItem is selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
    	int itemId = item.getItemId(); //saving selection
        switch (itemId) {
        case R.id.position:
        	position();
            return true;
        case R.id.manageMaps:
        	manageMaps();
            return true;
        case R.id.manageAccessPoints:
        	manageAccesspoints();
            return true;
        case R.id.loadSave:
        	loadsave();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    /**
     * Called when the load-save screen should be displayed
     */
	private void loadsave() {
		setContentView(R.layout.loadsave_layout);
	}

	/**
	 * Called when the user wants to display his position
	 */
	private void position() {
		setContentView(R.layout.location_layout); //set the main layout
		
		//Get the buttons
		Button centerLocationButton = (Button) findViewById(R.id.centerLocation);
		Button showApsButton = (Button) findViewById(R.id.showAccesspoints);
		Button showhintButton = (Button) findViewById(R.id.hints);
		Button showDataButton = (Button) findViewById(R.id.showData);
		
		//Get other elements
		TextView errorText = (TextView) findViewById(R.id.errorText);
		touchView = (TouchView) findViewById(R.id.areaMap);
		
		//get the Map of the current position
		if(currentPosition.getLocation() != null) { //only if there is a current position
			final AreaMap am = areaMapManager.getAreaMapFromLocation(currentPosition.getLocation());
			if(am != null) { //If there is a map to the current position try to display it
				try {
					touchView.setPicture(createBitmap(am.getMapOfArea()));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				errorText.setVisibility(2); //set the TextView gone
				
				//activate button behaviour
				centerLocationButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						touchView.setLocation(am.getxyFromLocation(currentPosition.getLocation()));
					}
				});
				
				//TODO Buttons einbauen
				
			}
			else { //Display failure text
				errorText.setText(R.string.noMap);
			}
		}
		else { //Display failure text
			errorText.setText(R.string.noPosition);
		}	
	}

	
	/**
	 * Called when the accesspoint Management should be displayed
	 */
	private void manageAccesspoints() {
		setContentView(R.layout.manageaccesspoints_layout);
		touchView = (TouchView) findViewById(R.id.areaMap);
		
		showMapList();
		
		apList = (ListView) findViewById(R.id.accesspointList);
		if(apadapter != null) apList.setAdapter(apadapter);
		
		if(wLanManager.countWLanAccesspoints() > 0) {
			ListView mappedAps = (ListView) findViewById(R.id.mappedaccesspoints);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, wLanManager.getWLanAccesspointList());
			mappedAps.setAdapter(adapter);
			mappedAps.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> res, View arg1,	int position, long arg3) {
					String bssid = (String) res.getItemAtPosition(position);
					WLanAccessPoint ap = wLanManager.getWLanAccesspoint(bssid);
					 AreaMap am = areaMapManager.getAreaMapFromLocation(ap.getLocation());
						if(am != null) { //If there is a map to the current position try to display it
							float[] xy = am.getxyFromLocation(ap.getLocation());
							try {
								touchView.drawPoint(xy);
								touchView.setPicture(createBitmap(am.getMapOfArea()));
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
					touchView.setLocation(xy);
						}
				}
			});
			
		}
		
		Button newApButton = (Button) findViewById(R.id.newAccesspoint);
			newApButton.setOnClickListener(new View.OnClickListener() {
	
				@Override
				public void onClick(View v) {
					
					touchView.setOnTouchListener(new OnTouchListener() {
						
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							double touchedX = event.getX();
							double touchedY = event.getY();
							double currentMapX = touchView.getPictureX() * (-1);
							double currentMapY = touchView.getPictureY() * (-1);
							AreaMap am = areaMapManager.getAreaMapFromString(selectedMap);
							double[] touchedXY = am.getKoordinatesFromXYPosition(touchedX + currentMapX, touchedY +currentMapY);
							
							final Location touchedLocation = new Location(touchedXY[0], touchedXY[1], am.getLocation().getHeight());
							
							AcesspointDefiner ad = new AcesspointDefiner(context, new AcesspointDefiner.OnAccesspointChangeListener() { //Listen for AP definition
								
								@Override
								public void apChanged(WLanAccessPoint ap) {
										wLanManager.addWLanAccesspoint(ap);
								}
							}, apadapter, touchedLocation, false);
							ad.show();
							touchView.setOnTouchListener(null);
							return false;
						}
					});
				}
			});
	}
	
	
	/**
	 * 	Called when the MapList should be displayed
	 */
	private void showMapList() {
		ListView mapList = (ListView) findViewById(R.id.mapList);
		String[] maplist = areaMapManager.getNameList();
		if(maplist.length > 0) {
			ArrayAdapter<String> mapAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, maplist);
			mapList.setAdapter(mapAdapter);
			mapList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,	int arg2, long arg3) {
					String aMapName = (String) arg0.getItemAtPosition(arg2);
					//Save the current mapname fpr use in map management
					selectedMap = aMapName;
					AreaMap am = areaMapManager.getAreaMapFromString(aMapName);
					try {
						Bitmap mappic = createBitmap(am.getMapOfArea());
						touchView.setPicture(mappic);
						am.setMaxx(mappic.getWidth());
						am.setMaxy(mappic.getHeight());
						touchView.reset();
						touchView.noDrawPoint();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
			});
		}
	}

	
	/**
	 * 	Called when the User wants to manage the maps
	 */
	private void manageMaps() {
		setContentView(R.layout.managemaps_layout);
		touchView = (TouchView) findViewById(R.id.areaMap);
		
		Button newMapButton = (Button) findViewById(R.id.newMap);
		newMapButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Select picture from gallery
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setType("image/*");
				startActivityForResult(intent, SELECT_PICTURE);
			}
		});
		
		Button editMapButton = (Button) findViewById(R.id.changeMap);
		editMapButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new AreaMapChanger(context, areaMapManager.getAreaMapFromString(selectedMap), new AreaMapChanger.OnMapChangeListener() {
					
					@Override
					public void mapChanged(AreaMap aMap, String command) {
						if(command == "delete") {
							areaMapManager.deleteAreaMap(aMap);
						}
						if(command == "change") {
							areaMapManager.changeAreaMap(aMap);
						}
						manageMaps();
					}
				}).show(); 
			}
		});
		
		
		//Show List of all maps
		showMapList();
		
	}
	
	/** Called when a picture has been selected. */
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
            	
                Uri selectedImageUri = data.getData();
				AreaMapDefiner amd = new AreaMapDefiner(context, selectedImageUri, new AreaMapDefiner.OnMapChangeListener() {
					
					@Override
					public void mapChanged(AreaMap aMap) {
						areaMapManager.addAreaMap(aMap);
						manageMaps();
					}
				});
				amd.show();
            }
        }
    }

	/** Called when the activity is notified by the subject. */
	@Override
	public void update(final List<ScanResult> scanlist) {
		this.scanmap.clear();
		for(ScanResult res : scanlist) {
			this.scanmap.put(res.BSSID, new WLanResult(res.SSID, res.BSSID, res.level));
		}
		this.currentPosition.setLocation(this.locationCalculator.getLocation(this.scanmap));
		
		if(apadapter == null) apadapter = new ScanResultAdapter(context, R.layout.scanlistitem_layout, scanmap);
		else {
			Runnable run = new Runnable(){
				@Override
				public void run(){
					apadapter.update(scanmap);
					apadapter.notifyDataSetChanged();
				}
			};
			this.runOnUiThread(run);
		}
	}

	/**
	 * Method to start the scanning for accesspoints and values
	 */
	private void startWLanScanning() {
		wLanData = new WLanData(this, 1000);
        wLanData.registerObserver(this);
		wLanData.execute();
	}
	
	/**
	 * Method to stop the scanning
	 */
	private void stopWLanScanning() {
		wLanData.endTask();
	}
	
	/**
	 * Create bitmap from uri
	 * 
	 * @param uri
	 * @return bitmap
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Bitmap createBitmap(Uri uri) throws FileNotFoundException, IOException {
		InputStream input = this.getContentResolver().openInputStream(uri);
		BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
		onlyBoundsOptions.inJustDecodeBounds = true;
		onlyBoundsOptions.inDither=true;//optional
		onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
		BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
		input.close();
		if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))    
			return null;
		int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
		int THUMBNAIL_SIZE = display.getHeight() * 2;
		double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
		bitmapOptions.inDither=true;//optional
		bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
		input = this.getContentResolver().openInputStream(uri);
		Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
		input.close();
		return bitmap;     
		}      
	
	/**
	 * Get the power of two
	 * 
	 * @param ratio
	 * @return 
	 */
	private static int getPowerOfTwoForSampleRatio(double ratio){
		int k = Integer.highestOneBit((int)Math.floor(ratio));
		if(k==0) return 1;
		else return k;
	} 
	
}
