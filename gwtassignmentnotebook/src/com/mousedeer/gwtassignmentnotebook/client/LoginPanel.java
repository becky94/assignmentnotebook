package com.mousedeer.gwtassignmentnotebook.client;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.mousedeer.gwtassignmentnotebook.shared.CourseEnrollment;
import com.mousedeer.gwtassignmentnotebook.shared.Quiz;

public class LoginPanel extends Composite {
	
	Label loginLabel;
	TextBox nameText;
	PasswordTextBox passText;
	VerticalPanel panel;
	HorizontalPanel loginPanel;
	Label loadingLabel;
	ListDataProvider<Quiz> dataProvider;
	CourseEnrollment course;
	Button loginButton;
	Label errorLabel;
	
	//create a login box
	public LoginPanel()
	{
		//create fields
		nameText = new TextBox();
		passText = new PasswordTextBox();
		loginLabel = new Label();
		panel = new VerticalPanel();		
		loginPanel = new HorizontalPanel();
		loginButton = new Button();
		errorLabel = new Label();
		loadingLabel = new Label("Loading..");
		
		//set text
		nameText.getElement().setPropertyString("placeholder", "Email");
		passText.getElement().setPropertyString("placeholder", "Password");
		loginLabel.setText("Log in to Coursera");
		loginButton.setText("Login");
		
		//add to page
		panel.add(loginLabel);
		loginPanel.add(nameText);
		loginPanel.add(passText);
		loginPanel.add(loginButton);
		loginPanel.add(errorLabel);
		panel.add(loginPanel);
		

		nameText.setFocus(true);
	  	nameText.selectAll();
	  	
		initWidget(panel);
		
	}


}
