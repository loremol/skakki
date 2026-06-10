package net.ironkernel.skakki.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.entity.Tournament;

@RequiredArgsConstructor
@Service
public class TournamentAuthorizationService {
    private final AuthenticationService authenticationService;

    @Transactional(readOnly = true)
    public void requireArbiter(Tournament tournament) {
        Member member = authenticationService.getLoggedInMember();
        if (!tournament.isArbiter(member))
            throw new AccessDeniedException("Non sei un arbitro di questo torneo");
    }

    @Transactional(readOnly = true)
    public void requireOrganizer(Tournament tournament) {
        Member member = authenticationService.getLoggedInMember();
        if (!tournament.isOrganizer(member))
            throw new AccessDeniedException("Non sei l'organizzatore di questo torneo");
    }
}
