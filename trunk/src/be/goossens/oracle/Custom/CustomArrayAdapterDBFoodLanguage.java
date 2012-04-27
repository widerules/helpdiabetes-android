package be.goossens.oracle.Custom;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import be.goossens.oracle.R;
import be.goossens.oracle.Objects.DBFoodLanguage;

public class CustomArrayAdapterDBFoodLanguage extends
		ArrayAdapter<DBFoodLanguage> {

	private int layout;
	private Context context;
	private List<DBFoodLanguage> objects;

	public CustomArrayAdapterDBFoodLanguage(Context context,
			int textViewResourceId, List<DBFoodLanguage> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.objects = objects;
		this.layout = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(layout, null);
		}

		TextView tt = (TextView) v.findViewById(android.R.id.text1);

		tt.setText(objects.get(position).getLanguage());

		tt.setTextColor(context.getResources().getColor(R.color.ColorText));

		return v;
	}

}
