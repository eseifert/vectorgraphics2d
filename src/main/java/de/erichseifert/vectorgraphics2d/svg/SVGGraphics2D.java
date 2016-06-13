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
package de.erichseifert.vectorgraphics2d.svg;

import java.awt.Color;

import de.erichseifert.vectorgraphics2d.Document;
import de.erichseifert.vectorgraphics2d.VectorGraphics2D;
import de.erichseifert.vectorgraphics2d.intermediate.commands.Command;
import de.erichseifert.vectorgraphics2d.intermediate.filters.FillPaintedShapeAsImageFilter;
import de.erichseifert.vectorgraphics2d.intermediate.filters.StateChangeGroupingFilter;
import de.erichseifert.vectorgraphics2d.util.PageSize;

/**
 * {@code Graphics2D} implementation that saves all operations to a string
 * in the <i>Scaled Vector Graphics</i> (SVG) format.
 */
@SuppressWarnings("restriction")
public class SVGGraphics2D extends VectorGraphics2D {
	/**
	 * Initializes a new VectorGraphics2D pipeline for translating Graphics2D
	 * commands to SVG data. The document dimensions must be specified as
	 * parameters.
	 * @param pageSize Document size.
	 */
	public SVGGraphics2D(PageSize pageSize) {
		super(pageSize);
		// Make graphics state match default state of Graphics2D
		setColor(Color.BLACK);
	}

	@Override
	protected Document process(Iterable<Command<?>> commands) {
		FillPaintedShapeAsImageFilter shapesAsImages = new FillPaintedShapeAsImageFilter(commands);
		Iterable<Command<?>> filtered = new StateChangeGroupingFilter(shapesAsImages);
		SVGDocument doc = new SVGDocument(getPageSize());
		for (Command<?> command : filtered) {
			doc.handle(command);
		}
		doc.close();
		return doc;
	}
}
