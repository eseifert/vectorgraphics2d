/*
 * VectorGraphics2D: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010-2011 Erich Seifert <dev[at]erichseifert.de>
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Map;

/**
 * <code>Graphics2D</code> implementation that saves all operations to a string
 * in the <i>Encapsulated PostScript®</i> (EPS) format.
 */
public class EPSGraphics2D extends VectorGraphics2D {
	/** Constant to convert values from millimeters to PostScript® units
	(1/72th inch). */
	protected static final double MM_IN_UNITS = 72.0 / 25.4;

	/** Mapping of stroke endcap values from Java to PostScript®. */
	private static final Map<Integer, Integer> STROKE_ENDCAPS = DataUtils.map(
		new Integer[] { BasicStroke.CAP_BUTT, BasicStroke.CAP_ROUND, BasicStroke.CAP_SQUARE },
		new Integer[] { 0, 1, 2 }
	);

	/** Mapping of line join values for path drawing from Java to
	PostScript®. */
	private static final Map<Integer, Integer> STROKE_LINEJOIN = DataUtils.map(
		new Integer[] { BasicStroke.JOIN_MITER, BasicStroke.JOIN_ROUND, BasicStroke.JOIN_BEVEL },
		new Integer[] { 0, 1, 2 }
	);

	/**
	 * Constructor that initializes a new <code>EPSGraphics2D</code> instance.
	 * The document dimension must be specified as parameters.
	 */
	public EPSGraphics2D(double x, double y, double width, double height) {
		super(x, y, width, height);
		writeHeader();
	}

	@Override
	protected void writeString(String str, double x, double y) {
		// Escape string
		str = str.replaceAll("\\\\", "\\\\\\\\").replaceAll("\t", "\\\\t")
			.replaceAll("\b", "\\\\b").replaceAll("\f", "\\\\f")
			.replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)");

		//float fontSize = getFont().getSize2D();
		//float leading = getFont().getLineMetrics("", getFontRenderContext())
		//	.getLeading();

		write("gsave 1 -1 scale ");

		/*
		// Extract lines
		String[] lines = str.replaceAll("\r\n", "\n").replaceAll("\r", "\n")
			.split("\n");
		// Output lines
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			write(x, " -", y + i*fontSize + ((i>0) ? leading : 0f),
				" M (", line, ") show ");
		}
		*/

		str = str.replaceAll("[\r\n]", "");
		write(x, " -", y, " M (", str, ") show ");

