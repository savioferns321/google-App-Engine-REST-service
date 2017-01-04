package com.sjsu.cmpe281.gaewebservice.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import com.google.appengine.api.datastore.Key;
import com.sjsu.cmpe281.gaewebservice.dao.EmployeeDao;
import com.sjsu.cmpe281.gaewebservice.exceptions.DataStoreNotInitializedException;
import com.sjsu.cmpe281.gaewebservice.exceptions.EntityAlreadyExistsException;
import com.sjsu.cmpe281.gaewebservice.util.DataStoreUtil;
import com.sjsu.cmpe281.gaewebservice.util.HeaderUtil;
import com.sjsu.cmpe281.gaewebservice.util.RequestReaderUtil;

@SuppressWarnings("serial")
public class EmployeeListServlet extends HttpServlet {

	private static Logger logger = Logger.getGlobal();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//super.doPost(req, resp);
		//Validate the request
		try {
			logger.log(Level.INFO, "Entered POST request method.");
			System.out.println("Entered POST request method.");
			JSONObject employeeObject = RequestReaderUtil.getJsonFromRequest(req);

			EmployeeDao employee = new EmployeeDao(employeeObject, false);

			DataStoreUtil dataStoreUtil = DataStoreUtil.getInstance();

			Key empKey = dataStoreUtil.storeDao(employee);
			logger.log(Level.INFO, "Successfully stored employee with ID "+employee.getEmpId()
			+" and empKey : "+empKey.getId());
			System.out.println("Successfully stored employee with ID "+employee.getEmpId()
			+" and empKey : "+empKey.getId());
			resp.setStatus(HttpServletResponse.SC_CREATED);
			resp.addHeader("Location", HeaderUtil.getEmployeeLocationHeader(empKey, employee));

		} catch (JSONException e) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		} catch (DataStoreNotInitializedException e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} catch (EntityAlreadyExistsException e) {
			resp.setStatus(HttpServletResponse.SC_CONFLICT);
		} finally {
			resp.getWriter().flush();
			resp.getWriter().close();			
		}
	}

	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		resp.setContentType(req.getContentType());

		try {
			DataStoreUtil dataStoreUtil = DataStoreUtil.getInstance();
			List<Entity> empEntities = dataStoreUtil.getAllEntities("employee");
			List<JSONObject> empList = new ArrayList<JSONObject>();
			JSONObject outputJsonList = new JSONObject();

			for(Entity empEntity : empEntities){
				empList.add(new EmployeeDao(empEntity).addWrapper());
			}
			
			if(empList.isEmpty()){
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}else{
				outputJsonList.put("employees", empList);
				resp.setStatus(HttpServletResponse.SC_OK);
				if(HeaderUtil.XML_CONTENT_TYPE.equals(req.getContentType())){
					resp.getWriter().write(XML.toString(outputJsonList));
					resp.setContentType(HeaderUtil.XML_CONTENT_TYPE);
				} else {
					resp.getWriter().write(outputJsonList.toString(4));
					resp.setContentType(HeaderUtil.JSON_CONTENT_TYPE);
				}
			}
			
		} catch (DataStoreNotInitializedException | JSONException e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} catch (IOException e) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	
	}


}
