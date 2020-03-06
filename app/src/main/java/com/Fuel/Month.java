package com.Fuel;

import android.graphics.Point;

public class Month {

	public Month(int year, int month, Point position) {
	
		this.year = year;
		this.month = month;
		this.position = position;
	}
	
	public Month() { }
	
	public int getYear() {
		return year;
	}
	
	public void setYear(int year) {
		this.year = year;
	}
	
	public int getMonth() {
		return month;
	}
	
	public void setMonth(int month) {
		this.month = month;
	}
	
	public void setPosition(Point position) {
		this.position = position;
	}

	public Point getPosition() {
		return position;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getCount() {
		return count;
	}
	
	public void increaseCount() {
		this.count++;
	}

	private int year;
	private int month;
	private int count = 1;
	private Point position;
}