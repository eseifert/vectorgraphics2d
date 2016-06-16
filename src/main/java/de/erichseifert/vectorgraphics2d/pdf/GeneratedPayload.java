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

import java.io.IOException;

public abstract class GeneratedPayload extends Payload {
	public GeneratedPayload() {
	}

	@Override
	public byte[] getBytes() {
		try {
			for (byte b : generatePayload()) {
				super.write(b);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return super.getBytes();
	}

	@Override
	public void write(int b) throws IOException {
		throw new UnsupportedOperationException("Payload will be calculated and is read only.");
	}

	protected abstract byte[] generatePayload();
}

