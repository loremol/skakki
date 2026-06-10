package net.ironkernel.skakki.functional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.entity.Role;
import net.ironkernel.skakki.entity.Tournament;
import net.ironkernel.skakki.support.AbstractFunctionalTest;

class UC05ListTournamentsTest extends AbstractFunctionalTest {

    @Test
    @WithMockUser(username = "alice", roles = { "MEMBER" })
    public void basicFlow_listsAllTournaments() throws Exception {
        member("alice", Role.MEMBER);
        Member organizer = member("bob", Role.ORGANIZER, Role.MEMBER);
        tournament(organizer, LocalDate.now().plusDays(30), (short) 5);
        tournament(organizer, LocalDate.now().plusDays(60), (short) 4);
        tournament(organizer, LocalDate.now().plusDays(90), (short) 3);

        mockMvc.perform(MockMvcRequestBuilders.get("/tournaments"))
                .andExpect(status().isOk())
                .andExpect(view().name("tournaments"))
                .andExpect(model().attributeExists("tournaments"));

        List<Tournament> all = tournamentRepository.findAll();
        assertThat(all).hasSize(3);
    }
}
