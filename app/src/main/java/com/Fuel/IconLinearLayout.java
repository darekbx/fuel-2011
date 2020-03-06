package com.Fuel;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IconLinearLayout extends LinearLayout {

	private ImageView icon;
	private TextView liters;
	private TextView cost;
	private TextView date;
	private TextView price;
	private TextView distance;
	
	public IconLinearLayout(Context context) {
		
		super(context);
		
		this.setOrientation(LinearLayout.HORIZONTAL);
		
		float ratio = (float)Static.SCREEN_WIDTH / 320f;
		
		this.icon = new ImageView(context);
		this.icon.setImageResource(R.drawable.diesel);
		this.icon.setPadding(0, 1, 0, 0);
		
		this.addView(this.icon, 
				new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		this.liters = new TextView(context);
		this.liters.setPadding(4, 6, 0, 0);

		this.addView(this.liters, 
				new LinearLayout.LayoutParams((int)(48 * ratio), LayoutParams.WRAP_CONTENT));

		this.cost = new TextView(context);
		this.cost.setPadding(4, 6, 0, 0);
		
		this.addView(this.cost, 
				new LinearLayout.LayoutParams((int)(58 * ratio), LayoutParams.WRAP_CONTENT));

		this.price = new TextView(context);
		this.price.setPadding(4, 6, 0, 0);
		
		this.addView(this.price, 
				new LinearLayout.LayoutParams((int)(48 * ratio), LayoutParams.WRAP_CONTENT));

		this.distance = new TextView(context);
		this.distance.setPadding(4, 6, 0, 0);
		
		this.addView(this.distance, 
				new LinearLayout.LayoutParams((int)(58 * ratio), LayoutParams.WRAP_CONTENT));

		this.date = new TextView(context);
		this.date.setPadding(4, 6, 0, 0);
		
		this.addView(this.date, 
				new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	}
	
	public void setIconFile(IconFile file) {

		String literFormat = getContext().getString(R.string.liter_format);
		String costFormat = getContext().getString(R.string.cost_format);
		String priceFormat = getContext().getString(R.string.price_format);
		String distanceFormat = getContext().getString(R.string.distance_format);
		
		this.icon.setImageResource(file.getIcon());
		this.date.setText(file.getDate());
		this.liters.setText(String.format(literFormat, String.valueOf(file.getLiters())));
		this.cost.setText(String.format(costFormat, String.valueOf(file.getCost())));
		this.price.setText(String.format(priceFormat, file.getPrice()));
		this.distance.setText(String.format(distanceFormat, String.valueOf(file.getDistance()))); 
	}
	
}