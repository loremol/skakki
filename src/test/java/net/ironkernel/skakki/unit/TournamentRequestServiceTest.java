package net.ironkernel.skakki.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.entity.RequestStatus;
import net.ironkernel.skakki.entity.SignupRequest;
import net.ironkernel.skakki.entity.Tournament;
import net.ironkernel.skakki.repository.SignupRequestRepository;
import net.ironkernel.skakki.repository.TournamentRepository;
import net.ironkernel.skakki.service.SignupRequestService;

@ExtendWith(MockitoExtension.class)
class TournamentRequestServiceTest {
    @Mock
    private SignupRequestRepository signupRequestRepository;
    @Mock
    private TournamentRepository tournamentRepository;

    private SignupRequestService signupRequestService;

    @BeforeEach
    void setUp() {
        signupRequestService = new SignupRequestService(signupRequestRepository, tournamentRepository);
    }

    @Test
    public void submit_deadlinePassed_throws() {
        Member m = member();
        Tournament t = tournament(LocalDate.now().minusDays(1));

        assertThatThrownBy(() -> signupRequestService.submit(m, t))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("La scadenza per l'iscrizione è passata.");

        verify(signupRequestRepository, never()).save(any());
    }

    @Test
    public void submit_duplicateExists_throws() {
        Member m = member();
        Tournament t = tournament(LocalDate.now().plusDays(7));
        when(signupRequestRepository.existsByMemberAndTournament(m, t)).thenReturn(true);

        assertThatThrownBy(() -> signupRequestService.submit(m, t))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Sei già iscritto o hai già una richiesta in attesa.");

        verify(signupRequestRepository, never()).save(any());
    }

    @Test
    public void submit_valid_savesRequest() {
        Member m = member();
        Tournament t = tournament(LocalDate.now().plusDays(7));
        when(signupRequestRepository.existsByMemberAndTournament(m, t)).thenReturn(false);

        SignupRequest saved = signupRequestService.submit(m, t);

        verify(signupRequestRepository).save(saved);
        assertThat(saved.getMember()).isSameAs(m);
        assertThat(saved.getTournament()).isSameAs(t);
        assertThat(saved.getStatus()).isEqualTo(RequestStatus.PENDING);
        assertThat(saved.getRequestDate()).isEqualTo(LocalDate.now());
    }

    @Test
    public void accept_notInCollection_addsAndMarksAccepted() {
        Member m = member();
        Tournament t = tournament(LocalDate.now().plusDays(7));
        SignupRequest request = pendingRequest(m, t);

        signupRequestService.accept(request);

        assertThat(t.getParticipants()).containsExactly(m);
        assertThat(request.getStatus()).isEqualTo(RequestStatus.ACCEPTED);
        verify(tournamentRepository).save(t);
        verify(signupRequestRepository).save(request);
    }

    @Test
    public void accept_alreadyInCollection_onlyMarksAccepted() {
        Member m = member();
        Tournament t = tournament(LocalDate.now().plusDays(7));
        t.addParticipant(m);
        SignupRequest request = pendingRequest(m, t);

        signupRequestService.accept(request);

        assertThat(t.getParticipants()).containsExactly(m);
        assertThat(request.getStatus()).isEqualTo(RequestStatus.ACCEPTED);
        verify(tournamentRepository, never()).save(any());
        verify(signupRequestRepository).save(request);
    }

    @Test
    public void reject_inCollection_removesAndMarksRejected() {
        Member m = member();
        Tournament t = tournament(LocalDate.now().plusDays(7));
        t.addParticipant(m);
        SignupRequest request = pendingRequest(m, t);

        signupRequestService.reject(request);

        assertThat(t.getParticipants()).isEmpty();
        assertThat(request.getStatus()).isEqualTo(RequestStatus.REJECTED);
        verify(tournamentRepository).save(t);
        verify(signupRequestRepository).save(request);
    }

    @Test
    public void reject_notInCollection_onlyMarksRejected() {
        Member m = member();
        Tournament t = tournament(LocalDate.now().plusDays(7));
        SignupRequest request = pendingRequest(m, t);

        signupRequestService.reject(request);

        assertThat(t.getParticipants()).isEmpty();
        assertThat(request.getStatus()).isEqualTo(RequestStatus.REJECTED);
        verify(tournamentRepository, never()).save(any());
        verify(signupRequestRepository).save(request);
    }

    private Member member() {
        return new Member();
    }

    private Tournament tournament(LocalDate deadline) {
        Tournament t = new Tournament();
        t.setRegistrationDeadline(deadline);
        return t;
    }

    private SignupRequest pendingRequest(Member member, Tournament tournament) {
        SignupRequest r = new SignupRequest();
        r.setMember(member);
        r.setTournament(tournament);
        r.setStatus(RequestStatus.PENDING);
        return r;
    }
}
