package net.ironkernel.skakki.functional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import net.ironkernel.skakki.entity.Match;
import net.ironkernel.skakki.entity.MatchResult;
import net.ironkernel.skakki.entity.MatchType;
import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.entity.Role;
import net.ironkernel.skakki.entity.Tournament;
import net.ironkernel.skakki.support.AbstractFunctionalTest;

class UC15_1RegisterMatchTest extends AbstractFunctionalTest {

        protected Member organizer;
        protected Member alice;
        protected Member white;
        protected Member black;

        @BeforeEach
        void setUp() {
                organizer = member("bob", Role.ORGANIZER, Role.MEMBER);
                alice = member("alice", Role.MEMBER);
                white = member("white", Role.MEMBER);
                black = member("black", Role.MEMBER);
        }

        @Test
        @WithMockUser(username = "alice", roles = { "MEMBER" })
        public void basicFlow_registersNormalMatchAndAssignsPoints() throws Exception {
                Tournament t = tournamentWithArbiter(organizer, alice,
                                LocalDate.now().plusDays(30), (short) 3, white, black);

                mockMvc.perform(post("/tournaments/" + t.getId() + "/register-match")
                                .with(csrf())
                                .param("roundNumber", "1")
                                .param("whiteId", white.getId().toString())
                                .param("blackId", black.getId().toString())
                                .param("result", MatchResult.WHITE_WIN.name())
                                .param("matchType", MatchType.NORMAL.name()))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/tournaments/" + t.getId() + "/register-match?success"));

                List<Match> all = matchRepository.findAll();
                assertThat(all).hasSize(1);
                Match m = all.get(0);
                assertThat(m.getMatchType()).isEqualTo(MatchType.NORMAL);
                assertThat(m.getResult()).isEqualTo(MatchResult.WHITE_WIN);
                assertThat(m.getWhitePoints()).isEqualTo(1.0f);
                assertThat(m.getBlackPoints()).isEqualTo(0.0f);
                assertThat(m.getRound().getRoundNumber()).isEqualTo((short) 1);
        }

        @Test
        @WithMockUser(username = "alice", roles = { "MEMBER" })
        public void alt2a_notArbiter_returnsForbidden() throws Exception {
                Member arbiter = member("charlie", Role.MEMBER);
                Tournament t = tournamentWithArbiter(organizer, arbiter,
                                LocalDate.now().plusDays(30), (short) 3, white, black);

                mockMvc.perform(post("/tournaments/" + t.getId() + "/register-match")
                                .with(csrf())
                                .param("roundNumber", "1")
                                .param("whiteId", white.getId().toString())
                                .param("blackId", black.getId().toString())
                                .param("result", MatchResult.WHITE_WIN.name())
                                .param("matchType", MatchType.NORMAL.name()))
                                .andExpect(status().isForbidden())
                                .andExpect(view().name("error/forbidden"));

                assertThat(matchRepository.findAll()).isEmpty();
        }

        @Test
        @WithMockUser(username = "alice", roles = { "MEMBER" })
        public void alt5a_validationFailure_rerendersForm() throws Exception {
                Tournament t = tournamentWithArbiter(organizer, alice,
                                LocalDate.now().plusDays(30), (short) 3, white, black);

                mockMvc.perform(post("/tournaments/" + t.getId() + "/register-match")
                                .with(csrf())
                                .param("matchType", MatchType.NORMAL.name()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("arbiter/register-match"))
                                .andExpect(model().attributeHasFieldErrors("matchDto", "roundNumber", "whiteId",
                                                "result"));

                assertThat(matchRepository.findAll()).isEmpty();
        }

        @Test
        @WithMockUser(username = "alice", roles = { "MEMBER" })
        public void alt6a_roundNumberAboveMax_returnsConflict() throws Exception {
                Tournament t = tournamentWithArbiter(organizer, alice,
                                LocalDate.now().plusDays(30), (short) 2, white, black);
                round(t, (short) 1, LocalDate.now());
                round(t, (short) 2, LocalDate.now());

                mockMvc.perform(post("/tournaments/" + t.getId() + "/register-match")
                                .with(csrf())
                                .param("roundNumber", "3")
                                .param("whiteId", white.getId().toString())
                                .param("blackId", black.getId().toString())
                                .param("result", MatchResult.WHITE_WIN.name())
                                .param("matchType", MatchType.NORMAL.name()))
                                .andExpect(status().isConflict())
                                .andExpect(view().name("error/conflict"));

                assertThat(matchRepository.findAll()).isEmpty();
        }

        @Test
        @WithMockUser(username = "alice", roles = { "MEMBER" })
        public void alt7a_normalWithoutBlack_returnsBadRequest() throws Exception {
                Tournament t = tournamentWithArbiter(organizer, alice,
                                LocalDate.now().plusDays(30), (short) 3, white);

                mockMvc.perform(post("/tournaments/" + t.getId() + "/register-match")
                                .with(csrf())
                                .param("roundNumber", "1")
                                .param("whiteId", white.getId().toString())
                                .param("result", MatchResult.WHITE_WIN.name())
                                .param("matchType", MatchType.NORMAL.name()))
                                .andExpect(status().isBadRequest())
                                .andExpect(view().name("error/bad-request"));

                assertThat(matchRepository.findAll()).isEmpty();
        }
}
