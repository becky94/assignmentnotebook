package com.mousedeer.gwtassignmentnotebook.client;

import java.util.List;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.mousedeer.gwtassignmentnotebook.shared.CourseEnrollment;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface CourseraServiceAsync {
	void getCourses(String name, String pass, AsyncCallback<List<CourseEnrollment>> asyncCallback)
			throws IllegalArgumentException;
	 void getAssignments(CourseEnrollment course, AsyncCallback<CourseEnrollment> asyncCallback) throws Exception;
	
}
