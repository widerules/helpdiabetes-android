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



public class CustomArrayAdapterFontSize extends ArrayAdapter<String> {
	private Context ctx;
	private List<String> items;

	public CustomArrayAdapterFontSize(Context context, int textViewResourceId,
			List<String> objects) {
		super(context, textViewResourceId, objects);
		this.ctx = context;
		this.items = objects;
	}
		
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.row_custom_array_adapter, null);
		}
		TextView tt = (TextView) v.findViewById(R.id.text1);
		
		//text size = position + 15 so we start at 15
		tt.setText(items.get(position).toString() + " " + (position+15));
		tt.setTextSize((position+15));
		return v;
	}

}
