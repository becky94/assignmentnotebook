package com.mousedeer.gwtassignmentnotebook.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.google.api.client.xml.Xml;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.gson.Gson;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.mousedeer.gwtassignmentnotebook.client.CourseraService;
import com.mousedeer.gwtassignmentnotebook.shared.CourseEnrollment;
import com.mousedeer.gwtassignmentnotebook.shared.FieldVerifier;
import com.mousedeer.gwtassignmentnotebook.shared.Quiz;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class CourseraServiceImpl extends RemoteServiceServlet implements
		CourseraService {

	private String cookieString = "";
	
	/*
	 * Set to true if testing offline - will generate dummy courses
	 */
	private boolean testingOffline = false;

	/*
	 * Types of assignments to fetch - must be exact capitalization used in
	 * Coursera URL
	 */
	public enum AssignType {
		quiz, assignment
	};

	/*
	 * Login, read main Coursera courses page, and get course details
	 * 
	 * 
	 * @param coursera user name, password as entered by user
	 * 
	 * @return List of course enrollment details
	 */
	public List<CourseEnrollment> getCourses(String name, String pass)
			throws Exception {
		
		//testing offline - use dummy data
		if (testingOffline)
		{
			return generateDummyCourses();
			
		}
		
		// Verify that the input is valid.
		if (!FieldVerifier.isValidText(name)
				|| !FieldVerifier.isValidText(pass)) {
			// If the input is not valid, throw an IllegalArgumentException back
			// to
			// the client.
			throw new IllegalArgumentException(
					"Please enter user name and password!");
		}

		try {
			// login and get the user's cookie
			String cookie = loginOnPage(escapeHtml(name), escapeHtml(pass));

			// throw exception if bad login - will be displayed on page
			if (cookie == null || cookie.isEmpty()) {
				throw new Exception(
						"Login unsuccessful. Make sure you are using your Coursera email address and password.");
			}

			// get course and assignment details
			return getCoursesFromPage();

		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}

	/*
	 *  * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * 
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}



	/* log in to site directly, without APIs, to get CAUTH token 

	 * @param coursera user name, password
	 
	 */
	private String loginOnPage(String username, String password)
			throws IOException {
		
			HTTPRequest req = new HTTPRequest(new URL(
					"https://accounts.coursera.org/api/v1/login"),
					HTTPMethod.POST);
			req.getFetchOptions().setDeadline(50000.0);
			req.addHeader(new HTTPHeader("X-CSRFToken", "0"));
			req.addHeader(new HTTPHeader("Content-Type",
					"application/x-www-form-urlencoded"));
			req.addHeader(new HTTPHeader("Cookie", "csrftoken=0"));
			req.setPayload(new String("email=" + username + "&password="
					+ password + "&webrequest=true").getBytes());
			URLFetchService urlFetchService = URLFetchServiceFactory
					.getURLFetchService();
			HTTPResponse resp = urlFetchService.fetch(req);
			
			cookieString = "";
			for (HTTPHeader header : resp.getHeaders()) {
				if (header.getName().equalsIgnoreCase("Set-Cookie")) {
					cookieString += header.getValue();
				}
			}
			return cookieString;

	}

	/* Retrieve and parse Coursera courses page
	 * 
	 * @returns list of Coursera course enrollments
	 */
	@SuppressWarnings("unchecked")
	private List<CourseEnrollment> getCoursesFromPage() throws IOException,
			XmlPullParserException, ParseException {
		ArrayList<CourseEnrollment> enrollmentsList = new ArrayList<CourseEnrollment>();
		// HTTPRequest req = new HTTPRequest(new
		// URL("https://www.coursera.org/maestro/api/topic/list_my?user_id=216876"),
		// HTTPMethod.GET);
		HTTPRequest req = new HTTPRequest(new URL(
				"https://www.coursera.org/maestro/api/topic/list_my"),
				HTTPMethod.GET);
		req.addHeader(new HTTPHeader("X-CSRFToken", "0"));
		// req.addHeader(new HTTPHeader("Content-Type",
		// "application/x-www-form-urlencoded"));
		req.addHeader(new HTTPHeader("Cookie", cookieString));
		req.addHeader(new HTTPHeader("Host", "www.coursera.org"));
		req.getFetchOptions().setDeadline(50000.0);
		URLFetchService urlFetchService = URLFetchServiceFactory
				.getURLFetchService();

		HTTPResponse resp = urlFetchService.fetch(req);

		// parse course data, adding courses to enrollment list
		Gson gson = new Gson();
		Object object = new Object();
		object = gson
				.fromJson(new String(resp.getContent()), object.getClass());
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		list = (ArrayList<HashMap<String, Object>>) gson.fromJson(new String(
				resp.getContent()), list.getClass());
		ArrayList<String> urlList = new ArrayList<String>();
		for (HashMap<String, Object> i : list) {
			String courseName = i.get("name").toString();
			String courseImageUrl = i.get("photo").toString();
			//Double courseId = (Double) i.get("id");
			ArrayList<HashMap<String, Object>> enrollmentDetails = (ArrayList<HashMap<String, Object>>) i
					.get("courses");
			for (HashMap<String, Object> enrollment : enrollmentDetails) {
				boolean active = (boolean) enrollment.get("active");

				//Double enrollId = (Double) enrollment.get("id");
				String homeUrl = (String) enrollment.get("home_link");
				DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String endDateStr = (String) enrollment.get("end_date");
				Date endDate = sdf.parse("2099-01-01");
				if (endDateStr != null) {
					endDate = sdf.parse((String) enrollment.get("end_date"));
				}
				Date today = new Date();
				//Double status = (Double) enrollment.get("status");

				if (active && endDate.after(today)) {
					CourseEnrollment ce = new CourseEnrollment();
					urlList.add(homeUrl);
					ce.setHomeLink(homeUrl);
					ce.setName(courseName);
					ce.setImageUrl(courseImageUrl);
					

					enrollmentsList.add(ce);
				}
			}

		}
		return enrollmentsList;
		//

	}

	/* return list of all assignments for this course 
	 * 
	 * @param course 
	 * @returns course including list of all assignments
	 */
	public CourseEnrollment getAssignments(CourseEnrollment course)
			throws Exception {
		//if testing, skip this - assignments will be set up in dummy course
		if (testingOffline)
		{
			return course;
		}
		getQuizzes(course, AssignType.quiz.toString());
		getQuizzes(course, AssignType.assignment.toString());
		return course;

	}

	/* get assignments of the given type */
	private CourseEnrollment getQuizzes(CourseEnrollment course,
			String assignType) throws IOException, XmlPullParserException {
		// HTTPRequest req = setupGetRequest(course.getHomeLink() + "quiz");
		HTTPRequest req = setupGetRequest(course.getHomeLink()
				+ assignType.toString());

		HTTPResponse resp = setupResponse(req);

		XmlPullParser xpp = Xml.createParser();
		xpp.setInput(new StringReader(new String(resp.getContent())));
		int eventType = xpp.getEventType();
		HashMap<String, String> assignmentDetails = new HashMap<String, String>();
		boolean parsingAssignmentDetails = true;
		String strKey = "";
		String strValue = "";

		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.END_TAG) {
				if (assignmentDetails.size() >= 4)
					System.out.print(assignmentDetails.size() + " "
							+ xpp.getName());
			}
			if (eventType == XmlPullParser.START_TAG) {
				if (xpp.getName() == "li"/*
										 * && xpp.getProperty("class") ==
										 * "hidden"
										 */) {
					parsingAssignmentDetails = true;
				} else if (parsingAssignmentDetails && xpp != null
						&& xpp.getName() != null) {

					switch (xpp.getName()) {
					case "h4": // assignment name
						xpp.next();
						assignmentDetails.put(Quiz.HDR_NAME, xpp.getText());
						break;

					case "th": // assignment name

						xpp.next(); // go to the text
						strKey = xpp.getText();
						break;
					case "td":
					case "time":
						xpp.next(); // go to the text
						if (xpp.getName() == "span") {
							xpp.next();
						}
						strValue = xpp.getText();
						assignmentDetails.put(strKey.trim(), strValue.trim());

						break;
					case "a":
						if (xpp.getAttributeValue(null, "class") != null
								&& xpp.getAttributeValue(null, "class")
										.equalsIgnoreCase("btn btn-primary"))
							;
						{
							assignmentDetails.put(Quiz.HDR_URL, xpp
									.getAttributeValue(null, "href").trim());
						}

						break;

					}

				}
			} else if (assignmentDetails.size() > 0
					&& eventType == XmlPullParser.END_TAG
					&& (xpp.getName() == "li" || xpp.getName() == "table")) {

				Quiz quiz = null;
				if (assignmentDetails.get(Quiz.HDR_NAME) != null) {
					assignmentDetails.put(Quiz.HDR_TYPE, assignType.toString());
					quiz = new Quiz(assignmentDetails);
					System.out.println("Assignment: "
							+ quiz.getAssignmentName() + " Course: "
							+ course.getHomeLink());

					course.addAssignment(quiz);
					System.out.println("Assignment: "
							+ quiz.getAssignmentName() + " Course: "
							+ course.getHomeLink());
				}
				assignmentDetails = new HashMap<String, String>();

				parsingAssignmentDetails = false;
			}

			try {
				eventType = xpp.next();
			} catch (XmlPullParserException e) {
				// ignore mismatched tags
			} catch (EOFException e) {
				// stop parsing
				break;
			}

		}

		return course;
	}

	/* create a get request for Coursera using the user's cookie */
	private HTTPRequest setupGetRequest(String strUrl)
			throws MalformedURLException {
		HTTPRequest req = new HTTPRequest(new URL(strUrl), HTTPMethod.GET);
		req.addHeader(new HTTPHeader("Cookie", cookieString));
		req.addHeader(new HTTPHeader("Host", "class.coursera.org"));
		req.getFetchOptions().setDeadline(50000.0);
		return req;
	}

	/* create a URL response */
	private HTTPResponse setupResponse(HTTPRequest req) throws IOException {
		URLFetchService urlFetchService = URLFetchServiceFactory
				.getURLFetchService();
		HTTPResponse resp = urlFetchService.fetch(req);
		return resp;
	}

	//for testing - return a list of fake courses
	private List<CourseEnrollment> generateDummyCourses()
	  {
		  Quiz assignment = new Quiz();
		  assignment.setAssignmentName("Test");
		  assignment.setAssignmentUrl("http://google.com");
		  CourseEnrollment ce = new CourseEnrollment();

		  CourseEnrollment ce2 = new CourseEnrollment();
		  ce.addAssignment(assignment);
		  ce2.addAssignment(assignment);
		  ArrayList<CourseEnrollment> courses = new ArrayList<CourseEnrollment>();
		  courses.add(ce);
		  courses.add(ce2);
		  return courses;
		  
	  }
}
