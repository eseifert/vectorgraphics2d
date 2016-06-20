package de.erichseifert.vectorgraphics2d.pdf;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a dictionary in the sense of the <i>Portable Document Format</i>.
 * A dictionary can store and retrieve key-value pairs.
 */
public class PDFDictionary {
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
