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

import de.erichseifert.vectorgraphics2d.Document;
import de.erichseifert.vectorgraphics2d.VectorGraphics2D;
import de.erichseifert.vectorgraphics2d.intermediate.commands.Command;
import de.erichseifert.vectorgraphics2d.intermediate.filters.AbsoluteToRelativeTransformsFilter;
import de.erichseifert.vectorgraphics2d.intermediate.filters.FillPaintedShapeAsImageFilter;
import de.erichseifert.vectorgraphics2d.intermediate.filters.StateChangeGroupingFilter;
import de.erichseifert.vectorgraphics2d.util.PageSize;

/**
 * {@code Graphics2D} implementation that saves all operations to a string
 * in the <i>Portable Document Format</i> (PDF).
 */
public class PDFGraphics2D extends VectorGraphics2D {
	private final boolean compressed;

	/**
	 * Initializes a new VectorGraphics2D pipeline for translating Graphics2D
	 * commands to PDF data. The document dimensions must be specified as
	 * parameters.
	 * @param pageSize Document size.
	 */
	public PDFGraphics2D(PageSize pageSize) {
		this(pageSize, false);
	}

	/**
	 * Initializes a new VectorGraphics2D pipeline for translating Graphics2D
	 * commands to PDF data. The document dimensions must be specified as
	 * parameters.
	 * @param pageSize Document size.
	 * @param compressed Compression enabled.
	 */
	public PDFGraphics2D(PageSize pageSize, boolean compressed) {
		super(pageSize);
		this.compressed = compressed;
	}

	/**
	 * Returns whether the current PDF document is compressed.
	 * @return {@code true} if the document is compressed, {@code false} otherwise.
	 */
	public boolean isCompressed() {
		return compressed;
	}

	@Override
	protected Document process(Iterable<Command<?>> commands) {
		AbsoluteToRelativeTransformsFilter absoluteToRelativeTransformsFilter = new AbsoluteToRelativeTransformsFilter(commands);
		FillPaintedShapeAsImageFilter paintedShapeAsImageFilter = new FillPaintedShapeAsImageFilter(absoluteToRelativeTransformsFilter);
		Iterable<Command<?>> filtered = new StateChangeGroupingFilter(paintedShapeAsImageFilter);
		PDFDocument doc = new PDFDocument(getPageSize(), isCompressed());
		for (Command<?> command : filtered) {
			doc.handle(command);
		}
		doc.close();
		return doc;
	}
}
