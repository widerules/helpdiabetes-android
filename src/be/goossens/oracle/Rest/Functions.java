package be.goossens.oracle.Rest;

/*
 * This class got functions that are used in the application.
 */

import java.util.Date;

public class Functions {

	//This method will get a string in the format of HH:mm out the database
	//and returns a real date object
	public Date parseStringToDate(String date) {
		Date value = new Date();
		int indexOfSeperator = date.indexOf(":");
		value.setHours(Integer.parseInt(date.substring(0, indexOfSeperator)));
		value.setMinutes(Integer.parseInt(date.substring(indexOfSeperator + 1)));
		return value;
	}
	
	//This method will get a string in the real timestamp format out the database
	//and returns a real date object
	public Date parseRealTimestampStringToDate(String timestamp){
		Date value = new Date();
		return value;
	}
}
