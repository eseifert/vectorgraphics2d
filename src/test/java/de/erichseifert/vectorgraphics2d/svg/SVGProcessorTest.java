/*
 * VectorGraphics2D: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010-2013 Erich Seifert <dev[at]erichseifert.de>
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

import de.erichseifert.vectorgraphics2d.Document;
import de.erichseifert.vectorgraphics2d.intermediate.Command;
import de.erichseifert.vectorgraphics2d.intermediate.CommandStream;
import de.erichseifert.vectorgraphics2d.intermediate.commands.DrawShapeCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.FillShapeCommand;
import de.erichseifert.vectorgraphics2d.util.PageSize;
import org.junit.Test;

import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static de.erichseifert.vectorgraphics2d.TestUtils.Template;
import static de.erichseifert.vectorgraphics2d.TestUtils.assertTemplateEquals;

public class SVGProcessorTest {
	private static final String EOL = "\n";
	private static final Template HEADER = new Template(
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
		"<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">",
		"<svg height=\"7.937499999999999mm\" version=\"1.1\" viewBox=\"0 10 20 30\" width=\"5.291666666666666mm\" x=\"0mm\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" y=\"2.645833333333333mm\" xmlns=\"http://www.w3.org/2000/svg\">"
	);
	private static final Template FOOTER = new Template("</svg>");
	private static final PageSize PAGE_SIZE = new PageSize(0.0, 10.0, 20.0, 30.0);

	private final SVGProcessor processor = new SVGProcessor();
	private final CommandStream commands = new CommandStream();
	private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();

	private String process(Command<?>... commands) throws IOException {
		for (Command<?> command : commands) {
			this.commands.add(null, command);
		}
		Document processed = processor.process(this.commands, PAGE_SIZE);
		processed.write(bytes);
		return bytes.toString("UTF-8");
	}

	@Test public void envelopeForEmptyDocument() throws IOException {
		String result = process();
		Template actual = new Template(result.split(EOL));

		Template expected = new Template(HEADER);
		expected.set(expected.size() - 1, ((String) expected.getLast()).replaceAll(">\n*$", "/>"));

		assertTemplateEquals(expected, actual);
	}

	@Test public void drawShapeBlack() throws IOException {
		String result = process(
			new DrawShapeCommand(new Rectangle2D.Double(1, 2, 3, 4))
		);
		Template actual = new Template(result.split(EOL));

		Template expected = new Template(
			HEADER,
			new Template("  <rect height=\"4\" style=\"fill:none;stroke:rgb(255,255,255);stroke-miterlimit:10;stroke-linecap:square;\" width=\"3\" x=\"1\" y=\"2\"/>\n"),
			FOOTER);

		assertTemplateEquals(expected, actual);
	}

	@Test public void fillShapeBlack() throws IOException {
		String result = process(
			new FillShapeCommand(new Rectangle2D.Double(1, 2, 3, 4))
		);
		Template actual = new Template(result.split(EOL));

		Template expected = new Template(
			HEADER,
			new Template("  <rect height=\"4\" style=\"fill:rgb(255,255,255);stroke:none;\" width=\"3\" x=\"1\" y=\"2\"/>\n"),
			FOOTER);

		assertTemplateEquals(expected, actual);
	}
}

