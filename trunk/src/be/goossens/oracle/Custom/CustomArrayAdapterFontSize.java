package be.goossens.oracle.Custom;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import be.goossens.oracle.R;

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
		//text size = position + 8 so we start at 8
		tt.setText(items.get(position).toString() + " " + (position+8));
		tt.setTextSize((position+8));
		return v;
	}

}
