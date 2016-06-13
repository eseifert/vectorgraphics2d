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
package de.erichseifert.vectorgraphics2d;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.erichseifert.vectorgraphics2d.intermediate.commands.Command;
import de.erichseifert.vectorgraphics2d.util.PageSize;

public abstract class AbstractProcessor implements Processor {
	private final PageSize pageSize;
	private final List<Command<?>> commands;

	public AbstractProcessor(PageSize pageSize) {
		this.pageSize = pageSize;
		commands = new LinkedList<Command<?>>();
	}

	public PageSize getPageSize() {
		return pageSize;
	}

	@Override
	public void add(Command<?> command) {
		commands.add(command);
	}

	@Override
	public Iterable<Command<?>> getCommands() {
		return Collections.unmodifiableList(commands);
	}
}
