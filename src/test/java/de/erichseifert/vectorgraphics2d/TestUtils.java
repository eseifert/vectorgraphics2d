/*
 * VectorGraphics2D: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010-2016 Erich Seifert <dev[at]erichseifert.de>
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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Utility class for unit tests.
 */
public abstract class TestUtils {
	protected TestUtils() {
		throw new UnsupportedOperationException();
	}

	public static double getMeanSquareError(BufferedImage reference, BufferedImage actual) {
		float meanSquareError = 0f;
		for (int y = 0; y < reference.getHeight(); y++) {
			for (int x = 0; x < reference.getWidth(); x++) {
				Color rgbReference = new Color(reference.getRGB(x, y));
				Color rgbActual = new Color(actual.getRGB(x, y));
				float[] colorComponentsReference = rgbReference.getColorComponents(null);
				float[] colorComponentsActual = rgbActual.getColorComponents(null);
				for (int color = 0; color < colorComponentsReference.length; color++) {
					float squareError = (float) Math.pow(colorComponentsReference[color] - colorComponentsActual[color], 2.0);
					meanSquareError += squareError;
				}
			}
		}
		meanSquareError /= reference.getWidth()*reference.getHeight()*3;
		return meanSquareError;
	}

	public static class Template extends LinkedList<Object> {
		public Template(Object... lines) {
			Collections.addAll(this, lines);
		}

		public Template(Template... templates) {
			for (Template template : templates) {
				addAll(template);
			}
		}
	}

	public static class XMLFragment {
		private static final Pattern CDATA = Pattern.compile("\\s*<!\\[CDATA\\[(.*?)\\]\\]>");
		private static final Pattern COMMENT = Pattern.compile("\\s*<!--(.*?)-->");
		private static final Pattern TAG_BEGIN = Pattern.compile("\\s*<(/|\\?|!)?\\s*([^\\s>/\\?]+)");
		private static final Pattern TAG_END = Pattern.compile("\\s*(/|\\?)?>");
		private static final Pattern TAG_ATTRIBUTE = Pattern.compile("\\s*([^\\s>=]+)=(\"[^\"]*\"|'[^']*')");
		private static final Pattern DOCTYPE_PART = Pattern.compile("\\s*(\"[^\"]*\"|'[^']*'|[^\\s>]+)");

		public enum FragmentType {
			START_TAG, END_TAG, EMPTY_ELEMENT, CDATA,
			DECLARATION, DOCTYPE, COMMENT }

		public final String name;
		public final FragmentType type;
		public final Map<String, String> attributes;

		public final int matchStart;
		public final int matchEnd;

		public XMLFragment(String name, FragmentType type, Map<String, String> attributes,
						   int matchStart, int matchEnd) {
			this.name = name;
			this.type = type;
			this.attributes = Collections.unmodifiableMap(
					new TreeMap<String, String>(attributes));
			this.matchStart = matchStart;
			this.matchEnd = matchEnd;
		}

		public static XMLFragment parse(String xmlString, int matchStart) {
			Map<String, String> attrs = new IdentityHashMap<String, String>();

			Matcher cdataMatch = CDATA.matcher(xmlString);
			cdataMatch.region(matchStart, xmlString.length());
			if (cdataMatch.lookingAt()) {
				attrs.put("value", cdataMatch.group(1));
				return new XMLFragment("", FragmentType.CDATA, attrs, matchStart, cdataMatch.end());
			}

			Matcher commentMatch = COMMENT.matcher(xmlString);
			commentMatch.region(matchStart, xmlString.length());
			if (commentMatch.lookingAt()) {
				attrs.put("value", commentMatch.group(1).trim());
				return new XMLFragment("", FragmentType.COMMENT, attrs, matchStart, commentMatch.end());
			}

			Matcher beginMatch = TAG_BEGIN.matcher(xmlString);
			beginMatch.region(matchStart, xmlString.length());
			if (!beginMatch.lookingAt()) {
				return null;
			}
			int matchEndPrev = beginMatch.end();

			String modifiers = beginMatch.group(1);
			String name = beginMatch.group(2);
			boolean endTag = "/".equals(modifiers);
			boolean declarationStart = "?".equals(modifiers);
			boolean doctype = "!".equals(modifiers) && "DOCTYPE".equals(name);

			if (doctype) {
				int partNo = 0;
				while (true) {
					Matcher attrMatch = DOCTYPE_PART.matcher(xmlString);
					attrMatch.region(matchEndPrev, xmlString.length());
					if (!attrMatch.lookingAt()) {
						break;
					}
					matchEndPrev = attrMatch.end();

					String partValue = attrMatch.group(1);
					if (partValue.startsWith("\"") || partValue.startsWith("'")) {
						partValue = partValue.substring(1, partValue.length() - 1);
					}

					String partId = String.format("doctype %02d", partNo++);
					attrs.put(partId, partValue);
				}
			} else {
				while (true) {
					Matcher attrMatch = TAG_ATTRIBUTE.matcher(xmlString);
					attrMatch.region(matchEndPrev, xmlString.length());
					if (!attrMatch.lookingAt()) {
						break;
					}
					matchEndPrev = attrMatch.end();

					String attrName = attrMatch.group(1);
					String attrValue = attrMatch.group(2);
					attrValue = attrValue.substring(1, attrValue.length() - 1);
					attrs.put(attrName, attrValue);
				}
			}

			Matcher endMatch = TAG_END.matcher(xmlString);
			endMatch.region(matchEndPrev, xmlString.length());
			if (!endMatch.lookingAt()) {
				throw new AssertionError(String.format("No tag end found: %s", xmlString.substring(0, matchEndPrev)));
			}
			matchEndPrev = endMatch.end();

			modifiers = endMatch.group(1);
			boolean emptyElement = "/".equals(modifiers);
			boolean declarationEnd = "?".equals(modifiers);

			FragmentType type = FragmentType.START_TAG;
			if (endTag) {
				type = FragmentType.END_TAG;
			} else if (emptyElement) {
				type = FragmentType.EMPTY_ELEMENT;
			} else if (declarationStart && declarationEnd) {
				type = FragmentType.DECLARATION;
			} else if (doctype) {
				type = FragmentType.DOCTYPE;
			}

			return new XMLFragment(name, type, attrs, matchStart, matchEndPrev);
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof XMLFragment)) {
				return false;
			}
			XMLFragment frag = (XMLFragment) o;

