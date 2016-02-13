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
package de.erichseifert.vectorgraphics2d.intermediate.filters;

import static org.junit.Assert.assertEquals;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;

import de.erichseifert.vectorgraphics2d.intermediate.commands.Command;
import de.erichseifert.vectorgraphics2d.intermediate.commands.Group;
import de.erichseifert.vectorgraphics2d.intermediate.commands.DrawShapeCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.SetColorCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.SetStrokeCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.SetTransformCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.StateCommand;

public class GroupingFilterTest {
	@Test public void filtered() {
		List<Command<?>> resultStream = new LinkedList<Command<?>>();
		resultStream.add(new SetColorCommand(Color.BLACK));
		resultStream.add(new SetStrokeCommand(new BasicStroke(1f)));
		resultStream.add(new DrawShapeCommand(new Line2D.Double(0.0, 1.0, 10.0, 11.0)));
		resultStream.add(new SetTransformCommand(AffineTransform.getTranslateInstance(5.0, 5.0)));
		resultStream.add(new DrawShapeCommand(new Line2D.Double(0.0, 1.0, 5.0, 6.0)));

		List<Command<?>> expectedStream = new LinkedList<Command<?>>();
		Iterator<Command<?>> resultCloneIterator = resultStream.iterator();
		Group group1 = new Group();
		group1.add(resultCloneIterator.next());
		group1.add(resultCloneIterator.next());
		expectedStream.add(group1);
		expectedStream.add(resultCloneIterator.next());
		Group group2 = new Group();
		group2.add(resultCloneIterator.next());
		expectedStream.add(group2);
		expectedStream.add(resultCloneIterator.next());
		Iterator<Command<?>> expectedIterator = expectedStream.iterator();

		Filter resultIterator = new GroupingFilter(resultStream) {
			@Override
			protected boolean isGrouped(Command<?> command) {
				return command instanceof StateCommand;
			}
		};

		for (; resultIterator.hasNext() || expectedIterator.hasNext();) {
			Command<?> result = resultIterator.next();
			Command<?> expected = expectedIterator.next();
			assertEquals(expected, result);
		}
	}
}

