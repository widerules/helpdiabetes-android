package be.goossens.oracle;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class CustomArrayAdapterSelectedFoodUpdateValues extends ArrayAdapter {
	private ArrayList<DBSelectedFood> items;
	private Context ctx;
	private DbAdapter dbHelper;

	public CustomArrayAdapterSelectedFoodUpdateValues(Context context,
			ArrayList<DBSelectedFood> list) {
		super(context, R.layout.row_listview_update_template_values, list);
		ctx = context;
		this.items = list;
		dbHelper = new DbAdapter(context);
		dbHelper.open();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.row_listview_update_template_values, null);
		}
		DBSelectedFood o = items.get(position);
		EditText et = (EditText) v
				.findViewById(R.id.editTextUpdateTemplateValues);
		Spinner sp = (Spinner) v.findViewById(R.id.spinnerUpdateTemplateValues);
		TextView tv = (TextView) v
				.findViewById(R.id.textViewUpdateTemplateValues);

		// set amound on the editText
		et.setText("");
		// Set the foodName on the textView
		tv.setText("" + o.getFoodName());
		// Fill the spinner with the units
		
		Cursor cUnit = dbHelper.fetchFoodUnit(o.getUnitID());
		cUnit.moveToFirst();
		Cursor cUnits = dbHelper.fetchFoodUnitByFoodId(cUnit.getLong(cUnit
				.getColumnIndexOrThrow(DbAdapter.DATABASE_FOODUNIT_FOODID)));
		cUnits.moveToFirst();
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(ctx,
				android.R.layout.simple_spinner_item, cUnits,
				new String[] { DbAdapter.DATABASE_FOODUNIT_NAME },
				new int[] { android.R.id.text1 });
		sp.setAdapter(adapter);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		//cUnits cant be closed caus it is needed in the spinner
		cUnit.close();
		
		return v;
	}

}
