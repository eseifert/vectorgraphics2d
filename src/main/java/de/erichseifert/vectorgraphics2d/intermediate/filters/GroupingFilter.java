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
package de.erichseifert.vectorgraphics2d.intermediate.filters;

import java.util.Arrays;
import java.util.List;

import de.erichseifert.vectorgraphics2d.intermediate.Command;
import de.erichseifert.vectorgraphics2d.intermediate.Filter;
import de.erichseifert.vectorgraphics2d.intermediate.Group;


public abstract class GroupingFilter extends Filter {
	private Group group;

	public GroupingFilter(Iterable<Command<?>> stream) {
		super(stream);
	}

	@Override
	public boolean hasNext() {
		return group != null || super.hasNext();
	}

	@Override
	public Command<?> next() {
		if (group == null) {
			return super.next();
		}
		Group g = group;
		group = null;
		return g;
	}

	@Override
	protected List<Command<?>> filter(Command<?> command) {
		boolean grouped = isGrouped(command);
		if (grouped) {
			if (group == null) {
				group = new Group();
			}
			group.add(command);
			return null;
		}
		return Arrays.<Command<?>>asList(command);
	}

	protected abstract boolean isGrouped(Command<?> command);
}

