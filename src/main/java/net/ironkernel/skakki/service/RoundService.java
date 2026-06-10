package net.ironkernel.skakki.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.ironkernel.skakki.entity.Round;
import net.ironkernel.skakki.entity.Tournament;
import net.ironkernel.skakki.repository.RoundRepository;

@RequiredArgsConstructor
@Service
public class RoundService {
    private final RoundRepository roundRepository;

    @Transactional
    public Round createRound(Tournament tournament, LocalDate playDate) {
        long currentCount = roundRepository.countByTournament(tournament);
        Short limit = tournament.getNumberOfRounds();
        if (limit != null && currentCount >= limit) {
            throw new IllegalStateException(
                    "Numero massimo di round raggiunto (" + limit + ").");
        }
        short nextRoundNumber = (short) (currentCount + 1);
        Round round = new Round();
        round.setTournament(tournament);
        round.setRoundNumber(nextRoundNumber);
        round.setPlayDate(playDate);
        return roundRepository.save(round);
    }

    @Transactional
    public Round findOrCreateRound(Tournament tournament, Short roundNumber) {
        return roundRepository.findByTournamentAndRoundNumber(tournament, roundNumber)
                .orElseGet(() -> {
                    long currentCount = roundRepository.countByTournament(tournament);
                    Short limit = tournament.getNumberOfRounds();
                    if (limit != null && currentCount >= limit) {
                        throw new IllegalStateException(
                                "Numero massimo di round (" + limit + ") raggiunto.");
                    }
                    Round round = new Round();
                    round.setTournament(tournament);
                    round.setRoundNumber(roundNumber);
                    return roundRepository.save(round);
                });
    }

    @Transactional(readOnly = true)
    public Round getRound(Long id) {
        return roundRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nessun round con id=" + id + " trovato."));
    }

    @Transactional
    public Round updateRound(Long id, LocalDate playDate) {
        Round round = getRound(id);
        round.setPlayDate(playDate);
        return roundRepository.save(round);
    }

    @Transactional
    public void deleteRound(Long id) {
        roundRepository.deleteById(id);
    }
}
