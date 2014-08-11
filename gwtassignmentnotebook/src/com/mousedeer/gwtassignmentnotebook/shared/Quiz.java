package com.mousedeer.gwtassignmentnotebook.shared;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;



public class Quiz implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4912254717137581013L;
	private Date dateHardDeadline;
	private Double dblEffectiveScore;
	private Double dblpossibleScore;
	private int intNumAttempts;
	private int intPossibleAttempts;
	private Date dateLastAttempted;
	private Date dueDate;
	private String assignmentName;
	private String assignmentType;
	private String assignmentUrl;
	
	public String getAssignmentName() {
		return assignmentName;
	}
	public void setAssignmentName(String assignmentName) {
		this.assignmentName = assignmentName;
	}
	
	public String getAssignmentType() {
		return assignmentType;
	}
	public void setAssignmentType(String assignmentType) {
		this.assignmentType = assignmentType;
	}

	public String getAssignmentUrl() {
		return assignmentUrl;
	}
	public void setAssignmentUrl(String assignmentUrl) {
		this.assignmentUrl = assignmentUrl;
	}


	public static final String HDR_HARD_DEADLINE = "Hard Deadline";
	public static final String HDR_SCORE = "Effective Score";
	public static final String HDR_ATTEMPTS = "# of Attempts";
	public static final String HDR_ATTEMPT_DATE = "Last Attempted";
	public static final String HDR_ATTEMPTED_SCORE = "Last Attempted Score";
	public static final String HDR_DUE_DATE = "Due Date";
	public static final String HDR_NAME = "Assignment Name";
	public static final String HDR_TYPE = "Assignment Type";
	public static final String HDR_URL = "Assignment URL";
	
	public static final String dateFormat = "EEE d MMM yyyy h:mm a z";
	
	//private static final DateTimeFormat sdf = DateTimeFormat.getFormat("EEE d MMM yyyy h:mm a z");
	public Quiz()
	{
		//for serializability
	}
	public Quiz(String assignmentName, Date dateHardDeadline, Double dblEffectiveScore,
			Double dblpossibleScore, int intNumAttempts,
			int intPossibleAttempts, Date dateLastAttempted, Date dueDate, String assignmentType, String assignmentUrl) {
		super();
		this.dateHardDeadline = dateHardDeadline;
		this.dblEffectiveScore = dblEffectiveScore;
		this.dblpossibleScore = dblpossibleScore;
		this.intNumAttempts = intNumAttempts;
		this.intPossibleAttempts = intPossibleAttempts;
		this.dateLastAttempted = dateLastAttempted;
		this.dueDate = dueDate;
		this.assignmentName = assignmentName;
		this.assignmentType = assignmentType;
		this.assignmentUrl = assignmentUrl;
	}
	
	public Quiz(HashMap<String, String> assignmentDetails) {
		super();
		this.setDateHardDeadline(assignmentDetails.get(HDR_HARD_DEADLINE)); 
		this.setEffectiveAndPossibleScore(assignmentDetails.get(HDR_SCORE));
		this.setAttempts(assignmentDetails.get(HDR_ATTEMPTS));
//		this.intNumAttempts = assignmentDetails.get(key);
//		this.intPossibleAttempts = intPossibleAttempts;
		this.setDateLastAttempted(assignmentDetails.get(HDR_ATTEMPT_DATE));
		this.setDueDate(assignmentDetails.get(HDR_DUE_DATE));
		this.setAssignmentName(assignmentDetails.get(HDR_NAME));
		this.setAssignmentType(assignmentDetails.get(HDR_TYPE));
		this.setAssignmentUrl(assignmentDetails.get(HDR_URL));
	}
	
	private void setAttempts(String attemptString) {
		try
		{

			String[] attempts = attemptString.split("/");
			this.intNumAttempts = Integer.parseInt(attempts[0].trim());
			this.intPossibleAttempts = Integer.parseInt(attempts[1].trim());
		
		}
		catch (Exception e)
		{
			this.intNumAttempts = -1; //bad data or not taken yet
			this.intPossibleAttempts = -1;
		}
		
	}

	private void setEffectiveAndPossibleScore(String score) {
		try
		{

			String[] scores = score.split("/");
			this.dblEffectiveScore = Double.parseDouble(scores[0]);
			this.dblpossibleScore = Double.parseDouble(scores[1]);
		
		}
		catch (Exception e)
		{
			this.dblEffectiveScore = null; //bad data or not taken yet
			this.dblpossibleScore = null;
		}
		
	}
	

	public Date getDateHardDeadline() {
		return dateHardDeadline;
	}
	public void setDateHardDeadline(Date dateHardDeadline) {
		this.dateHardDeadline = dateHardDeadline;
	}
	public void setDateHardDeadline(String strHardDeadline) {
		if (strHardDeadline != null && !strHardDeadline.isEmpty())
			this.dateHardDeadline = getDateVal(strHardDeadline);
	}
	public Double getDblEffectiveScore() {
		return dblEffectiveScore;
	}
	public void setDblEffectiveScore(Double dblEffectiveScore) {
		this.dblEffectiveScore = dblEffectiveScore;
	}
	public Double getDblpossibleScore() {
		return dblpossibleScore;
	}
	public void setDblpossibleScore(Double dblpossibleScore) {
		this.dblpossibleScore = dblpossibleScore;
	}
	public int getIntNumAttempts() {
		return intNumAttempts;
	}
	public void setIntNumAttempts(int intNumAttempts) {
		this.intNumAttempts = intNumAttempts;
	}
	public int getIntPossibleAttempts() {
		return intPossibleAttempts;
	}
	public void setIntPossibleAttempts(int intPossibleAttempts) {
		this.intPossibleAttempts = intPossibleAttempts;
	}
	public Date getDateLastAttempted() {
		return dateLastAttempted;
	}
	public void setDateLastAttempted(Date dateLastAttempted) {
		this.dateLastAttempted = dateLastAttempted;
	}
	public void setDateLastAttempted(String strLastAttemptDate) {
		if (strLastAttemptDate != null && !strLastAttemptDate.isEmpty())
		this.dateLastAttempted =  getDateVal(strLastAttemptDate);
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	
	public void setDueDate(String strDueDate) {
		if (strDueDate != null && !strDueDate.isEmpty())
		this.dueDate =  getDateVal(strDueDate);
	}
	
	private Date getDateVal(String strDate){
		DateTimeFormat dtf = DateTimeFormat.getFormat(dateFormat);  
					//DateTimeFormat dtf = DateTimeFormat.getFormat("EEE d MMM yyyy h:mm a z");
			return dtf.parse(strDate);
		
		
		//return null;
	}
	public boolean isDueThisWeek() {
		if (dueDate.compareTo(new Date()) > 7)
		{
			return true;
		}
		return false;
	}
}
