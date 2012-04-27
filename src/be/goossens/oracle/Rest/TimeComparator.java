package be.goossens.oracle.Rest;

import java.util.Comparator;

import be.goossens.oracle.Objects.DBTracking;


public class TimeComparator implements Comparator<DBTracking> {

	public int compare(DBTracking arg0, DBTracking arg1) {
		return arg0.getTimestamp().compareTo(arg1.getTimestamp());
	}

}
