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

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import de.erichseifert.vectorgraphics2d.intermediate.commands.AffineTransformCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.Command;
import de.erichseifert.vectorgraphics2d.intermediate.commands.CreateCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.DisposeCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.SetTransformCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.TransformCommand;

public class AbsoluteToRelativeTransformsFilter extends Filter {
	private Stack<AffineTransform> transforms;

	public AbsoluteToRelativeTransformsFilter(Iterable<Command<?>> stream) {
		super(stream);
		transforms = new Stack<AffineTransform>();
	}

	@Override
	public Command<?> next() {
		Command<?> nextCommand = super.next();
		if (nextCommand instanceof AffineTransformCommand) {
			AffineTransformCommand affineTransformCommand = (AffineTransformCommand) nextCommand;
			getCurrentTransform().concatenate(affineTransformCommand.getValue());
		} else if (nextCommand instanceof CreateCommand) {
			AffineTransform newTransform = transforms.isEmpty() ? new AffineTransform() : new AffineTransform(getCurrentTransform());
			transforms.push(newTransform);
		} else if (nextCommand instanceof DisposeCommand) {
			transforms.pop();
		}

		return nextCommand;
	}

	@Override
	protected List<Command<?>> filter(Command<?> command) {
		if (command instanceof SetTransformCommand) {
			SetTransformCommand setTransformCommand = (SetTransformCommand) command;
			AffineTransform absoluteTransform = setTransformCommand.getValue();
			AffineTransform relativeTransform = new AffineTransform();
			try {
				AffineTransform invertedOldTransformation = getCurrentTransform().createInverse();
				relativeTransform.concatenate(invertedOldTransformation);
			} catch (NoninvertibleTransformException e) {
				e.printStackTrace();
			}
			relativeTransform.concatenate(absoluteTransform);
			TransformCommand transformCommand = new TransformCommand(relativeTransform);
			return Arrays.<Command<?>>asList(transformCommand);
		}
		return Arrays.<Command<?>>asList(command);
	}

	private AffineTransform getCurrentTransform() {
		return transforms.peek();
	}
}

