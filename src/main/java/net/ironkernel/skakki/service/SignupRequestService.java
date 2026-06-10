package net.ironkernel.skakki.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.entity.Tournament;
import net.ironkernel.skakki.entity.SignupRequest;
import net.ironkernel.skakki.repository.TournamentRepository;
import net.ironkernel.skakki.repository.SignupRequestRepository;

@Service
public class SignupRequestService extends AbstractTournamentRequestService<SignupRequest> {
    private final SignupRequestRepository signupRequestRepository;

    public SignupRequestService(SignupRequestRepository signupRequestRepository,
            TournamentRepository tournamentRepository) {
        super(tournamentRepository);
        this.signupRequestRepository = signupRequestRepository;
        this.deadlineErrorMessage = "La scadenza per l'iscrizione è passata.";
        this.duplicateErrorMessage = "Sei già iscritto o hai già una richiesta in attesa.";
    }

    @Override
    protected JpaRepository<SignupRequest, Long> getRepository() {
        return signupRequestRepository;
    }

    @Override
    protected boolean duplicateExists(Tournament tournament, Member member) {
        return signupRequestRepository.existsByMemberAndTournament(member, tournament);
    }

    @Override
    protected SignupRequest buildRequest() {
        return new SignupRequest();
    }

    @Override
    protected boolean isInCollection(Tournament tournament, Member member) {
        return tournament.getParticipants().contains(member);
    }

    @Override
    protected void addToCollection(Tournament tournament, Member member) {
        tournament.addParticipant(member);
    }

    @Override
    protected void removeFromCollection(Tournament tournament, Member member) {
        tournament.removeParticipant(member);
    }

    @Transactional(readOnly = true)
    public SignupRequest getRequest(Long id) {
        return signupRequestRepository.findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException("Nessuna richiesta di iscrizione con id=" + id + " trovata."));
    }

    @Transactional(readOnly = true)
    public List<SignupRequest> getTournamentRequests(Tournament tournament) {
        return signupRequestRepository.findByTournament(tournament);
    }
}
