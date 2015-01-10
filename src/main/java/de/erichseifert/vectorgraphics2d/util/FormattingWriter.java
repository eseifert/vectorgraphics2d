/*
 * VectorGraphics2D: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010-2015 Erich Seifert <dev[at]erichseifert.de>
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

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;

public class FormattingWriter implements Closeable, Flushable {
	private final OutputStream out;
	private final String encoding;
	private final String eolString;
	private long position;

	public FormattingWriter(OutputStream out, String encoding, String eol) {
		this.out = out;
		this.encoding = encoding;
		this.eolString = eol;
	}

	public FormattingWriter write(String string) throws IOException {
		byte[] bytes = string.getBytes(encoding);
		out.write(bytes, 0, bytes.length);
		position += bytes.length;
		return this;
	}

	public FormattingWriter write(Number number) throws IOException {
		write(DataUtils.format(number));
		return this;
	}

	public FormattingWriter writeln() throws IOException {
		write(eolString);
		return this;
	}

	public FormattingWriter writeln(String string) throws IOException {
		write(string);
		write(eolString);
		return this;
	}

	public FormattingWriter writeln(Number number) throws IOException {
		write(number);
		write(eolString);
		return this;
	}

	public FormattingWriter format(String format, Object... args) throws IOException {
		write(String.format(null, format, args));
		return this;
	}

	public void flush() throws IOException {
		out.flush();
	}

	public void close() throws IOException {
		out.close();
	}

	public long tell() {
		return position;
	}
}

