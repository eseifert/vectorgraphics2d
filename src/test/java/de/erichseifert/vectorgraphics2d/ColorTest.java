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
package de.erichseifert.vectorgraphics2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import static org.junit.Assert.assertEquals;

import org.ghost4j.Ghostscript;
import org.ghost4j.GhostscriptException;
import org.junit.BeforeClass;
import org.junit.Test;

public class ColorTest {
	private static BufferedImage reference;
	private static final int width = 150;
	private static final int height = 150;
	private static final double EPSILON = 1e-2;

	public static void draw(Graphics2D g) {
		final float wPage = width;
		final float hPage = height;
		final float wTile = Math.min(wPage/15f, hPage/15f);
		final float hTile = wTile;

		float w = wPage - wTile;
		float h = hPage - hTile;

		for (float y = (hPage - h)/2f; y < h; y += hTile) {
			float yRel = y/h;
			for (float x = (wPage - w)/2f; x < w; x += wTile) {
				float xRel = x/w;
				Color c = Color.getHSBColor(yRel, 1f, 1f);
				int alpha = 255 - (int) (xRel*255f);
				g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
				g.fill(new Rectangle2D.Float(x, y, wTile, hTile));
			}
		}
	}

	public double getMeanSquareError(BufferedImage reference, BufferedImage actual) {
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

	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		reference = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D referenceGraphics = (Graphics2D) reference.getGraphics();
		referenceGraphics.setBackground(new Color(1f, 1f, 1f, 0f));
		referenceGraphics.clearRect(0, 0, reference.getWidth(), reference.getHeight());
		referenceGraphics.setColor(Color.BLACK);
		draw(referenceGraphics);
		File referenceImage = File.createTempFile(ColorTest.class.getName() + ".reference", ".png");
		referenceImage.deleteOnExit();
		ImageIO.write(reference, "png", referenceImage);
	}

	@Test
	public void testEPS() throws IOException, GhostscriptException {
		EPSGraphics2D epsGraphics = new EPSGraphics2D(0, 0, width, height);
		draw(epsGraphics);

		File epsInputFile = File.createTempFile(getClass().getName() + ".testEPS", ".eps");
		epsInputFile.deleteOnExit();
		OutputStream epsInput = new FileOutputStream(epsInputFile);
		epsInput.write(epsGraphics.getBytes());
		epsInput.close();

		File pngOutputFile = File.createTempFile(getClass().getName() + ".testEPS", "png");
		pngOutputFile.deleteOnExit();
		Ghostscript gs = Ghostscript.getInstance();
		gs.initialize(new String[] {
				"-dBATCH",
				"-dQUIET",
				"-dNOPAUSE",
				"-dSAFER",
				String.format("-g%dx%d", width, height),
				"-dEPSCrop",
				"-dPSFitPage",
				"-sDEVICE=png16m",
				"-sOutputFile=" + pngOutputFile.toString(),
				epsInputFile.toString()
		});
		gs.exit();

		BufferedImage actual = ImageIO.read(pngOutputFile);
		assertEquals(reference.getWidth(), actual.getWidth());
		assertEquals(reference.getHeight(), actual.getHeight());
		double difference = getMeanSquareError(reference, actual);
		assertEquals(0.0, difference, EPSILON);
	}
}
