package de.erichseifert.vectorgraphics2d.intermediate;

import de.erichseifert.vectorgraphics2d.intermediate.commands.Command;

/**
 * Represents a sequence of {@link Command} objects.
 * It is possible to add commands to this sequence and to retrieve them through an {@code Iterator}.
 */
public interface CommandSequence extends Iterable<Command<?>> {
	/**
	 * Adds the specified command to the end of this sequence.
	 * @param command Command to be added.
	 */
	void add(Command<?> command);
}
