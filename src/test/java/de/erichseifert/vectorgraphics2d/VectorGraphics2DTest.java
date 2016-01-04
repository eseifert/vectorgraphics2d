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
package de.erichseifert.vectorgraphics2d;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Iterator;
import org.junit.Test;

import de.erichseifert.vectorgraphics2d.intermediate.commands.Command;
import de.erichseifert.vectorgraphics2d.intermediate.commands.CreateCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.DisposeCommand;

public class VectorGraphics2DTest {
	@Test
	public void testEmptyVectorGraphics2DStartsWithCreateCommand() {
		VectorGraphics2D g = new VectorGraphics2D();
		Iterable<Command<?>> commands = g.getCommands();
		Iterator<Command<?>> commandIterator = commands.iterator();
		assertTrue(commandIterator.hasNext());

		Command<?> firstCommand = commandIterator.next();
		assertTrue(firstCommand instanceof CreateCommand);
		assertEquals(g, ((CreateCommand) firstCommand).getValue());
	}

	@Test
	public void testCreateEmitsCreateCommand() {
		VectorGraphics2D g = new VectorGraphics2D();
		Iterable<Command<?>> gCommands = g.getCommands();
		Iterator<Command<?>> gCommandIterator = gCommands.iterator();
		CreateCommand gCreateCommand = (CreateCommand) gCommandIterator.next();

		VectorGraphics2D g2 = (VectorGraphics2D) g.create();
		CreateCommand g2CreateCommand = null;
		for (Command<?> g2Command : g2.getCommands()) {
			if (g2Command instanceof CreateCommand) {
				g2CreateCommand = (CreateCommand) g2Command;
			}
		}
		assertNotEquals(gCreateCommand, g2CreateCommand);
		assertEquals(g2, g2CreateCommand.getValue());
	}

	@Test
	public void testDisposeCommandEmitted() {
		Graphics2D g = new VectorGraphics2D();
		g.setColor(Color.RED);

		Graphics2D g2 = (Graphics2D) g.create();
		g2.setColor(Color.BLUE);
		g2.dispose();

		Iterable<Command<?>> commands = ((VectorGraphics2D) g).getCommands();
		Command<?> lastCommand = null;
		for (Command<?> command : commands) {
			lastCommand = command;
		}

		assertTrue(lastCommand instanceof DisposeCommand);
		assertEquals(Color.BLUE, ((DisposeCommand) lastCommand).getValue().getColor());
	}
}
