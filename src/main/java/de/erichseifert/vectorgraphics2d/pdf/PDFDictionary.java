/*
 * VectorGraphics2D: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010-2016 Erich Seifert <dev[at]erichseifert.de>,
 * Michael Seifert <mseifert[at]error-reports.org>
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
package de.erichseifert.vectorgraphics2d.pdf;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a dictionary in the sense of the <i>Portable Document Format</i>.
 * A dictionary can store and retrieve key-value pairs.
 */
public class PDFDictionary implements PDFObject {
	private final Map<String, Object> dict;

	/**
	 * Initializes a new {@code PDFDictionary}.
	 */
	public PDFDictionary() {
		this.dict = new HashMap<String, Object>();
	}

	/**
	 * Inserts or updates the specified key-value pair.
	 * @param key Key associated with the value.
	 * @param value Value to be set.
	 */
	public void put(String key, Object value) {
		dict.put(key, value);
	}

	/**
	 * Returns the value stored under the specified key.
	 * @param key Key associated with the value.
	 * @return Stored {@code Object}.
	 */
	public Object get(String key) {
		return dict.get(key);
	}
}
