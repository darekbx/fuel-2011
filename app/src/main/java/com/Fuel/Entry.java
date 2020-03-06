package com.Fuel;

import java.util.Calendar;

public class Entry {

	private int id;
	private double liters;
	private double cost;
	private long date;
	private int type;
	
	public Entry() { }
	
	public Entry(double liters, double cost, int type) { 
	
		this.liters = liters;
		this.cost = cost;
		this.type = type;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public double getLiters() {
		return liters;
	}
	
	public void setLiters(double liters) {
		this.liters = liters;
	}
	
	public double getCost() {
		return cost;
	}
	
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	public long getDate() {
		return date;
	}
	
	public Calendar getDateFormatted() {
	
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(this.date * 1000);
		
		return calendar;
	}
	
	public void setDate(long date) {
		this.date = date;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public int getDistance() {
		
		float consumption = Static.DEFAULT_CONSUMPTION_PB;
		
		if (this.type == 0)
			consumption = Static.DEFAULT_CONSUMPTION;
		
		return (int)((this.liters / consumption) * 100);
	}

	public double getPrice() {
		
		return this.cost / this.liters;
	}
}