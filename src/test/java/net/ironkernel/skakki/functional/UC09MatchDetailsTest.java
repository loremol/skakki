package net.ironkernel.skakki.functional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;

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

class UC09MatchDetailsTest extends AbstractFunctionalTest {

        protected Member alice;

        @BeforeEach
        void setUp() {
                alice = member("alice", Role.MEMBER);
        }

        @Test
        @WithMockUser(username = "alice", roles = { "MEMBER" })
        public void basicFlow_showsMatchDetails() throws Exception {
                Member organizer = member("bob", Role.ORGANIZER, Role.MEMBER);
                Member arbiter = member("charlie", Role.MEMBER);
                Member other = member("dave", Role.MEMBER);
                Tournament t = tournamentWithArbiter(organizer, arbiter,
                                LocalDate.now().plusDays(30), (short) 3, alice, other);
                Round r = round(t, (short) 1, LocalDate.now());
                Match m = normalMatch(r, alice, other, MatchResult.WHITE_WIN);

                mockMvc.perform(MockMvcRequestBuilders.get("/matches/" + m.getId()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("match-details"))
                                .andExpect(model().attributeExists("match"))
                                .andExpect(model().attributeExists("tournament"))
                                .andExpect(model().attribute("isArbiter", false));
        }

        @Test
        @WithMockUser(username = "charlie", roles = { "MEMBER" })
        public void arbiterFlow_setsIsArbiterTrue() throws Exception {
                Member organizer = member("bob", Role.ORGANIZER, Role.MEMBER);
                Member arbiter = member("charlie", Role.MEMBER);
                Member playerB = member("dave", Role.MEMBER);
                Tournament t = tournamentWithArbiter(organizer, arbiter,
                                LocalDate.now().plusDays(30), (short) 3, alice, playerB);
                Round r = round(t, (short) 1, LocalDate.now());
                Match m = normalMatch(r, alice, playerB, MatchResult.DRAW);

                mockMvc.perform(MockMvcRequestBuilders.get("/matches/" + m.getId()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("match-details"))
                                .andExpect(model().attribute("isArbiter", true));
        }
}
