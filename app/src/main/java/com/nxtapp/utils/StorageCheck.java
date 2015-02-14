package com.nxtapp.utils;

import android.os.Environment;
import android.os.StatFs;

public class StorageCheck {
	public static boolean isFreeSpace(String path) {

		StatFs stat = new StatFs(path);
		double sdAvailSize = (double) stat.getAvailableBlocks()
				* (double) stat.getBlockSize();
		double megaAvailable = sdAvailSize / 1048576;
		if (megaAvailable > 10) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isSDCardMounted() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}
}
