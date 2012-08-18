// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Custom;

import java.util.List;

import com.hippoandfriends.helpdiabetes.R;

import android.content.Context;
import com.hippoandfriends.helpdiabetes.R;

import android.view.LayoutInflater;
import com.hippoandfriends.helpdiabetes.R;

import android.view.View;
import com.hippoandfriends.helpdiabetes.R;

import android.view.ViewGroup;
import com.hippoandfriends.helpdiabetes.R;

import android.widget.ArrayAdapter;
import com.hippoandfriends.helpdiabetes.R;

import android.widget.TextView;



public class CustomArrayAdapterHome extends ArrayAdapter<String> {
	private Context context;
	private List<String> objects;
	private int[] colors;
	
	public CustomArrayAdapterHome(Context context, int textViewResourceId,
			List<String> objects, int[] colors) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.objects = objects;
		this.colors = colors;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.row_custom_array_adapter_home, null);
		}

		TextView tt = (TextView) v.findViewById(R.id.text1);

		tt.setText(objects.get(position).toString());
		tt.setTextColor(colors[position]);
		
		return v;
	}

}
