package com.Fuel.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.Fuel.Entry;
import com.Fuel.Month;
import com.Fuel.R;
import com.Fuel.Storage.StorageManager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.view.View;

public class LitersView extends View {

	private Paint p;
	private Paint chart;
	private Paint line;
	private StorageManager manager;
	
	public LitersView(Context context, StorageManager manager) {
		
		super(context);
		
		this.manager = manager;
		
		this.p = new Paint();
		this.p.setTextSize(12);
		this.p.setColor(Color.BLACK);
		
		this.chart = new Paint();
		this.chart.setColor(Color.RED);
		this.chart.setAntiAlias(true);

		this.line = new Paint();
		this.line.setColor(Color.rgb(200, 200, 200));
		this.line.setPathEffect(new DashPathEffect(new float[] { 3, 3 }, 4));
	}

	@Override
	protected void onDraw(Canvas canvas) {

		int bottom = this.getHeight() - 80;
		int padding = 10;
		
		canvas.drawLine(padding * 3, padding, 
				padding * 3, bottom - padding, this.p);
		canvas.drawLine(padding * 3, bottom - padding, 
				this.getWidth() - padding, bottom - padding, this.p);
		
		Path topArrow = new Path();
		topArrow.moveTo(padding * 3, padding);
		topArrow.lineTo(padding * 3 + 4, padding + 8);
		topArrow.lineTo(padding * 3, padding + 7);
		topArrow.lineTo(padding * 3 - 4, padding + 8);
		topArrow.lineTo(padding * 3, padding);

		Path rightArrow = new Path();
		rightArrow.moveTo((this.getWidth() - padding), bottom - padding);
		rightArrow.lineTo((this.getWidth() - padding) - 8, bottom - padding - 4);
		rightArrow.lineTo((this.getWidth() - padding) - 7, bottom - padding);
		rightArrow.lineTo((this.getWidth() - padding) - 8, bottom - padding + 4);
		rightArrow.lineTo((this.getWidth() - padding), bottom - padding);
		
		canvas.drawPath(topArrow, this.p);
		canvas.drawPath(rightArrow, this.p);
		
		canvas.drawText(getContext().getString(R.string.liter_unit), padding * 1, padding + 8, p);
		
		if (manager != null) {
		
			List<Entry> entries = manager.getEntries();
			List<Double> liters = new ArrayList<Double>();
			List<Month> months = new ArrayList<Month>();
			List<Integer> monthsAdded = new ArrayList<Integer>();

			boolean isFirst = true;
			double liter;
			double tmp = -1;
			double offset = 1;
			int left = padding * 3;
			int top = bottom;
			int scale = 10;
			int offsetCount = 0;
			int count = Math.max(entries.size(), 2);
			int step = Math.max((int)((this.getWidth() - padding * 4) / (count - 1)), 1);
			int lastType = -1;
			int dateSum = 0;
			
			for (Entry entry : entries) {
			
				liter = entry.getLiters();
				
				if (tmp == -1) {
				
					tmp = liter;
					continue;
				}

				if (lastType != entry.getType()) {
				
					lastType = entry.getType();
					
					if (lastType == 0)
						this.chart.setColor(Color.BLACK);
					else
						this.chart.setColor(Color.rgb(0, 120, 0));
				}

				for (double p : liters)
					if (liter > p + offset || liter < p - offset)
						offsetCount++;
				
				if (offsetCount == liters.size()) {
					
					int y = top - (int)(liter * scale);
					canvas.drawText(String.valueOf(Math.round(liter)), 4, y + 4, this.p);
					canvas.drawLine(padding * 3, y, this.getWidth() - padding, y, this.line);
				}
				
				canvas.drawLine(
						left, top - (int)(tmp * scale), 
						left + step, top - (int)(liter * scale), 
						this.chart);

				dateSum = entry.getDateFormatted().get(Calendar.YEAR) + 
				entry.getDateFormatted().get(Calendar.MONTH) + 1;
				
				if (!monthsAdded.contains(dateSum)) {

					months.add(new Month(
							entry.getDateFormatted().get(Calendar.YEAR),
							entry.getDateFormatted().get(Calendar.MONTH) + 1, 
							new Point(left, top - (int)(tmp * scale))));
					
					monthsAdded.add(dateSum);
					
					if (isFirst) {

						months.get(months.size() - 1).increaseCount();
						isFirst = false;
					}
				}
				else
					months.get(months.size() - 1).increaseCount();
				
				offsetCount = 0;
				tmp = liter;
				left += step;

				if (!liters.contains(liter))
					liters.add(liter);
			}
			
			for (Month month : months)
				canvas.drawLine(month.getPosition().x, bottom - 14, month.getPosition().x, bottom - 4, this.p);
				
			isFirst = true;
			
			this.p.setAntiAlias(true);
			this.p.setTextSize(14);
			this.line.setColor(Color.rgb(140, 200, 240));
			
			for (Month month : months) {

				if (!isFirst)
					canvas.drawLine(month.getPosition().x, month.getPosition().y, month.getPosition().x, bottom - 10, this.line);

				canvas.rotate(45, month.getPosition().x - 2, bottom + 8);
				
				canvas.drawText(String.format(getContext().getString(R.string.m), 
						month.getMonth(), month.getYear(), month.getCount()), 
						month.getPosition().x - 2, bottom + 8, this.p);

				canvas.rotate(-45, month.getPosition().x - 2, bottom + 8);
				isFirst = false;
			}
			
			this.p.setAntiAlias(false);
			this.p.setTextSize(12);
			this.line.setColor(Color.rgb(200, 200, 200));
		}
		
		super.onDraw(canvas);
	}
}