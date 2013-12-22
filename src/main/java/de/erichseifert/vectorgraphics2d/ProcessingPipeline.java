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
package de.erichseifert.vectorgraphics2d;

import de.erichseifert.vectorgraphics2d.util.PageSize;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Base class for convenience implementations of {@code VectorGraphics2D}.
 */
public abstract class ProcessingPipeline extends VectorGraphics2D {
	private final PageSize pageSize;

	/**
	 * Initializes a processing pipeline.
	 * @param x Left offset.
	 * @param y Top offset
	 * @param width Width.
	 * @param height Height.
	 */
	public ProcessingPipeline(double x, double y, double width, double height) {
		pageSize = new PageSize(x, y, width, height);
	}

	public PageSize getPageSize() {
		return pageSize;
	}

	protected abstract Processor getProcessor();

	public void writeTo(OutputStream out) throws IOException {
		Document doc = getProcessor().process(getCommands(), getPageSize());
		doc.write(out);
	}

	public byte[] getBytes() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			writeTo(out);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return out.toByteArray();
	}
}
