/**
 * 
 */
package com.rainbowagri.profilepage;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * @author G1019 SARAVANAN
 *
 */
public class PincodeAdapter extends ArrayAdapter<String> {
	private Activity context;
	private int ilayout;
	List<String> itemValues = new ArrayList<String>();
	public PincodeAdapter(Activity context, int resource,
			List<String> objects) {
		 super(context, resource, objects);
		   this.context = context;
		   
		   this.itemValues=objects;
		// TODO Auto-generated constructor stub
	}
	public long getItemId(int position) {
	    return position;
	}

	static class ViewHolder {
	    public TextView pincode;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    ViewHolder viewHolder;
	    View rowView = convertView;
	    if (rowView == null) {

	        LayoutInflater layoutInflater= context.getLayoutInflater();
	        rowView = layoutInflater.inflate(R.layout.pincode_textview, null, true);
	        viewHolder = new ViewHolder();
	        viewHolder.pincode = (TextView) rowView.findViewById(R.id.tv);
	        rowView.setTag(viewHolder);
	    } else {
	        viewHolder = (ViewHolder) rowView.getTag();
	    }

	     String value = itemValues.get(position);
	    viewHolder.pincode.setText(value);
	    return rowView;
	}
	}