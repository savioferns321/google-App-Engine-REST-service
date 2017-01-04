package com.sjsu.cmpe281.gaewebservice.util;

import com.google.appengine.api.datastore.Key;
import com.sjsu.cmpe281.gaewebservice.dao.EmployeeDao;
import com.sjsu.cmpe281.gaewebservice.dao.ProjectDao;

public class HeaderUtil {
	
	public static final String XML_CONTENT_TYPE = "application/xml";
	public static final String JSON_CONTENT_TYPE = "application/json";
	

	public static String getEmployeeLocationHeader(Key key, EmployeeDao employee){
		return String.format("http://%s.appspot.com/cmpe281SavioFernandes480/rest/employee/%d", 
				key.getAppId().substring(2), employee.getEmpId());
	}
	
	public static String getProjectLocationHeader(Key key, ProjectDao project){
		return String.format("http://%s.appspot.com/cmpe281SavioFernandes480/rest/project/%d", 
				key.getAppId().substring(2), project.getProjId());
	}
}
