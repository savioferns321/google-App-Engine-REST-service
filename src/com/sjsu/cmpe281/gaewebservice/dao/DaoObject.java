package com.sjsu.cmpe281.gaewebservice.dao;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.Entity;

public abstract class DaoObject {

	protected DaoObject(JSONObject json, boolean isPartialUpdateAllowed){};

	protected DaoObject(Entity entity){};

	protected DaoObject(int id){};

	public abstract JSONObject toJson() throws JSONException;

	public abstract Entity toEntity();

	public abstract String getListName();

	public abstract String getObjectName();

	public abstract String getIdName();

	public abstract int getIdValue();
	
	public abstract void setValue(int id);

	public JSONObject addWrapper() throws JSONException{
		JSONObject outputJson = new JSONObject();
		if(!outputJson.has(getObjectName())){
			outputJson.put(getObjectName(), toJson());
		}
		return outputJson;
	}

	public JSONObject removeWrapper() throws JSONException {
		JSONObject outputJson = toJson();
		if(outputJson.has(getObjectName())){
			outputJson = outputJson.getJSONObject(getObjectName());
		}
		return outputJson;
	}
}
