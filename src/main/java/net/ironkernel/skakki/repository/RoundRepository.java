package net.ironkernel.skakki.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.ironkernel.skakki.entity.Round;
import net.ironkernel.skakki.entity.Tournament;

@Repository
public interface RoundRepository extends JpaRepository<Round, Long> {
    Optional<Round> findByTournamentAndRoundNumber(Tournament tournament, Short roundNumber);

    long countByTournament(Tournament tournament);
}
