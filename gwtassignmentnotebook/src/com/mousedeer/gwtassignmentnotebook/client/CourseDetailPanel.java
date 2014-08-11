package com.mousedeer.gwtassignmentnotebook.client;


import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.mousedeer.gwtassignmentnotebook.shared.CourseEnrollment;
import com.mousedeer.gwtassignmentnotebook.shared.Quiz;

public class CourseDetailPanel extends Composite {
	
	CellTable<Quiz> table;
	Label courseNameLabel;
	//VerticalPanel panel;
	VerticalPanel panel;
	Label loadingLabel;
	Image courseImage;
	ListDataProvider<Quiz> dataProvider;
	CourseEnrollment course;
	
	//create a course detail panel for course
	public CourseDetailPanel(CourseEnrollment course, boolean loading)
	{
		this.course = course;
		dataProvider = new ListDataProvider<Quiz>();
		courseNameLabel = new Label(course.getName());
		loadingLabel = new Label("Loading..");
		table = generateTable(course.getAssignmentList());
		courseImage = new Image(course.getImageUrl());
		panel = new VerticalPanel();		
		panel.add(courseNameLabel);
		panel.add(table);
		panel.add(loadingLabel);
		
	/*	panel = new DockLayoutPanel(Unit.EM);
		panel.addNorth(courseNameLabel, 2);
		panel.addSouth(loadingLabel, 2);
		panel.addWest(courseImage, 10);
		panel.add(table);
		panel.setVisible(true);*/
		if (loading)
		{
			loadingLabel.setVisible(true);
		}
		
		else
		{
			loadingLabel.setVisible(false);
			refreshTable(course);
		}				
		
		initWidget(panel);
	}

	public void refreshTable(CourseEnrollment course)
	{
	//	table = generateTable(course.getAssignmentList());
		loadingLabel.setText("loaded");
		// Add the data to the data provider, which automatically pushes it to the
	    // widget.

		List<Quiz> list  = dataProvider.getList();
		list.clear();
		
		if (course.getAssignmentList().size() < 1)
		{
			loadingLabel.setText("No assignments found");
		}
		else
		{
		    for (Quiz quiz : course.getAssignmentList()) {
		      list.add(quiz);  
		    }
			loadingLabel.setVisible(false);
		}
	}
	
	//generate the assignment detail table
	private CellTable<Quiz> generateTable(List<Quiz> quizList)
	 {
		 CellTable<Quiz> table = new CellTable<Quiz>();

		    // Create name column.
		 	
		    
		    Column<Quiz, SafeHtml> nameColumn = 
		    	    new Column<Quiz, SafeHtml>(new SafeHtmlCell()) { 
		    	      @Override 
		    	      public SafeHtml getValue(Quiz quiz) {
		    	    	  SafeHtmlBuilder sb = new SafeHtmlBuilder();
	    	    		  sb.appendEscaped("");
	    	    		  
		    	    	  if (quiz.getAssignmentName() != null && quiz.getAssignmentUrl() !=null)
		    	    	  {
		    	    		  sb.appendHtmlConstant("<a href='"+quiz.getAssignmentUrl()+"'>" + quiz.getAssignmentName() + "</a>");
		    	 
		    	    		  
		    	    	  }
		    	    	  return sb.toSafeHtml();
		    	    	  //else if (quiz.getAssignmentName() != null)  return quiz.getAssignmentName().toString();
		    	    	  //else return "N/A";
		    	      }
		    	};

		    // Make the name column sortable.
		    nameColumn.setSortable(true);

		    Column<Quiz, Date> dueDateColumn = 
		    	    new Column<Quiz, Date>(new DateCell()) { 
		    	      @Override 
		    	      public Date getValue(Quiz quiz) {
		    	    	  return quiz.getDueDate();
		    	    	  //else if (quiz.getAssignmentName() != null)  return quiz.getAssignmentName().toString();
		    	    	  //else return "N/A";
		    	      }
		    	};
		    dueDateColumn.setSortable(true);
		    
		    TextColumn<Quiz> scoreColumn = new TextColumn<Quiz>() {
		      @Override
		      public String getValue(Quiz quiz) {
		    	  if (quiz.getDblEffectiveScore() != null)  return quiz.getDblEffectiveScore().toString() + "/" + quiz.getDblpossibleScore().toString();
		    	  else return "N/A";
		      }
		    };

		    TextColumn<Quiz> deadlineColumn = new TextColumn<Quiz>() {
		      @Override
		      public String getValue(Quiz quiz) {
		    	  if (quiz.getDateHardDeadline()!= null)  return quiz.getDateHardDeadline().toString();
		    	  else return "N/A";
		      }
		    };
		    
		    TextColumn<Quiz> typeColumn = new TextColumn<Quiz>() {
			      @Override
			      public String getValue(Quiz quiz) {
			    	  if (quiz.getAssignmentType()!= null)  return quiz.getAssignmentType().toString();
			    	  else return "N/A";
			      }
			    };

		    // Add the columns.
		   table.addColumn(nameColumn, "Assignment");
		   table.addColumn(typeColumn, "Assignment Type");
		   table.addColumn(dueDateColumn, "Due Date");
		    table.addColumn(scoreColumn, "Score");
		    table.addColumn(deadlineColumn, "Hard Deadline");

		    // We know that the data is sorted alphabetically by default.
		    table.getColumnSortList().push(dueDateColumn);
		    
		// Create a data provider.
		   
		    // Connect the table to the data provider.
		    dataProvider.addDataDisplay(table);

		    List<Quiz> list = dataProvider.getList();
		    

		    // Add a ColumnSortEvent.ListHandler to connect sorting to the
		    // java.util.List.
		    ListHandler<Quiz> columnSortHandler = new ListHandler<Quiz>( list);
		    columnSortHandler.setComparator(nameColumn,
		        new Comparator<Quiz>() {
		          public int compare(Quiz o1, Quiz o2) {
		            if (o1 == o2) {
		              return 0;
		            }

		            // Compare the name columns.
		            if (o1 != null) {
		              return (o2 != null) ? o1.getDueDate().compareTo(o2.getDueDate()) : 1;
		            }
		            return -1;
		          }
		        });

		    
		    table.addColumnSortHandler(columnSortHandler);
		    return table;

	 }
}
