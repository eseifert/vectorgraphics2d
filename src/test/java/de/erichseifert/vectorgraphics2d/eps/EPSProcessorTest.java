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
package de.erichseifert.vectorgraphics2d.eps;

import static de.erichseifert.vectorgraphics2d.TestUtils.Template;
import static de.erichseifert.vectorgraphics2d.TestUtils.assertTemplateEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;
import org.junit.Test;

import de.erichseifert.vectorgraphics2d.Document;
import de.erichseifert.vectorgraphics2d.intermediate.commands.Command;
import de.erichseifert.vectorgraphics2d.util.PageSize;

public class EPSProcessorTest {
	private static final String EOL = "\n";
	private static final Object[] HEADER = {
		"%!PS-Adobe-3.0 EPSF-3.0",
		"%%BoundingBox: 0 28 57 114",
		"%%HiResBoundingBox: 0.0 28.34645669291339 56.69291338582678 113.38582677165356",
		"%%LanguageLevel: 3",
		"%%Pages: 1",
		"%%EndComments",
		"%%Page: 1 1",
		"/M /moveto load def",
		"/L /lineto load def",
		"/C /curveto load def",
		"/Z /closepath load def",
		"/RL /rlineto load def",
		"/rgb /setrgbcolor load def",
		"/cmyk /setcmykcolor load def",
		"/rect { /height exch def /width exch def /y exch def /x exch def x y M width 0 RL 0 height RL width neg 0 RL } bind def",
		"/ellipse { /endangle exch def /startangle exch def /ry exch def /rx exch def /y exch def /x exch def /savematrix matrix currentmatrix def x y translate rx ry scale 0 0 1 startangle endangle arcn savematrix setmatrix } bind def",
		"/imgdict { /datastream exch def /hasdata exch def /decodeScale exch def /bits exch def /bands exch def /imgheight exch def /imgwidth exch def << /ImageType 1 /Width imgwidth /Height imgheight /BitsPerComponent bits /Decode [bands {0 decodeScale} repeat] ",
		"/ImageMatrix [imgwidth 0 0 imgheight 0 0] hasdata { /DataSource datastream } if >> } bind def",
		"/latinize { /fontName exch def /fontNameNew exch def fontName findfont 0 dict copy begin /Encoding ISOLatin1Encoding def fontNameNew /FontName def currentdict end dup /FID undef fontNameNew exch definefont pop } bind def",
		Pattern.compile("/\\S+?Lat /\\S+ latinize /\\S+?Lat 12.0 selectfont"),
		"gsave",
		"clipsave",
		"/DeviceRGB setcolorspace",
		"0 85.03937007874016 translate",
		"2.834645669291339 -2.834645669291339 scale",
		"/basematrix matrix currentmatrix def",
		"%%EOF"
	};
	private static final PageSize PAGE_SIZE = new PageSize(0.0, 10.0, 20.0, 30.0);

	private final EPSProcessor epsProcessor = new EPSProcessor(PAGE_SIZE);
	private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();

	private String process(Command<?>... commands) throws IOException {
		for (Command<?> command : commands) {
			epsProcessor.add(command);
		}
		Document processed = epsProcessor.getDocument();
		processed.writeTo(bytes);
		return bytes.toString("ISO-8859-1");
	}

	@Test public void envelopeForEmptyDocument() throws IOException {
		String result = process();
		Template actual = new Template((Object[]) result.split(EOL));
		Template expected = new Template(HEADER);
		assertTemplateEquals(expected, actual);
	}

}
