/*
 * VectorGraphics2D: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010-2019 Erich Seifert <dev[at]erichseifert.de>,
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

		byte[] expected = bytes;
		assertArrayEquals(expected, stream.toByteArray());
	}

	@Test
	public void writelnBytesEmitsBytesAndEOLToStream() throws IOException {
		FormattingWriter writer = new FormattingWriter(stream, DEFAULT_ENCODING, DEFAULT_EOL);
		byte[] eolBytes = DEFAULT_EOL.getBytes(DEFAULT_ENCODING);
		byte[] bytes = { 86, 71, 50, 68 };

		writer.writeln(bytes);

		byte[] expected = new byte[bytes.length + eolBytes.length];
		System.arraycopy(bytes, 0, expected, 0, bytes.length);
		System.arraycopy(eolBytes, 0, expected, bytes.length, eolBytes.length);
		assertArrayEquals(expected, stream.toByteArray());
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

	@Test
	public void writeDoubleOutputsAFormattedNumber() throws IOException {
		FormattingWriter writer = new FormattingWriter(stream, DEFAULT_ENCODING, DEFAULT_EOL);

		writer.write(4.2);

		byte[] expected = "4.2".getBytes(DEFAULT_ENCODING);
		assertArrayEquals(expected, stream.toByteArray());
	}

	@Test
	public void writeDoubleOutputsAFormattedNumberAndAppendsAnEOL() throws IOException {
		FormattingWriter writer = new FormattingWriter(stream, DEFAULT_ENCODING, DEFAULT_EOL);

		writer.writeln(4.2);

		byte[] expected = ("4.2" + DEFAULT_EOL).getBytes(DEFAULT_ENCODING);
		assertArrayEquals(expected, stream.toByteArray());
	}

	@Test
	public void writeFormatsStringWithParameters() throws IOException {
		FormattingWriter writer = new FormattingWriter(stream, DEFAULT_ENCODING, DEFAULT_EOL);

		writer.write("%.02f => %s", 4.2, "foo");

		byte[] expected = "4.20 => foo".getBytes(DEFAULT_ENCODING);
		assertArrayEquals(expected, stream.toByteArray());
	}

	@Test
	public void writelnFormatsStringWithParametersAndAppendsAnEOL() throws IOException {
		FormattingWriter writer = new FormattingWriter(stream, DEFAULT_ENCODING, DEFAULT_EOL);

		writer.writeln("%.02f => %s", 4.2, "foo");

		byte[] expected = ("4.20 => foo" + DEFAULT_EOL).getBytes(DEFAULT_ENCODING);
		assertArrayEquals(expected, stream.toByteArray());
	}

	private final static class MockOutputStream extends OutputStream {
		private boolean flushed;
		private boolean closed;
		@Override public void write(int b) throws IOException {}
		@Override public void flush() throws IOException { flushed = true; }
		@Override public void close() throws IOException { closed = true; }
	}

	@Test
	public void closeClosesOutputStream() throws IOException {
		MockOutputStream mockStream = new MockOutputStream();
		FormattingWriter writer = new FormattingWriter(mockStream, DEFAULT_ENCODING, DEFAULT_EOL);

		writer.close();

		assertTrue(mockStream.closed);
	}

	@Test
	public void flushFlushesOutputStream() throws IOException {
		MockOutputStream mockStream = new MockOutputStream();
		FormattingWriter writer = new FormattingWriter(mockStream, DEFAULT_ENCODING, DEFAULT_EOL);

		writer.flush();

		assertTrue(mockStream.flushed);
	}

	@Test
	public void tellReturnsCorrectPosition() throws IOException {
		FormattingWriter writer = new FormattingWriter(stream, DEFAULT_ENCODING, DEFAULT_EOL);
		byte[] bytes = { 86, 71, 50, 68 };

		writer.write(bytes);

		assertEquals(bytes.length, writer.tell());
	}
}
