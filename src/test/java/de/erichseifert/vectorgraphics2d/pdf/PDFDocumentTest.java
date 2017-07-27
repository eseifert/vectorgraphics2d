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

import static org.hamcrest.CoreMatchers.is;

import static org.junit.Assert.assertThat;

import java.io.IOException;
import org.junit.Test;

public class PDFDocumentTest {
	@Test
	public void testSerializeTrueTypeFont() {
		String encoding = "CustomEncoding";
		String baseFont = "MyBaseFont";
		TrueTypeFont font = new TrueTypeFont(encoding, baseFont);

		String serialized = PDFDocument.serialize(font);

		String expected =
				"<<\n" +
						"/Type /Font\n" +
						"/Subtype /TrueType\n" +
						"/Encoding /" + encoding + "\n" +
						"/BaseFont /" + baseFont + "\n" +
						">>";
		assertThat(serialized, is(expected));
	}

	@Test
	public void testSerializeStreamWhenStreamIsFiltered() throws IOException {
		Stream stream = new Stream(Stream.Filter.FLATE);
		byte[] inputData = new byte[] {4, 2, 42, -1, 0};
		stream.write(inputData);
		stream.close();

		String serialized = PDFDocument.serialize(stream);

		String expected =
				"<<\n" +
						"/Length " + stream.getLength() + "\n" +
						"/Filter /FlateDecode\n" +
						">>\n" +
						"stream\n" +
						new String(stream.getContent()) + "\n" +
						"endstream";
		assertThat(serialized, is(expected));
	}

	@Test
	public void testSerializeStreamWhenStreamIsNotFiltered() throws IOException {
		Stream stream = new Stream();
		byte[] inputData = new byte[] {4, 2, 42, -1, 0};
		stream.write(inputData);
		stream.close();

		String serialized = PDFDocument.serialize(stream);

		String expected =
				"<<\n" +
						"/Length " + stream.getLength() + "\n" +
						">>\n" +
						"stream\n" +
						new String(stream.getContent()) + "\n" +
						"endstream";
		assertThat(serialized, is(expected));
	}
}

