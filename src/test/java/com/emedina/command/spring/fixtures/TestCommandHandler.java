package com.emedina.command.spring.fixtures;

import com.emedina.sharedkernel.command.core.CommandHandler;

/**
 * Test command handler fixture for testing the command bus.
 * 
 * @author Enrique Medina Montenegro
 */
public class TestCommandHandler implements CommandHandler<TestCommand> {

    private boolean wasExecuted = false;
    private TestCommand lastCommand;

    @Override
    public void handle(TestCommand command) {
        this.wasExecuted = true;
        this.lastCommand = command;
    }

    public boolean wasExecuted() {
        return wasExecuted;
    }

    public TestCommand getLastCommand() {
        return lastCommand;
    }

    public void reset() {
        this.wasExecuted = false;
        this.lastCommand = null;
    }
}
