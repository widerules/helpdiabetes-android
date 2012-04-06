package be.goossens.oracle.Custom;

import java.util.List;

import be.goossens.oracle.R;
import be.goossens.oracle.Objects.DBFood;
import be.goossens.oracle.Objects.DBFoodTemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomBaseAdapterFoodTemplates extends BaseAdapter {
	private Context context;
	private List<DBFoodTemplate> listFoodTemplates;
	private int fontSize; 
	
	public CustomBaseAdapterFoodTemplates(Context context, List<DBFoodTemplate> listFoodTemplates, int fontSize){
		this.context = context;
		this.listFoodTemplates = listFoodTemplates;
		this.fontSize = fontSize;
	} 
	
	public int getCount() {
		return listFoodTemplates.size();
	}
 
	public Object getItem(int position) {
		return listFoodTemplates.get(position);
	}

	public long getItemId(int position) {
		return listFoodTemplates.get(position).getId();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		DBFoodTemplate entry = listFoodTemplates.get(position);
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row_custom_base_adapter_food_templates, null);
		}
		
		TextView tv = (TextView) convertView.findViewById(R.id.textViewCustomBaseAdapter);
		
		//Get the food from foodTemplate
		String tvString = "";
	
		for(DBFood obj : entry.getFoods()){
			tvString += "\n \t - " + obj.getName();
		}
		
		tv.setText(entry.getFoodTemplateName() + tvString);
		tv.setTextSize(fontSize);
		
		return convertView;
	}

}
