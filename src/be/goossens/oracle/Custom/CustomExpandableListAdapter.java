// Please read info.txt for license and legal information

package be.goossens.oracle.Custom;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import be.goossens.oracle.R;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

	private Context context;
	private ArrayList<ArrayList<String>> children;
	private String calculatedInsuline = "";
	private String defaultCalculated = "";
	private String insulineRatio = "";
	private String defaultCalculatedText = "";
	private int fontSize;
	private int defaultValue;

	public void clear() {
		calculatedInsuline = "";
		defaultCalculated = "";
		insulineRatio = "";
		defaultCalculatedText = "";
		defaultValue = 1;
		children = new ArrayList<ArrayList<String>>();
		children.add(new ArrayList<String>());
	}

	public int getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(int defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getInsulineRatio() {
		return insulineRatio;
	}

	public void setInsulineRatio(String insulineRatio) {
		this.insulineRatio = insulineRatio;
	}

	public String getDefaultCalculatedText() {
		return defaultCalculatedText;
	}

	public void setDefaultCalculatedText(String defaultCalculatedText) {
		this.defaultCalculatedText = defaultCalculatedText;
	}

	public CustomExpandableListAdapter(Context context, int fontSize) {
		this.context = context;
		this.fontSize = fontSize;
	}

	public String getCalculatedInsuline() {
		return calculatedInsuline;
	}

	public void setCalculatedInsuline(String calculatedInsuline) {
		this.calculatedInsuline = calculatedInsuline;
	}

	public String getDefaultCalculated() {
		return defaultCalculated;
	}

	public void setDefaultCalculated(String defaultCalculated) {
		this.defaultCalculated = defaultCalculated;
	}

	/*
	 * This method allows you to add a string to the list
	 */
	public void addItem(String item) {
		if (children == null) {
			children = new ArrayList<ArrayList<String>>();
			children.add(new ArrayList<String>());
		}
		children.get(0).add(item);
	}

	public Object getChild(int groupPosition, int childPosition) {
		return children.get(groupPosition).get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	// return a child view. with a custom layout
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		String item = (String) getChild(groupPosition, childPosition);
		if (convertView == null) {
			LayoutInflater infl = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infl
					.inflate(
							R.layout.row_custom_expandable_listview_adapter_child_layout,
							null);
		}

		TextView tv = (TextView) convertView.findViewById(R.id.tv1);
		tv.setText(item);
		tv.setTextSize(fontSize);
		return convertView;
	}

	public int getChildrenCount(int groupPosition) {
		return children.get(groupPosition).size();
	}

	public Object getGroup(int groupPosition) {
		return "";
	}

	public int getGroupCount() {
		return 1;
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	// return a group view with a custom layout
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater infl = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infl
					.inflate(
							R.layout.row_custom_expandable_listview_adapter_group_layout,
							null);
		}

		TextView tv1 = (TextView) convertView.findViewById(R.id.tv1);
		TextView tv2 = (TextView) convertView.findViewById(R.id.tv2);
		TextView tv3 = (TextView) convertView.findViewById(R.id.tv3);
		TextView tv4 = (TextView) convertView.findViewById(R.id.tv4);
		LinearLayout ll = (LinearLayout) convertView
				.findViewById(R.id.LinearLayoutInsuline);

		tv1.setTextSize(fontSize);
		tv2.setTextSize(fontSize - 7);

		tv3.setTextSize(fontSize);
		tv4.setTextSize(fontSize - 7);

		if (calculatedInsuline.equals("")) {
			ll.setVisibility(View.GONE);
		} else {
			ll.setVisibility(View.VISIBLE);
			tv1.setText(calculatedInsuline);
			tv2.setText(insulineRatio);
		}
		
		tv3.setText(defaultCalculated);
		tv4.setText(defaultCalculatedText);

		switch (defaultValue) {
		case 1:
			tv3.setBackgroundColor(context.getResources().getColor(
					R.color.ColorBackgroundTrackingDate));
			break;
		case 2:
			tv3.setBackgroundColor(context.getResources().getColor(
					R.color.colorSport));
			break;
		case 3:
			tv3.setBackgroundColor(context.getResources().getColor(
					R.color.colorGlucose));
			break;
		case 4:
			tv3.setBackgroundColor(context.getResources().getColor(
					R.color.colorInsuline));
			break;
		}

		return convertView;
	}

	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}

}
