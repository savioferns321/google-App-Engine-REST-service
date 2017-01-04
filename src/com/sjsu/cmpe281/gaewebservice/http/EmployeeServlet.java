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
import com.sjsu.cmpe281.gaewebservice.dao.EmployeeDao;
import com.sjsu.cmpe281.gaewebservice.exceptions.DataStoreNotInitializedException;
import com.sjsu.cmpe281.gaewebservice.util.DataStoreUtil;
import com.sjsu.cmpe281.gaewebservice.util.HeaderUtil;
import com.sjsu.cmpe281.gaewebservice.util.RequestReaderUtil;

@SuppressWarnings("serial")
public class EmployeeServlet extends HttpServlet {

	private static Logger logger = Logger.getGlobal();


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//super.doGet(req, resp);

		EmployeeDao employee = null;

		try {
			int empId = RequestReaderUtil.getIdFromUrl(req);
			logger.log(Level.INFO, String.format("Received request for ID : %d", empId));
			System.out.printf("Received request for ID : %d %n", empId);
			DataStoreUtil dataStoreUtil = DataStoreUtil.getInstance();

			//Creating an empty DAO Object with only ID set.
			employee = new EmployeeDao(empId);

			Entity employeeEntity = dataStoreUtil.getEntity(employee);
			if(employeeEntity == null){
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}else{
				employee = new EmployeeDao(employeeEntity);

				//Write to body in XML or JSON as per the request: XML/JSON
				if(HeaderUtil.XML_CONTENT_TYPE.equals(req.getContentType())){
					resp.getWriter().write(XML.toString(employee.addWrapper()));
					resp.setContentType(HeaderUtil.XML_CONTENT_TYPE);
				} else {
					resp.getWriter().write(employee.addWrapper().toString(4));
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
			int empId = RequestReaderUtil.getIdFromUrl(req);
			DataStoreUtil dataStore = DataStoreUtil.getInstance();
			//Check if employee exists
			Entity employeeEntity = dataStore.getEntity(new EmployeeDao(empId));

			if(employeeEntity != null){
				//Modify the employee properties.

				JSONObject jsonObject = RequestReaderUtil.getJsonFromRequest(req);
				EmployeeDao employee = new EmployeeDao(jsonObject, true);
				employee.setValue(empId);
				dataStore.updateDao(employee);
				resp.setStatus(HttpServletResponse.SC_OK);

			}else{
				logger.log(Level.INFO, String.format("Employee with ID: %d not found", empId));
				System.out.printf("Employee with ID: %d not found %n", empId);
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
			int empId = RequestReaderUtil.getIdFromUrl(req);
			DataStoreUtil dataStore = DataStoreUtil.getInstance();
			EmployeeDao employee = new EmployeeDao(empId);
			dataStore.deleteEntity(employee);
			resp.setStatus(HttpServletResponse.SC_OK);
		} catch (NumberFormatException e) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} catch (NullPointerException e) {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} catch (DataStoreNotInitializedException e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} 
	}
/*
	//TODO Delete
	public static void main(String[] args) {
		try {
			JSONObject jsonObject = new JSONObject();
			List<JSONObject> jsonList = new ArrayList<JSONObject>();
			jsonList.add(new JSONObject().put("employee", new JSONObject().put("id",1).put("firstName", "Savio").put("lastName", "Fernandes")) );
			jsonList.add(new JSONObject().put("employee", new JSONObject().put("id",2).put("firstName", "Savio1").put("lastName", "Fernandes1")) );
			jsonObject.put("employees", jsonList);
			String jsonPrettyPrintString = jsonObject.toString(4);
			System.out.println(jsonPrettyPrintString);

			System.out.println(XML.toString(jsonObject));

			String employeeArray = "<employees><employee><id>1</id><name>Savio</name></employee>"
					+ "<employee><id>2</id><name>Fernandes</name></employee></employees>";

			String empJsn = "<employee><id>1</id><name>Savio</name></employee>";
			JSONObject finalJSON= XML.toJSONObject(employeeArray);
			System.out.println(finalJSON.toString(4));
			System.out.println(XML.toJSONObject(empJsn).toString(4));
			JSONObject temp = XML.toJSONObject(empJsn);
			System.out.println(XML.toString(temp));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

}
