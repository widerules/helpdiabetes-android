package be.goossens.oracle;

/*
 * This class is used in the food list from ShowFoodList
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomArrayAdapterFoodList extends ArrayAdapter<String> {
  
	private List<Food> foodItemList = null;
	private String previousSearchString = null;
	private int[] firstIndex;
	private int[] lastIndex;
	private Context callingContext;
	private Thread backgroundThread;
	private final Handler callingThreadHanlder = new Handler();
	
	public CustomArrayAdapterFoodList(Context context, int textViewResourceId,
			int maximuSearchStringLength) {
		super(context, textViewResourceId, new ArrayList<String>());
		callingContext = context;
		foodItemList = new ArrayList<Food>();
		firstIndex = new int[maximuSearchStringLength + 1];
		lastIndex = new int[maximuSearchStringLength + 1];
		firstIndex[0] = 0;
		lastIndex[0] = 0;
		previousSearchString = null;
	}

	public Food getFoodItem(int position) {
		try {
			return new Food(foodItemList.get(position));
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public void addFood(Food newFood) {
		foodItemList.add(new Food(newFood));
		Collections.sort(foodItemList);
		firstIndex[0] = 0;
		lastIndex[0] = foodItemList.size() - 1;
		previousSearchString = null;
	}

	private int[] searchFirst(int low, int high, char value, int index) {
		int temp = 0;
		int temp2 = 0;
		int mid = 0;
		int belenth;
		low++;
		high++;
		int[] returnValue = { -1, high };
		char[] be;
		temp = high + 1;
		while (low < temp) {
			mid = (low + temp) / 2;
			be = this.foodItemList.get(mid - 1).getName().toCharArray();
			belenth = be.length;
			if (!(belenth > index)) {
				low = mid + 1;
			} else {
				if (ExcelCharacter.compareToAsInExcel(be[index], value) < 0) {
					low = mid + 1;
				} else {
					if (temp2 > value) {
						returnValue[1] = mid;
					}
					temp = mid;
				}
			}
		}
		if (low > high) {
			;
		} else {
			be = this.foodItemList.get(low - 1).getName().toCharArray();
			belenth = be.length;
			if (belenth > index) {
				if ((low < (high + 1))
						&& (ExcelCharacter.compareToAsInExcel(be[index], value) == 0))
					returnValue[0] = low;
			} else {
				;
			}
		}
		returnValue[0] = returnValue[0] - 1;
		returnValue[1] = returnValue[1] - 1;
		return returnValue;
	}

	private int searchLast(int low, int high, char value, int index) {
		int temp = 0;
		int mid = 0;
		int returnvalue = -1;
		char[] be;
		int belength;
		low++;
		high++;
		temp = low - 1;
		while (high > temp) {
			if ((high + temp) % 2 > 0) {
				mid = (high + temp) / 2 + 1;
			} else {
				mid = (high + temp) / 2;
			}
			be = this.foodItemList.get(mid - 1).getName().toCharArray();
			belength = be.length;
			if (!(belength > index)) {
				temp = mid;
			} else {
				if (ExcelCharacter.compareToAsInExcel(be[index], value) > 0)
					high = mid - 1;
				else
					temp = mid;
			}
		}
		if (high < low) {
			;
		} else {
			be = this.foodItemList.get(high - 1).getName().toCharArray();
			belength = be.length;
			if (belength > index) {
				if (((low - 1) < high)
						& (ExcelCharacter.compareToAsInExcel(be[index], value) == 0))
					returnvalue = high;
			} else {
				;
			}
		}
		returnvalue = returnvalue - 1;
		return returnvalue;
	}

	public int getFirstMatchingItem(CharSequence s) {
		int index = 0;
		int[] result = new int[2];
		if (backgroundThread != null) {
			if (backgroundThread.isAlive())
				return -1;
		}

		if (previousSearchString != null) {
			while ((index < s.length())
					&& (index < previousSearchString.length())
					&& (ExcelCharacter.compareToAsInExcel(s.charAt(index),
							previousSearchString.charAt(index)) == 0)) {
				index++;
			}
		}

		if (index != s.length()) {
			while ((index < s.length()) && (index < (firstIndex.length - 1))) {
				result = searchFirst(firstIndex[index], lastIndex[index],
						s.charAt(index), index);
				if (result[0] > -1) {
					firstIndex[index + 1] = result[0];
					lastIndex[index + 1] = searchLast(result[0], result[1],
							s.charAt(index), index);
				} else {
					if (index < (firstIndex.length - 1)) {
						firstIndex[index + 1] = firstIndex[index];
						lastIndex[index + 1] = lastIndex[index];
					}
				}
				index++;
			}
		}

		previousSearchString = s.toString();
		return firstIndex[index];
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) callingContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.row_food, null);
		}
		TextView tt = (TextView) v.findViewById(R.id.row_food_text);
		if (tt != null) {
			tt.setText((String) getItem(position));
		}
		return v;
	}

	private void updateList() {
		lastIndex[0] = foodItemList.size() - 1;
		previousSearchString = null;
		clear();
		for (int index = 0; index < foodItemList.size(); index++) {
			add(foodItemList.get(index).toString());
		}
		((ShowFoodList) callingContext).triggerSearching();
	}

	public void initializeFoodItemList(Bundle savedBundle) {
		final CustomArrayAdapterFoodList thisList = this;
		backgroundThread = new Thread(new Runnable() {

			public void run() {
 				thisList.initialize();
			}
		});
		backgroundThread.start();
	}

	private void initialize() {
		foodItemList = new ArrayList<Food>();
		
		DbAdapter dbHelper = new DbAdapter(callingContext);
		dbHelper.open();
		
		Cursor cFoodList = dbHelper.fetchAllFood();
		cFoodList.moveToFirst();
		do {
			Food newFood = new Food(
					cFoodList.getInt(cFoodList
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_ID)),
					null,
					0,
					0,
					0,
					0,
					0,
					cFoodList.getString(cFoodList
							.getColumnIndexOrThrow(DbAdapter.DATABASE_FOOD_NAME)));
			foodItemList.add(newFood);
		} while (cFoodList.moveToNext());
		
		cFoodList.close();
		dbHelper.close();

		/*
		 * Use the food comparator to sort the list of food on foodName and sort them like in excel
		 * meaning 'jä' comes on same hight as 'ja'.
		 * */
		FoodComparator comparator = new FoodComparator();
		Collections.sort(foodItemList,comparator);
		
		if (callingContext != null) {
			final Runnable runInUIThread = new Runnable() {

				public void run() {
					updateList();
				}
			};
			callingThreadHanlder.post(runInUIThread);
		} else {
			lastIndex[0] = foodItemList.size() - 1;
		}
	}
}
