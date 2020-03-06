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
import android.graphics.PointF;
import android.view.View;

public class PriceView extends View {

	private Paint mPaint;
	private Paint mChartPaint;
	private Paint mLinePaint;
	private StorageManager mManager;
	
	private List<Entry> mEntries;
	
	public PriceView(Context context, StorageManager manager) {
		
		super(context);

		this.mManager = manager;
		this.mEntries = mManager.getEntries();
		
		this.mPaint = new Paint();
		this.mPaint.setTextSize(12);
		this.mPaint.setColor(Color.BLACK);
		this.mPaint.setAntiAlias(true);
		
		this.mChartPaint = new Paint();
		this.mChartPaint.setColor(Color.RED);
		this.mChartPaint.setAntiAlias(true);

		this.mLinePaint = new Paint();
		this.mLinePaint.setColor(Color.rgb(100, 100, 100));
		this.mLinePaint.setPathEffect(new DashPathEffect(new float[] { 3, 3 }, 4));
	}

	@Override
	protected void onDraw(Canvas canvas) {

		final int padding = 10;
		
		this.drawAxes(canvas, padding);
		this.drawChart(canvas, padding);		
	}
	
	private void drawAxes(Canvas canvas, int padding) {

		final int width = this.getWidth();
		final int bottom = this.getHeight() - 40 - padding;
		final int padding3 = padding * 3;
		
		canvas.drawLine(padding3, padding,  padding3, bottom, this.mLinePaint);
		canvas.drawLine(padding3, bottom,  width - padding, bottom, this.mLinePaint);
		
		Path topArrow = new Path();
		topArrow.moveTo(padding3, padding);
		topArrow.lineTo(padding3 + 4, padding + 8);
		topArrow.lineTo(padding3, padding + 7);
		topArrow.lineTo(padding3 - 4, padding + 8);
		topArrow.lineTo(padding3, padding);

		Path rightArrow = new Path();
		rightArrow.moveTo((width - padding), bottom);
		rightArrow.lineTo((width - padding) - 8, bottom - 4);
		rightArrow.lineTo((width - padding) - 7, bottom);
		rightArrow.lineTo((width - padding) - 8, bottom + 4);
		rightArrow.lineTo((width - padding), bottom);
		
		canvas.drawPath(topArrow, this.mLinePaint);
		canvas.drawPath(rightArrow, this.mLinePaint);	
	}
	
	private void drawChart(Canvas canvas, int padding) {

		final double weightOffset = 0.7d;
		
		double minPrice = Double.MAX_VALUE;
		double maxPrice = Double.MIN_VALUE;
		
		for (Entry entry : this.mEntries) {
		
			minPrice = Math.min(entry.getPrice(), minPrice);
			maxPrice = Math.max(entry.getPrice(), maxPrice);
		}
		
		minPrice -= weightOffset;
		maxPrice += weightOffset;
		
		minPrice = Math.round(minPrice);
		maxPrice = Math.round(maxPrice);
		
		final int areaWidth = this.getWidth() - padding * 3;
		final int areaHeight = (this.getHeight() - 40 - padding);
		final double stepCount = (double)(maxPrice - minPrice);
		final double size = (double)areaHeight / (double)stepCount;
		final double scale = (double)areaWidth / (double)(this.mEntries.size());
		
		int count = this.mEntries.size();
		Entry entry;
		
		PointF start = new PointF();
		PointF end;
		int index = 0;
		
		for (int i = 0; i < count; i++) {
		
			entry = this.mEntries.get(i);
		
			if (index == 0) {

				start = new PointF(
						(float)(index * scale + padding * 3), 
						(float)((maxPrice - entry.getPrice()) * size));
				
				index++;
				continue;
			}
			
			end = new PointF(
					(float)(index * scale + padding * 3), 
					(float)((maxPrice - entry.getPrice()) * size));

			this.mPaint.setColor(entry.getType() == 0 ? Color.BLACK : Color.rgb(0, 120, 0));
			
			canvas.drawLine(
				start.x, start.y, 
				end.x, end.y, 
				this.mPaint);
			
			start = end;
			index++;
		}
	}
	
