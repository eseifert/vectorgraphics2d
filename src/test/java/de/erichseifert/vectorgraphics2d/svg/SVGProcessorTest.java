/*
 * VectorGraphics2D: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010-2018 Erich Seifert <dev[at]erichseifert.de>,
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

import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Test;

import de.erichseifert.vectorgraphics2d.Document;
import de.erichseifert.vectorgraphics2d.intermediate.MutableCommandSequence;
import de.erichseifert.vectorgraphics2d.intermediate.commands.Command;
import de.erichseifert.vectorgraphics2d.intermediate.commands.DrawShapeCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.FillShapeCommand;
import de.erichseifert.vectorgraphics2d.util.PageSize;

public class SVGProcessorTest {
	private static final String EOL = "\n";
	private static final String HEADER =
		"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" + EOL +
		"<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">" + EOL +
		"<svg height=\"30px\" version=\"1.1\" viewBox=\"0 10 20 30\" width=\"20px\" x=\"0px\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" y=\"10px\">" + EOL;
	private static final String FOOTER = "</svg>";
	private static final PageSize PAGE_SIZE = new PageSize(0.0, 10.0, 20.0, 30.0);

	private final SVGProcessor svgProcessor = new SVGProcessor();
	private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();

	private String process(Command<?>... commands) throws IOException {
		MutableCommandSequence sequence = new MutableCommandSequence();
		for (Command<?> command : commands) {
			sequence.add(command);
		}
		Document processed = svgProcessor.getDocument(sequence, PAGE_SIZE);
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

	@Test
	public void fillShapeBlackEvenOdd() throws Exception {
	    // Example based on java.awt.LineBorder
        Rectangle2D rectOuter = new Rectangle2D.Double(0, 0, 10, 10);
	    Rectangle2D rectInner = new Rectangle2D.Double(1, 1, 8, 8);
	    Path2D path = new Path2D.Double(Path2D.WIND_EVEN_ODD);
	    path.append(rectOuter, false);
	    path.append(rectInner, false);
		String result = process(
			new FillShapeCommand(path)
		);
		String expected =
			HEADER + EOL +
			"  <path d=\"M0,0 L10.0,0 L10.0,10 L0.0,10 L0.0,0 Z M1,1 L9.0,1 L9.0,9 L1.0,9 L1.0,1 Z\" style=\"fill:rgb(255,255,255);fill-rule:evenodd;stroke:none;\"/>" + EOL +
			FOOTER;
		assertXMLEquals(expected, result);
	}
}
