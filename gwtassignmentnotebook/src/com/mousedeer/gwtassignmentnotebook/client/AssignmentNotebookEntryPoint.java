package com.mousedeer.gwtassignmentnotebook.client;

import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.mousedeer.gwtassignmentnotebook.shared.CourseEnrollment;
import com.mousedeer.gwtassignmentnotebook.shared.FieldVerifier;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class AssignmentNotebookEntryPoint implements EntryPoint {

	/**
	 * Create a remote service proxy to talk to the server-side service.
	 */
	private final CourseraServiceAsync courseraService = GWT
			.create(CourseraService.class);

	private DialogBox dialogBox;
	private LoginPanel loginPanel;

	public void onModuleLoad() {

		// show login info
		loginPanel = new LoginPanel();
		Button logoutButton = new Button("Logout");

		RootPanel.get("loginContainer").add(loginPanel);
		RootPanel.get("logoutContainer").add(logoutButton);
		showLogin(true);

		// Create the popup dialog box.
		dialogBox = new DialogBox();

		class LogoutHandler implements ClickHandler {

			@Override
			public void onClick(ClickEvent event) {

				showLogin(true);
				RootPanel.get("coursesContainer").clear();

			}

		}

		// Create a handler for the sendButton and nameField
		class LoginHandler implements ClickHandler, KeyUpHandler {
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

			/** login and fetch courses from the Coursera site
			 * 
			 */
			private void getCourses() {
				// validate input
				loginPanel.errorLabel.setText("");
				String name = loginPanel.nameText.getText();
				String pass = loginPanel.passText.getText();
				if (!FieldVerifier.isValidText(name)
						|| !FieldVerifier.isValidText(pass)) {
					loginPanel.errorLabel
							.setText("Please enter a user name and password");
					return;
				}
				
				//set up the course container
			//	final Panel coursesContainer = RootPanel
				//		.get("coursesContainer");
				final Panel coursesContainer = RootPanel.get("coursesContainer");
				coursesContainer.clear();

				//hide login details
				showLogin(false);
				
				//set up "loading.." dialog box
				dialogBox.setText("Loading..");
				dialogBox.setModal(false);
				dialogBox.show();

				//get the course details from the Coursera site
				courseraService.getCourses(name, pass,
						new AsyncCallback<List<CourseEnrollment>>() {

							public void onFailure(Throwable caught) {
								// Show the RPC error message to the user
								dialogBox
										.setText("Error loading courses: "
												+ caught.getMessage());
								dialogBox.setText(caught.getMessage());
								
								//allow to login again
								showLogin(true);
							}

							//if successful, then pull the assignments from coursera
							public void onSuccess(List<CourseEnrollment> courses) {
								
								dialogBox.removeFromParent();
								//pull courses with async callback for each course
								for (CourseEnrollment course : courses) {

									final CourseDetailPanel courseDetailPanel = new CourseDetailPanel(
											course, true);
									coursesContainer.add(courseDetailPanel);

									try {
										courseraService
												.getAssignments(
														course,
														new AsyncCallback<CourseEnrollment>() {

															//display the error
															public void onFailure(
																	Throwable caught) {
																courseDetailPanel.loadingLabel.setText("Error loading assignments");
																
															}

															//update the table
															public void onSuccess(
																	CourseEnrollment course) {
																
																// update the
																// table
																courseDetailPanel
																		.refreshTable(course);

															}

														}

												);
									} catch (Exception e) {
										e.printStackTrace();
									}

								}

							}

						});

			}
		}

		// Add a handler to send the name to the server
		LoginHandler handler = new LoginHandler();
		loginPanel.loginButton.addClickHandler(handler);
		loginPanel.nameText.addKeyUpHandler(handler);
		loginPanel.passText.addKeyUpHandler(handler);

		// add handler to logout button
		logoutButton.addClickHandler(new LogoutHandler());
	}

	// hide login box and show logout or vice versa
	// if show == true, show Login screen, else show logout button
	private void showLogin(boolean show) {
		loginPanel.loginButton.setEnabled(show);
		RootPanel.get("loginContainer").setVisible(show);
		RootPanel.get("logoutContainer").setVisible(!show);
		if (show) {
			RootPanel.get("coursesContainer").clear();
		}

	}
}
