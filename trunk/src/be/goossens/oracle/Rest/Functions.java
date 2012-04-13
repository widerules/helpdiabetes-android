package be.goossens.oracle.Rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * This class got functions that are used in the application.
 */

public class Functions {

	public long getCurrentDateInSeconds() {
		String sDate = "";
		Date dDate = new Date();
		SimpleDateFormat fmt = new SimpleDateFormat("MM-dd-yyyy");

		if (("" + (dDate.getMonth()+1)).length() == 0)
			sDate += "0" + (dDate.getMonth()+1);
		else
			sDate += "" + (dDate.getMonth()+1);

		sDate += "-";

		if (("" + dDate.getDate()).length() == 0)
			sDate += "0" + dDate.getDate();
		else
			sDate += "" + dDate.getDate();

		sDate += "-" + (dDate.getYear() + 1900);

		try {
			return fmt.parse(sDate).getTime();
		} catch (ParseException e) {
			return -1;
		}

	}

}
