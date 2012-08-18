// Please read info.txt for license and legal information

package com.hippoandfriends.helpdiabetes.Custom;

import com.hippoandfriends.helpdiabetes.R;

import android.content.Context;
import com.hippoandfriends.helpdiabetes.R;

import android.database.Cursor;
import com.hippoandfriends.helpdiabetes.R;

import android.view.LayoutInflater;
import com.hippoandfriends.helpdiabetes.R;

import android.view.View;
import com.hippoandfriends.helpdiabetes.R;

import android.view.ViewGroup;
import com.hippoandfriends.helpdiabetes.R;

import android.widget.SimpleCursorAdapter;
import com.hippoandfriends.helpdiabetes.R;

import android.widget.TextView;

public class CustomSimpleCursorAdapter extends SimpleCursorAdapter {
	private Context context;
	private int fontSize;
	private String[] from;
	
	public CustomSimpleCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int fontSize) {
		super(context, layout, c, from, to);
		this.context = context;
		this.fontSize = fontSize;
		this.from = from;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		Cursor cursor = getCursor();
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(android.R.layout.simple_list_item_1, null);
		}
		TextView tt = (TextView) v.findViewById(android.R.id.text1);
		tt.setText(cursor.getString(cursor.getColumnIndexOrThrow(from[0])));
		tt.setTextSize(fontSize);
		cursor.moveToNext();
		return v;
	}

}
