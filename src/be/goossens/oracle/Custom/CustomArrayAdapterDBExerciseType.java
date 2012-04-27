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
	private int layout;
	
	public CustomArrayAdapterDBExerciseType(Context context,
			int textViewResourceId, List<DBExerciseType> objects, int fontSize) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.fontSize = fontSize;
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
		TextView tt = (TextView) v.findViewById(R.id.text1);
		TextView ttTwo = (TextView) v.findViewById(R.id.text2);
		
		String name = objects.get(position).getName();
		
		if(name.length()>10){
			name = name.substring(0,9) + "...";
		}
		
		tt.setText(name);
		
		tt.setTextSize(fontSize);
		
		if (position % 2 == 0) {
			tt.setBackgroundColor(context.getResources().getColor(
					R.color.ColorListViewOne));
			ttTwo.setBackgroundColor(context.getResources().getColor(
					R.color.ColorListViewOne));
		} else { 
			tt.setBackgroundColor(context.getResources().getColor( 
					R.color.ColorListViewTwo));
			ttTwo.setBackgroundColor(context.getResources().getColor( 
					R.color.ColorListViewTwo));
		} 
		
		return v;
	}

}
