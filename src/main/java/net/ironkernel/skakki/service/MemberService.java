package net.ironkernel.skakki.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.ironkernel.skakki.dto.AdminCreateMemberDto;
import net.ironkernel.skakki.dto.RegistrationDto;
import net.ironkernel.skakki.dto.UpdateMemberDto;
import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.entity.Role;
import net.ironkernel.skakki.entity.Tournament;
import net.ironkernel.skakki.repository.MatchRepository;
import net.ironkernel.skakki.repository.MemberRepository;
import net.ironkernel.skakki.repository.ArbiterApplicationRepository;
import net.ironkernel.skakki.repository.TournamentRepository;
import net.ironkernel.skakki.repository.SignupRequestRepository;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final SignupRequestRepository signupRequestRepository;
    private final ArbiterApplicationRepository arbiterApplicationRepository;
    private final TournamentRepository tournamentRepository;
    private final MatchRepository matchRepository;

    private Member buildBaseMember(String username, String email, String rawPassword) {
        if (memberRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username già in uso");
        }
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email già registrata");
        }
        Member member = new Member();
        member.setUsername(username);
        member.setEmail(email);
        member.setPassword(passwordEncoder.encode(rawPassword));
        return member;
    }

    @Transactional
    public Member createMemberByAdmin(AdminCreateMemberDto dto) {
        Member member = buildBaseMember(dto.getUsername(), dto.getEmail(), dto.getPassword());
        member.setFirstName(dto.getFirstName());
        member.setLastName(dto.getLastName());
        member.setDateOfBirth(dto.getDateOfBirth());
        member.setGender(dto.getGender());
        member.setFideId(dto.getFideId());
        member.setFsiId(dto.getFsiId());
        member.setCountryCode(dto.getCountryCode());
        member.setProvince(dto.getProvince());
        member.setTitle(dto.getTitle());
        member.getRoles().addAll(dto.getRoles());

        return memberRepository.save(member);
    }

    @Transactional
    public Member registerNewMember(RegistrationDto dto) {
        Member member = buildBaseMember(dto.getUsername(), dto.getEmail(), dto.getPassword());
        member.getRoles().add(Role.MEMBER);
        return memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public List<Member> getAllMembers() {
        return memberRepository.findAll(Sort.by("lastName"));
    }

    @Transactional(readOnly = true)
    public Member getMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException("Nessun membro con id=" + id + " presente nel database."));
    }

    @Transactional
    public Member updateMember(UpdateMemberDto fields) {
        Member member = memberRepository.findById(fields.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Nessun membro ha id=" + fields.getId() + ". Gli id non possono essere modificati."));
        if (!member.getUsername().equals(fields.getUsername())
                && memberRepository.existsByUsername(fields.getUsername())) {
            throw new IllegalArgumentException("Username già in uso");
        }
        if (!member.getEmail().equals(fields.getEmail())
                && memberRepository.existsByEmail(fields.getEmail())) {
            throw new IllegalArgumentException("Email già registrata");
        }

        member.setEmail(fields.getEmail());
        member.setUsername(fields.getUsername());
        member.setFirstName(fields.getFirstName());
        member.setLastName(fields.getLastName());
        member.setDateOfBirth(fields.getDateOfBirth());
        member.setGender(fields.getGender());
        member.setFideId(fields.getFideId());
        member.setFsiId(fields.getFsiId());
        member.setCountryCode(fields.getCountryCode());
        member.setProvince(fields.getProvince());
        member.setTitle(fields.getTitle());
        member.getRoles().clear();
        member.getRoles().addAll(fields.getRoles());

        return memberRepository.save(member);
    }

    @Transactional
    public void deleteMember(Long id) {
        Member member = getMember(id);
        if (!tournamentRepository.findByOrganizer(member).isEmpty()) {
            throw new IllegalStateException("Impossibile eliminare: il membro ha organizzato dei tornei.");
        }
        if (matchRepository.existsByWhite(member) || matchRepository.existsByBlack(member)) {
            throw new IllegalStateException("Impossibile eliminare: il membro ha partite registrate.");
        }
        signupRequestRepository.deleteByMember(member);
        arbiterApplicationRepository.deleteByMember(member);
        for (Tournament tournament : tournamentRepository.findByParticipantsContaining(member)) {
            tournament.removeParticipant(member);
        }
        for (Tournament tournament : tournamentRepository.findByArbitersContaining(member)) {
            tournament.removeArbiter(member);
        }
        memberRepository.delete(member);
    }

    @Transactional(readOnly = true)
    public Member getMember(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Nessun membro con username = " + username + " è presente nel database."));
    }

    @Transactional
    public void changePassword(Member member, String oldPassword, String newPassword) {
        if (!passwordEncoder.matches(oldPassword, member.getPassword())) {
            throw new IllegalArgumentException("La password attuale non è corretta");
        }
        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
    }
}
