package net.ironkernel.skakki.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.security.crypto.password.PasswordEncoder;

import net.ironkernel.skakki.repository.ArbiterApplicationRepository;
import net.ironkernel.skakki.repository.MatchRepository;
import net.ironkernel.skakki.repository.MemberRepository;
import net.ironkernel.skakki.repository.RoundRepository;
import net.ironkernel.skakki.repository.SignupRequestRepository;
import net.ironkernel.skakki.repository.TournamentRepository;
import net.ironkernel.skakki.service.strategy.MatchStrategyFactory;

class MockDataInitializerConditionTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withBean(MemberRepository.class, () -> mock(MemberRepository.class))
            .withBean(TournamentRepository.class, () -> mock(TournamentRepository.class))
            .withBean(RoundRepository.class, () -> mock(RoundRepository.class))
            .withBean(MatchRepository.class, () -> mock(MatchRepository.class))
            .withBean(SignupRequestRepository.class, () -> mock(SignupRequestRepository.class))
            .withBean(ArbiterApplicationRepository.class, () -> mock(ArbiterApplicationRepository.class))
            .withBean(PasswordEncoder.class, () -> mock(PasswordEncoder.class))
            .withBean(MatchStrategyFactory.class, () -> mock(MatchStrategyFactory.class))
            .withUserConfiguration(MockDataInitializer.class);

    @Test
    void initializerIsDisabledByDefault() {
        contextRunner.run(context -> assertThat(context).doesNotHaveBean(MockDataInitializer.class));
    }

    @ParameterizedTest
    @ValueSource(strings = { "false", "yes" })
    void initializerIsDisabledWhenDemoIsNotTrue(String value) {
        withDemoEnvironmentVariable(value)
                .run(context -> assertThat(context).doesNotHaveBean(MockDataInitializer.class));
    }

    @Test
    void initializerIsEnabledWhenDemoIsTrue() {
        withDemoEnvironmentVariable("true")
                .run(context -> assertThat(context).hasSingleBean(MockDataInitializer.class));
    }

    private ApplicationContextRunner withDemoEnvironmentVariable(String value) {
        return contextRunner.withInitializer(context -> TestPropertyValues.of("SKAKKI_DEMO=" + value)
                .applyTo(context.getEnvironment(), TestPropertyValues.Type.SYSTEM_ENVIRONMENT));
    }
}
