package be.goossens.oracle.Custom;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import be.goossens.oracle.R;
import be.goossens.oracle.Objects.DBTracking;

public class CustomArrayAdapterDBTracking extends ArrayAdapter<DBTracking> {
	private Context context;
	private int fontSize;
	private List<DBTracking> objects;

	public CustomArrayAdapterDBTracking(Context context,
			int textViewResourceId, List<DBTracking> objects, int fontSize) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.fontSize = fontSize;
		this.objects = objects;
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.row_custom_array_adapter, null);
		}
		TextView tt = (TextView) v.findViewById(R.id.text1);

		if (!objects.get(position).getNoRecors().equals("")) {
			tt.setText(objects.get(position).getNoRecors());
		} else {
			String date = "";

			// add date if last date is not same as this date
			if (position != 0) {
				if ((objects.get(position).getTimestamp().getDay() != objects
						.get(position - 1).getTimestamp().getDay())
						|| (objects.get(position).getTimestamp().getMonth() != objects
								.get(position - 1).getTimestamp().getMonth())
						|| (objects.get(position).getTimestamp().getYear() != objects
								.get(position - 1).getTimestamp().getYear())) {
					date = (objects.get(position).getTimestamp().getDay() + "-"
							+ objects.get(position).getTimestamp().getMonth()
							+ "-" + objects.get(position).getTimestamp()
							.getYear() + " \n");
				}
			} else {
				date = (objects.get(position).getTimestamp().getDay() + "-"
						+ objects.get(position).getTimestamp().getMonth() + "-" + objects
						.get(position).getTimestamp().getYear() + " \n");
			}

			String text = "";

			if (objects.get(position).getExerciseEvent() != null) {
				text = "Exercise: " + (objects.get(position).getExerciseEvent()
						.getDescription());
			}

			tt.setText(date + text);

		}

		tt.setTextSize(fontSize);
		return v;
	}
}
