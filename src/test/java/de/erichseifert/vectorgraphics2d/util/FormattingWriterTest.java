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
package de.erichseifert.vectorgraphics2d.util;

import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.junit.Before;
import org.junit.Test;


public class FormattingWriterTest {
	private static final String DEFAULT_ENCODING = "ISO-8859-1";
	private static final String DEFAULT_EOL = "\n";

	private ByteArrayOutputStream stream;

	@Before
	public void setUp() {
		stream = new ByteArrayOutputStream();
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructorFailsWithoutStream() throws UnsupportedEncodingException {
		new FormattingWriter(null, DEFAULT_ENCODING, DEFAULT_EOL);
	}

	@Test(expected = UnsupportedEncodingException.class)
	public void constructorFailsWithUnknownEncoding() throws UnsupportedEncodingException {
		new FormattingWriter(stream, "<unknown>", DEFAULT_EOL);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructorFailsWithEmptyEOL() throws UnsupportedEncodingException {
		new FormattingWriter(stream, DEFAULT_ENCODING, "");
	}

	@Test
	public void writeBytesEmitsBytesToStream() throws IOException {
		FormattingWriter writer = new FormattingWriter(stream, DEFAULT_ENCODING, DEFAULT_EOL);
		byte[] bytes = { 86, 71, 50, 68 };

		writer.write(bytes);

		assertArrayEquals(bytes, stream.toByteArray());
	}

	@Test
	public void writeStringHasCorrectEncoding() throws IOException {
		FormattingWriter writer = new FormattingWriter(stream, DEFAULT_ENCODING, DEFAULT_EOL);
		String string = "f\\u00F6\\u00F6bar";

		writer.write(string);

		byte[] expected = string.getBytes(DEFAULT_ENCODING);
		assertArrayEquals(expected, stream.toByteArray());
	}

	@Test
	public void writeStringEmitsCorrectEOLs() throws IOException {
		FormattingWriter writer = new FormattingWriter(stream, DEFAULT_ENCODING, "\r\n");

		writer.writeln("foo").writeln("bar");

		byte[] expected = "foo\r\nbar\r\n".getBytes(DEFAULT_ENCODING);
		assertArrayEquals(expected, stream.toByteArray());
	}
}
