package gui.util;

import com.jfoenix.validation.RequiredFieldValidator;

public class Validators {
	
	public static RequiredFieldValidator getRequiredFieldValidator(){
		RequiredFieldValidator validator = new RequiredFieldValidator();
		validator.setMessage("é necessário");
		return validator;
	}

}
