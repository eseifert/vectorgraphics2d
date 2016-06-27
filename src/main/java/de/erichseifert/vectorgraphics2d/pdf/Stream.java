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

/**
 * Represents a stream object in the sense of the PDF specification.
 * The {@code Stream} has a defined length.
 */
class Stream implements PDFObject {
	private final ByteArrayOutputStream byteStream;

	/**
	 * Initializes a new {@code Stream}.
	 */
	public Stream() {
		byteStream = new ByteArrayOutputStream();
	}

	/**
	 * Returns the size of the stream contents in bytes.
	 * @return Number of bytes.
	 */
	public int getLength() {
		return byteStream.size();
	}

	/**
	 * Appends the specified byte array to the {@code Stream}.
	 * @param data Data to be appended.
	 */
	public void write(byte[] data) {
		try {
			byteStream.write(data);
		} catch (IOException e) {
			throw new RuntimeException("Unable to write to ByteArrayOutputStream", e);
		}
	}
}
