package be.goossens.oracle.Custom;

import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import be.goossens.oracle.R;
import be.goossens.oracle.Objects.DBTracking;
import be.goossens.oracle.Rest.Functions;

public class CustomArrayAdapterDBTracking extends ArrayAdapter<DBTracking> {
	private Context context;
	private int fontSize;
	private List<DBTracking> objects;
	int test;

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
		LayoutInflater vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		v = vi.inflate(R.layout.row_custom_array_adapter_tracking_date, null);

		// if norecords != "" then
		if (!objects.get(position).getNoRecords().equals("")) {
			v = vi.inflate(R.layout.row_custom_array_adapter_tracking_date,
					null);
			TextView tv = (TextView) v.findViewById(R.id.textViewTracking1);
			tv.setTextSize(fontSize);
			tv.setTextColor(context.getResources().getColor(R.color.ColorText));
			tv.setText(objects.get(position).getNoRecords());
		} else {
			// if the timestamp != 0 then we have to display the date in the
			// record
			if (objects.get(position).getTimestamp() != null) {
				v = vi.inflate(R.layout.row_custom_array_adapter_tracking_date,
						null);
				TextView tv = (TextView) v.findViewById(R.id.textViewTracking1);
				tv.setTextSize(fontSize);
				tv.setBackgroundColor(context.getResources().getColor(
						R.color.ColorBackgroundTrackingDate));
				tv.setTextColor(context.getResources().getColor(
						R.color.ColorTextTrackingDate));
				tv.setGravity(Gravity.CENTER);
				tv.setText(android.text.format.DateFormat
						.getDateFormat(context).format(
								objects.get(position).getTimestamp()));
			} else {
				// if the exercise event != null we have to display the exercise
				// event
				if (objects.get(position).getExerciseEvent() != null) {
					v = vi.inflate(
							R.layout.row_custom_array_adapter_tracking_tracking,
							null);

					ImageView iv = (ImageView) v
							.findViewById(R.id.imageViewTracking);
					TextView tv1 = (TextView) v
							.findViewById(R.id.textViewTracking1);
					TextView tv2 = (TextView) v
							.findViewById(R.id.textViewTracking2);

					TextView tv3 = (TextView) v
							.findViewById(R.id.textViewTracking3);

					TextView tv4 = (TextView) v
							.findViewById(R.id.textViewTracking4);

					iv.setImageDrawable(context.getResources().getDrawable(
							R.drawable.ic_tab_exercise_selected));

					tv1.setText(new Functions().getTimeFromString(objects
							.get(position).getExerciseEvent().getTimeStamp()));

					tv2.setText(objects.get(position).getExerciseEvent()
							.getDescription());
					tv3.setText(new Functions().getDurationFromSeconds(objects
							.get(position).getExerciseEvent().getStartTime(),
							objects.get(position).getExerciseEvent()
									.getEndTime()));

					tv4.setText(objects.get(position).getExerciseEvent()
							.getType());

					tv4.setTextColor(context.getResources().getColor(
							R.color.colorSport));

					tv4.setTextSize(fontSize);

					tv4.setGravity(Gravity.RIGHT);
				} else if (objects.get(position).getMealEvent() != null) {
					v = vi.inflate(
							R.layout.row_custom_array_adapter_tracking_meal,
							null);

					ImageView iv = (ImageView) v
							.findViewById(R.id.imageViewTracking);

					TextView tv1 = (TextView) v
							.findViewById(R.id.textViewTracking1);
					TextView tv2 = (TextView) v
							.findViewById(R.id.textViewTracking2);

					iv.setImageDrawable(context.getResources().getDrawable(
							R.drawable.ic_tab_meal_selected));

					tv1.setText(new Functions().getTimeFromString(objects
							.get(position).getMealEvent().getEventDateTime()));

					tv2.setText(""
							+ objects.get(position).getMealEvent()
									.getCalculatedInsulineAmount() + " " + context.getResources().getString(R.string.showSelectedFoodUnitsInsuline));

					tv2.setTextSize(fontSize);

					tv2.setTextColor(context.getResources().getColor(
							R.color.colorFood));

					tv2.setGravity(Gravity.RIGHT);

				} else if (objects.get(position).getBloodGlucoseEvent() != null) {
					v = vi.inflate(
							R.layout.row_custom_array_adapter_tracking_glucose,
							null);

					ImageView iv = (ImageView) v
							.findViewById(R.id.imageViewTracking);
					TextView tv1 = (TextView) v
							.findViewById(R.id.textViewTracking1);
					TextView tv2 = (TextView) v
							.findViewById(R.id.textViewTracking2);

					iv.setImageDrawable(context.getResources().getDrawable(
							R.drawable.ic_tab_glucose_selected));

					tv1.setText(new Functions().getTimeFromString(objects
							.get(position).getBloodGlucoseEvent()
							.getTimeStamp()));

					tv2.setText(""
							+ objects.get(position).getBloodGlucoseEvent()
									.getAmount()
							+ " "
							+ objects.get(position).getBloodGlucoseEvent()
									.getUnit());

					tv2.setTextColor(context.getResources().getColor(
							R.color.colorGlucose));

					tv2.setGravity(Gravity.RIGHT);

					tv2.setTextSize(fontSize);
				} else {
					v = vi.inflate(
							R.layout.row_custom_array_adapter_tracking_date,
							null);
				}
			}

		}

		return v;
	}
}
