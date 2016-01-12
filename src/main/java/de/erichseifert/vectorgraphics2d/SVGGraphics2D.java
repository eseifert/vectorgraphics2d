/*
 * VectorGraphics2D: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010-2016 Erich Seifert <dev[at]erichseifert.de>
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
package de.erichseifert.vectorgraphics2d;

import de.erichseifert.vectorgraphics2d.svg.SVGProcessor;

/**
 * {@code Graphics2D} implementation that saves all operations to a string
 * in the <i>Scaled Vector Graphics</i> (SVG) format.
 */
@SuppressWarnings("restriction")
public class SVGGraphics2D extends ProcessingPipeline {
	private final Processor processor;

	/**
	 * Initializes a new VectorGraphics2D pipeline for translating Graphics2D
	 * commands to SVG data. The document dimensions must be specified as
	 * parameters.
	 * @param x Left offset.
	 * @param y Top offset
	 * @param width Width.
	 * @param height Height.
	 */
	public SVGGraphics2D(double x, double y, double width, double height) {
		super(x, y, width, height);
		processor = new SVGProcessor();
	}

	@Override
	protected Processor getProcessor() {
		return processor;
	}
}
