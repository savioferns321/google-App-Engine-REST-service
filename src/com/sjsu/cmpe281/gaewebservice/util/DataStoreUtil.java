package com.sjsu.cmpe281.gaewebservice.util;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.sjsu.cmpe281.gaewebservice.dao.DaoObject;
import com.sjsu.cmpe281.gaewebservice.exceptions.DataStoreNotInitializedException;
import com.sjsu.cmpe281.gaewebservice.exceptions.EntityAlreadyExistsException;

public class DataStoreUtil {

	private static DatastoreService datastoreService = null;
	private static DataStoreUtil _instance = null;
	private static Logger logger = Logger.getGlobal();
	private DataStoreUtil(){
		datastoreService = DatastoreServiceFactory.getDatastoreService();
	}

	public static void init(){
		if(_instance == null){
			_instance = new DataStoreUtil();
		}
	}

	public static DataStoreUtil getInstance() throws DataStoreNotInitializedException{
		if(_instance != null)
			return _instance;
		else
			throw new DataStoreNotInitializedException("DataStore has not been initialized");
	}

	public Key storeDao(DaoObject daoObject) throws EntityAlreadyExistsException{
		Key entityKey = null;
		if(getEntity(daoObject) == null){
			Entity daoEntity = daoObject.toEntity();
			entityKey = datastoreService.put(daoEntity);
			logger.log(Level.INFO, String.format("Entity of type: %s and ID: %d created.", daoObject.getObjectName(),
					daoObject.getIdValue()));
			System.out.printf("Entity of type: %s and ID: %d created. %n", daoObject.getObjectName(),
					daoObject.getIdValue());
		} else {
			throw new EntityAlreadyExistsException(String.format("Entity of type: %s and ID: %d already present.",
					daoObject.getObjectName(),daoObject.getIdValue()));
		}
		return entityKey;
	}

	public void updateDao(DaoObject dao) throws JSONException{
		JSONObject daoJson = dao.toJson();
		Iterator<?> jsonIterator = daoJson.keys();
		Entity result = getEntity(dao);
		if(result != null){
			while (jsonIterator.hasNext()) {
				String jsonKey = (String) jsonIterator.next();
				if(result.hasProperty(jsonKey) && !dao.getIdName().equals(jsonKey) && 
						!result.getProperty(jsonKey).equals(daoJson.get(jsonKey))){
					result.setProperty(jsonKey, daoJson.get(jsonKey));
					logger.log(Level.INFO, String.format("Updating entity(%s) property: %s from %s to %s", 
							dao.getObjectName(), jsonKey, 
							String.valueOf(result.getProperty(jsonKey)),
							String.valueOf(daoJson.get(jsonKey))));
					System.out.printf("Updating entity(%s) property: %s from %s to %s", 
							dao.getObjectName(), jsonKey, 
							String.valueOf(result.getProperty(jsonKey)),
							String.valueOf(daoJson.get(jsonKey)));
				}
			}
			deleteEntity(dao);
			datastoreService.put(result);
		}

	}

	public Entity getEntity(DaoObject dao){
		Key daoKey = KeyFactory.createKey(dao.getObjectName(), dao.getObjectName());
		Query getDaoQuery = new Query(dao.getObjectName(), daoKey).
				setAncestor(daoKey).
				setFilter(new FilterPredicate(dao.getIdName(), 
						FilterOperator.EQUAL, dao.getIdValue()));
		PreparedQuery getEntityPreparedQuery =  datastoreService.prepare(getDaoQuery);
		Entity result = getEntityPreparedQuery.asSingleEntity();
		return result;
	}

	public void deleteEntity(DaoObject dao) throws NullPointerException{
		try {
			datastoreService.delete( getEntity(dao).getKey());
		} catch (NullPointerException e) {
			logger.log(Level.WARNING, String.format("Employee with ID: %d not found.", dao.getIdValue()), e);
			System.out.printf("Employee with ID: %d not found.", dao.getIdValue());
			throw e;
		}
	}

	public List<Entity> getAllEntities(String entityName){
		Key entityKey = KeyFactory.createKey(entityName, entityName);
		Query getAllEntitiesQuery = new Query(entityName, entityKey).setAncestor(entityKey);
		PreparedQuery preparedQuery = datastoreService.prepare(getAllEntitiesQuery);
		List<Entity> outputEntities = preparedQuery.asList(FetchOptions.Builder.withDefaults());
		return outputEntities;
	}

}
