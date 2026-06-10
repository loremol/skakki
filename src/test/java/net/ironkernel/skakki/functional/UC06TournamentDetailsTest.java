package net.ironkernel.skakki.functional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.entity.Role;
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
                .andExpect(model().attribute("isOrganizer", false));
    }
}
