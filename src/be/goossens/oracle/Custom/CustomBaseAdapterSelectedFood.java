// Please read info.txt for license and legal information

// Please read info.txt for license and legal information

package be.goossens.oracle.Custom;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import be.goossens.oracle.R;
import be.goossens.oracle.Objects.DBSelectedFood;
import be.goossens.oracle.Rest.Functions;

public class CustomBaseAdapterSelectedFood extends BaseAdapter implements
		OnClickListener {
	private Context context;
	private List<DBSelectedFood> listSelectedFood;
	private int fontSize;
	private int defaultValue;

	public CustomBaseAdapterSelectedFood(Context context,
			List<DBSelectedFood> listSelectedFood, int fontSize,
			int defaultValue) {
		this.context = context;
		this.listSelectedFood = listSelectedFood;
		this.fontSize = fontSize;
		this.defaultValue = defaultValue;
	}

	public int getCount() {
		return listSelectedFood.size();
	}

	public Object getItem(int arg0) {
		return listSelectedFood.get(arg0);
	}

	public long getItemId(int arg0) {
		return listSelectedFood.get(arg0).getId();
	}

	public View getView(int position, View convertView, ViewGroup viewGroup) {
		DBSelectedFood entry = listSelectedFood.get(position);
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row_selected_food, null);
		}

		TextView tv1 = (TextView) convertView.findViewById(R.id.textView1);
		TextView tv2 = (TextView) convertView.findViewById(R.id.textView2);
		TextView tv3 = (TextView) convertView.findViewById(R.id.textView3);

		tv1.setText(entry.getFoodName());
		tv2.setText(entry.getAmound() + " " + entry.getUnit().getName());

		String value = "";
		 
		// if standard amount == 100 we have to /100 to get the right value
		if (entry.getUnit().getStandardamound() == 100) {
			 
			switch (defaultValue) {
			case 1:
				value = "" + new Functions().roundFloats(((entry.getAmound() * entry.getUnit().getCarbs()) / 100),1) + " " + context.getResources().getString(R.string.short_carbs);
				break;
			case 2:
				value = "" + new Functions().roundFloats(((entry.getAmound() * entry.getUnit().getProtein()) / 100),1) + " " + context.getResources().getString(R.string.amound_of_protein);
				break;
			case 3:
				value = "" + new Functions().roundFloats(((entry.getAmound() * entry.getUnit().getFat()) / 100),1) + " " + context.getResources().getString(R.string.amound_of_fat);
				break;
			case 4:
				value = "" + new Functions().roundFloats(((entry.getAmound() * entry.getUnit().getKcal()) / 100),1) + " " + context.getResources().getString(R.string.short_kcal);
				break;
			}
		} else {

			switch (defaultValue) {
			case 1:
				value = "" + new Functions().roundFloats(((entry.getAmound() * entry.getUnit().getCarbs())),1) + " " + context.getResources().getString(R.string.short_carbs);
				break;
			case 2:
				value = "" + new Functions().roundFloats(((entry.getAmound() * entry.getUnit().getProtein())),1) + " " + context.getResources().getString(R.string.amound_of_protein);
				break;
			case 3:
				value = "" + new Functions().roundFloats(((entry.getAmound() * entry.getUnit().getFat())),1) + " " + context.getResources().getString(R.string.amound_of_fat);
				break;
			case 4:
				value = "" + new Functions().roundFloats(((entry.getAmound() * entry.getUnit().getKcal())),1) + " " + context.getResources().getString(R.string.short_kcal);
				break;
			}
			
		}

		// set text  
		tv3.setText(value + " >");

		tv1.setTextSize(fontSize - 3);
		tv2.setTextSize(fontSize - 3);
		tv3.setTextSize(fontSize);

		return convertView;
	}

	public void onClick(View arg0) {

	}
}
