/**
 * VectorGraphics2D : Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010 Erich Seifert <info[at]erichseifert.de>
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
 * Lesser GNU Lesser General Public License for more details.
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
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * <code>Graphics2D</code> implementation that saves all operations to a SVG string.
 */
public class PDFGraphics2D extends VectorGraphics2D {
	protected static final double MM_IN_UNITS = 72.0 / 25.4;

	private static final Map<Integer, Integer> STROKE_ENDCAPS;
	private static final Map<Integer, Integer> STROKE_LINEJOIN;

	static {
		STROKE_ENDCAPS = new HashMap<Integer, Integer>();
		STROKE_ENDCAPS.put(BasicStroke.CAP_BUTT, 0);
		STROKE_ENDCAPS.put(BasicStroke.CAP_ROUND, 1);
		STROKE_ENDCAPS.put(BasicStroke.CAP_SQUARE, 2);

		STROKE_LINEJOIN = new HashMap<Integer, Integer>();
		STROKE_LINEJOIN.put(BasicStroke.JOIN_MITER, 0);
		STROKE_LINEJOIN.put(BasicStroke.JOIN_ROUND, 1);
		STROKE_LINEJOIN.put(BasicStroke.JOIN_BEVEL, 2);
	}

	private int curObjId;
	private final Map<Integer, Integer> objPositions;
	private final Map<Double, String> transpResources;
	private final Map<BufferedImage, String> imageResources;
	private int contentStart;

	/**
	 * Constructor that initializes a new <code>SVGGraphics2D</code> instance.
	 */
	public PDFGraphics2D(double x, double y, double width, double height) {
		super(x, y, width, height);
		curObjId = 1;
		objPositions = new TreeMap<Integer, Integer>();
		transpResources = new TreeMap<Double, String>();
		imageResources = new TreeMap<BufferedImage, String>();
		writeHeader();
	}

