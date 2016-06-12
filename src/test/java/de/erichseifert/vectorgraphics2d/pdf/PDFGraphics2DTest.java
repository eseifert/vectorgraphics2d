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

import static de.erichseifert.vectorgraphics2d.TestUtils.Template;
import static de.erichseifert.vectorgraphics2d.TestUtils.assertTemplateEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.Test;

import de.erichseifert.vectorgraphics2d.Document;
import de.erichseifert.vectorgraphics2d.intermediate.commands.Command;
import de.erichseifert.vectorgraphics2d.util.PageSize;

public class PDFGraphics2DTest {
	private static final String EOL = "\n";
	private static final String HEADER = "%PDF-1.4";
	private static final String FOOTER = "%%EOF";
	private static final PageSize PAGE_SIZE = new PageSize(0.0, 10.0, 20.0, 30.0);

	private final PDFGraphics2D processor = new PDFGraphics2D(PAGE_SIZE.getX(), PAGE_SIZE.getY(), PAGE_SIZE.getWidth(), PAGE_SIZE.getHeight(), false);
	private final List<Command<?>> commands = new LinkedList<Command<?>>();
	private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();

	private String process(Command<?>... commands) throws IOException {
		for (Command<?> command : commands) {
			this.commands.add(command);
		}
		Document processed = processor.process(this.commands, PAGE_SIZE);
		processed.writeTo(bytes);
		return bytes.toString("ISO-8859-1");
	}

	@Test public void envelopeForEmptyDocument() throws IOException {
		String result = process();
		Template actual = new Template((Object[]) result.split(EOL));
		Template expected = new Template(
			HEADER,
			"1 0 obj",
			"<<",
			"/Type /Catalog",
			"/Pages 2 0 R",
			">>",
			"endobj",
			"2 0 obj",
			"<<",
			"/Type /Pages",
			"/Kids [3 0 R]",
			"/Count 1",
			">>",
			"endobj",
			"3 0 obj",
			"<<",
			"/Type /Page",
			"/Parent 2 0 R",
			"/MediaBox [0 28.34645669291339 56.69291338582678 85.03937007874016]",
			"/Contents 4 0 R",
			"/Resources 6 0 R",
			">>",
			"endobj",
			"4 0 obj",
			"<<",
			"/Length 5 0 R",
			">>",
			"stream",
			"q",
			"1 1 1 rg 1 1 1 RG",
			"2.834645669291339 0 0 -2.834645669291339 0 85.03937007874016 cm",
			"/Fnt0 12.0 Tf",
			"Q",
			"endstream",
			"endobj",
			"5 0 obj",
			"100",
			"endobj",
			"6 0 obj",
			"<<",
			"/ProcSet [/PDF /Text /ImageB /ImageC /ImageI]",
			"/Font <<",
			"/Fnt0 <<",
			"/Type /Font",
			"/Subtype /TrueType",
			"/Encoding /WinAnsiEncoding",
			Pattern.compile("/BaseFont /\\S+"),
			">>",
			">>",
			">>",
			"endobj",
			"xref",
			"0 7",
			"0000000000 65535 f ",
			Pattern.compile("\\d{10} 00000 n "),
			Pattern.compile("\\d{10} 00000 n "),
			Pattern.compile("\\d{10} 00000 n "),
			Pattern.compile("\\d{10} 00000 n "),
			Pattern.compile("\\d{10} 00000 n "),
			Pattern.compile("\\d{10} 00000 n "),
			"trailer",
			"<<",
			"/Size 7",
			"/Root 1 0 R",
			">>",
			"startxref",
			Pattern.compile("[1-9]\\d*"),
			FOOTER
		);

		assertTemplateEquals(expected, actual);
	}
}

