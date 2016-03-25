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

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

import de.erichseifert.vectorgraphics2d.intermediate.commands.Command;
import de.erichseifert.vectorgraphics2d.intermediate.commands.CreateCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.DisposeCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.SetTransformCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.TransformCommand;
import de.erichseifert.vectorgraphics2d.intermediate.commands.TranslateCommand;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

public class AbsoluteToRelativeTransformsFilterTest {
	@Test
	public void testSetTransformCommandReplaced() {
		AffineTransform absoluteTransform = new AffineTransform();
		absoluteTransform.rotate(42.0);
		absoluteTransform.translate(4.0, 2.0);
		List<Command<?>> commands = wrapCommands(
			new SetTransformCommand(absoluteTransform)
		);

		AbsoluteToRelativeTransformsFilter filter = new AbsoluteToRelativeTransformsFilter(commands);

		Matcher<Iterable<Command<?>>> elem = Matchers.iterableWithSize(3);
		Matcher<Iterable<? super SetTransformCommand>> matches = not(hasItem(any(SetTransformCommand.class)));

		assertThat(filter, allOf(elem, matches));
	}

	@Test
	public void testAbsoluteAndRelativeTransformsIdentical() {
		AffineTransform absoluteTransform = new AffineTransform();
		absoluteTransform.rotate(42.0);
		absoluteTransform.translate(4.0, 2.0);
		List<Command<?>> commands = wrapCommands(
			new SetTransformCommand(absoluteTransform)
		);

		AbsoluteToRelativeTransformsFilter filter = new AbsoluteToRelativeTransformsFilter(commands);

		filter.next();
		AffineTransform relativeTransform = ((TransformCommand) filter.next()).getValue();
		assertThat(relativeTransform, is(absoluteTransform));
	}

	@Test
	public void testTranslateCorrect() {
		AffineTransform absoluteTransform = new AffineTransform();
		absoluteTransform.scale(2.0, 2.0);
		absoluteTransform.translate(4.2, 4.2); // (8.4, 8.4)
		List<Command<?>> commands = wrapCommands(
			new TranslateCommand(4.0, 2.0),
			new SetTransformCommand(absoluteTransform)
		);

		AbsoluteToRelativeTransformsFilter filter = new AbsoluteToRelativeTransformsFilter(commands);

		TransformCommand transformCommand = null;
		while (filter.hasNext()) {
			Command<?> filteredCommand = filter.next();
			if (filteredCommand instanceof TransformCommand) {
				transformCommand = (TransformCommand) filteredCommand;
			}
		}
		AffineTransform relativeTransform = transformCommand.getValue();
		assertThat(relativeTransform.getTranslateX(), is(4.4));
		assertThat(relativeTransform.getTranslateY(), is(6.4));
	}

	@Test
	public void testRelativeTransformAfterDispose() {
		AffineTransform absoluteTransform = new AffineTransform();
		absoluteTransform.rotate(42.0);
		absoluteTransform.translate(4.0, 2.0);
		List<Command<?>> commands = wrapCommands(
			new CreateCommand(null),
			new TransformCommand(absoluteTransform),
			new DisposeCommand(null),
			new SetTransformCommand(absoluteTransform)
		);

		AbsoluteToRelativeTransformsFilter filter = new AbsoluteToRelativeTransformsFilter(commands);
		TransformCommand lastTransformCommand = null;
		for (Command<?> filteredCommand : filter) {
			if (filteredCommand instanceof TransformCommand) {
				lastTransformCommand = (TransformCommand) filteredCommand;
			}
		}
		assertThat(lastTransformCommand.getValue(), is(absoluteTransform));
	}

	private List<Command<?>> wrapCommands(Command<?>... commands) {
		List<Command<?>> commandList = new ArrayList<Command<?>>(commands.length + 2);
		commandList.add(new CreateCommand(null));
		commandList.addAll(Arrays.asList(commands));
		commandList.add(new DisposeCommand(null));
		return commandList;
	}
}

