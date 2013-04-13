/*
 * VectorGraphics2D: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010-2013 Erich Seifert <dev[at]erichseifert.de>
 *
 * This file is part of VectorGraphics2D.
 *
 * VectorGraphics2D is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VectorGraphics2D is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with VectorGraphics2D.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.erichseifert.vectorgraphics2d.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class that contains utility functions for working with data
 * collections like maps or lists.
 */
public abstract class DataUtils {
	/**
	 * Default constructor that prevents creation of class.
	 */
	protected DataUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Creates a mapping from two arrays, one with keys, one with values.
	 * @param <K> Data type of the keys.
	 * @param <V> Data type of the values.
	 * @param keys Array containing the keys.
	 * @param values Array containing the values.
	 * @return Map with keys and values from the specified arrays.
	 */
	public static <K,V> Map<K, V> map(K[] keys, V[] values) {
		// Check for valid parameters
		if (keys.length != values.length) {
			throw new IllegalArgumentException(
					"Number of keys and values is different. " +
					"Cannot create map.");
		}
		// Fill map with keys and values
		Map<K, V> map = new HashMap<K, V>();
		for (int i = 0; i < keys.length; i++) {
			K key = keys[i];
			V value = values[i];
			map.put(key, value);
		}
		return map;
	}

	/**
	 * Returns a string with all float values divided by a specified separator.
	 * @param separator Separator string.
	 * @param elements Float array.
	 * @return Joined string.
	 */
	public static String join(String separator, float... elements) {
		if (elements == null || elements.length == 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer(elements.length*3);
		for (int i = 0; i < elements.length; i++) {
			if (i > 0) {
				sb.append(separator);
			}
			sb.append(elements[i]);
		}
		return sb.toString();
	}

	/**
	 * Returns a string with all float values divided by a specified separator.
	 * @param separator Separator string.
	 * @param elements Double array.
	 * @return Joined string.
	 */
	public static String join(String separator, double... elements) {
		if (elements == null || elements.length == 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer(elements.length*3);
		for (int i = 0; i < elements.length; i++) {
			if (i > 0) {
				sb.append(separator);
			}
			sb.append(elements[i]);
		}
		return sb.toString();
	}

	public static void encodeAscii85(InputStream input, OutputStream output,
			String prefix, String suffix) throws IOException {
		for (byte b : prefix.getBytes()) {
			output.write(b);
		}

	    byte[] bytes = new byte[4];
		byte[] encoded = new byte[5];
		int byteCount;
	    do {
	    	byteCount = input.read(bytes);
	        long uint32 = toUInt32(bytes, byteCount);
	        int padByteCount = 4 - byteCount;
	        int encodedSize = encodeAscii85Chunk(uint32, encoded, padByteCount);
	        output.write(encoded, 0, encodedSize);
	    } while (byteCount == 4);
		for (byte b : suffix.getBytes()) {
			output.write(b);
		}
	}

	private static final long[] POW_85 = { 52200625, 614125, 7225, 85, 1 };

	private static long toUInt32(byte[] bytes, int size) {
		long uint32 = 0L;
	    for (int i = 0; i < 4 && i < size; i++) {
	        uint32 |= (bytes[i] & 0xff) << (3 - i)*8;
	    }
	    return toUnsignedInt(uint32);
	}

	private static int encodeAscii85Chunk(long uint32, byte[] encoded,
			int padByteCount) {
		if (uint32 == 0L && padByteCount == 0) {
			encoded[0] = 'z';
			return 1;
		}
	    int size = 5 - padByteCount;
	    for (int i = 0; i < size; i++) {
	        encoded[i] = (byte) (uint32/POW_85[i]%85 + 33);
	    }
	    return size;
	}

	private static long toUnsignedInt(long x) {
	    return x & 0x00000000ffffffffL;
	}
}
