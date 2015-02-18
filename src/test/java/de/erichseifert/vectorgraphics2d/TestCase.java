package de.erichseifert.vectorgraphics2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import static org.junit.Assert.assertEquals;

import de.erichseifert.vectorgraphics2d.util.PageSize;
import org.ghost4j.Ghostscript;
import org.ghost4j.GhostscriptException;
import org.junit.Test;

public abstract class TestCase {
	private static final double EPSILON = 1e-2;
	private final PageSize pageSize;
	private final BufferedImage reference;

	public TestCase() throws IOException {
		int width = 150;
		int height = 150;
		pageSize = new PageSize(0.0, 0.0, width, height);

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

	public abstract void draw(Graphics2D g);

	@Test
	public void testEPS() throws IOException, GhostscriptException {
		int width = (int) Math.round(getPageSize().width);
		int height = (int) Math.round(getPageSize().height);
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
		double difference = TestUtils.getMeanSquareError(reference, actual);
		assertEquals(0.0, difference, EPSILON);
	}

	public PageSize getPageSize() {
		return pageSize;
	}
}