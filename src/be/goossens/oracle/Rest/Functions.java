package be.goossens.oracle.Rest;

/*
 * This class got functions that are used in the application.
 */

import java.util.Date;

import android.widget.Toast;

public class Functions {

	//This method will get a string in the format of daymonthyear (31121990) out the database
	//and returns a real date object
	public Date parseStringTimeStampToDate(String date) {
		Date value = new Date(); 
		value.setDate(Integer.parseInt(date.substring(0,2)));
		value.setMonth(Integer.parseInt(date.substring(2,4)));
		value.setYear(Integer.parseInt(date.substring(4,8)));
		return value;
	}
	
	//This method will get a string in the real timestamp format out the database
	//and returns a real date object
	public Date parseRealTimestampStringToDate(String timestamp){
		Date value = new Date();
		return value;
	}
}
