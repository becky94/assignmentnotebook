package com.mousedeer.gwtassignmentnotebook.client;

import java.io.IOException;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.mousedeer.gwtassignmentnotebook.server.CourseraServiceImpl.AssignType;
import com.mousedeer.gwtassignmentnotebook.shared.CourseEnrollment;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("coursera")
public interface CourseraService extends RemoteService {
 	List<CourseEnrollment> getCourses(String name, String pass) throws IllegalArgumentException, Exception;
 	CourseEnrollment getAssignments(CourseEnrollment course) throws Exception;
	
}
