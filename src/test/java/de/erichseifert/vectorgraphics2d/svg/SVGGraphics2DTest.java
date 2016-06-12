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

import static de.erichseifert.vectorgraphics2d.TestUtils.assertXMLEquals;

import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;

import de.erichseifert.vectorgraphics2d.Document;
import de.erichseifert.vectorgraphics2d.intermediate.commands.Command;
import de.erichseifert.vectorgraphics2d.intermediate.commands.DrawShapeCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.FillShapeCommand;
import de.erichseifert.vectorgraphics2d.util.PageSize;

public class SVGGraphics2DTest {
	private static final String EOL = "\n";
	private static final String HEADER =
		"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" + EOL +
		"<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">" + EOL +
		"<svg height=\"10.583333333333332mm\" version=\"1.1\" viewBox=\"0 10 20 30\" width=\"7.0555555555555545mm\" x=\"0mm\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" y=\"3.5277777777777772mm\">" + EOL;
	private static final String FOOTER = "</svg>";
	private static final PageSize PAGE_SIZE = new PageSize(0.0, 10.0, 20.0, 30.0);

	private final SVGGraphics2D svgGraphics = new SVGGraphics2D(PAGE_SIZE);
	private final List<Command<?>> commands = new LinkedList<Command<?>>();
	private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();

	private String process(Command<?>... commands) throws IOException {
		for (Command<?> command : commands) {
			this.commands.add(command);
		}
		Document processed = svgGraphics.process(this.commands, PAGE_SIZE);
		processed.writeTo(bytes);
		return bytes.toString("UTF-8");
	}

	@Test
	public void envelopeForEmptyDocument() throws Exception {
		String result = process();
		String expected = HEADER.replaceAll(">$", "/>");
		assertXMLEquals(expected, result);
	}

	@Test
	public void drawShapeBlack() throws Exception {
		String result = process(
			new DrawShapeCommand(new Rectangle2D.Double(1, 2, 3, 4))
		);
		String expected =
			HEADER + EOL +
			"  <rect height=\"4\" style=\"fill:none;stroke:rgb(255,255,255);stroke-miterlimit:10;stroke-linecap:square;\" width=\"3\" x=\"1\" y=\"2\"/>" + EOL +
			FOOTER;
		assertXMLEquals(expected, result);
	}

	@Test
	public void fillShapeBlack() throws Exception {
		String result = process(
			new FillShapeCommand(new Rectangle2D.Double(1, 2, 3, 4))
		);
		String expected =
			HEADER + EOL +
			"  <rect height=\"4\" style=\"fill:rgb(255,255,255);stroke:none;\" width=\"3\" x=\"1\" y=\"2\"/>" + EOL +
			FOOTER;
		assertXMLEquals(expected, result);
	}
}
