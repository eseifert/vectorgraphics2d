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
package de.erichseifert.vectorgraphics2d.visual;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.imageio.ImageIO;

import de.erichseifert.vectorgraphics2d.eps.EPSGraphics2D;
import de.erichseifert.vectorgraphics2d.pdf.PDFGraphics2D;
import de.erichseifert.vectorgraphics2d.svg.SVGGraphics2D;
import de.erichseifert.vectorgraphics2d.util.PageSize;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.ghost4j.Ghostscript;
import org.ghost4j.GhostscriptException;

public abstract class TestCase {
	private static final double EPSILON = 1;
	private final PageSize pageSize;
	private final BufferedImage reference;
	private final EPSGraphics2D epsGraphics;
	private final PDFGraphics2D pdfGraphics;
	private final SVGGraphics2D svgGraphics;
	private BufferedImage rasterizedEPS;
	private BufferedImage rasterizedPDF;
	private BufferedImage rasterizedSVG;

	public TestCase() throws IOException {
		int width = 150;
		int height = 150;
		pageSize = new PageSize(0.0, 0.0, width, height);

		epsGraphics = new EPSGraphics2D(pageSize);
		draw(epsGraphics);
		pdfGraphics = new PDFGraphics2D(pageSize);
		draw(pdfGraphics);
		svgGraphics = new SVGGraphics2D(pageSize);
		draw(svgGraphics);

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

	public InputStream getSVG() {
		return new ByteArrayInputStream(svgGraphics.getBytes());
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
				String.format("-g%dx%d", Math.round(getPageSize().getWidth()), Math.round(getPageSize().getHeight())),
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
				String.format("-g%dx%d", Math.round(getPageSize().getWidth()), Math.round(getPageSize().getHeight())),
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

	public BufferedImage getRasterizedSVG() throws TranscoderException {
		if (rasterizedSVG != null) {
			return rasterizedSVG;
		}

		rasterizedSVG = new BufferedImage(
				(int) Math.round(getPageSize().getWidth()), (int) Math.round(getPageSize().getHeight()),
				BufferedImage.TYPE_INT_ARGB);

		ImageTranscoder transcoder = new ImageTranscoder() {
			@Override
			public BufferedImage createImage(int width, int height) {
				return rasterizedSVG;
			}

			@Override
			public void writeImage(BufferedImage bufferedImage, TranscoderOutput transcoderOutput) throws TranscoderException {
			}
		};

		transcoder.transcode(new TranscoderInput(getSVG()), null);

		return rasterizedSVG;
	}
}
