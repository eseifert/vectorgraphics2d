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

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSlider;

public class SwingExportTest extends TestCase {

	public SwingExportTest() throws IOException {
	}

	@Override
	public void draw(Graphics2D g) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(new JButton("Hello Swing!"), BorderLayout.CENTER);
		frame.getContentPane().add(new JSlider(), BorderLayout.NORTH);
		frame.setSize(200, 250);

		//g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		frame.setVisible(true);
		frame.printAll(g);
		frame.setVisible(false);
		frame.dispose();
	}
}
