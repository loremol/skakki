package net.ironkernel.skakki.functional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import net.ironkernel.skakki.entity.Match;
import net.ironkernel.skakki.entity.MatchResult;
import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.entity.Role;
import net.ironkernel.skakki.entity.Round;
import net.ironkernel.skakki.entity.Tournament;
import net.ironkernel.skakki.support.AbstractFunctionalTest;

class UC06TournamentDetailsTest extends AbstractFunctionalTest {
    protected Member alice;

    @BeforeEach
    void setUp() {
        alice = member("alice", Role.MEMBER);
    }

    @Test
    @WithMockUser(username = "alice", roles = { "MEMBER" })
    public void basicFlow_showsDetailsAndLeaderboard() throws Exception {
        Member organizer = member("bob", Role.ORGANIZER, Role.MEMBER);
        Member arbiter = member("charlie", Role.MEMBER);
        Member other = member("dave", Role.MEMBER);
        Tournament t = tournamentWithArbiter(organizer, arbiter,
                LocalDate.now().plusDays(30), (short) 3, alice, other);

        mockMvc.perform(MockMvcRequestBuilders.get("/tournaments/" + t.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("tournament-details"))
                .andExpect(model().attributeExists("tournament"))
                .andExpect(model().attributeExists("leaderboard"))
                .andExpect(model().attributeExists("rounds"))
                .andExpect(model().attribute("isParticipant", true))
                .andExpect(model().attribute("isArbiter", false))
                .andExpect(model().attribute("isOrganizer", false))
                .andExpect(content().string(containsString(">Sonneborn-Berger</th>")));
    }

    @Test
    @WithMockUser(username = "alice", roles = { "MEMBER" })
    public void leaderboard_displaysQuarterSonnebornBergerPoints() throws Exception {
        Member organizer = member("bob", Role.ORGANIZER, Role.MEMBER);
        Member bob = member("charlie", Role.MEMBER);
        Member carol = member("dave", Role.MEMBER);
        Tournament tournament = tournament(organizer, LocalDate.now().plusDays(30), (short) 3,
                alice, bob, carol);
        Round firstRound = round(tournament, (short) 1, LocalDate.now());
        Match aliceWin = normalMatch(firstRound, alice, bob, MatchResult.WHITE_WIN);
        Match aliceDraw = normalMatch(firstRound, alice, carol, MatchResult.DRAW);
        Match bobWin = normalMatch(firstRound, bob, carol, MatchResult.WHITE_WIN);
        firstRound.getMatches().addAll(List.of(aliceWin, aliceDraw, bobWin));
        tournament.getRounds().add(firstRound);

        mockMvc.perform(MockMvcRequestBuilders.get("/tournaments/" + tournament.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("1¼")));
    }
}
