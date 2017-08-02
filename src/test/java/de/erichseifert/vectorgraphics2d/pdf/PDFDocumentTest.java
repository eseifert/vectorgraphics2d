/*
 * VectorGraphics2D: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010-2017 Erich Seifert <dev[at]erichseifert.de>,
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

import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Test;

import de.erichseifert.vectorgraphics2d.util.FormattingWriter;

public class PDFDocumentTest {
	public static final String PDF_CHARSET = "ISO-8859-1";
	public static final String PDF_EOL = "\n";

	@Test
	public void testSerializeTrueTypeFont() throws IOException {
		String encoding = "CustomEncoding";
		String baseFont = "MyBaseFont";
		TrueTypeFont font = new TrueTypeFont(encoding, baseFont);

		byte[] serialized = PDFDocument.serialize(font);

		ByteArrayOutputStream expected = new ByteArrayOutputStream();
		FormattingWriter expectedString = new FormattingWriter(expected, PDF_CHARSET, PDF_EOL);
		expectedString.writeln("<<");
		expectedString.writeln("/Type /Font");
		expectedString.writeln("/Subtype /TrueType");
		expectedString.write("/Encoding /").writeln(encoding);
		expectedString.write("/BaseFont /").writeln(baseFont);
		expectedString.write(">>");
		assertArrayEquals(expected.toByteArray(), serialized);
	}

	@Test
	public void testSerializeStreamWhenStreamIsFiltered() throws IOException {
		Stream stream = new Stream(Stream.Filter.FLATE);
		byte[] inputData = new byte[] {4, 2, 42, -1, 0};
		stream.write(inputData);
		stream.close();

		byte[] serialized = PDFDocument.serialize(stream);

		ByteArrayOutputStream expected = new ByteArrayOutputStream();
		FormattingWriter expectedString = new FormattingWriter(expected, PDF_CHARSET, PDF_EOL);
		expectedString.writeln("<<");
		expectedString.write("/Length ").writeln(stream.getLength());
		expectedString.writeln("/Filter /FlateDecode");
		expectedString.writeln(">>");
		expectedString.writeln("stream");
		expectedString.writeln(stream.getContent());
		expectedString.write("endstream");
		assertArrayEquals(expected.toByteArray(), serialized);
	}

	@Test
	public void testSerializeStreamWhenStreamIsNotFiltered() throws IOException {
		Stream stream = new Stream();
		byte[] inputData = new byte[] {4, 2, 42, -1, 0};
		stream.write(inputData);
		stream.close();

		byte[] serialized = PDFDocument.serialize(stream);

		ByteArrayOutputStream expected = new ByteArrayOutputStream();
		FormattingWriter expectedString = new FormattingWriter(expected, PDF_CHARSET, PDF_EOL);
		expectedString.writeln("<<");
		expectedString.write("/Length ").writeln(stream.getLength());
		expectedString.writeln(">>");
		expectedString.writeln("stream");
		expectedString.writeln(stream.getContent());
		expectedString.write("endstream");
		assertArrayEquals(expected.toByteArray(), serialized);
	}
}

