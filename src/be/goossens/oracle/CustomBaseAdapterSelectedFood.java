package be.goossens.oracle;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomBaseAdapterSelectedFood extends BaseAdapter implements OnClickListener {
	private Context context;
	private List<DBSelectedFood> listSelectedFood;
	  
	public CustomBaseAdapterSelectedFood(Context context, List<DBSelectedFood> listSelectedFood){
		this.context = context;
		this.listSelectedFood = listSelectedFood;
	}
	
	public int getCount() {
		return listSelectedFood.size();
	}

	public Object getItem(int arg0) {
		return listSelectedFood.get(arg0);
	}

	public long getItemId(int arg0) {
		return Long.parseLong(listSelectedFood.get(arg0).getId().toString());
	}

	public View getView(int position, View convertView, ViewGroup viewGroup) {
		DBSelectedFood entry = listSelectedFood.get(position);
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row_selected_food, null);
		}

		TextView tvSelectedFoodName = (TextView) convertView.findViewById(R.id.textViewSelectedFoodValues);
		tvSelectedFoodName.setText(entry.getFoodName() + " (" + entry.getAmound()  + " " + entry.getUnitName() + ")");
				
		return convertView;
	}
	
	public void onClick(View arg0) {
		
	}
}
