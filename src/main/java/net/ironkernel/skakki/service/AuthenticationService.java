package net.ironkernel.skakki.service;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.repository.MemberRepository;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Member getLoggedInMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            return null;
        String username = authentication.getName();
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException(
                        "Il principal autenticato '" + username + "' non ha un record membro corrispondente."));
    }
}
