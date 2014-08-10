package com.mousedeer.gwtassignmentnotebook.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class CourseEnrollment implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5495375427559689994L;
	private int id;
	private String homeLink;
	private boolean isSigTrack;
	private Date startDate;
	private Date endDate;
	//private enum status{ Past, Present, Future};
	private String startStatus;
	private ArrayList<Quiz> assignmentList = new ArrayList<Quiz>();
	private int sessionId;
	private int courseId;
	
	
	public ArrayList<Quiz> getAssignmentList() {
		return assignmentList;
	}

	public void setAssignmentList(ArrayList<Quiz> assignmentList) {
		this.assignmentList = assignmentList;
	}

	public CourseEnrollment()
	{
		//everything stays null
	}
	
	public CourseEnrollment(Map<String, Object> map){
		this.id = ((Double)map.get("id")).intValue();
		this.sessionId = ((Double)map.get("sessionId")).intValue();
		this.courseId = ((Double)map.get("courseId")).intValue();
		this.isSigTrack = (Boolean)map.get("isSigTrack");
		//this.startDate = new Date(Long.parseLong(map.get("startDate")));
		//this.endDate = new Date(Long.parseLong(map.get("endDate")));
		this.startStatus = String.valueOf(map.get("startStatus"));
	}
	
	public void addAssignment(Quiz quiz)
	{
		assignmentList.add(quiz);
	}
	public int getId() {
		return id;
	}
	
	public String getHomeLink() {
		return homeLink;
	}
	
	public void setHomeLink(String homeLink) {
		this.homeLink = homeLink;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}

	public CourseEnrollment(int enrollmentId, int sessionId,
			int courseId, boolean isSigTrack, Date startDate, Date endDate,
			String startStatus) {
		super();
		this.id = enrollmentId;
		this.sessionId = sessionId;
		this.courseId = courseId;
		this.isSigTrack = isSigTrack;
		this.startDate = startDate;
		this.endDate = endDate;
		this.startStatus = startStatus;
	}

	public String getStartStatus() {
		return startStatus;
	}

	public void setStartStatus(String startStatus) {
		this.startStatus = startStatus;
	}


	public boolean isSigTrack() {
		return isSigTrack;
	}

	public void setSigTrack(boolean isSigTrack) {
		this.isSigTrack = isSigTrack;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
