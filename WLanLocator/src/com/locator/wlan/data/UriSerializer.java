package com.locator.wlan.data;

import java.lang.reflect.Type;

import android.net.Uri;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;


/**
 * Class to serialize JSON-Data
 */
public class UriSerializer implements JsonSerializer<Uri>{

	@Override
	public JsonElement serialize(Uri src, Type srcType,
		      JsonSerializationContext context) {
		return new JsonPrimitive(src.toString());
	}

}
