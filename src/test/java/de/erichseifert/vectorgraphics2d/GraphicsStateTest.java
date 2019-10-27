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
package de.erichseifert.vectorgraphics2d;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.junit.Test;

public class GraphicsStateTest {

	@Test
	public void testInitialStateIsEqualToGraphics2D() {
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		GraphicsState state = new GraphicsState();

		assertEquals(state.getBackground(), g2d.getBackground());
		assertEquals(state.getColor(), g2d.getColor());
		assertEquals(state.getClip(), g2d.getClip());
		assertEquals(state.getComposite(), g2d.getComposite());
		assertEquals(state.getFont(), g2d.getFont());
		assertEquals(state.getPaint(), g2d.getPaint());
		assertEquals(state.getStroke(), g2d.getStroke());
		assertEquals(state.getTransform(), g2d.getTransform());
	}

	@Test
	public void testEquals() {
		GraphicsState state1 = new GraphicsState();
		state1.setBackground(Color.WHITE);
		state1.setColor(Color.BLACK);
		state1.setClip(new Rectangle2D.Double(0, 0, 10, 10));

		GraphicsState state2 = new GraphicsState();
		state2.setBackground(Color.WHITE);
		state2.setColor(Color.BLACK);
		state2.setClip(new Rectangle2D.Double(0, 0, 10, 10));

		assertEquals(state1, state2);

		state2.setTransform(AffineTransform.getTranslateInstance(5, 5));

		assertFalse(state1.equals(state2));
	}

	@Test
	public void testClone() throws CloneNotSupportedException {
		GraphicsState state = new GraphicsState();
		state.setBackground(Color.BLUE);
		state.setColor(Color.GREEN);
		state.setClip(new Rectangle2D.Double(2, 3, 4, 2));

		GraphicsState clone = (GraphicsState) state.clone();

		assertNotSame(state, clone);
		assertEquals(state, clone);
	}
}