	@Override
	protected void writeString(String str, double x, double y) {
		// TODO: Encode string
		//byte[] bytes = str.getBytes("ISO-8859-1");
		// Escape string
		str = str.replaceAll("\\\\", "\\\\")
			.replaceAll("\t", "\\t").replaceAll("\b", "\\b").replaceAll("\f", "\\f")
			.replaceAll("\\(", "\\(").replaceAll("\\)", "\\)");
		// Extract lines
		String[] lines = str.replaceAll("\r\n", "\n").replaceAll("\r", "\n").split("\n");
		// Output lines
		write("BT ");
		write(x, " ", y, " Td ");
		for (String line : lines) {
			if (line != lines[0]) {
				write("T* ");
			}
			write("(", line, ") Tj ");
		}
		writeln("ET");
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
				writeln(bs.getLineWidth(), " w");
			}
			if (bs.getLineJoin() != bsPrev.getLineJoin()) {
				writeln(STROKE_LINEJOIN.get(bs.getLineJoin()), " j");
			}
			if (bs.getEndCap() != bsPrev.getEndCap()) {
				writeln(STROKE_ENDCAPS.get(bs.getEndCap()), " J");
			}
			if ((!Arrays.equals(bs.getDashArray(), bsPrev.getDashArray())) ||
				(bs.getDashPhase() != bsPrev.getDashPhase())) {
				write("[");
				float[] pattern = bs.getDashArray();
				for (int i = 0; i < pattern.length; i++) {
					if (i > 0) {
						write(" ");
					}
					write(pattern[i]);
				}
				writeln("] ", bs.getDashPhase(), " d");
			}
		}
	}

	@Override
	protected void writeImage(Image img, int imgWidth, int imgHeight, double x, double y, double width, double height) {
		// TODO: Create PDF image object (see PDF Spec. 1.7, p. 209)
		/*String imgData = getPdf(img);
		writeln("q");
		writeln(imgWidth/width, " 0 0 ", imgHeight/height, " ", x, " ", y, " cm");
		writeln(imgObj, ">");
		writeln("Q");*/
	}

	@Override
	public void setColor(Color c) {
		Color color = getColor();
		if (c != null) {
			super.setColor(c);
			if (color.getAlpha() != c.getAlpha()) {
				// Add a new graphics state to resources
				double a = c.getAlpha()/255.0;
				String transpResourceId = getTransparencyResource(a);
				writeln("/", transpResourceId, " gs");
			}
			if (color.getRed() != c.getRed() || color.getGreen() != c.getGreen() || color.getBlue() != c.getBlue()) {
				double r = c.getRed()/255.0;
				double g = c.getGreen()/255.0;
				double b = c.getBlue()/255.0;
				write(r, " ", g, " ", b, " rg ");
				writeln(r, " ", g, " ", b, " RG");
			}
		}
	}

	@Override
	public void setFont(Font font) {
		if (!getFont().equals(font)) {
			super.setFont(font);
			writeln("/", font.getPSName(), " ", font.getSize2D(), " Tf");
		}
	}

	@Override
	protected void writeHeader() {
		Rectangle2D bounds = getBounds();
		int x = (int)Math.floor(bounds.getX() * MM_IN_UNITS);
		int y = (int)Math.floor(bounds.getY() * MM_IN_UNITS);
		int w = (int)Math.ceil(bounds.getWidth() * MM_IN_UNITS);
		int h = (int)Math.ceil(bounds.getHeight() * MM_IN_UNITS);

		writeln("%PDF-1.4");
		// Object 1
		writeObj(
			"Type", "/Catalog",
			"Outlines", "2 0 R",
			"Pages", "3 0 R"
		);
		// Object 2
		writeObj(
			"Type", "/Outlines",
			"Count", "0"
		);
		// Object 3
		writeObj(
			"Type", "/Pages",
			"Kids", "[4 0 R]",
			"Count", "1"
		);
		// Object 4
		writeObj(
			"Type", "/Page",
			"Parent", "[4 0 R]",
			"MediaBox", String.format("[%d %d %d %d]", x, y, w, h),
			"Contents", "5 0 R",
			"Resources", "7 0 R"
		);
		// Object 5
		writeln(nextObjId(size()), " 0 obj");
		writeDict("Length", "6 0 R");
		contentStart = size();
		writeln("stream");
		writeln("q");
		// Adjust page size and page origin
		writeln(MM_IN_UNITS, " 0 0 ", -MM_IN_UNITS, " 0 ", h, " cm");
	}

	protected void writeDict(Object... strs) {
		writeln("<<");
		for (int i = 0; i < strs.length; i += 2) {
			writeln("/", strs[i], " ", strs[i+1]);
		}
		writeln(">>");
	}

	protected int writeObj(Object... strs) {
		int objId = nextObjId(size());
		writeln(objId, " 0 obj");
		writeDict(strs);
		writeln("endobj");
		return objId;
	}

	protected int peekObjId() {
		return curObjId + 1;
	}

	private int nextObjId(int position) {
		objPositions.put(curObjId, position);
		return curObjId++;
	}

	protected String getTransparencyResource(double a) {
		String name = transpResources.get(a);
		if (name == null) {
			name = String.format("Trn%04d", transpResources.size());
			transpResources.put(a, name);
		}
		return name;
	}

	protected String getImageResource(Image image) {
		BufferedImage bufferedImg = GraphicsUtils.toBufferedImage(image);
		String name = imageResources.get(bufferedImg);
		if (name == null) {
			name = String.format("Img%04d", imageResources.size());
			imageResources.put(bufferedImg, name);
		}
		return name;
	}

	/**
	 * Utility method for writing a tag closing fragment for drawing operations.
	 */
	@Override
	protected void writeClosingDraw() {
		writeln(" S");
	}

	/**
	 * Utility method for writing a tag closing fragment for filling operations.
	 */
	@Override
	protected void writeClosingFill() {
		writeln(" f");
	}

	/**
	 * Utility method for writing an arbitrary shape to.
	 * It tries to translate Java2D shapes to the corresponding SVG shape tags.
	 */
	@Override
	protected void writeShape(Shape s) {
		AffineTransform transform = getTransform();
		if (!isDistorted()) {
			double sx = transform.getScaleX();
			double sy = transform.getScaleX();
			double tx = transform.getTranslateX();
			double ty = transform.getTranslateY();
			if (s instanceof Line2D) {
				Line2D l = (Line2D) s;
				double x1 = sx*l.getX1() + tx;
				double y1 = sy*l.getY1() + ty;
				double x2 = sx*l.getX2() + tx;
				double y2 = sy*l.getY2() + ty;
				write(x1, " ", y1, " m ", x2, " ", y2, " l");
				return;
			} else if (s instanceof Rectangle2D) {
				Rectangle2D r = (Rectangle2D) s;
				double x = sx*r.getX() + tx;
				double y = sy*r.getY() + ty;
				double width = sx*r.getWidth();
				double height = sy*r.getHeight();
				write(x, " ", y, " ", width, " ", height, " re");
				return;
			}
		}

		s = transform.createTransformedShape(s);
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
				write(coordsCur[0], " ", coordsCur[1], " m");
				pointPrev[0] = coordsCur[0];
				pointPrev[1] = coordsCur[1];
				break;
			case PathIterator.SEG_LINETO:
				write(coordsCur[0], " ", coordsCur[1], " l");
				pointPrev[0] = coordsCur[0];
				pointPrev[1] = coordsCur[1];
				break;
			case PathIterator.SEG_CUBICTO:
				write(coordsCur[0], " ", coordsCur[1], " ", coordsCur[2], " ", coordsCur[3], " ", coordsCur[4], " ", coordsCur[5], " c");
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
				write(x1, " ", y1, " ", x2, " ", y2, " ", x3, " ", y3, " c");
				pointPrev[0] = x3;
				pointPrev[1] = y3;
				break;
			case PathIterator.SEG_CLOSE:
				write("h");
				break;
			}
		}
	}

	@Override
	protected String getFooter() {
		StringBuffer footer = new StringBuffer("Q\nendstream\n");
		int contentEnd = size() + footer.length();

		int lenObjId = nextObjId(size() + footer.length());
		footer.append(lenObjId).append(" 0 obj\n");
		footer.append(contentEnd - contentStart).append("\n");
		footer.append("endobj\n");

		int resourcesObjId = nextObjId(size() + footer.length());
		footer.append(resourcesObjId).append(" 0 obj\n");
		footer.append("<<\n");
		footer.append(" /ProcSet [/PDF /Text /ImageB /ImageC /ImageI]\n");
		footer.append(" /ExtGState <<\n");
		for (Map.Entry<Double, String> entry : transpResources.entrySet()) {
			Double alpha = entry.getKey();
			String name = entry.getValue();
			footer.append("  /").append(name).append(" << /Type /ExtGState")
				.append(" /ca ").append(alpha).append(" /CA ").append(alpha)
				.append(" >>\n");
		}
		footer.append(" >>\n");
		footer.append(">>\n");
		footer.append("endobj\n");

		int objs = objPositions.size() + 1;

		int xrefPos = size() + footer.length();
		footer.append("xref\n");
		footer.append("0 ").append(objs).append("\n");
		footer.append(String.format("%010d %05d f", 0, 65535)).append("\n");
		for (int pos : objPositions.values()) {
			footer.append(String.format("%010d %05d n", pos, 0)).append("\n");
		}

		footer.append("trailer\n");
		footer.append("<<\n");
		footer.append("/Size ").append(objs).append("\n");
		footer.append("/Root 1 0 R\n");
		footer.append(">>\n");
		footer.append("startxref\n");
		footer.append(xrefPos).append("\n");

		footer.append("%%EOF\n");

		return footer.toString();
	}

}
