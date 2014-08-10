package com.mousedeer.gwtassignmentnotebook.client;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.gargoylesoftware.htmlunit.javascript.host.Console;
import com.google.appengine.datanucleus.DatastorePropertyValidator;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.Handler;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.mousedeer.gwtassignmentnotebook.shared.CourseEnrollment;
import com.mousedeer.gwtassignmentnotebook.shared.Quiz;
import com.mousedeer.gwtassignmentnotebook.shared.FieldVerifier;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class AssignmentNotebookEntryPoint implements EntryPoint {
	
	/**
	 * Create a remote service proxy to talk to the server-side service.
	 */
	private final CourseraServiceAsync courseraService = GWT.create(CourseraService.class);
 
 
  public void onModuleLoad() {

		
		/*final Button sendButton = new Button("Send");
		final TextBox nameField = new TextBox();
		nameField.setText("Email Address");

		final PasswordTextBox passwordField = new PasswordTextBox();
		passwordField.setText("Password");
		
		final Label errorLabel = new Label();
		 
		
		sendButton.addStyleName("sendButton");

		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
	/*	RootPanel.get("nameFieldContainer").add(nameField);
		RootPanel.get("nameFieldContainer").add(passwordField);
		RootPanel.get("sendButtonContainer").add(sendButton);
		RootPanel.get("errorLabelContainer").add(errorLabel);
*/
		// Focus the cursor on the name field when the app loads
	//	/l.setFocus(true);
	//	nameField.selectAll();

	  
	  	final LoginPanel loginPanel = new LoginPanel();
	  	loginPanel.nameText.setFocus(true);
	  	loginPanel.nameText.selectAll();
	  	RootPanel.get("loginContainer").add(loginPanel);
		// Create the popup dialog box.
		final DialogBox dialogBox = new DialogBox();
		
		

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler, KeyUpHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				getCourses();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					getCourses();
				}
			}

			private void getCourses() {
				// First, we validate the input.
				loginPanel.errorLabel.setText("");
				String name = loginPanel.nameText.getText();
				String pass = loginPanel.passText.getText();
				if (!FieldVerifier.isValidText(name) || !FieldVerifier.isValidText(pass)) {
					loginPanel.errorLabel.setText("Please enter a user name and password");
					return;
				}
				final Panel coursesContainer = RootPanel.get("coursesContainer");
				coursesContainer.clear();
				
				// Then, we send the input to the server.
				loginPanel.loginButton.setEnabled(false);
				//textToServerLabel.setText(name);
				//serverResponseLabel.setText("");
				dialogBox.setText("Loading..");
				dialogBox.show();
				courseraService.getCourses(name, pass,
						new AsyncCallback<List<CourseEnrollment>>() {
						
							public void onFailure(Throwable caught) {
								// Show the RPC error message to the user
								dialogBox
										.setText("Remote Procedure Call - Failure: " + caught.getMessage());
								dialogBox.setText(caught.getMessage());
								loginPanel.loginButton.setEnabled(true);
							}

							public void onSuccess(List<CourseEnrollment> courses) {
								for (CourseEnrollment course: courses)
								{

									final DialogBox courseDialog = new DialogBox();
									courseDialog.setText("Loading courses for Course "+ course.getHomeLink());
								/*	courseDialog.show();
									RootPanel.get().add(courseDialog);
									final HTML html = new HTML();
									RootPanel.get().add(html);*/
									final CourseDetailPanel courseDetailPanel= new CourseDetailPanel(course, true);
									coursesContainer.add(courseDetailPanel);
									
									try {
										courseraService.getAssignments(course,
												new AsyncCallback<CourseEnrollment>() {
												
													public void onFailure(Throwable caught) {
														// Show the RPC error message to the user
														courseDialog
																.setText("Remote Procedure Call - Failure: " + caught.getMessage());
														courseDialog.setText(caught.getMessage());
													}

													public void onSuccess(CourseEnrollment course) {
														courseDialog.hide();
														coursesContainer.remove(courseDialog);
															
														courseDetailPanel.refreshTable(course);
															
														}
														
													}
													
														
												);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									dialogBox.hide();
									
									//RootPanel.get().add(new HTML("<br/>" + course.getHomeLink()));
							
									/*if (course.getAssignmentList().size() <= 0)
									{
										RootPanel.get().add(new HTML("No assignments<br/></br/>"));
									}
									else
									{
										CellTable<Quiz> table = generateTable(course.getAssignmentList());
										
										RootPanel.get().add(table);
									}*/
								}
								
							}
							
								
						});
				
			}
		}

		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		loginPanel.loginButton.addClickHandler(handler);
		loginPanel.nameText.addKeyUpHandler(handler);
		loginPanel.passText.addKeyUpHandler(handler);
	}

}
