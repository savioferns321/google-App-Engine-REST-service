package com.sjsu.cmpe281.gaewebservice.dao;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class ProjectDao extends DaoObject{

	//private static Logger logger = Logger.getGlobal();
	private int projId;

	private String name;

	private float budget;
	
	/**
	 * Json based constructor for Project object.
	 * @param inputJson The project JSON type.
	 * @throws JSONException Thrown when invalid parameters are entered.
	 */
	public ProjectDao(JSONObject inputJson, boolean partialUpdateAllowed) throws JSONException{
		super(inputJson, partialUpdateAllowed);
		if(partialUpdateAllowed){
			name = inputJson.optString("name");
			if(inputJson.has("budget")){
				budget = Float.parseFloat(inputJson.optString("budget"));
			}
			projId = inputJson.optInt("id");
		} else{
			name = inputJson.getString("name");
			budget = Float.parseFloat(String.valueOf(inputJson.get("budget")));
			projId = inputJson.getInt("id");
		}
	}
	
	public ProjectDao(Entity inputEntity){
		super(inputEntity);
		long inputId = 0;
		try {
			name = inputEntity.getProperty("name").toString();
			budget = Float.parseFloat(inputEntity.getProperty("budget").toString());
			//projId = Math.toIntExact((Long)inputEntity.getProperty("projId"));
			inputId = Long.parseLong(String.valueOf(inputEntity.getProperty("projId")));
			if(inputId > Integer.MAX_VALUE || inputId < Integer.MIN_VALUE){
				throw new ArithmeticException(String.format("Input ID value: %d is not valid.", inputId));
			}
			projId = Integer.parseInt(String.valueOf(inputEntity.getProperty("projId")));
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (ArithmeticException e) {
			throw new ArithmeticException(String.format("Invalid value for ID: %d", inputId));
		}
	}
	
	public ProjectDao(int value){
		super(value);
		projId = value;
	}
	
	public int getProjId() {
		return projId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getBudget() {
		return budget;
	}

	public void setBudget(float budget) {
		this.budget = budget;
	}

	@Override
	public JSONObject toJson() throws JSONException {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", projId);
			jsonObject.put("name", name);
			jsonObject.put("budget", budget);
			return jsonObject;
		} catch (JSONException e) {
			throw new JSONException(String.format("Error while creating Project JSON with ID : %d", projId));

		}
	}

	@Override
	public Entity toEntity() {
		Key projKey = KeyFactory.createKey(getObjectName(), getObjectName());
		Entity projEntity = new Entity(getObjectName(), projKey);
		projEntity.setProperty("name", name);
		projEntity.setProperty("budget", budget);
		projEntity.setProperty("projId", projId);
		return projEntity;
	}

	@Override
	public String getListName() {
		return "projects";
	}

	@Override
	public String getObjectName() {
		return "project";
	}

	@Override
	public String getIdName() {
		return "projId";
	}

	@Override
	public int getIdValue() {
		return projId;
	}
	
	@Override
	public void setValue(int id) {
		projId = id;		
	}
}
