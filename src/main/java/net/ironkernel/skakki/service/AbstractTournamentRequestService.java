package net.ironkernel.skakki.service;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.entity.RequestStatus;
import net.ironkernel.skakki.entity.Tournament;
import net.ironkernel.skakki.entity.TournamentRequest;
import net.ironkernel.skakki.repository.TournamentRepository;

public abstract class AbstractTournamentRequestService<R extends TournamentRequest> {
    protected final TournamentRepository tournamentRepository;
    protected String deadlineErrorMessage;
    protected String duplicateErrorMessage;

    protected AbstractTournamentRequestService(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    protected abstract JpaRepository<R, Long> getRepository();

    protected abstract boolean duplicateExists(Tournament tournament, Member member);

    protected abstract boolean isInCollection(Tournament tournament, Member member);

    protected abstract void addToCollection(Tournament tournament, Member member);

    protected abstract void removeFromCollection(Tournament tournament, Member member);

    protected abstract R buildRequest();

    @Transactional
    public R submit(Member member, Tournament tournament) {
        LocalDate deadline = tournament.getRegistrationDeadline();
        if (deadline != null && LocalDate.now().isAfter(deadline))
            throw new IllegalStateException(deadlineErrorMessage);
        if (duplicateExists(tournament, member))
            throw new IllegalStateException(duplicateErrorMessage);
        R request = buildRequest();
        request.setMember(member);
        request.setTournament(tournament);
        request.setRequestDate(LocalDate.now());
        request.setStatus(RequestStatus.PENDING);
        getRepository().save(request);
        return request;
    }

    @Transactional
    public void accept(R request) {
        Tournament tournament = request.getTournament();
        Member member = request.getMember();
        if (!isInCollection(tournament, member)) {
            addToCollection(tournament, member);
            tournamentRepository.save(tournament);
        }
        request.setStatus(RequestStatus.ACCEPTED);
        getRepository().save(request);
    }

    @Transactional
    public void reject(R request) {
        Tournament tournament = request.getTournament();
        Member member = request.getMember();
        if (isInCollection(tournament, member)) {
            removeFromCollection(tournament, member);
            tournamentRepository.save(tournament);
        }
        request.setStatus(RequestStatus.REJECTED);
        getRepository().save(request);
    }
}
