package be.goossens.oracle.Rest;

import java.util.Comparator;

import be.goossens.oracle.Objects.DBValueOrder;

public class ValueOrderComparator implements Comparator<DBValueOrder> {

	public int compare(DBValueOrder lhs, DBValueOrder rhs) {
		int order1 = lhs.getOrder();
		int order2 = rhs.getOrder();

		if (order1 > order2)
			return +1;
		else if (order1 < order2)
			return -1;
		else
			return 0;
	}

}
