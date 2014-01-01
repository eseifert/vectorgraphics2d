/*
 * VectorGraphics2D: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010-2014 Erich Seifert <dev[at]erichseifert.de>
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
package de.erichseifert.vectorgraphics2d.intermediate.commands;

import java.awt.geom.AffineTransform;
import java.util.Locale;

public class TranslateCommand extends AffineTransformCommand {
	private final double deltaX;
	private final double deltaY;

	public TranslateCommand(double x, double y, AffineTransform result) {
		super(result);
		this.deltaX = x;
		this.deltaY = y;
	}

	public double getDeltaX() {
		return deltaX;
	}

	public double getDeltaY() {
		return deltaY;
	}

	@Override
	public String toString() {
		return String.format((Locale) null,
				"%s[deltaX=%f, deltaY=%f, value=%s]", getClass().getName(),
				getDeltaX(), getDeltaY(), getValue());
	}
}

