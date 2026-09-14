package net.ironkernel.skakki.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.ironkernel.skakki.dto.LeaderboardRow;
import net.ironkernel.skakki.dto.NewTournamentDto;
import net.ironkernel.skakki.dto.UpdateTournamentDto;
import net.ironkernel.skakki.entity.Match;
import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.entity.Tournament;
import net.ironkernel.skakki.repository.ArbiterApplicationRepository;
import net.ironkernel.skakki.repository.TournamentRepository;
import net.ironkernel.skakki.repository.SignupRequestRepository;

@RequiredArgsConstructor
@Service
public class TournamentService {
    private final TournamentRepository tournamentRepository;
    private final SignupRequestRepository signupRequestRepository;
    private final ArbiterApplicationRepository arbiterApplicationRepository;

    @Transactional
    public Tournament createTournament(NewTournamentDto fields) {
        Tournament tournament = new Tournament();
        tournament.setName(fields.getName());
        tournament.setDescription(fields.getDescription());
        tournament.setRegistrationDeadline(fields.getRegistrationDeadline());
        tournament.setStartDate(fields.getStartDate());
        tournament.setEndDate(fields.getEndDate());
        tournament.setLocation(fields.getLocation());
        tournament.setNumberOfRounds(fields.getNumberOfRounds());
        tournament.setOrganizer(fields.getOrganizer());
        return tournamentRepository.save(tournament);
    }

    @Transactional(readOnly = true)
    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findAll(Sort.by("startDate"));
    }

    @Transactional(readOnly = true)
    public Tournament getTournament(Long id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Nessun torneo con id=" + id + " presente nel database."));
    }

    @Transactional
    public void deleteTournament(Long id) {
        Tournament tournament = getTournament(id);
        signupRequestRepository.deleteAll(signupRequestRepository.findByTournament(tournament));
        arbiterApplicationRepository.deleteAll(arbiterApplicationRepository.findByTournament(tournament));
        tournamentRepository.delete(tournament);
    }

    @Transactional(readOnly = true)
    public List<Tournament> getTournamentsByOrganizer(Member member) {
        return tournamentRepository.findByOrganizer(member);
    }

    @Transactional(readOnly = true)
    public List<Tournament> getTournamentsByParticipant(Member member) {
        return tournamentRepository.findByParticipantsContaining(member);
    }

    @Transactional(readOnly = true)
    public List<Tournament> getTournamentsByArbiter(Member member) {
        return tournamentRepository.findByArbitersContaining(member);
    }

    @Transactional(readOnly = true)
    public List<LeaderboardRow> getLeaderboard(Tournament tournament) {
        List<Match> matches = tournament.getMatches();
        Map<Member, Float> points = calculatePoints(tournament.getParticipants(), matches);
        Map<Member, Float> buchholz = calculateBuchholz(tournament.getParticipants(), matches, points);
        Map<Member, Float> sonnebornBerger = calculateSonnebornBerger(tournament.getParticipants(), matches, points);
        List<Member> sorted = sortByPointsThenBuchholzThenSonnebornBerger(
                tournament.getParticipants(), points, buchholz, sonnebornBerger);
        return buildLeaderboard(sorted, points, buchholz, sonnebornBerger);
    }

    private Map<Member, Float> calculatePoints(Set<Member> participants, List<Match> matches) {
        Map<Member, Float> points = new HashMap<>();
        for (Member participant : participants) {
            points.put(participant, 0f);
        }
        for (Match match : matches) {
            if (match.getWhitePoints() != null) {
                Member white = match.getWhite();
                points.put(white, points.getOrDefault(white, 0f) + match.getWhitePoints());
            }
            if (match.getBlack() != null && match.getBlackPoints() != null) {
                Member black = match.getBlack();
                points.put(black, points.getOrDefault(black, 0f) + match.getBlackPoints());
            }
        }
        return points;
    }

    private Map<Member, Float> calculateBuchholz(Set<Member> participants, List<Match> matches,
            Map<Member, Float> points) {
        Map<Member, Float> buchholz = new HashMap<>();
        for (Member participant : participants) {
            buchholz.put(participant, 0f);
        }
        for (Match match : matches) {
            Member white = match.getWhite();
            Member black = match.getBlack();
            if (black == null) // Bye
                continue;
            buchholz.put(white, buchholz.getOrDefault(white, 0f) + points.getOrDefault(black, 0f));
            buchholz.put(black, buchholz.getOrDefault(black, 0f) + points.getOrDefault(white, 0f));
        }
        return buchholz;
    }

    private Map<Member, Float> calculateSonnebornBerger(Set<Member> participants, List<Match> matches,
            Map<Member, Float> points) {
        Map<Member, Float> sonnebornBerger = new HashMap<>();
        for (Member participant : participants) {
            sonnebornBerger.put(participant, 0f);
        }
        for (Match match : matches) {
            Member white = match.getWhite();
            Member black = match.getBlack();
            if (black == null) // Bye
                continue;
            if (match.getWhitePoints() != null) {
                float contribution = match.getWhitePoints() * points.getOrDefault(black, 0f);
                sonnebornBerger.put(white, sonnebornBerger.getOrDefault(white, 0f) + contribution);
            }
            if (match.getBlackPoints() != null) {
                float contribution = match.getBlackPoints() * points.getOrDefault(white, 0f);
                sonnebornBerger.put(black, sonnebornBerger.getOrDefault(black, 0f) + contribution);
            }
        }
        return sonnebornBerger;
    }

    private List<Member> sortByPointsThenBuchholzThenSonnebornBerger(Set<Member> participants,
            Map<Member, Float> points, Map<Member, Float> buchholz, Map<Member, Float> sonnebornBerger) {
        List<Member> sorted = new ArrayList<>(participants);
        sorted.sort((a, b) -> {
            int cmp = Float.compare(points.getOrDefault(b, 0f), points.getOrDefault(a, 0f));
            if (cmp != 0)
                return cmp;
            cmp = Float.compare(buchholz.getOrDefault(b, 0f), buchholz.getOrDefault(a, 0f));
            if (cmp != 0)
                return cmp;
            return Float.compare(sonnebornBerger.getOrDefault(b, 0f), sonnebornBerger.getOrDefault(a, 0f));
        });
        return sorted;
    }

    private List<LeaderboardRow> buildLeaderboard(List<Member> sorted, Map<Member, Float> points,
            Map<Member, Float> buchholz, Map<Member, Float> sonnebornBerger) {
        List<LeaderboardRow> leaderboard = new ArrayList<>();
        for (int i = 0; i < sorted.size(); i++) {
            Member member = sorted.get(i);
            leaderboard.add(new LeaderboardRow(
                    i + 1,
                    member,
                    points.getOrDefault(member, 0f),
                    buchholz.getOrDefault(member, 0f),
                    sonnebornBerger.getOrDefault(member, 0f)));
        }
        return leaderboard;
    }

    @Transactional
    public Tournament updateTournament(Long id, UpdateTournamentDto fields) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Impossibile aggiornare: nessun torneo con id=" + id + " trovato."));
        tournament.setName(fields.getName());
        tournament.setDescription(fields.getDescription());
        tournament.setRegistrationDeadline(fields.getRegistrationDeadline());
        tournament.setStartDate(fields.getStartDate());
        tournament.setEndDate(fields.getEndDate());
        tournament.setLocation(fields.getLocation());
        tournament.setNumberOfRounds(fields.getNumberOfRounds());
        return tournamentRepository.save(tournament);
    }
}
