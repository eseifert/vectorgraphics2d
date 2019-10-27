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

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.Test;


public class Base64EncodeStreamTest {

	private static void assertEncodedString(String expected, String input) throws IOException {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		OutputStream encodeStream = new Base64EncodeStream(outStream);

		byte[] buffer = new byte[1024];
		for (int count = inputStream.read(buffer); count >= 0; count = inputStream.read(buffer)) {
			encodeStream.write(buffer, 0, count);
		}
		encodeStream.close();

		String encoded = outStream.toString("ISO-8859-1");

		assertEquals(expected, encoded);
	}

	@Test public void testEncoding() throws IOException {
		String input =
			"Man is distinguished, not only by his reason, but by this singular passion " +
			"from other animals, which is a lust of the mind, that by a perseverance of " +
			"delight in the continued and indefatigable generation of knowledge, exceeds " +
			"the short vehemence of any carnal pleasure.";

		String expected =
		    "TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0aGlz" +
		    "IHNpbmd1bGFyIHBhc3Npb24gZnJvbSBvdGhlciBhbmltYWxzLCB3aGljaCBpcyBhIGx1c3Qgb2Yg" +
		    "dGhlIG1pbmQsIHRoYXQgYnkgYSBwZXJzZXZlcmFuY2Ugb2YgZGVsaWdodCBpbiB0aGUgY29udGlu" +
		    "dWVkIGFuZCBpbmRlZmF0aWdhYmxlIGdlbmVyYXRpb24gb2Yga25vd2xlZGdlLCBleGNlZWRzIHRo" +
		    "ZSBzaG9ydCB2ZWhlbWVuY2Ugb2YgYW55IGNhcm5hbCBwbGVhc3VyZS4=";

		assertEncodedString(expected, input);
	}

	@Test public void testPadding() throws IOException {
		assertEncodedString("YW55IGNhcm5hbCBwbGVhc3VyZS4=", "any carnal pleasure.");
		assertEncodedString("YW55IGNhcm5hbCBwbGVhc3VyZQ==", "any carnal pleasure");
		assertEncodedString("YW55IGNhcm5hbCBwbGVhc3Vy",     "any carnal pleasur");
		assertEncodedString("YW55IGNhcm5hbCBwbGVhc3U=",     "any carnal pleasu");
		assertEncodedString("YW55IGNhcm5hbCBwbGVhcw==",     "any carnal pleas");

		assertEncodedString("cGxlYXN1cmUu", "pleasure.");
		assertEncodedString("bGVhc3VyZS4=", "leasure.");
		assertEncodedString("ZWFzdXJlLg==", "easure.");
		assertEncodedString("YXN1cmUu",     "asure.");
		assertEncodedString("c3VyZS4=",     "sure.");
	}

	@Test public void testEmpty() throws IOException {
		assertEncodedString("", "");
	}

}

