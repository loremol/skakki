package net.ironkernel.skakki.service;

import java.util.Set;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.entity.Role;
import net.ironkernel.skakki.repository.MemberRepository;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
        private final MemberRepository memberRepository;

        @Override
        @Transactional(readOnly = true)
        public UserDetails loadUserByUsername(String username) {
                Member member = memberRepository.findByUsername(username)
                                .orElseThrow(() -> new IllegalArgumentException("Nessun membro ha questo username"));
                Set<Role> roles = member.getRoles();
                String[] roleNames = roles.stream().map(Role::name).toArray(String[]::new);
                return User
                                .withUsername(member.getUsername())
                                .password(member.getPassword())
                                .roles(roleNames)
                                .build();
        }
}
