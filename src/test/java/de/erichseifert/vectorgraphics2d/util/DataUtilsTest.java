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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Test;


public class DataUtilsTest {
	@Test
	public void stripTrailingSpaces() {
		String result = DataUtils.stripTrailing(" foo bar!   ", " ");

		String expected = " foo bar!";
		assertEquals(expected, result);
	}

	@Test
	public void stripTrailingSpacesInMultilineString() {
		String result = DataUtils.stripTrailing(" foo bar! \n   ", " ");

		String expected = " foo bar! \n";
		assertEquals(expected, result);
	}

	@Test
	public void stripComplexSubstring() {
		String result = DataUtils.stripTrailing("+bar foo+bar+bar+bar", "+bar");

		String expected = "+bar foo";
		assertEquals(expected, result);
	}

	@Test
	public void formattingIntegersDoesntCauseTrailingZeros() {
		String smallDecimalString = DataUtils.format(42);

		String expected = "42";
		assertEquals(expected, smallDecimalString);
	}

	@Test
	public void formattingSmallDecimalsDoesntCauseScientificNotation() {
		String result = DataUtils.format(1e-4d);

		String expected = "0.0001";
		assertEquals(expected, result);
	}

	@Test
	public void formattingZeroDecimalsDoesntCauseTrailingZeros() {
		String result = DataUtils.format(0d);

		String expected = "0";
		assertEquals(expected, result);
	}

	@Test
	public void formattingBigDecimalsDoesntCauseScientificNotation() {
		String result = DataUtils.format(1e+8d);

		String expected = "100000000";
		assertEquals(expected, result);
	}

	@Test
	public void mapCreatesCorrectMappingWithValidParameters() {
		Map<String, Integer> result = DataUtils.map(
				new String[] {"foo"},
				new Integer[] {42});

		assertTrue(result.containsKey("foo"));
		assertEquals(new Integer(42), result.get("foo"));
	}


	@Test(expected = IllegalArgumentException.class)
	public void mapFailsWithInvalidParameterCount() {
		Map<String, Integer> result = DataUtils.map(
				new String[] {"foo", "bar"},
				new Integer[] {42});
	}

	@Test
	public void joinReturnsEmptyStringForEmptyList() {
		String result = DataUtils.join("@@", Collections.emptyList());

		assertTrue(result.isEmpty());
	}

	@Test
	public void joinReturnsEmptyStringForEmptyArray() {
		String result = DataUtils.join("@@", new String[] {});

		assertTrue(result.isEmpty());
	}

	@Test
	public void joinReturnsEmptyStringForFloatArray() {
		String result = DataUtils.join("@@", new float[] {});

		assertTrue(result.isEmpty());
	}

	@Test
	public void joinReturnsEmptyStringForDoubleArray() {
		String result = DataUtils.join("@@", new double[] {});

		assertTrue(result.isEmpty());
	}

	@Test
	public void joinReturnsEmptyStringForNullParameter() {
		String result = DataUtils.join("@@", (String[]) null);

		assertTrue(result.isEmpty());
	}

	@Test
	public void joinReturnsOnlyElementForSingletonArray() {
		String result = DataUtils.join("@@", new String[] {"foo"});

		assertEquals("foo", result);
	}

	@Test
	public void joinReturnsOnlyElementForSingletonList() {
		String result = DataUtils.join("@@", Collections.singletonList("foo"));

		assertEquals("foo", result);
	}

	@Test
	public void joinReturnsStringWithElementsAndSeparatorForArray() {
		String result = DataUtils.join("@@", new String[] {"foo", "bar"});

		assertEquals("foo@@bar", result);
	}

	@Test
	public void joinReturnsStringWithElementsAndSeparatorForFloatArray() {
		String result = DataUtils.join("@@", new float[] {1.2f, 3.4f});

		assertEquals("1.2@@3.4", result);
	}

	@Test
	public void joinReturnsStringWithElementsAndSeparatorForDoubleArray() {
		String result = DataUtils.join("@@", new double[] {1.2d, 3.4d});

		assertEquals("1.2@@3.4", result);
	}

	@Test
	public void joinReturnsStringWithElementsAndSeparatorForList() {
		String result = DataUtils.join("@@", Arrays.asList("foo", "bar"));

		assertEquals("foo@@bar", result);
	}

	@Test
	public void maxReturnsMaximumOfIntegers() {
		int result = DataUtils.max(23, 42, -128, 4, 0);

		assertEquals(42, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void maxFailsWithoutParameters() {
		DataUtils.max();
	}

	@Test
	public void asListReturnsListContainingCorrectFloatValues() {
		float[] floatValues = {1f, 2f, 3f};

		List<Float> result = DataUtils.asList(floatValues);

		List<Float> expected = Arrays.asList(1f, 2f, 3f);
		assertEquals(expected, result);
	}

	@Test
	public void asListReturnsEmptyListForNullFloatArray() {
		List<Float> result = DataUtils.asList((float[]) null);

		assertEquals(Collections.<Float>emptyList(), result);
	}

	@Test
	public void asListReturnsListContainingCorrectDoubleValues() {
		double[] doubleValues = {1d, 2d, 3d};

		List<Double> result = DataUtils.asList(doubleValues);

		List<Double> expected = Arrays.asList(1d, 2d, 3d);
		assertEquals(expected, result);
	}
}
