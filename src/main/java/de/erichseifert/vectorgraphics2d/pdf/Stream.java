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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.DeflaterOutputStream;

/**
 * Represents a stream object in the sense of the PDF specification.
 * The {@code Stream} has a defined length.
 */
class Stream implements PDFObject {
	public enum Filter {
		FLATE
	};

	public static class Builder {
		private final ByteArrayOutputStream data;
		private final List<Filter> filters;

		public Builder() {
			data = new ByteArrayOutputStream();
			filters = new LinkedList<Filter>();
		}

		/**
		 * Appends the specified byte array to the {@code Stream}.
		 * @param data Data to be appended.
		 */
		public Builder write(byte[] data) {
			try {
				this.data.write(data);
			} catch (IOException e) {
				throw new RuntimeException("Unable to write to ByteArrayOutputStream", e);
			}
			return this;
		}

		public Builder filters(Filter... filters) {
			this.filters.addAll(Arrays.asList(filters));
			return this;
		}

		private byte[] getFilteredData(List<Filter> filters) {
			ByteArrayOutputStream filteredDataOutput = new ByteArrayOutputStream();
			OutputStream filteredData = filteredDataOutput;
			for (Filter filter : filters) {
				if (filter == Filter.FLATE) {
					filteredData = new DeflaterOutputStream(filteredData);
				}
			}
			try {
				filteredData.write(data.toByteArray());
			} catch (IOException e) {
				throw new RuntimeException("Unable to write to ByteArrayOutputStream", e);
			}
			try {
				filteredData.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return filteredDataOutput.toByteArray();
		}

		public Stream build() {
			return new Stream(getFilteredData(this.filters));
		}
	}

	private final byte[] content;

	/**
	 * Initializes a new {@code Stream}.
	 */
	private Stream(byte[] content) {
		this.content = new byte[content.length];
		System.arraycopy(content, 0, this.content, 0, content.length);
	}

	/**
	 * Returns the size of the stream contents in bytes.
	 * @return Number of bytes.
	 */
	public int getLength() {
		return content.length;
	}

	/**
	 * Returns the content that has been written to this {@code Stream}.
	 * @return Stream content.
	 */
	public byte[] getContent() {
		byte[] contentCopy = new byte[content.length];
		System.arraycopy(content, 0, contentCopy, 0, content.length);
		return contentCopy;
	}
}

