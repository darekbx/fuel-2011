package com.Fuel;

import java.util.Calendar;
import android.content.Context;

public class IconFile {

	private Context context;
	private int icon;
	private double cost;
	private double liters;
	private long date;
	private int distance;
	private double price;
	private int type;
	private int id;
	
	public IconFile(Context context) {
	
		this.context = context;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public void setIcon(int icon) {
		this.icon = icon;
	}
	
	public int getIcon() {
		return icon;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public double getCost() {
		return cost;
	}

	public void setLiters(double liters) {
		this.liters = liters;
	}

	public double getLiters() {
		return liters;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public String getDate() {
		
		if (this.date == -1)
			return null;
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(this.date * 1000);
		
		String year = String.valueOf(cal.get(Calendar.YEAR));
		
		return String.format(this.context.getString(R.string.date_format), 
				cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, year);
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getDistance() {
		return distance;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getPrice() {
		return price;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}
}