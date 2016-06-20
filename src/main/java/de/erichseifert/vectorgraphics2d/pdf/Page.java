package de.erichseifert.vectorgraphics2d.pdf;

import java.awt.geom.Rectangle2D;

/**
 * Represents a page of a PDF document.
 */
class Page extends PDFDictionary {
	/**
	 * Initializes a {@code Page}.
	 * Sets the {@literal Type} entry of this object to {@literal Page}.
	 * Sets the {@literal MediaBox} entry.
	 * @param mediaBox Boundaries of the page.
	 */
	public Page(Rectangle2D mediaBox) {
		put("Type", "Page");
		put("MediaBox", mediaBox);
	}
}

