package com.example.p_c.masterycar.camera.bean;

import android.graphics.Bitmap;

public class VideoRecordBean {
	private byte[] length;
	private byte[] time;
	private byte[] picture;
	private int tspan;
	private int type;
	private Bitmap bitmap;
	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	private int width;//
	private int height;//

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getTspan() {
		return tspan;
	}

	public void setTspan(int tspan) {
		this.tspan = tspan;
	}

	public byte[] getLength() {
		return length;
	}

	public void setLength(byte[] length) {
		this.length = length;
	}

	public byte[] getTime() {
		return time;
	}

	public void setTime(byte[] time) {
		this.time = time;
	}

	public byte[] getPicture() {
		return picture;
	}

	public void setPicture(byte[] picture) {
		this.picture = picture;
	}

}
