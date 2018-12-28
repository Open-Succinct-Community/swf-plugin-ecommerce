package in.succinct.plugins.ecommerce.db.model.participation;

import com.venky.swf.db.annotations.column.validations.ExactLength;
import com.venky.swf.db.annotations.column.validations.RegEx;

public interface User extends com.venky.swf.plugins.collab.db.model.user.User{

	@RegEx("\\+[0-9]+") //Ensures that it starts with + and all other characters are numbers.
	@ExactLength(13) // Ensures that user types in 13 characters in all in a phone field.
	public String  getPhoneNumber();
	public void setPhoneNumber(String phoneNumber);
	
}
