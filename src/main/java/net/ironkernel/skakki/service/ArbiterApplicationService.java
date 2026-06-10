package net.ironkernel.skakki.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.entity.Tournament;
import net.ironkernel.skakki.entity.ArbiterApplication;
import net.ironkernel.skakki.repository.ArbiterApplicationRepository;
import net.ironkernel.skakki.repository.TournamentRepository;

@Service
public class ArbiterApplicationService extends AbstractTournamentRequestService<ArbiterApplication> {
    private final ArbiterApplicationRepository arbiterApplicationRepository;

    public ArbiterApplicationService(ArbiterApplicationRepository arbiterApplicationRepository,
            TournamentRepository tournamentRepository) {
        super(tournamentRepository);
        this.arbiterApplicationRepository = arbiterApplicationRepository;
        this.deadlineErrorMessage ="La scadenza per candidarsi come arbitro è passata.";
        this.duplicateErrorMessage = "Hai già inviato una candidatura come arbitro per questo torneo.";
    }

    @Override
    protected JpaRepository<ArbiterApplication, Long> getRepository() {
        return arbiterApplicationRepository;
    }

    @Override
    protected boolean duplicateExists(Tournament tournament, Member member) {
        return arbiterApplicationRepository.existsByMemberAndTournament(member, tournament);
    }

    @Override
    protected ArbiterApplication buildRequest() {
        return new ArbiterApplication();
    }

    @Override
    protected boolean isInCollection(Tournament tournament, Member member) {
        return tournament.getArbiters().contains(member);
    }

    @Override
    protected void addToCollection(Tournament tournament, Member member) {
        tournament.addArbiter(member);
    }

    @Override
    protected void removeFromCollection(Tournament tournament, Member member) {
        tournament.removeArbiter(member);
    }

    @Transactional(readOnly = true)
    public ArbiterApplication getApplication(Long id) {
        return arbiterApplicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Nessuna candidatura come arbitro con id=" + id + " trovata."));
    }

    @Transactional(readOnly = true)
    public List<ArbiterApplication> getArbiterApplications(Tournament tournament) {
        return arbiterApplicationRepository.findByTournament(tournament);
    }
}
