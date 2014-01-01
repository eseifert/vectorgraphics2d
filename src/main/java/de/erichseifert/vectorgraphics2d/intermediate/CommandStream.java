/*
 * VectorGraphics2D: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010-2014 Erich Seifert <dev[at]erichseifert.de>
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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.erichseifert.vectorgraphics2d.VectorGraphics2D;
import de.erichseifert.vectorgraphics2d.intermediate.commands.SetBackgroundCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.SetClipCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.SetColorCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.SetCompositeCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.SetFontCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.SetPaintCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.SetStrokeCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.SetTransformCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.SetXORModeCommand;

public class CommandStream implements Iterable<Command<?>> {
	private final List<Command<?>> commands;
	private VectorGraphics2D state;

	public CommandStream() {
		commands = new LinkedList<Command<?>>();
	}

	public Iterator<Command<?>> iterator() {
		return commands.iterator();
	}

	public void add(VectorGraphics2D newState, Command<?> e) {
		// Check if the state has changed, i.e. different Graphics2D objects
		// have been used.
		if (state != null && newState != state) {
			addStateChangeCommands(newState);
		}
		commands.add(e);
		state = newState;
	}

	public boolean contains(Object o) {
		return commands.contains(o);
	}

	public int size() {
		return commands.size();
	}

	public Command<?> get(int index) {
		return commands.get(index);
	}

	private void addStateChangeCommands(VectorGraphics2D newState) {
		//TODO Transfer renderingHints
		if (!state.getBackground().equals(newState.getBackground())) {
			commands.add(new SetBackgroundCommand(newState.getBackground()));
		}
		if (!state.getColor().equals(newState.getColor())) {
			commands.add(new SetColorCommand(newState.getColor()));
		}
		if (!state.getComposite().equals(newState.getComposite())) {
			commands.add(new SetCompositeCommand(newState.getComposite()));
		}
		if (!state.getFont().equals(newState.getFont())) {
			commands.add(new SetFontCommand(newState.getFont()));
		}
		if (!state.getPaint().equals(newState.getPaint())) {
			commands.add(new SetPaintCommand(newState.getPaint()));
		}
		if (!state.getStroke().equals(newState.getStroke())) {
			commands.add(new SetStrokeCommand(newState.getStroke()));
		}
		if (!state.getXORMode().equals(newState.getXORMode())) {
			commands.add(new SetXORModeCommand(newState.getXORMode()));
		}
		if (!state.getTransform().equals(newState.getTransform())) {
			commands.add(new SetTransformCommand(newState.getTransform()));
		}
		if (state.getClip() != newState.getClip()) {
			if ((state.getClip() == null || newState.getClip() == null) ||
					!state.getClip().equals(newState.getClip())) {
				commands.add(new SetClipCommand(newState.getClip()));
			}
		}
	}
}

