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

package de.erichseifert.vectorgraphics2d.eps;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import de.erichseifert.vectorgraphics2d.Document;
import de.erichseifert.vectorgraphics2d.intermediate.Command;
import de.erichseifert.vectorgraphics2d.intermediate.CommandStream;
import de.erichseifert.vectorgraphics2d.util.PageSize;

public class EPSProcessorTest {
	private static final String HEADER =
			"%!PS-Adobe-3.0 EPSF-3.0\n" +
			"%%BoundingBox: 0 28 57 114\n" +
			"%%HiResBoundingBox: 0.0 28.34645669291339 56.69291338582678 113.38582677165356\n" +
			"%%LanguageLevel: 3\n" +
			"%%Pages: 1\n" +
			"%%Page: 1 1\n" +
			"/M /moveto load def\n" +
			"/L /lineto load def\n" +
			"/C /curveto load def\n" +
			"/Z /closepath load def\n" +
			"/RL /rlineto load def\n" +
			"/rgb /setrgbcolor load def\n" +
			"/rect { /height exch def /width exch def /y exch def /x exch def x y M width 0 RL 0 height RL width neg 0 RL } bind def\n" +
			"/ellipse { /endangle exch def /startangle exch def /ry exch def /rx exch def /y exch def /x exch def /savematrix matrix currentmatrix def x y translate rx ry scale 0 0 1 startangle endangle arcn savematrix setmatrix } bind def\n" +
			"/imgdict { /datastream exch def /hasdata exch def /decodeScale exch def /bits exch def /bands exch def /imgheight exch def /imgwidth exch def << /ImageType 1 /Width imgwidth /Height imgheight /BitsPerComponent bits /Decode [bands {0 decodeScale} repeat] /ImageMatrix [imgwidth 0 0 imgheight 0 0] hasdata { /DataSource datastream } if >> } bind def\n" +
			"/latinize { /fontName exch def /fontNameNew exch def fontName findfont 0 dict copy begin /Encoding ISOLatin1Encoding def fontNameNew /FontName def currentdict end dup /FID undef fontNameNew exch definefont pop } bind def\n" +
			"/LucidaSansLat /LucidaSans latinize /LucidaSansLat 12.0 selectfont\n" +
			"gsave\n" +
			"clipsave\n" +
			"/DeviceRGB setcolorspace\n" +
			"0 85.03937007874016 translate\n" +
			"2.834645669291339 -2.834645669291339 scale\n" +
			"/basematrix matrix currentmatrix def\n" +
			"1.0 1.0 1.0 rgb\n";
	private static final PageSize PAGE_SIZE = new PageSize(0.0, 10.0, 20.0, 30.0);

	private final EPSProcessor processor = new EPSProcessor();
	private final CommandStream commands = new CommandStream();
	private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();

	private String process(Command<?>... commands) throws IOException {
		for (Command<?> command : commands) {
			this.commands.add(null, command);
		}
		Document processed = processor.process(this.commands, PAGE_SIZE);
		processed.write(bytes);
		return bytes.toString("ISO-8859-1");
	}

	@Test public void envelopeForEmptyDocument() throws IOException {
		String result = process();
		String expected = HEADER;
		assertEquals(expected, result);
	}

}

