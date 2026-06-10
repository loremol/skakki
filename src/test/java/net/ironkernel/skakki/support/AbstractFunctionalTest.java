package net.ironkernel.skakki.support;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import net.ironkernel.skakki.entity.ArbiterApplication;
import net.ironkernel.skakki.entity.Gender;
import net.ironkernel.skakki.entity.Match;
import net.ironkernel.skakki.entity.MatchResult;
import net.ironkernel.skakki.entity.MatchType;
import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.entity.RequestStatus;
import net.ironkernel.skakki.entity.Role;
import net.ironkernel.skakki.entity.Round;
import net.ironkernel.skakki.entity.SignupRequest;
import net.ironkernel.skakki.entity.Tournament;
import net.ironkernel.skakki.repository.ArbiterApplicationRepository;
import net.ironkernel.skakki.repository.MatchRepository;
import net.ironkernel.skakki.repository.MemberRepository;
import net.ironkernel.skakki.repository.RoundRepository;
import net.ironkernel.skakki.repository.SignupRequestRepository;
import net.ironkernel.skakki.repository.TournamentRepository;
import net.ironkernel.skakki.service.strategy.MatchStrategyFactory;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class AbstractFunctionalTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected MemberRepository memberRepository;
    @Autowired
    protected TournamentRepository tournamentRepository;
    @Autowired
    protected RoundRepository roundRepository;
    @Autowired
    protected MatchRepository matchRepository;
    @Autowired
    protected SignupRequestRepository signupRequestRepository;
    @Autowired
    protected ArbiterApplicationRepository arbiterApplicationRepository;
    @Autowired
    protected PasswordEncoder passwordEncoder;
    @Autowired
    protected MatchStrategyFactory matchStrategyFactory;

    protected Member member(String username, Role... roles) {
        Member m = new Member();
        m.setUsername(username);
        m.setEmail(username + "@test.local");
        m.setPassword(passwordEncoder.encode("password"));
        m.setFirstName("First" + username);
        m.setLastName("Last" + username);
        m.setDateOfBirth(LocalDate.of(1990, 1, 1));
        m.setGender(Gender.MALE);
        m.setCountryCode("ITA");
        if (roles.length == 0) {
            m.getRoles().add(Role.MEMBER);
        } else {
            for (Role r : roles) {
                m.getRoles().add(r);
            }
        }
        return memberRepository.save(m);
    }

    protected Tournament tournament(Member organizer, LocalDate deadline, short numberOfRounds,
            Member... participants) {
        Tournament t = new Tournament();
        t.setName("Test Tournament");
        t.setDescription("Test tournament description");
        t.setRegistrationDeadline(deadline);
        LocalDate base = deadline != null ? deadline : LocalDate.now();
        t.setStartDate(base.plusDays(7));
        t.setEndDate(base.plusDays(14));
        t.setLocation("Test Location");
        t.setNumberOfRounds(numberOfRounds);
        t.setOrganizer(organizer);
        for (Member p : participants) {
            t.addParticipant(p);
        }
        return tournamentRepository.save(t);
    }

    protected Tournament tournamentWithArbiter(Member organizer, Member arbiter, LocalDate deadline,
            short numberOfRounds, Member... participants) {
        Tournament t = tournament(organizer, deadline, numberOfRounds, participants);
        t.addArbiter(arbiter);
        return tournamentRepository.save(t);
    }

    protected Round round(Tournament t, short number, LocalDate playDate) {
        Round r = new Round();
        r.setTournament(t);
        r.setRoundNumber(number);
        r.setPlayDate(playDate);
        return roundRepository.save(r);
    }

    protected Match normalMatch(Round r, Member white, Member black, MatchResult result) {
        Match m = new Match();
        m.setRound(r);
        m.setMatchType(MatchType.NORMAL);
        m.setWhite(white);
        m.setBlack(black);
        matchStrategyFactory.getStrategy(MatchType.NORMAL).setMatchPoints(m, result);
        return matchRepository.save(m);
    }

    protected SignupRequest pendingSignup(Member member, Tournament t) {
        SignupRequest req = new SignupRequest();
        req.setMember(member);
        req.setTournament(t);
        req.setStatus(RequestStatus.PENDING);
        req.setRequestDate(LocalDate.now());
        return signupRequestRepository.save(req);
    }

    protected ArbiterApplication pendingArbiterApplication(Member member, Tournament t) {
        ArbiterApplication app = new ArbiterApplication();
        app.setMember(member);
        app.setTournament(t);
        app.setStatus(RequestStatus.PENDING);
        app.setRequestDate(LocalDate.now());
        return arbiterApplicationRepository.save(app);
    }
}
