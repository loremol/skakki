package net.ironkernel.skakki.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.entity.Role;
import net.ironkernel.skakki.repository.MemberRepository;

@ExtendWith({ MockitoExtension.class, OutputCaptureExtension.class })
class AdminAccountInitializerTest {
    private static final String CREDENTIALS_LOG_PREFIX =
            "Initial admin credentials: username=admin password=";

    @Mock
    private MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void createsAdminAndLogsGeneratedPasswordLast(CapturedOutput output) {
        when(memberRepository.existsByUsername("admin")).thenReturn(false);
        AdminAccountInitializer initializer = new AdminAccountInitializer(memberRepository, passwordEncoder);

        initializer.run(null);

        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(memberCaptor.capture());
        Member admin = memberCaptor.getValue();
        assertThat(admin.getUsername()).isEqualTo("admin");
        assertThat(admin.getEmail()).isEqualTo("admin@skakki.local");
        assertThat(admin.getRoles()).containsExactlyInAnyOrder(Role.ADMIN, Role.ORGANIZER, Role.MEMBER);

        String lastLine = output.getAll().strip().lines().reduce((first, second) -> second).orElseThrow();
        assertThat(lastLine).contains(CREDENTIALS_LOG_PREFIX);
        String generatedPassword = lastLine.substring(lastLine.indexOf(CREDENTIALS_LOG_PREFIX)
                + CREDENTIALS_LOG_PREFIX.length());
        assertThat(generatedPassword).matches("[A-Za-z0-9_-]{32}");
        assertThat(passwordEncoder.matches(generatedPassword, admin.getPassword())).isTrue();
    }

    @Test
    void leavesExistingAdminUnchangedWithoutLoggingCredentials(CapturedOutput output) {
        when(memberRepository.existsByUsername("admin")).thenReturn(true);
        AdminAccountInitializer initializer = new AdminAccountInitializer(memberRepository, passwordEncoder);

        initializer.run(null);

        verify(memberRepository, never()).save(any());
        assertThat(output.getAll()).doesNotContain(CREDENTIALS_LOG_PREFIX);
    }

    @Test
    void initializerIsAvailableWhenDemoVariableIsMissing() {
        contextRunner().run(context -> assertThat(context).hasSingleBean(AdminAccountInitializer.class));
    }

    @ParameterizedTest
    @ValueSource(strings = { "false", "", "yes" })
    void initializerIsAvailableWhenDemoVariableIsNotTrue(String value) {
        withDemoEnvironmentVariable(value)
                .run(context -> assertThat(context).hasSingleBean(AdminAccountInitializer.class));
    }

    @Test
    void initializerIsDisabledWhenDemoVariableIsTrue() {
        withDemoEnvironmentVariable("true")
                .run(context -> assertThat(context).doesNotHaveBean(AdminAccountInitializer.class));
    }

    private ApplicationContextRunner contextRunner() {
        return new ApplicationContextRunner()
                .withBean(MemberRepository.class, () -> mock(MemberRepository.class))
                .withBean(PasswordEncoder.class, () -> mock(PasswordEncoder.class))
                .withUserConfiguration(AdminAccountInitializer.class);
    }

    private ApplicationContextRunner withDemoEnvironmentVariable(String value) {
        return contextRunner().withInitializer(context -> TestPropertyValues.of("SKAKKI_DEMO=" + value)
                .applyTo(context.getEnvironment(), TestPropertyValues.Type.SYSTEM_ENVIRONMENT));
    }
}
