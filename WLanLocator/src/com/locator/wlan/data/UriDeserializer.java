package com.locator.wlan.data;

import android.net.Uri;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

/**
 * Class to deserialize JSON-Data
 */
public class UriDeserializer implements JsonDeserializer<Uri> {
	  @Override
	  public Uri deserialize(final JsonElement src, final Type srcType,
	      final JsonDeserializationContext context) throws JsonParseException {
	    return Uri.parse(src.getAsString());
	  }
}