			if (!type.equals(frag.type) || !name.equals(frag.name)) {
				return false;
			}

			Iterator<Map.Entry<String, String>> itThis = attributes.entrySet().iterator();
			Iterator<Map.Entry<String, String>> itFrag = frag.attributes.entrySet().iterator();
			while (itThis.hasNext() && itFrag.hasNext()) {
				Map.Entry<String, String> attrThis = itThis.next();
				Map.Entry<String, String> attrFrag = itFrag.next();
				if (!attrThis.getKey().equals(attrFrag.getKey()) ||
						!attrThis.getValue().equals(attrFrag.getValue())) {
					return false;
				}
			}

			return true;
		}

		@Override
		public String toString() {
			StringBuilder s = new StringBuilder("<");
			if (FragmentType.END_TAG.equals(type)) {
				s.append("/");
			} else if (FragmentType.DECLARATION.equals(type)) {
				s.append("?");
			}

			if (FragmentType.DOCTYPE.equals(type)) {
				s.append("!").append(name);
				for (String partValue : attributes.values()) {
					s.append(" ").append(partValue);
				}
			} else {
				s.append(name);
				for (Map.Entry<String, String> attr : attributes.entrySet()) {
					s.append(" ").append(attr.getKey()).append("=\"").append(attr.getValue()).append("\"");
				}
			}
			if (FragmentType.DECLARATION.equals(type)) {
				s.append("?");
			}
			s.append(">");
			return s.toString();
		}
	}

	public static void assertTemplateEquals(Template expected, Template actual) {
		Iterator<Object> itExpected = expected.iterator();
		Iterator<Object> itActual = actual.iterator();
		while (itExpected.hasNext() && itActual.hasNext()) {
			Object lineExpected = itExpected.next();
			Object lineActual = itActual.next();

			if (lineExpected == null) {
				continue;
			}
			assertTrue(String.format("Line is of type %s, expected String.",
					lineActual.getClass()), lineActual instanceof String);

			if (lineExpected instanceof String) {
				assertEquals(lineExpected, lineActual);
			} else if (lineExpected instanceof Pattern) {
				Pattern expectedPattern = (Pattern) lineExpected;
				Matcher matcher = expectedPattern.matcher((String) lineActual);
				assertTrue(String.format(
						"Line didn't match pattern.\nExpected: \"%s\"\nActual: \"%s\"",
						matcher.pattern(), lineActual),
						matcher.matches());
			}
		}
		assertEquals("Wrong number of lines in template.", expected.size(), actual.size());
	}

	private static List<XMLFragment> parseXML(String xmlString) {
		XMLFragment frag;
		List<XMLFragment> fragments = new LinkedList<XMLFragment>();
		int startPos = 0;
		while ((frag = XMLFragment.parse(xmlString, startPos)) != null) {
			fragments.add(frag);
			startPos = frag.matchEnd;
		}
		return fragments;
	}

	public static void assertXMLEquals(String expected, String actual) {
		List<XMLFragment> expectedFrags = parseXML(expected);
		List<XMLFragment> actualFrags = parseXML(actual);

		Iterator<XMLFragment> itExpected = expectedFrags.iterator();
		Iterator<XMLFragment> itActual = actualFrags.iterator();
		while (itExpected.hasNext() && itActual.hasNext()) {
			XMLFragment expectedFrag = itExpected.next();
			XMLFragment actualFrag = itActual.next();
			assertEquals(expectedFrag, actualFrag);
		}

		assertEquals(expectedFrags.size(), actualFrags.size());
	}
}
