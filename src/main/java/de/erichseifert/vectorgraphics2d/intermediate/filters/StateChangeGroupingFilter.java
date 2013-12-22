package de.erichseifert.vectorgraphics2d.intermediate.filters;

import de.erichseifert.vectorgraphics2d.intermediate.Command;
import de.erichseifert.vectorgraphics2d.intermediate.commands.StateCommand;


public class StateChangeGroupingFilter extends GroupingFilter {

	public StateChangeGroupingFilter(Iterable<Command<?>> stream) {
		super(stream);
	}

	@Override
	protected boolean isGrouped(Command<?> command) {
		return command instanceof StateCommand;
	}
}

