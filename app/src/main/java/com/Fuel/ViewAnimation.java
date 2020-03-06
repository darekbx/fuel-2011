package com.Fuel;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

public class ViewAnimation extends Animation {

	private Camera camera = new Camera();
	
	@Override
	public void initialize(int width, int height, int parentWidth, int parentHeight) {

		super.initialize(width, height, parentWidth, parentHeight);
		this.setDuration(800);
		this.setStartOffset(300);
		this.setFillAfter(true);
		this.setInterpolator(new LinearInterpolator());
	}
	
	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		
		final Matrix matrix = t.getMatrix();
		
		this.camera.save();
		this.camera.translate(0.0f, 0.0f, (1000 - 1000.0f * interpolatedTime));
		this.camera.rotateY(360 * interpolatedTime);
		this.camera.getMatrix(matrix);
		
		matrix.preTranslate(-240, -400);
		matrix.postTranslate(240, 400);
		
		this.camera.restore();
	}
}