		writeln("grestore");
	}

	@Override
	public void setStroke(Stroke s) {
		BasicStroke bsPrev;
		if (getStroke() instanceof BasicStroke) {
			bsPrev = (BasicStroke) getStroke();
		} else {
			bsPrev = new BasicStroke();
		}

		super.setStroke(s);

		if (s instanceof BasicStroke) {
			BasicStroke bs = (BasicStroke) s;
			if (bs.getLineWidth() != bsPrev.getLineWidth()) {
				writeln(bs.getLineWidth(), " setlinewidth");
			}
			if (bs.getLineJoin() != bsPrev.getLineJoin()) {
				writeln(STROKE_LINEJOIN.get(bs.getLineJoin()), " setlinejoin");
			}
			if (bs.getEndCap() != bsPrev.getEndCap()) {
				writeln(STROKE_ENDCAPS.get(bs.getEndCap()), " setlinecap");
			}
			if ((!Arrays.equals(bs.getDashArray(), bsPrev.getDashArray())) ||
				(bs.getDashPhase() != bsPrev.getDashPhase())) {
				writeln("[", DataUtils.join(" ", bs.getDashArray()), "] ",
						bs.getDashPhase(), " setdash");
			}
		}
	}

	@Override
	protected void writeImage(Image img, int imgWidth, int imgHeight,
			double x, double y, double width, double height) {
		BufferedImage bufferedImg = GraphicsUtils.toBufferedImage(img);
		String imgData = getEps(bufferedImg);
		int bands = bufferedImg.getSampleModel().getNumBands();
		int bitsPerPixel = (int) Math.ceil(
				bufferedImg.getColorModel().getPixelSize() / 8.0) * 8;
		int bitsPerSample = bitsPerPixel / bands;
		if (bands > 3) {
			bands = 3;
		}
		writeln("gsave");
		writeln(x, " ", y, " ", width, " ", height, " ",
			imgWidth, " ", imgHeight, " ", bitsPerSample, " img false ", bands,
			" colorimage"
		);
		writeln(imgData, ">");
		writeln("grestore");
	}

	@Override
	public void setColor(Color c) {
		Color color = getColor();
		if (c != null) {
			super.setColor(c);
			// TODO Add transparency hints for PDF conversion?
			/*if (color.getAlpha() != c.getAlpha()) {
				double a = c.getAlpha()/255.0;
				writeln("[ /ca ", a, " /SetTransparency pdfmark");
			}*/
			if (color.getRed() != c.getRed() || color.getGreen() != c.getGreen()
					|| color.getBlue() != c.getBlue()) {
				double r = c.getRed()/255.0;
				double g = c.getGreen()/255.0;
				double b = c.getBlue()/255.0;
				writeln(r, " ", g, " ", b, " rgb");
			}
		}
	}

	@Override
	public void setFont(Font font) {
		if (!getFont().equals(font)) {
			super.setFont(font);
			writeln("/", font.getPSName(), " ", font.getSize2D(),
					" selectfont");

		}
	}

	@Override
	public void setClip(Shape clip) {
		if (getClip() != null) {
			writeln("cliprestore");
		}
		super.setClip(clip);
		if (getClip() != null) {
			writeShape(getClip());
			writeln(" clip");
		}
	}

	@Override
	public void setTransform(AffineTransform tx) {
		if (getTransform().equals(tx)) {
			return;
		}
		super.setTransform(tx);
		double[] matrix = new double[6];
		getTransform().getMatrix(matrix);
		writeln("basematrix setmatrix [", DataUtils.join(" ", matrix),
				"] concat");
	}

	@Override
	public void translate(double tx, double ty) {
		super.translate(tx, ty);
		if ((tx != 0.0) || (ty != 0.0)) {
			writeln(tx, " ", ty, " translate");
		}
	}

	@Override
	public void scale(double sx, double sy) {
		super.scale(sx, sy);
		if ((sx != 1.0) || (sy != 1.0)) {
			writeln(sx, " ", sy, " scale");
		}
	}

	@Override
	public void rotate(double theta) {
		super.rotate(theta);
		if (theta != 0.0) {
			writeln(theta/Math.PI*180.0, " rotate");
		}
	}

	@Override
	public void rotate(double theta, double x, double y) {
		super.rotate(theta, x, y);
		if (theta != 0.0) {
			writeln(x, " ", y, " translate ", theta/Math.PI*180.0, " rotate ",
				-x, " ", -y, " translate");
		}
	}

	@Override
	public void shear(double sx, double sy) {
		super.shear(sx, sy);
		if ((sx != 0.0) || (sy != 0.0)) {
			setTransform(getTransform());
		}
	}

	@Override
	protected void writeHeader() {
		Rectangle2D bounds = getBounds();
		double x = bounds.getX() * MM_IN_UNITS;
		double y = bounds.getY() * MM_IN_UNITS;
		double w = bounds.getWidth() * MM_IN_UNITS;
		double h = bounds.getHeight() * MM_IN_UNITS;

		writeln("%!PS-Adobe-3.0 EPSF-3.0");
		writeln("%%BoundingBox: ",
				(int) Math.floor(x), " ", (int) Math.floor(y), " ",
				(int) Math.ceil(x + w), " ", (int) Math.ceil(y + h));
		writeln("%%HiResBoundingBox: ", x, " ", y, " ", x + w, " ", y + h);
		writeln("%%LanguageLevel: 3");
		writeln("%%Pages: 1");
		writeln("%%Page: 1 1");

		// Utility functions
		writeln("/M /moveto load def");
		writeln("/L /lineto load def");
		writeln("/C /curveto load def");
		writeln("/Z /closepath load def");
		writeln("/RL /rlineto load def");
		writeln("/rgb /setrgbcolor load def");
		writeln("/rect { ",
			"/height exch def /width exch def /y exch def /x exch def ",
			"x y M width 0 RL 0 height RL width neg 0 RL ",
			"} bind def");
		writeln("/ellipse { ",
			"/endangle exch def /startangle exch def ",
			"/ry exch def /rx exch def /y exch def /x exch def ",
			"/savematrix matrix currentmatrix def ",
			"x y translate rx ry scale 0 0 1 startangle endangle arcn ",
			"savematrix setmatrix ",
			"} bind def");
		writeln("/img { ",
			"/bits exch def /imgheight exch def /imgwidth exch def ",
			"/height exch def /width exch def /y exch def /x exch def ",
			"x y translate width height scale ",
			"imgwidth imgheight bits [imgwidth 0 0 imgheight 0 0] currentfile ",
			"/ASCIIHexDecode filter ",
			"} bind def");
		// Set default font
		writeln("/", getFont().getPSName(), " ", getFont().getSize2D(),
				" selectfont");
		//writeln("<< /AllowTransparency true >> setdistillerparams"); // TODO
		// Save state
		writeln("gsave");
		// Save state
		writeln("clipsave");
		// Settings
		writeln("/DeviceRGB setcolorspace");
		// Adjust page size and page origin
		writeln("0 ", h, " translate");
		writeln(MM_IN_UNITS, " -", MM_IN_UNITS, " scale");
		writeln("/basematrix matrix currentmatrix def");
	}

	/**
	 * Utility method for writing a tag closing fragment for drawing operations.
	 * @param s Shape.
	 */
	@Override
	protected void writeClosingDraw(Shape s) {
		writeln(" stroke");
	}

	/**
	 * Utility method for writing a tag closing fragment for filling operations.
	 * @param s Shape.
	 */
	@Override
	protected void writeClosingFill(Shape s) {
		// TODO Omit fill operation if paint isn't a Color object
		writeln(" fill");
		if (!(getPaint() instanceof Color)) {
			super.writeClosingFill(s);
		}
	}

	/**
	 * Utility method for writing an arbitrary shape to.
	 * It tries to translate Java2D shapes to the corresponding EPS shape
	 * commands.
	 * @param s Shape.
	 */
	@Override
	protected void writeShape(Shape s) {
		write("newpath ");
		if (s instanceof Line2D) {
			Line2D l = (Line2D) s;
			double x1 = l.getX1();
			double y1 = l.getY1();
			double x2 = l.getX2();
			double y2 = l.getY2();
			write(x1, " ", y1, " M ", x2, " ", y2, " L");
			return;
		} else if (s instanceof Rectangle2D) {
			Rectangle2D r = (Rectangle2D) s;
			double x = r.getX();
			double y = r.getY();
			double width = r.getWidth();
			double height = r.getHeight();
			write(x, " ", y, " ", width, " ", height, " rect Z");
			return;
		} else if (s instanceof Ellipse2D) {
			Ellipse2D e = (Ellipse2D) s;
			double x = e.getX() + e.getWidth()/2.0;
			double y = e.getY() + e.getHeight()/2.0;
			double rx = e.getWidth()/2.0;
			double ry = e.getHeight()/2.0;
			write(x, " ", y, " ", rx, " ", ry, " ", 360.0, " ", 0.0,
					" ellipse Z");
			return;
		} else if (s instanceof Arc2D) {
			Arc2D e = (Arc2D) s;
			double x = (e.getX() + e.getWidth()/2.0);
			double y = (e.getY() + e.getHeight()/2.0);
			double rx = e.getWidth()/2.0;
			double ry = e.getHeight()/2.0;
			double startAngle = -e.getAngleStart();
			double endAngle = -(e.getAngleStart() + e.getAngleExtent());
			write(x, " ", y, " ", rx, " ", ry, " ", startAngle, " ", endAngle,
					" ellipse");
			if (e.getArcType() == Arc2D.CHORD) {
				write(" Z");
			} else if (e.getArcType() == Arc2D.PIE) {
				write(" ", x, " ", y, " L Z");
			}
			return;
		} else {
			PathIterator segments = s.getPathIterator(null);
			double[] coordsCur = new double[6];
			double[] pointPrev = new double[2];
			for (int i = 0; !segments.isDone(); i++, segments.next()) {
				if (i > 0) {
					write(" ");
				}
				int segmentType = segments.currentSegment(coordsCur);
				switch (segmentType) {
				case PathIterator.SEG_MOVETO:
					write(coordsCur[0], " ", coordsCur[1], " M");
					pointPrev[0] = coordsCur[0];
					pointPrev[1] = coordsCur[1];
					break;
				case PathIterator.SEG_LINETO:
					write(coordsCur[0], " ", coordsCur[1], " L");
					pointPrev[0] = coordsCur[0];
					pointPrev[1] = coordsCur[1];
					break;
				case PathIterator.SEG_CUBICTO:
					write(coordsCur[0], " ", coordsCur[1], " ",
							coordsCur[2], " ", coordsCur[3], " ",
							coordsCur[4], " ", coordsCur[5], " C");
					pointPrev[0] = coordsCur[4];
					pointPrev[1] = coordsCur[5];
					break;
				case PathIterator.SEG_QUADTO:
					double x1 = pointPrev[0] + 2.0/3.0*(coordsCur[0] - pointPrev[0]);
					double y1 = pointPrev[1] + 2.0/3.0*(coordsCur[1] - pointPrev[1]);
					double x2 = coordsCur[0] + 1.0/3.0*(coordsCur[2] - coordsCur[0]);
					double y2 = coordsCur[1] + 1.0/3.0*(coordsCur[3] - coordsCur[1]);
					double x3 = coordsCur[2];
					double y3 = coordsCur[3];
					write(x1, " ", y1, " ", x2, " ", y2, " ", x3, " ", y3,
							" C");
					pointPrev[0] = x3;
					pointPrev[1] = y3;
					break;
				case PathIterator.SEG_CLOSE:
					write("Z");
					break;
				default:
					throw new IllegalStateException("Unknown path operation.");
				}
			}
		}
	}

	/**
	 * Returns a string containing the image data in an EPS compatible format.
	 * @param bufferedImg Image object.
	 * @return String representation in EPS hexadecimal format.
	 */
	private static String getEps(BufferedImage bufferedImg) {
		int width = bufferedImg.getWidth();
		int height = bufferedImg.getHeight();
		int bands = bufferedImg.getSampleModel().getNumBands();
		StringBuffer str = new StringBuffer(width*height*bands*2);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = bufferedImg.getRGB(x, y) & 0xffffff;
				if (bands >= 3) {
					String hex = String.format("%06x", pixel);
					str.append(hex);
				} else if (bands == 1) {
					str.append(String.format("%02x", pixel));
				}
			}
			str.append("\n");
		}
		return str.toString();
	}

	@Override
	protected String getFooter() {
		return "grestore  % Restore state\n%%EOF\n";
	}

	@Override
	public byte[] getBytes() {
		try {
			return toString().getBytes("ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			return super.getBytes();
		}
	}
}
