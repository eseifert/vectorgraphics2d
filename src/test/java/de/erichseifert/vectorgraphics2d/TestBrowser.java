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
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

import org.ghost4j.GhostscriptException;

public class TestBrowser extends JFrame {
	private final List<TestCase> testCases;

	public TestBrowser() {
		super("Test browser");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(1024, 768);

		testCases = new ArrayList<TestCase>();
		try {
			testCases.add(new ColorTest());
			testCases.add(new StrokeTest());
			testCases.add(new ShapesTest());
		} catch (IOException e) {
			e.printStackTrace();
		}

		JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		// TODO: Allow test case selection
		TestCase test = testCases.get(0);
		BufferedImage reference = test.getReference();
		jSplitPane.setTopComponent(new JLabel(new ImageIcon(reference)));
		try {
			jSplitPane.setBottomComponent(new JLabel(new ImageIcon(test.getRasterizedEPS())));
		} catch (GhostscriptException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		getContentPane().add(jSplitPane, BorderLayout.CENTER);
	}

	public static void main(String[] args) {
		new TestBrowser().setVisible(true);
	}
}
