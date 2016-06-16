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

import java.awt.Font;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import de.erichseifert.vectorgraphics2d.util.DataUtils;
import de.erichseifert.vectorgraphics2d.util.GraphicsUtils;

public class Resources extends PDFObject {
	private static final String KEY_PROC_SET = "ProcSet";
	private static final String KEY_TRANSPARENCY = "ExtGState";
	private static final String KEY_FONT = "Font";
	private static final String KEY_IMAGE = "XObject";

	private static final String[] VALUE_PROC_SET = {"PDF", "Text", "ImageB", "ImageC", "ImageI"};

	private static final String PREFIX_FONT = "Fnt";
	private static final String PREFIX_IMAGE = "Img";
	private static final String PREFIX_TRANSPARENCY = "Trp";

	private final Map<Font, String> fonts;
	private final Map<PDFObject, String> images;
	private final Map<Double, String> transparencies;

	private final AtomicInteger currentFontId = new AtomicInteger();
	private final AtomicInteger currentImageId = new AtomicInteger();
	private final AtomicInteger currentTransparencyId = new AtomicInteger();

	public Resources(int id, int version) {
		super(id, version, null, null, false);

		fonts = new HashMap<Font, String>();
		images = new HashMap<PDFObject, String>();
		transparencies = new HashMap<Double, String>();

		dict.put(KEY_PROC_SET, VALUE_PROC_SET);
	}

	private <T> String getResourceId(Map<T, String> resources, T resource,
			String idPrefix, AtomicInteger idCounter) {
		String id = resources.get(resource);
		if (id == null) {
			id = String.format("%s%d", idPrefix, idCounter.getAndIncrement());
			resources.put(resource, id);
		}
		return id;
	}

	public String getId(Font font) {
		// Make sure a dictionary entry for fonts exists
		Map<String, Map<String, Object>> dictEntry =
				(Map<String, Map<String, Object>>) dict.get(KEY_FONT);
		if (dictEntry == null) {
			dictEntry = new LinkedHashMap<String, Map<String, Object>>();
			dict.put(KEY_FONT, dictEntry);
		}

		font = GraphicsUtils.getPhysicalFont(font);
		String resourceId = getResourceId(fonts, font, PREFIX_FONT, currentFontId);

		String fontName = font.getPSName();
		// TODO: Determine font encoding (e.g. MacRomanEncoding, MacExpertEncoding, WinAnsiEncoding)
		String fontEncoding = "WinAnsiEncoding";
		dictEntry.put(resourceId, DataUtils.map(
			new String[] {"Type", "Subtype", "Encoding", "BaseFont"},
			new Object[] {"Font", "TrueType", fontEncoding, fontName}
		));

		return resourceId;
	}

	public String getId(PDFObject image) {
		// Make sure a dictionary entry for images exists
		Map<String, PDFObject> dictEntry =
				(Map<String, PDFObject>) dict.get(KEY_IMAGE);
		if (dictEntry == null) {
			dictEntry = new LinkedHashMap<String, PDFObject>();
			dict.put(KEY_IMAGE, dictEntry);
		}

		String resourceId = getResourceId(images, image, PREFIX_IMAGE, currentImageId);
		dictEntry.put(resourceId, image);

		return resourceId;
	}

	public String getId(Double transparency) {
		// Make sure a dictionary entry for transparency levels exists
		Map<String, Map<String, Object>> dictEntry =
				(Map<String, Map<String, Object>>) dict.get(KEY_TRANSPARENCY);
		if (dictEntry == null) {
			dictEntry = new LinkedHashMap<String, Map<String, Object>>();
			dict.put(KEY_TRANSPARENCY, dictEntry);
		}

		String resourceId = getResourceId(transparencies, transparency,
				PREFIX_TRANSPARENCY, currentTransparencyId);
		dictEntry.put(resourceId, DataUtils.map(
			new String[] {"Type", "ca", "CA"},
			new Object[] {"ExtGState", transparency, transparency}
		));

		return resourceId;
	}
}

