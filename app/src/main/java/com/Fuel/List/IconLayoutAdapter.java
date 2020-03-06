package com.Fuel.List;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class IconLayoutAdapter extends BaseAdapter {

	private Context context;
	private List<IconFile> items;
	
	public IconLayoutAdapter(Context context) {

		this.setContext(context);
		this.items = new LinkedList<IconFile>();
	}
	
	public void addItem(int id, int icon, double liters, double cost, long date, int distance, double price, int type) {
	
		IconFile file = new IconFile(this.getContext());
		file.setId(id);
		file.setIcon(icon);
		file.setCost(cost);
		file.setLiters(liters);
		file.setDate(date);
		file.setDistance(distance);
		file.setPrice(price);
		file.setType(type);
		
		this.items.add(file);
	}
	
	public void clear() {
	
		this.items.clear();
	}
	
	@Override
	public int getCount() {

		return this.items.size();
	}

	@Override
	public Object getItem(int position) {

		return this.items.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		IconLinearLayout v;
		
		if (convertView == null)
			v = new IconLinearLayout(this.getContext());
		else
			v = (IconLinearLayout)convertView;
		
		v.setIconFile(this.items.get(position));
		
		return v;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return context;
	}

}