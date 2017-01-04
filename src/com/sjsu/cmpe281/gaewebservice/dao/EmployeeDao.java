package com.sjsu.cmpe281.gaewebservice.dao;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class EmployeeDao extends DaoObject{

	//private static Logger logger = Logger.getGlobal();
	private int empId;

	private String firstName;

	private String lastName;

	/**
	 * Json based constructor for Employee object.
	 * @param inputJson The employee JSON type.
	 * @throws JSONException Thrown when invalid parameters are entered.
	 */
	public EmployeeDao(JSONObject inputJson, boolean partialUpdateAllowed) throws JSONException{
		super(inputJson, partialUpdateAllowed);
		
		if(partialUpdateAllowed){
			firstName = inputJson.optString("firstName");
			lastName = inputJson.optString("lastName");
			empId = inputJson.optInt("id");
		}else{
			firstName = inputJson.getString("firstName");
			lastName = inputJson.getString("lastName");
			empId = inputJson.getInt("id");
		}
	}

	public EmployeeDao(Entity inputEntity){
		super(inputEntity);
		long inputId = 0;
		try {
			firstName = inputEntity.getProperty("firstName").toString();
			lastName = inputEntity.getProperty("lastName").toString();
			//empId = Math.toIntExact((Long)inputEntity.getProperty("empId"));
			inputId = Long.parseLong(String.valueOf(inputEntity.getProperty("empId")));
			if(inputId > Integer.MAX_VALUE || inputId < Integer.MIN_VALUE){
				throw new ArithmeticException(String.format("Input ID value: %d is not valid.", inputId));
			}
			empId = Integer.parseInt(String.valueOf(inputEntity.getProperty("empId")));
		} catch (NullPointerException e) {
			throw e;
		} catch (ArithmeticException e) {
			throw new ArithmeticException(String.format("Invalid value for ID: %d", inputId));
		}
	}
	
	public EmployeeDao(int value){
		super(value);
		empId = value;
	}

	public int getEmpId() {
		return empId;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public JSONObject toJson() throws JSONException {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", empId);
			jsonObject.put("firstName", firstName);
			jsonObject.put("lastName", lastName);
			return jsonObject;
		} catch (JSONException e) {
			throw new JSONException(String.format("Error while creating employee JSON with ID : %d", empId));

		}
	}

	@Override
	public Entity toEntity() {
		Key empKey = KeyFactory.createKey(getObjectName(), getObjectName());
		Entity empEntity = new Entity(getObjectName(), empKey);
		empEntity.setProperty("firstName", firstName);
		empEntity.setProperty("lastName", lastName);
		empEntity.setProperty("empId", empId);
		return empEntity;
	}

	@Override
	public String getListName() {
		return "employees";
	}

	@Override
	public String getObjectName() {
		return "employee";
	}

	@Override
	public String getIdName() {
		return "empId";
	}

	@Override
	public int getIdValue() {
		return empId;
	}
	
	@Override
	public void setValue(int id) {
		empId = id;		
	}
	
}
