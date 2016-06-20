package de.erichseifert.vectorgraphics2d.pdf;

/**
 * Represents a page of a PDF document.
 */
class Page extends PDFDictionary {
	/**
	 * Initializes a {@code Page}.
	 * Sets the {@literal Type} entry of this object to {@literal Page}.
	 */
	public Page() {
		put("Type", "Page");
	}
}

