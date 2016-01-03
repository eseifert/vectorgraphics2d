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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import de.erichseifert.vectorgraphics2d.eps.EPSTests;
import de.erichseifert.vectorgraphics2d.intermediate.IRTests;
import de.erichseifert.vectorgraphics2d.pdf.PDFTests;
import de.erichseifert.vectorgraphics2d.svg.SVGTests;
import de.erichseifert.vectorgraphics2d.util.UtilTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	TestUtilsTest.class,
	UtilTests.class,
	IRTests.class,
	EPSTests.class,
	PDFTests.class,
	SVGTests.class,
	EmptyFileTest.class,
	ColorTest.class,
	ShapesTest.class,
	StrokeTest.class,
	FontTest.class,
	CharacterTest.class,
	ImageTest.class,
	ClippingTest.class,
	TransformTest.class,
	PaintTest.class,
	SwingExportTest.class
})
public class AllTests {
}
