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
package de.erichseifert.vectorgraphics2d.visual;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.imageio.ImageIO;

import de.erichseifert.vectorgraphics2d.Document;
import de.erichseifert.vectorgraphics2d.Processor;
import de.erichseifert.vectorgraphics2d.VectorGraphics2D;
import de.erichseifert.vectorgraphics2d.eps.EPSProcessor;
import de.erichseifert.vectorgraphics2d.pdf.PDFProcessor;
import de.erichseifert.vectorgraphics2d.svg.SVGProcessor;
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
	private final VectorGraphics2D vectorGraphics;
	private final Processor epsProcessor;
	private final Processor pdfProcessor;
	private final Processor svgProcessor;
	private BufferedImage rasterizedEPS;
	private BufferedImage rasterizedPDF;
	private BufferedImage rasterizedSVG;

	public TestCase() throws IOException {
		int width = 300;
		int height = 300;
		pageSize = new PageSize(0.0, 0.0, width, height);

		vectorGraphics = new VectorGraphics2D();
		draw(vectorGraphics);

		epsProcessor = new EPSProcessor();
		pdfProcessor = new PDFProcessor(false);
		svgProcessor = new SVGProcessor();

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
		Document document = epsProcessor.getDocument(vectorGraphics.getCommands(), getPageSize());
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		try {
			document.writeTo(byteOutput);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ByteArrayInputStream(byteOutput.toByteArray());
	}

	public InputStream getPDF() {
		Document document = pdfProcessor.getDocument(vectorGraphics.getCommands(), getPageSize());
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		try {
			document.writeTo(byteOutput);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ByteArrayInputStream(byteOutput.toByteArray());
	}

	public InputStream getSVG() {
		Document document = svgProcessor.getDocument(vectorGraphics.getCommands(), getPageSize());
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		try {
			document.writeTo(byteOutput);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ByteArrayInputStream(byteOutput.toByteArray());
	}

	public BufferedImage getRasterizedEPS() {
		if (rasterizedEPS != null) {
			return rasterizedEPS;
		}
		try {
			File epsInputFile = File.createTempFile(getClass().getName() + ".testEPS", ".eps");
			epsInputFile.deleteOnExit();
			OutputStream epsInput = new FileOutputStream(epsInputFile);
			epsProcessor.getDocument(vectorGraphics.getCommands(), getPageSize()).writeTo(epsInput);
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
		} catch (IOException | GhostscriptException e) {
			e.printStackTrace();
		}
		return rasterizedEPS;
	}

	public BufferedImage getRasterizedPDF() {
		if (rasterizedPDF != null) {
			return rasterizedPDF;
		}

		try {
			File pdfInputFile = File.createTempFile(getClass().getName() + ".testPDF", ".pdf");
			pdfInputFile.deleteOnExit();
			OutputStream pdfInput = new FileOutputStream(pdfInputFile);
			pdfProcessor.getDocument(vectorGraphics.getCommands(), getPageSize()).writeTo(pdfInput);
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
		} catch (IOException | GhostscriptException e) {
			e.printStackTrace();
		}
		return rasterizedPDF;
	}

	public BufferedImage getRasterizedSVG() {
		if (rasterizedSVG != null) {
			return rasterizedSVG;
		}

		try {
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
		} catch (TranscoderException e) {
			e.printStackTrace();
		}
		return rasterizedSVG;
	}
}
