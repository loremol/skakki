package net.ironkernel.skakki.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashSet;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.ironkernel.skakki.dto.LeaderboardRow;
import net.ironkernel.skakki.entity.Match;
import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.entity.Round;
import net.ironkernel.skakki.entity.Tournament;
import net.ironkernel.skakki.service.TournamentService;

class TournamentServiceTest {
    private final TournamentService tournamentService = new TournamentService(null, null, null);

    @Test
    void getLeaderboard_weightsOpponentScoresByGameResult() {
        Member alice = member("alice");
        Member bob = member("bob");
        Member carol = member("carol");
        Tournament tournament = tournament(alice, bob, carol);

        addMatches(tournament,
                match(alice, bob, 1.0f, 0.0f),
                match(alice, carol, 0.5f, 0.5f),
                match(bob, carol, 1.0f, 0.0f));

        List<LeaderboardRow> leaderboard = tournamentService.getLeaderboard(tournament);

        assertThat(rowFor(leaderboard, alice).sonnebornBergerPoints()).isEqualTo(1.25f);
        assertThat(rowFor(leaderboard, bob).sonnebornBergerPoints()).isEqualTo(0.5f);
        assertThat(rowFor(leaderboard, carol).sonnebornBergerPoints()).isEqualTo(0.75f);
    }

    @Test
    void getLeaderboard_excludesByesFromSonnebornBerger() {
        Member alice = member("alice");
        Member bob = member("bob");
        Tournament tournament = tournament(alice, bob);
        addMatches(tournament, match(alice, null, 1.0f, 0.0f));

        List<LeaderboardRow> leaderboard = tournamentService.getLeaderboard(tournament);

        assertThat(rowFor(leaderboard, alice).sonnebornBergerPoints()).isZero();
    }

    @Test
    void getLeaderboard_usesSonnebornBergerAfterPointsAndBuchholz() {
        Member alice = member("alice");
        Member bob = member("bob");
        Member carol = member("carol");
        Member dave = member("dave");
        Tournament tournament = tournament(bob, alice, carol, dave);

        addMatches(tournament,
                match(alice, carol, 1.0f, 0.0f),
                match(dave, alice, 1.0f, 0.0f),
                match(carol, bob, 1.0f, 0.0f),
                match(bob, dave, 1.0f, 0.0f),
                match(carol, dave, 1.0f, 0.0f));

        List<LeaderboardRow> leaderboard = tournamentService.getLeaderboard(tournament);
        LeaderboardRow aliceRow = rowFor(leaderboard, alice);
        LeaderboardRow bobRow = rowFor(leaderboard, bob);

        assertThat(aliceRow.totalPoints()).isEqualTo(bobRow.totalPoints());
        assertThat(aliceRow.buchholzPoints()).isEqualTo(bobRow.buchholzPoints());
        assertThat(aliceRow.sonnebornBergerPoints()).isGreaterThan(bobRow.sonnebornBergerPoints());
        assertThat(aliceRow.rank()).isLessThan(bobRow.rank());
    }

    private Member member(String username) {
        Member member = new Member();
        member.setUsername(username);
        return member;
    }

    private Tournament tournament(Member... participants) {
        Tournament tournament = new Tournament();
        tournament.setParticipants(new LinkedHashSet<>(List.of(participants)));
        return tournament;
    }

    private void addMatches(Tournament tournament, Match... matches) {
        Round round = new Round();
        round.setTournament(tournament);
        round.setRoundNumber((short) 1);
        round.getMatches().addAll(List.of(matches));
        tournament.getRounds().add(round);
    }

    private Match match(Member white, Member black, float whitePoints, float blackPoints) {
        Match match = new Match();
        match.setWhite(white);
        match.setBlack(black);
        match.setWhitePoints(whitePoints);
        match.setBlackPoints(blackPoints);
        return match;
    }

    private LeaderboardRow rowFor(List<LeaderboardRow> leaderboard, Member member) {
        return leaderboard.stream()
                .filter(row -> row.member() == member)
                .findFirst()
                .orElseThrow();
    }
}
