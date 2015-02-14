package com.nxtapp.classes;

import android.graphics.Bitmap;

public class Item {

	public final String text;
	public final Bitmap icon;
	public String shareActivityName;
	public String PackageName;

	public Item(String text, Bitmap icon, String shareActivityName,
			String PackageName) {
		this.text = text;
		this.icon = icon;
		this.shareActivityName = shareActivityName;
		this.PackageName = PackageName;
	}

	@Override
	public String toString() {
		return text;
	}
}
