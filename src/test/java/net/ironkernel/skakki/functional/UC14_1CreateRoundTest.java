package net.ironkernel.skakki.functional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.entity.Role;
import net.ironkernel.skakki.entity.Round;
import net.ironkernel.skakki.entity.Tournament;
import net.ironkernel.skakki.support.AbstractFunctionalTest;

class UC14_1CreateRoundTest extends AbstractFunctionalTest {

        protected Member organizer;
        protected Member alice;

        @BeforeEach
        void setUp() {
                organizer = member("bob", Role.ORGANIZER, Role.MEMBER);
                alice = member("alice", Role.MEMBER);
        }

        @Test
        @WithMockUser(username = "alice", roles = { "MEMBER" })
        public void basicFlow_createsNextRound() throws Exception {
                Tournament t = tournamentWithArbiter(organizer, alice,
                                LocalDate.now().plusDays(30), (short) 5);
                round(t, (short) 1, LocalDate.now());
                round(t, (short) 2, LocalDate.now());

                mockMvc.perform(post("/tournaments/" + t.getId() + "/rounds/create")
                                .with(csrf())
                                .param("playDate", LocalDate.now().toString()))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/tournaments/" + t.getId() + "?success"));

                assertThat(roundRepository.countByTournament(t)).isEqualTo(3L);
                Round third = roundRepository.findByTournamentAndRoundNumber(t, (short) 3).orElseThrow();
                assertThat(third.getRoundNumber()).isEqualTo((short) 3);
        }

        @Test
        @WithMockUser(username = "alice", roles = { "MEMBER" })
        public void alt3a_notArbiter_returnsForbidden() throws Exception {
                Member arbiter = member("charlie", Role.MEMBER);
                Tournament t = tournamentWithArbiter(organizer, arbiter,
                                LocalDate.now().plusDays(30), (short) 5);

                mockMvc.perform(post("/tournaments/" + t.getId() + "/rounds/create").with(csrf()))
                                .andExpect(status().isForbidden())
                                .andExpect(view().name("error/forbidden"));

                assertThat(roundRepository.countByTournament(t)).isEqualTo(0L);
        }

        @Test
        @WithMockUser(username = "alice", roles = { "MEMBER" })
        public void alt4a_maxRoundsReached_returnsConflict() throws Exception {
                Tournament t = tournamentWithArbiter(organizer, alice,
                                LocalDate.now().plusDays(30), (short) 2);
                round(t, (short) 1, LocalDate.now());
                round(t, (short) 2, LocalDate.now());

                mockMvc.perform(post("/tournaments/" + t.getId() + "/rounds/create").with(csrf()))
                                .andExpect(status().isConflict())
                                .andExpect(view().name("error/conflict"));

                assertThat(roundRepository.countByTournament(t)).isEqualTo(2L);
        }
}
