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

import de.erichseifert.vectorgraphics2d.util.PageSize;
import org.ghost4j.Ghostscript;
import org.ghost4j.GhostscriptException;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import static org.junit.Assert.assertEquals;

public abstract class TestCase {
	private static final double EPSILON = 1;
	private final PageSize pageSize;
	private final BufferedImage reference;
	private final EPSGraphics2D epsGraphics;
	private final PDFGraphics2D pdfGraphics;
	private BufferedImage rasterizedEPS;
	private BufferedImage rasterizedPDF;

	public TestCase() throws IOException {
		int width = 150;
		int height = 150;
		pageSize = new PageSize(0.0, 0.0, width, height);

		epsGraphics = new EPSGraphics2D(0, 0, width, height);
		draw(epsGraphics);
		pdfGraphics = new PDFGraphics2D(0, 0, width, height);
		draw(pdfGraphics);

		reference = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D referenceGraphics = reference.createGraphics();
		referenceGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		referenceGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		referenceGraphics.setBackground(new Color(1f, 1f, 1f, 0f));
		referenceGraphics.clearRect(0, 0, reference.getWidth(), reference.getHeight());
		referenceGraphics.setColor(Color.BLACK);
		draw(referenceGraphics);
		File referenceImage = File.createTempFile(getClass().getName() + ".reference", ".png");
		referenceImage.deleteOnExit();
		ImageIO.write(reference, "png", referenceImage);
	}

	public abstract void draw(Graphics2D g);

	@Test
	public void testEPS() throws IOException, GhostscriptException {
		BufferedImage actual = getRasterizedEPS();
		assertEquals(reference.getWidth(), actual.getWidth());
		assertEquals(reference.getHeight(), actual.getHeight());
		double difference = TestUtils.getMeanSquareError(reference, actual);
		assertEquals(0.0, difference, EPSILON);
	}

	@Test
	public void testPDF() throws IOException, GhostscriptException {
		BufferedImage actual = getRasterizedPDF();
		assertEquals(reference.getWidth(), actual.getWidth());
		assertEquals(reference.getHeight(), actual.getHeight());
		double difference = TestUtils.getMeanSquareError(reference, actual);
		assertEquals(0.0, difference, EPSILON);
	}

	public PageSize getPageSize() {
		return pageSize;
	}

	public BufferedImage getReference() {
		return reference;
	}

	public InputStream getEPS() {
		return new ByteArrayInputStream(epsGraphics.getBytes());
	}

	public InputStream getPDF() {
		return new ByteArrayInputStream(pdfGraphics.getBytes());
	}

	public BufferedImage getRasterizedEPS() throws GhostscriptException, IOException {
		if (rasterizedEPS != null) {
			return rasterizedEPS;
		}

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
				String.format("-g%dx%d", Math.round(getPageSize().width), Math.round(getPageSize().height)),
				"-dGraphicsAlphaBits=4",
				"-dAlignToPixels=0",
				"-dEPSCrop",
				"-dPSFitPage",
				"-sDEVICE=pngalpha",
				"-sOutputFile=" + pngOutputFile.toString(),
				epsInputFile.toString()
		});
		gs.exit();
		rasterizedEPS = ImageIO.read(pngOutputFile);
		return rasterizedEPS;
	}

	public BufferedImage getRasterizedPDF() throws GhostscriptException, IOException {
		if (rasterizedPDF != null) {
			return rasterizedPDF;
		}

		File pdfInputFile = File.createTempFile(getClass().getName() + ".testPDF", ".pdf");
		pdfInputFile.deleteOnExit();
		OutputStream pdfInput = new FileOutputStream(pdfInputFile);
		pdfInput.write(pdfGraphics.getBytes());
		pdfInput.close();

		File pngOutputFile = File.createTempFile(getClass().getName() + ".testPDF", "png");
		pngOutputFile.deleteOnExit();
		Ghostscript gs = Ghostscript.getInstance();
		gs.initialize(new String[] {
				"-dBATCH",
				"-dQUIET",
				"-dNOPAUSE",
				"-dSAFER",
				String.format("-g%dx%d", Math.round(getPageSize().width), Math.round(getPageSize().height)),
				"-dGraphicsAlphaBits=4",
				// TODO: More robust settings for gs? DPI value is estimated.
				"-r25",
				"-dAlignToPixels=0",
				"-dPDFFitPage",
				"-sDEVICE=pngalpha",
				"-sOutputFile=" + pngOutputFile.toString(),
				pdfInputFile.toString()
		});
		gs.exit();
		rasterizedPDF = ImageIO.read(pngOutputFile);
		return rasterizedPDF;
	}
}
