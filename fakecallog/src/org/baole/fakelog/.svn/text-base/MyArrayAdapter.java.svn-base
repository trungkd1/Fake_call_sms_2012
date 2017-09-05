package org.baole.fakelog;

import java.util.ArrayList;

import org.baole.fakelog.model.FileItems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyArrayAdapter extends ArrayAdapter<FileItems> {

	private ArrayList<FileItems> arrayList;
	private Context mContext;
	private int TypeEntry;

	public MyArrayAdapter(Context context, ArrayList<FileItems> arraylist,
			int TypeEntry) {
		super(context, R.layout.list_content, arraylist);
		this.arrayList = arraylist;
		this.mContext = context;
		this.TypeEntry = TypeEntry;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View row = inflater.inflate(R.layout.items, parent, false);
		TextView textNumber = (TextView) row.findViewById(R.id.textNumber);
		
		TextView textDateSchedule = (TextView) row
				.findViewById(R.id.textDateSchedule);
		if (TypeEntry == 1) {
			TextView textCallType = (TextView)row.findViewById(R.id.textCallType);
			textCallType.setVisibility(TextView.VISIBLE);
			if(arrayList.get(position).getType() == 1)
				textCallType.setText(R.string.incoming);
			else if(arrayList.get(position).getType() == 2)
				textCallType.setText(R.string.outgoing);
			else
				textCallType.setText(R.string.missed);
			textNumber.setText(arrayList.get(position).getNumber());
			textDateSchedule.setText(arrayList.get(position).DateSchedule);
		} else {
			TextView textContent = (TextView) row.findViewById(R.id.textContent);
			textContent.setVisibility(TextView.VISIBLE);
			textNumber.setText(arrayList.get(position).getNumber());
			textContent.setText("Message: " +arrayList.get(position).getMessage());
			textDateSchedule.setText(arrayList.get(position).DateSchedule);
		}

		return row;
	}
	
	public void NotifyDataSetChanged(FileItems fileitem){
		this.remove(fileitem);
		this.notifyDataSetChanged();
		
		
	}

}
