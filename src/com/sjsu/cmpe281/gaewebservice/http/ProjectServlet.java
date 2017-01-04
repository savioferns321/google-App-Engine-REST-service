package com.sjsu.cmpe281.gaewebservice.http;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.google.appengine.api.datastore.Entity;
import com.sjsu.cmpe281.gaewebservice.dao.ProjectDao;
import com.sjsu.cmpe281.gaewebservice.exceptions.DataStoreNotInitializedException;
import com.sjsu.cmpe281.gaewebservice.util.DataStoreUtil;
import com.sjsu.cmpe281.gaewebservice.util.HeaderUtil;
import com.sjsu.cmpe281.gaewebservice.util.RequestReaderUtil;

@SuppressWarnings("serial")
public class ProjectServlet extends HttpServlet {

	private static Logger logger = Logger.getGlobal();


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//super.doGet(req, resp);

		ProjectDao project = null;

		try {
			int projId = RequestReaderUtil.getIdFromUrl(req);
			logger.log(Level.INFO, String.format("Received request for ID : %d", projId));
			System.out.printf("Received request for ID : %d %n", projId);
			DataStoreUtil dataStoreUtil = DataStoreUtil.getInstance();

			//Creating an empty DAO Object with only ID set.
			project = new ProjectDao(projId);

			Entity projectEntity = dataStoreUtil.getEntity(project);
			if(projectEntity == null){
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}else{
				project = new ProjectDao(projectEntity);

				//Write to body in XML or JSON as per the request: XML/JSON
				if(HeaderUtil.XML_CONTENT_TYPE.equals(req.getContentType())){
					resp.getWriter().write(XML.toString(project.addWrapper()));
					resp.setContentType(HeaderUtil.XML_CONTENT_TYPE);
				} else {
					resp.getWriter().write(project.addWrapper().toString(4));
					resp.setContentType(HeaderUtil.JSON_CONTENT_TYPE);
				}
				resp.setStatus(HttpServletResponse.SC_OK);				
			}

		} catch (NumberFormatException e) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} catch (DataStoreNotInitializedException e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} catch (JSONException e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} finally {
			resp.getWriter().flush();
			resp.getWriter().close();
		}
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//super.doPut(req, resp);

		try {
			int projId = RequestReaderUtil.getIdFromUrl(req);
			DataStoreUtil dataStore = DataStoreUtil.getInstance();
			//Check if project exists
			Entity projectEntity = dataStore.getEntity(new ProjectDao(projId));

			if(projectEntity != null){
				//Modify the project properties.

				JSONObject jsonObject = RequestReaderUtil.getJsonFromRequest(req);
				ProjectDao project = new ProjectDao(jsonObject, true);
				project.setValue(projId);
				dataStore.updateDao(project);
				resp.setStatus(HttpServletResponse.SC_OK);

			}else{
				logger.log(Level.INFO, String.format("Project with ID: %d not found", projId));
				System.out.printf("Project with ID: %d not found %n", projId);
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}

		} catch (NumberFormatException | JSONException e ) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			logger.log(Level.WARNING, String.format("Bad request received with URL: %s", req.getRequestURL()),
					e);
			System.out.printf("Bad request received with URL: %s %n", req.getRequestURL());
		} catch (DataStoreNotInitializedException e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			logger.log(Level.WARNING, "Datastore not intialized",
					e);
			System.out.println("Datastore not intialized");
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//super.doDelete(req, resp);
		try {
			int projId = RequestReaderUtil.getIdFromUrl(req);
			DataStoreUtil dataStore = DataStoreUtil.getInstance();
			ProjectDao project = new ProjectDao(projId);
			dataStore.deleteEntity(project);
			resp.setStatus(HttpServletResponse.SC_OK);
		} catch (NumberFormatException e) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} catch (NullPointerException e) {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} catch (DataStoreNotInitializedException e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} 
	}
}
