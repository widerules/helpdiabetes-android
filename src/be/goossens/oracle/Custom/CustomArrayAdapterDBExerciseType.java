package be.goossens.oracle.Custom;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import be.goossens.oracle.R;
import be.goossens.oracle.Objects.DBExerciseType;

public class CustomArrayAdapterDBExerciseType extends ArrayAdapter<DBExerciseType> {

	private Context context;
	private int fontSize;
	private List<DBExerciseType> objects;
	
	public CustomArrayAdapterDBExerciseType(Context context,
			int textViewResourceId, List<DBExerciseType> objects, int fontSize) {
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
		String name = objects.get(position).getName();
		if(name.length()>10){
			name = name.substring(0,9) + "...";
		}
		tt.setText(name);
		tt.setTextSize(fontSize);
		return v;
	}

}
