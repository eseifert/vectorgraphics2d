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
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import org.junit.Test;

public class StreamTest {
	@Test(expected = IllegalStateException.class)
	public void getLengthThrowsExceptionWhenStreamIsOpen() {
		Stream stream = new Stream();

		stream.getLength();
	}

	@Test(expected = IllegalStateException.class)
	public void getContentThrowsExceptionWhenStreamIsOpen() {
		Stream stream = new Stream();

		stream.getContent();
	}

	@Test
	public void writeIntWritesDataToStream() throws IOException {
		Stream stream = new Stream();

		stream.write(42);
		stream.close();

		assertArrayEquals(new byte[] {42}, stream.getContent());
	}

	@Test(expected = IOException.class)
	public void writeIntThrowsExceptionWhenStreamIsClosed() throws IOException {
		Stream stream = new Stream();
		stream.close();

		stream.write(42);
	}

	@Test
	public void writeBytesWritesDataToStream() throws IOException {
		Stream stream = new Stream();

		stream.write(new byte[] {42});
		stream.close();

		assertArrayEquals(new byte[] {42}, stream.getContent());
	}

	@Test(expected = IOException.class)
	public void writeBytesThrowsExceptionWhenStreamIsClosed() throws IOException {
		Stream stream = new Stream();
		stream.close();

		stream.write(new byte[] {42});
	}

	@Test
	public void lengthIsZeroOnInitialization() {
		Stream stream = new Stream();
		stream.close();

		int length = stream.getLength();

		assertThat(length, is(0));
	}

	@Test
	public void lengthEqualsByteCountInWrittenDataWhenNoFiltersAreSet() throws IOException {
		byte[] garbage = new byte[] {4, 2, 42, -1, 0};
		Stream stream = new Stream();
		stream.write(garbage);
		stream.close();

		int length = stream.getLength();

		assertThat(length, is(garbage.length));
	}

	@Test
	public void writtenDataIsIdenticalToStreamContentWhenNoFiltersAreUsed() throws IOException {
		byte[] data = new byte[] {4, 2, 42, -1, 0};
		Stream stream = new Stream();
		stream.write(data);
		stream.close();

		byte[] content = stream.getContent();

		assertThat(content, is(data));
	}

	@Test
	public void contentsAreCompressedWhenFlateFilterIsSet() throws DataFormatException, IOException {
		byte[] inputData = new byte[] {4, 2, 42, -1, 0};
		Stream stream = new Stream(Stream.Filter.FLATE);
		stream.write(inputData);
		stream.close();

		byte[] compressedContent = stream.getContent();

		Inflater decompressor = new Inflater();
		decompressor.setInput(compressedContent);
		byte[] decompressedOutput = new byte[inputData.length];
		decompressor.inflate(decompressedOutput);
		assertThat(decompressedOutput, is(inputData));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void getFiltersResultIsUnmodifiable() {
		Stream stream = new Stream();

		List<Stream.Filter> filters = stream.getFilters();
		filters.add(Stream.Filter.FLATE);
	}
}
