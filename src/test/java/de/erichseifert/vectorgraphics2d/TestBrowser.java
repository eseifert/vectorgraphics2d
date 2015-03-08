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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.ghost4j.GhostscriptException;

public class TestBrowser extends JFrame {
	private final List<TestCase> testCases;

	private final JSplitPane imageComparisonPanel;

	private static class ImageDisplayPanel extends JPanel {
		private final BufferedImage renderedImage;
		private final InputStream imageData;

		public ImageDisplayPanel(BufferedImage renderedImage, InputStream imageData) {
			super(new BorderLayout());
			this.renderedImage = renderedImage;
			this.imageData = imageData;

			JLabel imageLabel = new JLabel(new ImageIcon(renderedImage));
			add(imageLabel, BorderLayout.CENTER);

			JButton saveToFileButton = new JButton("Save as...");
			saveToFileButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JFileChooser saveFileDialog = new JFileChooser();
					saveFileDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
					saveFileDialog.setMultiSelectionEnabled(false);
					int userChoice = saveFileDialog.showSaveDialog(ImageDisplayPanel.this);
					if (userChoice != JFileChooser.APPROVE_OPTION) {
						return;
					}

					File dest = saveFileDialog.getSelectedFile();
					FileOutputStream destStream = null;
					try {
						destStream = new FileOutputStream(dest);
						int imageDataChunk;
						while ((imageDataChunk = ImageDisplayPanel.this.imageData.read()) != -1){
							destStream.write(imageDataChunk);
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					} finally {
						if (destStream != null) {
							try {
								destStream.close();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}
				}
			});
			add(saveToFileButton, BorderLayout.SOUTH);
		}
	}

	public TestBrowser() {
		super("Test browser");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(1024, 768);

		testCases = new ArrayList<TestCase>();
		try {
			testCases.add(new ColorTest());
			testCases.add(new StrokeTest());
			testCases.add(new ShapesTest());
			testCases.add(new FontTest());
			testCases.add(new EmptyFileTest());
			testCases.add(new ImageTest());
			testCases.add(new ClippingTest());
			testCases.add(new TransformTest());
		} catch (IOException e) {
			e.printStackTrace();
		}

		final JList testList = new JList(testCases.toArray());
		testList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		testList.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				String testName = value.getClass().getSimpleName();
				return super.getListCellRendererComponent(list, testName, index, isSelected, cellHasFocus);
			}
		});
		testList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					int index = testList.getSelectedIndex();
					TestCase test = testCases.get(index);
					try {
						setTestCase(test);
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (GhostscriptException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		getContentPane().add(testList, BorderLayout.WEST);

		imageComparisonPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		getContentPane().add(imageComparisonPanel, BorderLayout.CENTER);
	}

	public void setTestCase(TestCase test) throws IOException, GhostscriptException {
		BufferedImage reference = test.getReference();
		imageComparisonPanel.setTopComponent(new JLabel(new ImageIcon(reference)));
		imageComparisonPanel.setBottomComponent(new ImageDisplayPanel(test.getRasterizedEPS(), test.getEPS()));
		imageComparisonPanel.setDividerLocation(0.5);
		imageComparisonPanel.revalidate();
		imageComparisonPanel.repaint();
	}

	public static void main(String[] args) {
		new TestBrowser().setVisible(true);
	}
}
