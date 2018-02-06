/*
 * VectorGraphics2D: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010-2018 Erich Seifert <dev[at]erichseifert.de>,
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
package de.erichseifert.vectorgraphics2d.visual;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

public class CmykColorTest extends TestCase {
	/**
	 * @see "http://carback.us/rick/blog/java-cmyk-how-to"
	 */
	private static class CMYKColorSpace extends ColorSpace {
		public CMYKColorSpace() {
			super(ColorSpace.TYPE_CMYK, 4);
		}

		@Override
		public float[] fromCIEXYZ(float[] components) {
			ColorSpace rgbColorSpace = ColorSpace.getInstance(ColorSpace.TYPE_RGB);
			float[] rgb = rgbColorSpace.toCIEXYZ(components);
			return fromRGB(rgb);
		}

		@Override
		public float[] fromRGB(float[] rgb) {
			float[] cmyk = new float[4];
			if (rgb.length >= 3) {
				float c = 1f - rgb[0];
				float m = 1f - rgb[1];
				float y = 1f - rgb[2];
				float k = Math.min(Math.min(c, m), y);
				if (k < 1f) {
					float s = 1f - k;
					cmyk[0] = (c - k)/s;
					cmyk[1] = (m - k)/s;
					cmyk[2] = (y - k)/s;
				}
				cmyk[3] = k;
			}
			clamp(cmyk);
			return cmyk;
		}

		@Override
		public float[] toCIEXYZ(float[] cmyk) {
			float[] rgb = toRGB(cmyk);
			ColorSpace rgbColorSpace = ColorSpace.getInstance(ColorSpace.TYPE_RGB);
			return rgbColorSpace.toCIEXYZ(rgb);
		}

		@Override
		public float[] toRGB(float[] cmyk) {
			float[] rgb = new float[3];
			if (cmyk.length >= 4) {
				float r = 1f - cmyk[0];
				float g = 1f - cmyk[1];
				float b = 1f - cmyk[2];
				float s = 1f - cmyk[3];
				rgb[0] = r*s;
				rgb[1] = g*s;
				rgb[2] = b*s;
			}
			clamp(rgb);
			return rgb;
		}

		private static void clamp(float[] components) {
			for (int c = 0; c < components.length; c++) {
				if (components[c] < 0f) {
					components[c] = 0f;
				} else if (components[c] > 1f) {
					components[c] = 1f;
				}
			}
		}
	}

	public CmykColorTest() throws IOException {
	}

	@Override
	public void draw(Graphics2D g) {
		final float wPage = (float) getPageSize().getWidth();
		final float hPage = (float) getPageSize().getHeight();
		final float wTile = Math.min(wPage/15f, hPage/15f);
		final float hTile = wTile;

		float w = wPage - wTile;
		float h = hPage - hTile;

		ColorSpace cmykColorSpace = new CMYKColorSpace();
		float[] cmyk = new float[4];
		final float SQRT2 = (float) Math.sqrt(2.0);

		for (float y = (hPage - h)/2f; y < h; y += hTile) {
			float yRel = y/h;
			for (float x = (wPage - w)/2f; x < w; x += wTile) {
				float xRel = x/w;
				cmyk[0] = (float) Math.sqrt(xRel*xRel + yRel*yRel) / SQRT2;
				cmyk[1] = (float) Math.sqrt((1 - xRel)*(1 - xRel) + yRel*yRel) / SQRT2;
				cmyk[2] = (float) Math.sqrt(xRel*xRel + (1 - yRel)*(1 - yRel)) / SQRT2;
				cmyk[3] = 0f;
				Color color = new Color(cmykColorSpace, cmyk, 1f);
				g.setColor(color);
				g.fill(new Rectangle2D.Float(x, y, wTile, hTile));
			}
		}
	}

}
