package com.emedina.command.spring;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.emedina.command.spring.fixtures.AnotherTestCommand;
import com.emedina.command.spring.fixtures.AnotherTestCommandHandler;
import com.emedina.command.spring.fixtures.TestCommand;
import com.emedina.command.spring.fixtures.TestCommandHandler;
import com.emedina.sharedkernel.command.core.CommandBus;

/**
 * Integration tests for SpringCommandBus with real Spring context.
 * 
 * @author Enrique Medina Montenegro
 */
@SpringJUnitConfig
@ContextConfiguration(classes = SpringCommandBusIntegrationTest.TestConfiguration.class)
@DisplayName("SpringCommandBus Integration")
class SpringCommandBusIntegrationTest {

    @Autowired
    private CommandBus commandBus;

    @Autowired
    private TestCommandHandler testCommandHandler;

    @Autowired
    private AnotherTestCommandHandler anotherTestCommandHandler;

    @BeforeEach
    void setUp() {
        // given - reset handlers before each test
        testCommandHandler.reset();
        anotherTestCommandHandler.reset();
    }

    @Test
    @DisplayName("should execute test command through complete Spring integration")
    void shouldExecuteTestCommandThroughCompleteSpringIntegration() {
        // given
        TestCommand command = new TestCommand("integration test message");

        // when
        commandBus.execute(command);

        // then
        assertThat(testCommandHandler.wasExecuted()).isTrue();
        assertThat(testCommandHandler.getLastCommand()).isEqualTo(command);
        assertThat(testCommandHandler.getLastCommand().getMessage()).isEqualTo("integration test message");
    }

    @Test
    @DisplayName("should execute another test command through complete Spring integration")
    void shouldExecuteAnotherTestCommandThroughCompleteSpringIntegration() {
        // given
        AnotherTestCommand command = new AnotherTestCommand(42);

        // when
        commandBus.execute(command);

        // then
        assertThat(anotherTestCommandHandler.wasExecuted()).isTrue();
        assertThat(anotherTestCommandHandler.getLastCommand()).isEqualTo(command);
        assertThat(anotherTestCommandHandler.getLastCommand().getValue()).isEqualTo(42);
    }

    @Test
    @DisplayName("should handle multiple different commands independently")
    void shouldHandleMultipleDifferentCommandsIndependently() {
        // given
        TestCommand testCommand = new TestCommand("first command");
        AnotherTestCommand anotherCommand = new AnotherTestCommand(100);

        // when
        commandBus.execute(testCommand);
        commandBus.execute(anotherCommand);

        // then
        assertThat(testCommandHandler.wasExecuted()).isTrue();
        assertThat(testCommandHandler.getLastCommand()).isEqualTo(testCommand);

        assertThat(anotherTestCommandHandler.wasExecuted()).isTrue();
        assertThat(anotherTestCommandHandler.getLastCommand()).isEqualTo(anotherCommand);
    }

    @Test
    @DisplayName("should handle multiple commands of same type sequentially")
    void shouldHandleMultipleCommandsOfSameTypeSequentially() {
        // given
        TestCommand firstCommand = new TestCommand("first");
        TestCommand secondCommand = new TestCommand("second");

        // when
        commandBus.execute(firstCommand);
        commandBus.execute(secondCommand);

        // then
        assertThat(testCommandHandler.wasExecuted()).isTrue();
        assertThat(testCommandHandler.getLastCommand()).isEqualTo(secondCommand);
        assertThat(testCommandHandler.getLastCommand().getMessage()).isEqualTo("second");
    }

    /**
     * Test configuration for Spring integration tests.
     */
    @Configuration
    static class TestConfiguration {

        @Bean
        public TestCommandHandler testCommandHandler() {
            return new TestCommandHandler();
        }

        @Bean
        public AnotherTestCommandHandler anotherTestCommandHandler() {
            return new AnotherTestCommandHandler();
        }

        @Bean
        public Registry registry(org.springframework.context.ApplicationContext applicationContext) {
            return new Registry(applicationContext);
        }

        @Bean
        public CommandBus commandBus(Registry registry) {
            return new SpringCommandBus(registry);
        }
    }
}
