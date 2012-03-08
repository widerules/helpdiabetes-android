package be.goossens.oracle;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class SelectedFood extends ListActivity {
	private DbAdapter dbHelper;

	private List<DBSelectedFood> listOfSelectedFood;

	private static final int DELETE_ID = Menu.FIRST;
	private static final int EDIT_ID = Menu.FIRST + 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selected_food);

		dbHelper = new DbAdapter(this);
		dbHelper.open();

		listOfSelectedFood = new ArrayList<DBSelectedFood>();
		calculateValues();
		fillData();
		registerForContextMenu(getListView());
	}

	// converts the cursor with all selected food to a arrayList<DBSelectedFood>
	// and returns the arraylist
	private ArrayList<DBSelectedFood> getSelectedFood() {
		Cursor selectedFoodCursor = dbHelper.fetchAllSelectedFood();
		ArrayList<DBSelectedFood> list = new ArrayList<DBSelectedFood>();
		while (selectedFoodCursor.moveToNext()) {
			list.add(new DBSelectedFood(
					selectedFoodCursor
							.getString(selectedFoodCursor
									.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_ID)),
					selectedFoodCursor.getString(selectedFoodCursor
							.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_AMOUNT)),
					selectedFoodCursor.getString(selectedFoodCursor
							.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_FOODNAME)),
					selectedFoodCursor.getInt(selectedFoodCursor
							.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_KCAL)),
					selectedFoodCursor.getString(selectedFoodCursor
							.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_UNITNAME))));
		}
		return list;
	}

	// Show on top Total: amound calories
	private void calculateValues() {
		TextView tvTotal = (TextView) findViewById(R.id.textViewShowTotal);
		// calculate amound of kilocalories
		int totalKcal = 0;
		Cursor allSelectedFood = dbHelper.fetchAllSelectedFood();
		while (allSelectedFood.moveToNext()) {
			totalKcal += allSelectedFood
					.getInt(allSelectedFood
							.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_AMOUNT))
					* allSelectedFood
							.getInt(allSelectedFood
									.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_KCAL));
		}
		tvTotal.setText(getResources().getString(R.string.total) + " "
				+ totalKcal + " "
				+ getResources().getString(R.string.amound_of_kcal));
	}

	// create the context menu ( display if we long press on a item in the
	// listview )
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
		menu.add(0, EDIT_ID, 0, R.string.menu_edit);
	}

	// if we select a item in the context menu check if pressed delte or edit.
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// if pressed delete
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			dbHelper.deleteSelectedFood(info.id);
			calculateValues();
			fillData();
			break;
		// if pressed edit
		case EDIT_ID:
			Toast.makeText(this, "This is still under construction",
					Toast.LENGTH_LONG).show();
			break;
		}
		return super.onContextItemSelected(item);
	}

	// to fill the listview with data
	private void fillData() {
		listOfSelectedFood = getSelectedFood();
		SelectedFoodAdapter adapter = new SelectedFoodAdapter(this,
				listOfSelectedFood);
		setListAdapter(adapter);
	}

	// when we click on the button return
	public void onClickBack(View view) {
		setResult(RESULT_OK);
		finish();
	}

	// when we click on the button delete all
	public void onClickDeleteAll(View view) {
		Cursor allSelectedFood = dbHelper.fetchAllSelectedFood();
		while (allSelectedFood.moveToNext()) {
			dbHelper.deleteSelectedFood(allSelectedFood.getInt(allSelectedFood
					.getColumnIndexOrThrow(DbAdapter.DATABASE_SELECTEDFOOD_ID)));
		}
		calculateValues();
		fillData();
	}
}
