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

import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.entity.Role;
import net.ironkernel.skakki.entity.Tournament;
import net.ironkernel.skakki.support.AbstractFunctionalTest;

class UC10CreateTournamentTest extends AbstractFunctionalTest {

    protected Member alice;

    @BeforeEach
    void setUp() {
        alice = member("alice", Role.ORGANIZER, Role.MEMBER);
    }

    @Test
    @WithMockUser(username = "alice", roles = { "ORGANIZER", "MEMBER" })
    public void basicFlow_persistsTournamentWithCurrentOrganizer() throws Exception {
        mockMvc.perform(post("/organize/create-tournament")
                .with(csrf())
                .param("name", "Test Cup")
                .param("description", "A test tournament")
                .param("registrationDeadline", LocalDate.now().plusDays(10).toString())
                .param("startDate", LocalDate.now().plusDays(20).toString())
                .param("endDate", LocalDate.now().plusDays(22).toString())
                .param("location", "Firenze")
                .param("numberOfRounds", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organize/create-tournament?success"));

        List<Tournament> all = tournamentRepository.findAll();
        assertThat(all).hasSize(1);
        Tournament saved = all.get(0);
        assertThat(saved.getName()).isEqualTo("Test Cup");
        assertThat(saved.getNumberOfRounds()).isEqualTo((short) 5);
        assertThat(saved.getOrganizer().getId()).isEqualTo(alice.getId());
    }

    @Test
    @WithMockUser(username = "alice", roles = { "ORGANIZER", "MEMBER" })
    public void alt5a_invalidFields_rerendersFormWithErrors() throws Exception {
        mockMvc.perform(post("/organize/create-tournament")
                .with(csrf())
                .param("name", "")
                .param("numberOfRounds", "200"))
                .andExpect(status().isOk())
                .andExpect(view().name("organize/create-tournament"))
                .andExpect(model().attributeHasFieldErrors("newTournamentDto", "name", "numberOfRounds"));

        assertThat(tournamentRepository.findAll()).isEmpty();
    }
}
