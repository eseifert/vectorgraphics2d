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
package de.erichseifert.vectorgraphics2d.intermediate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import de.erichseifert.vectorgraphics2d.intermediate.commands.DrawShapeCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.SetColorCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.SetStrokeCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.SetTransformCommand;

public class FilterTest {
	@Test public void filterNone() {
		CommandStream stream = new CommandStream();
		stream.add(null, new SetColorCommand(Color.BLACK));
		stream.add(null, new SetStrokeCommand(new BasicStroke(1f)));
		stream.add(null, new DrawShapeCommand(new Line2D.Double(0.0, 1.0, 10.0, 11.0)));
		stream.add(null, new SetTransformCommand(AffineTransform.getTranslateInstance(5.0, 5.0)));
		stream.add(null, new DrawShapeCommand(new Line2D.Double(0.0, 1.0, 5.0, 6.0)));

		Iterator<Command<?>> unfiltered = stream.iterator();

		Filter filtered = new Filter(stream) {
			@Override
			protected List<Command<?>> filter(Command<?> command) {
				return Arrays.<Command<?>>asList(command);
			}
		};

		while (filtered.hasNext() || unfiltered.hasNext()) {
			Command<?> expected = unfiltered.next();
			Command<?> result = filtered.next();
			assertEquals(expected, result);
		}
	}

	@Test public void filterAll() {
		CommandStream stream = new CommandStream();
		stream.add(null, new SetColorCommand(Color.BLACK));
		stream.add(null, new SetStrokeCommand(new BasicStroke(1f)));
		stream.add(null, new DrawShapeCommand(new Line2D.Double(0.0, 1.0, 10.0, 11.0)));
		stream.add(null, new SetTransformCommand(AffineTransform.getTranslateInstance(5.0, 5.0)));
		stream.add(null, new DrawShapeCommand(new Line2D.Double(0.0, 1.0, 5.0, 6.0)));

		Iterator<Command<?>> unfiltered = stream.iterator();

		Filter filtered = new Filter(stream) {
			@Override
			protected List<Command<?>> filter(Command<?> command) {
				return null;
			}
		};

		assertTrue(unfiltered.hasNext());
		assertFalse(filtered.hasNext());
	}

	@Test public void duplicate() {
		CommandStream stream = new CommandStream();
		stream.add(null, new SetColorCommand(Color.BLACK));
		stream.add(null, new SetStrokeCommand(new BasicStroke(1f)));
		stream.add(null, new DrawShapeCommand(new Line2D.Double(0.0, 1.0, 10.0, 11.0)));
		stream.add(null, new SetTransformCommand(AffineTransform.getTranslateInstance(5.0, 5.0)));
		stream.add(null, new DrawShapeCommand(new Line2D.Double(0.0, 1.0, 5.0, 6.0)));

		Iterator<Command<?>> unfiltered = stream.iterator();

		Filter filtered = new Filter(stream) {
			@Override
			protected List<Command<?>> filter(Command<?> command) {
				return Arrays.asList(command, command);
			}
		};

		while (filtered.hasNext() || unfiltered.hasNext()) {
			Command<?> expected = unfiltered.next();
			Command<?> result1 = filtered.next();
			Command<?> result2 = filtered.next();
			assertEquals(expected, result1);
			assertEquals(expected, result2);
		}
	}
}

