/* Copyright (c) 2011-2012 Tank Tang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aretha.content;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.aretha.util.Utils;

import android.content.Context;

/**
 * A simple class to manage the cached file which saved in the
 * /data/data/{package name}/files/ folder or a specified directory
 * 
 * @author Tank
 */
public class FileCacheManager {
	private File mCacheFilePath;

	public FileCacheManager(Context context) {
		if (null == context) {
			throw new IllegalArgumentException("context can not be null");
		}
		mCacheFilePath = context.getFilesDir();
	}

	/**
	 * This method allow developer to specified a location for cache file to
	 * store in
	 * 
	 * @param directory
	 */
	public void setDefaultStorageDirectory(File directory) {
		if (directory == null || !directory.isDirectory()
				|| !directory.canWrite()) {
			return;
		}
		mCacheFilePath = directory;
	}

	/**
	 * Save a file to the cache folder
	 * 
	 * @param cacheIdentifier
	 *            cache file identifier
	 * @param inputstream
	 * 
	 * @return success or not
	 */
	public boolean writeCacheFile(String cacheIdentifier,
			InputStream inputStream) {
		File cacheFile = createFile(cacheIdentifier);
		try {
			if (!cacheFile.exists()) {
				cacheFile.createNewFile();
			}

			BufferedOutputStream fileOutputStream = new BufferedOutputStream(
					new FileOutputStream(cacheFile));

			int read;
			byte[] buffer = new byte[512];
			while ((read = inputStream.read(buffer)) != -1) {
				fileOutputStream.write(buffer, 0, read);
			}
			fileOutputStream.flush();
			return true;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		return false;
	}

	/**
	 * Read a cached file
	 * 
	 * @param cacheIdentifier
	 * @return Cache file's {@link InputStream}
	 */
	public InputStream readCacheFile(String cacheIdentifier) {
		try {
			return new FileInputStream(createFile(cacheIdentifier));
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	/**
	 * Check whether the cached file exist
	 * 
	 * @param cacheIdentifier
	 * @return
	 */
	public boolean hasCacheFile(String cacheIdentifier) {
		File cacheFile = createFile(cacheIdentifier);
		return hasCacheFile(cacheFile);
	}

	/**
	 * Check whether the cached file exist
	 * 
	 * @param cacheFile
	 * @return
	 */
	public boolean hasCacheFile(File cacheFile) {
		return cacheFile.exists();
	}

	/**
	 * Delete cached file
	 * 
	 * @param cacheIdentifier
	 * @return
	 */
	public boolean deleteCache(String cacheIdentifier) {
		File cacheFile = createFile(cacheIdentifier);
		boolean isExists = hasCacheFile(cacheFile);

		boolean isDelete = cacheFile.delete();

		return isExists && isDelete;
	}

	/**
	 * Clear all cached files
	 * 
	 * @return
	 */
	public boolean clearAllCaches() {
		File[] files = mCacheFilePath.listFiles();
		for (File file : files) {
			if (!file.isDirectory()) {
				file.delete();
			}
		}
		return mCacheFilePath.delete();
	}

	/**
	 * Create a {@link File} as the rule
	 * 
	 * @param cacheIdentifier
	 * @return
	 */
	private File createFile(String cacheIdentifier) {
		if (null == cacheIdentifier) {
			throw new IllegalArgumentException(
					"cacheIdentifier can not be null");
		}

		String cacheFileName = Utils.getUUID(cacheIdentifier).toString();
		return new File(mCacheFilePath, cacheFileName);
	}
}
