package net.ironkernel.skakki.config;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.EnumSet;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.entity.Role;
import net.ironkernel.skakki.repository.MemberRepository;

@Slf4j
@Component
@Profile("!test")
@Conditional(AdminAccountInitializer.NonDemoCondition.class)
@Order(Ordered.LOWEST_PRECEDENCE)
@RequiredArgsConstructor
public class AdminAccountInitializer implements ApplicationRunner {
    private static final String DEMO_PROPERTY = "skakki.demo";
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_EMAIL = "admin@skakki.local";
    private static final int PASSWORD_BYTES = 24;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (memberRepository.existsByUsername(ADMIN_USERNAME)) {
            return;
        }

        String password = generatePassword();
        Member admin = new Member();
        admin.setUsername(ADMIN_USERNAME);
        admin.setEmail(ADMIN_EMAIL);
        admin.setPassword(passwordEncoder.encode(password));
        admin.getRoles().addAll(EnumSet.of(Role.ADMIN, Role.ORGANIZER, Role.MEMBER));
        memberRepository.save(admin);

        log.warn("Initial admin credentials: username={} password={}", ADMIN_USERNAME, password);
    }

    private String generatePassword() {
        byte[] bytes = new byte[PASSWORD_BYTES];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    static final class NonDemoCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return !"true".equalsIgnoreCase(context.getEnvironment().getProperty(DEMO_PROPERTY));
        }
    }
}
