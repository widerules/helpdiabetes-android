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

		// if norecors != "" then
		if (!objects.get(position).getNoRecors().equals("")) {
			tt.setText(objects.get(position).getNoRecors());
		} else {
			// if the timestamp != 0 then we have to display the date in the
			// record
			if (objects.get(position).getTimestamp() != null) {
				tt.setText(android.text.format.DateFormat
						.getDateFormat(context).format(
								objects.get(position).getTimestamp()));
			} else {
				// if the exercise event != null we have to display the exercise
				// event
				if (objects.get(position).getExerciseEvent() != null) {
					tt.setText("'E' "
							+ objects.get(position).getExerciseEvent()
									.getDescription());
				} else if (objects.get(position).getMealEvent() != null) {
					tt.setText("'M' " + objects.get(position).getMealEvent().getCalculatedInsulineAmount() + " insuline eenheden");
				} else {
					tt.setText("record");
				}
			}

		}

		tt.setTextSize(fontSize);
		return v;
	}
}
