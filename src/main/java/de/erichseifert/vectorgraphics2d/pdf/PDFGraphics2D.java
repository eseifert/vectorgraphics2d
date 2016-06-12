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

import java.awt.BasicStroke;
import java.awt.Color;

import de.erichseifert.vectorgraphics2d.ProcessingPipeline;
import de.erichseifert.vectorgraphics2d.Processor;

/**
 * {@code Graphics2D} implementation that saves all operations to a string
 * in the <i>Portable Document Format</i> (PDF).
 */
public class PDFGraphics2D extends ProcessingPipeline {
	private final PDFProcessor processor;

	/**
	 * Initializes a new VectorGraphics2D pipeline for translating Graphics2D
	 * commands to PDF data. The document dimensions must be specified as
	 * parameters.
	 * @param x Left offset.
	 * @param y Top offset
	 * @param width Width.
	 * @param height Height.
	 */
	public PDFGraphics2D(double x, double y, double width, double height) {
		this(x, y, width, height, false);
	}

	/**
	 * Initializes a new VectorGraphics2D pipeline for translating Graphics2D
	 * commands to PDF data. The document dimensions must be specified as
	 * parameters.
	 * @param x Left offset.
	 * @param y Top offset
	 * @param width Width.
	 * @param height Height.
	 * @param compressed Compression enabled.
	 */
	public PDFGraphics2D(double x, double y, double width, double height, boolean compressed) {
		super(x, y, width, height);
		processor = new PDFProcessor(compressed);

		// TODO: Default graphics state does not need to be printed in the document
		setColor(Color.BLACK);
		setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, null, 0f));
	}

	@Override
	protected Processor getProcessor() {
		return processor;
	}

	/**
	 * Returns whether the current PDF document is compressed.
	 * @return {@code true} if the document is compressed, {@code false} otherwise.
	 */
	public boolean isCompressed() {
		return processor.isCompressed();
	}
}
