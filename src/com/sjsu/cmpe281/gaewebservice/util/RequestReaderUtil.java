package com.sjsu.cmpe281.gaewebservice.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;


public class RequestReaderUtil {

	/**
	 * Retrieves a JSON object from the POST data of a request.
	 * @param httpRequest The HTTP request containing JSON data.
	 * @return The required JSON object.
	 * @throws IOException Thrown on encountering error while reading the request.
	 * @throws JSONException Thrown encountering error while decoding the JSON string.
	 */
	public static JSONObject getJsonFromRequest(HttpServletRequest httpRequest) throws IOException, JSONException{
		StringBuffer jb = new StringBuffer();
		String line = null;
		JSONObject jsonObject = null;
		try {
			BufferedReader reader = httpRequest.getReader();
			while ((line = reader.readLine()) != null)
				jb.append(line);
		} catch (IOException e1) {
			throw new IOException("Error while reading request",e1);
		}
		
		//Handling XML requests
		if(HeaderUtil.XML_CONTENT_TYPE.equals(httpRequest.getContentType())){
			jsonObject = XML.toJSONObject(jb.toString());
		} else {
			jsonObject = new JSONObject(jb.toString());			
		}
		//Unwrapping the outer JSON
		if(jsonObject.has("employee")){
			jsonObject = jsonObject.getJSONObject("employee");
		}else if(jsonObject.has("project")){
			jsonObject = jsonObject.getJSONObject("project");
		}

		return jsonObject;
	}
	
	public static int getIdFromUrl(HttpServletRequest request) throws NumberFormatException{
		try {
			String reqQueryString = request.getRequestURI();
			Logger.getGlobal().log(Level.INFO, String.format("Query string: %s", reqQueryString));
			return Integer.parseInt(reqQueryString.substring(reqQueryString.lastIndexOf("/")+1));
		} catch (NumberFormatException e) {
			throw e;
		}

	}
	
	
}
