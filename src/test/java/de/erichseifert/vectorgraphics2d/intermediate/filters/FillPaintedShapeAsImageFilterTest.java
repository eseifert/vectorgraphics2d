/*
 * VectorGraphics2D: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010-2016 Erich Seifert <dev[at]erichseifert.de>
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

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;

import de.erichseifert.vectorgraphics2d.intermediate.Command;
import de.erichseifert.vectorgraphics2d.intermediate.commands.DrawImageCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.FillShapeCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.RotateCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.SetPaintCommand;

public class FillPaintedShapeAsImageFilterTest {
	@Test
	public void testFillShapeReplacedWithDrawImage() {
		List<Command<?>> commands = new LinkedList<Command<?>>();
		commands.add(new SetPaintCommand(new GradientPaint(0.0f, 0.0f, Color.BLACK, 100.0f, 100.0f, Color.WHITE)));
		commands.add(new RotateCommand(10.0, 4.0, 2.0, new AffineTransform()));
		commands.add(new FillShapeCommand(new Rectangle2D.Double(10.0, 10.0, 100.0, 100.0)));

		FillPaintedShapeAsImageFilter filter = new FillPaintedShapeAsImageFilter(commands);

		assertThat(filter, hasItem(any(DrawImageCommand.class)));
		assertThat(filter, not(hasItem(any(FillShapeCommand.class))));
	}

	@Test
	public void testFillShapeNotReplacedWithoutPaintCommand() {
		List<Command<?>> commands = new LinkedList<Command<?>>();
		commands.add(new RotateCommand(10.0, 4.0, 2.0, new AffineTransform()));
		commands.add(new FillShapeCommand(new Rectangle2D.Double(10.0, 10.0, 100.0, 100.0)));

		FillPaintedShapeAsImageFilter filter = new FillPaintedShapeAsImageFilter(commands);

		Iterator<Command<?>>  filterIterator = filter.iterator();
		for (Command<?> command : commands) {
			assertEquals(command, filterIterator.next());
		}
		assertFalse(filterIterator.hasNext());
	}
}

