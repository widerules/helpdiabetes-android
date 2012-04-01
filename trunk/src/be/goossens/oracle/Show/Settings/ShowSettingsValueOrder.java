package be.goossens.oracle.Show.Settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import be.goossens.oracle.R;
import be.goossens.oracle.Objects.DBValueOrder;
import be.goossens.oracle.Rest.DbAdapter;
import be.goossens.oracle.Rest.ValueOrderComparator;

public class ShowSettingsValueOrder extends Activity {
	private DbAdapter dbHelper;

	private List<RadioButton> listRadioButtons;
	private List<DBValueOrder> listValueOrders;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_settings_value_order);
		dbHelper = new DbAdapter(this);
		listValueOrders = new ArrayList<DBValueOrder>();
		listRadioButtons = new ArrayList<RadioButton>();

		listRadioButtons.add((RadioButton) findViewById(R.id.radio0));
		listRadioButtons.add((RadioButton) findViewById(R.id.radio1));
		listRadioButtons.add((RadioButton) findViewById(R.id.radio2));
		listRadioButtons.add((RadioButton) findViewById(R.id.radio3));
		listRadioButtons.get(0).setSelected(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		dbHelper.open();
		refresh();
	}

	// This method will fill the list of DBValueOrders with the right values
	private void fillListValueOrders() {
		// make the list empty
		listValueOrders = new ArrayList<DBValueOrder>();

		// get all the value orders
		Cursor cSettingValueOrderProt = dbHelper
				.fetchSettingByName(getResources().getString(
						R.string.value_order_prot));
		Cursor cSettingValueOrderCarb = dbHelper
				.fetchSettingByName(getResources().getString(
						R.string.value_order_carb));
		Cursor cSettingValueOrderFat = dbHelper
				.fetchSettingByName(getResources().getString(
						R.string.value_order_fat));
		Cursor cSettingValueOrderKcal = dbHelper
				.fetchSettingByName(getResources().getString(
						R.string.value_order_kcal));

		// Move cursors to first object
		cSettingValueOrderProt.moveToFirst();
		cSettingValueOrderCarb.moveToFirst();
		cSettingValueOrderFat.moveToFirst();
		cSettingValueOrderKcal.moveToFirst();

		// Fill list
		listValueOrders
				.add(new DBValueOrder(
						cSettingValueOrderProt
								.getInt(cSettingValueOrderProt
										.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)),
						cSettingValueOrderProt.getString(cSettingValueOrderProt
								.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_NAME)),
						getResources().getString(R.string.amound_of_protein)));

		listValueOrders
				.add(new DBValueOrder(
						cSettingValueOrderCarb
								.getInt(cSettingValueOrderCarb
										.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)),
						cSettingValueOrderCarb.getString(cSettingValueOrderCarb
								.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_NAME)),
						getResources().getString(R.string.amound_of_carbs)));

		listValueOrders
				.add(new DBValueOrder(
						cSettingValueOrderFat
								.getInt(cSettingValueOrderFat
										.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)),
						cSettingValueOrderFat.getString(cSettingValueOrderFat
								.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_NAME)),
						getResources().getString(R.string.amound_of_fat)));

		listValueOrders
				.add(new DBValueOrder(
						cSettingValueOrderKcal
								.getInt(cSettingValueOrderKcal
										.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_VALUE)),
						cSettingValueOrderKcal.getString(cSettingValueOrderKcal
								.getColumnIndexOrThrow(DbAdapter.DATABASE_SETTINGS_NAME)),
						getResources().getString(R.string.amound_of_kcal)));

		// Close all the cursor
		cSettingValueOrderProt.close();
		cSettingValueOrderCarb.close();
		cSettingValueOrderFat.close();
		cSettingValueOrderKcal.close();

		// Sort the list on order
		ValueOrderComparator comparator = new ValueOrderComparator();
		Collections.sort(listValueOrders, comparator);
	}

	// this method will fill the text in the radio buttons
	private void fillRadioButtonText() {
		for (int i = 0; i < listRadioButtons.size(); i++) {
			listRadioButtons.get(i).setText(
					"" + listValueOrders.get(i).getValueName());
		}
	}

	// This method will return the position of the selected radio button
	private int getSelectedRadioButton() {
		int returnValue = 0;
		// get the selected radio button
		for (int i = 0; i < listRadioButtons.size(); i++) {
			if (listRadioButtons.get(i).isChecked())
				returnValue = i;
		}
		return returnValue;
	}

	// If the user presses the button up
	public void onClickUp(View view) {
		int selectedRadioButton = getSelectedRadioButton();

		// if the id we selected = 0 we have to update all the orders
		// else we only update the selected one and the one above
		if (selectedRadioButton == 0) {
			// update all valueOrders
			for (DBValueOrder obj : listValueOrders) {
				if (obj.getOrder() != 1) {
					dbHelper.updateSettingsByName(obj.getSettingName(), ""
							+ (obj.getOrder() - 1));
				} else {
					dbHelper.updateSettingsByName(obj.getSettingName(), "4");
				}
			}

			// set the checked radio button on the last one
			listRadioButtons.get(listRadioButtons.size() - 1).setChecked(true);
		} else {
			dbHelper.updateSettingsByName(
					listValueOrders.get(selectedRadioButton).getSettingName(),
					"" + selectedRadioButton);
			
			dbHelper.updateSettingsByName(
					listValueOrders.get(selectedRadioButton - 1)
							.getSettingName(), "" + (selectedRadioButton + 1));

			// set the checked radio button 1 higher
			listRadioButtons.get(selectedRadioButton - 1).setChecked(true);
		}

		refresh();
	} 

	// If the user pressed the button down
	public void onClickDown(View view) {
		int selectedRadioButton = getSelectedRadioButton();
		
		// if we selected the last radiobutton we have to update all the orders
		// else we only update the selected on and the one below
		if (selectedRadioButton == listRadioButtons.size() - 1) {
			//update all valueOrders 
			for(DBValueOrder obj : listValueOrders){
				if(obj.getOrder() != 4){
					dbHelper.updateSettingsByName(obj.getSettingName(), "" + (obj.getOrder() + 1));
				} else {
					dbHelper.updateSettingsByName(obj.getSettingName(), "1");
				}
			} 
			 
			// set the checked radio button on the first one
			listRadioButtons.get(0).setChecked(true);
		} else {
			dbHelper.updateSettingsByName(listValueOrders.get(selectedRadioButton).getSettingName(), "" + (selectedRadioButton + 2));
			dbHelper.updateSettingsByName(listValueOrders.get(selectedRadioButton + 1).getSettingName(), "" + (selectedRadioButton + 1));
			
			// set the checked radio button 1 lower
			listRadioButtons.get(selectedRadioButton + 1).setChecked(true);
		}
		refresh();
	}

	private void refresh() {
		fillListValueOrders();
		fillRadioButtonText();
	}

	@Override
	protected void onPause() {
		super.onPause();
		dbHelper.close();
	}
	
	//when the user pressed on the back button
	public void onClickBack(View view){
		finish();
	}
}
