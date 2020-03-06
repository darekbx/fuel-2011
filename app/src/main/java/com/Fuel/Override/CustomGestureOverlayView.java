package com.Fuel.Override;

import android.content.Context;
import android.gesture.GestureOverlayView;
import android.util.AttributeSet;

public class CustomGestureOverlayView extends GestureOverlayView {

	private boolean createNew = false;
	
	public CustomGestureOverlayView(Context context, AttributeSet attrs) {
		
		super(context, attrs);
	}
	
	public void setCreateNew(boolean createNew) {
	
		this.createNew = createNew;
	}
	
	public boolean isCreateNew() {
	
		return this.createNew;
	}
}