	private void drawOldChart(Canvas canvas) {

		int bottom = this.getHeight() - 80;
		int padding = 10;
		canvas.drawLine(padding * 3, padding, 
				padding * 3, bottom - padding, this.mPaint);
		canvas.drawLine(padding * 3, bottom - padding, 
				this.getWidth() - padding, bottom - padding, this.mPaint);
		
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
		
		canvas.drawPath(topArrow, this.mPaint);
		canvas.drawPath(rightArrow, this.mPaint);

		canvas.drawText(getContext().getString(R.string.price_unit), padding - 3, padding + 8, mPaint);
		
		if (mManager != null) {
		
			List<Entry> entries = mManager.getEntries();
			List<Double> prices = new ArrayList<Double>();
			List<Double> chartPrices = new ArrayList<Double>();
			List<Month> months = new ArrayList<Month>();
			List<Integer> monthsAdded = new ArrayList<Integer>();

			boolean isFirst = true;
			double price;
			double tmp = -1;
			double offset = 0.05d;
			int left = padding * 3;
			int top = this.getHeight() + padding * 230;
			int scale = 540;
			int offsetCount = 0;
			int count = Math.max(entries.size(), 2);
			int step = Math.max((int)((this.getWidth() - padding * 4) / (count - 1)), 1);
			int lastType = -1;
			int dateSum = 0;
			
			for (Entry entry : entries) {
			
				price = entry.getPrice();
				
				if (tmp == -1) {
				
					tmp = price;
					continue;
				}

				if (lastType != entry.getType()) {
				
					lastType = entry.getType();
					
					if (lastType == 0)
						this.mChartPaint.setColor(Color.BLACK);
					else
						this.mChartPaint.setColor(Color.rgb(0, 120, 0));
				}
				
				for (double p : prices)
					if (price > p + offset || price < p - offset)
						offsetCount++;
				
				if (offsetCount == prices.size() || this.containsBorders(chartPrices, price, offset)) {
					
					int y = top - (int)(price * scale);
					canvas.drawText(String.format(getContext().getString(R.string.price_format_only), price), 4, y + 4, this.mPaint);
					canvas.drawLine(padding * 3, y, this.getWidth() - padding, y, this.mLinePaint);
					chartPrices.add(price);
				}
				
				canvas.drawLine(
						left, top - (int)(tmp * scale), 
						left + step, top - (int)(price * scale), 
						this.mChartPaint);

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
				tmp = price;
				left += step;
				
				if (!prices.contains(price))
					prices.add(price);
			}
			
			for (Month month : months)
				canvas.drawLine(month.getPosition().x, bottom - 14, month.getPosition().x, bottom - 4, this.mPaint);

			isFirst = true;
			
			this.mPaint.setTextSize(14);
			this.mPaint.setAntiAlias(true);
			this.mLinePaint.setColor(Color.rgb(140, 200, 240));
			
			for (Month month : months) {

				if (!isFirst)
					canvas.drawLine(month.getPosition().x, month.getPosition().y, month.getPosition().x, bottom - 10, this.mLinePaint);

				canvas.rotate(45, month.getPosition().x - 2, bottom + 8);
				
				canvas.drawText(String.format(getContext().getString(R.string.m), 
						month.getMonth(), month.getYear(), month.getCount()), 
						month.getPosition().x - 2, bottom + 8, this.mPaint);

				canvas.rotate(-45, month.getPosition().x - 2, bottom + 8);
				isFirst = false;
			}
			
			this.mPaint.setAntiAlias(false);
			this.mPaint.setTextSize(12);
			this.mLinePaint.setColor(Color.rgb(200, 200, 200));
		}
	}
	
	private boolean containsBorders(List<Double> list, double value, double offset) {
		
		int count = 0;
		
		for (Double v : list) {
			
			if (v == value)
				return false;
			
			if (value > v + offset || value < v - offset)
				count++;
		}
		
		return count >= list.size();
	}
}