/*
 * VectorGraphics2D: Vector export for Java(R) Graphics2D
 *
 * (C) Copyright 2010-2013 Erich Seifert <dev[at]erichseifert.de>
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

package de.erichseifert.vectorgraphics2d.eps;

import de.erichseifert.vectorgraphics2d.Document;
import de.erichseifert.vectorgraphics2d.Processor;
import de.erichseifert.vectorgraphics2d.intermediate.Command;
import de.erichseifert.vectorgraphics2d.util.PageSize;

public class EPSProcessor implements Processor {
	public Document process(Iterable<Command<?>> commands, PageSize pageSize) {
		// TODO Apply rotate(theta,x,y) => translate-rotate-translate filter
		// TODO Apply image transparency => image mask filter
		// TODO Apply optimization filter
		EPSDocument doc = new EPSDocument(pageSize);
		for (Command<?> command : commands) {
			doc.handle(command);
		}
		doc.close();
		return doc;
	}
}

