package net.ironkernel.skakki.functional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.entity.RequestStatus;
import net.ironkernel.skakki.entity.Role;
import net.ironkernel.skakki.entity.SignupRequest;
import net.ironkernel.skakki.entity.Tournament;
import net.ironkernel.skakki.support.AbstractFunctionalTest;

class UC07SignupRequestTest extends AbstractFunctionalTest {

    protected Member alice;
    protected Member organizer;

    @BeforeEach
    void setUp() {
        alice = member("alice", Role.MEMBER);
        organizer = member("bob", Role.ORGANIZER, Role.MEMBER);
    }

    @Test
    @WithMockUser(username = "alice", roles = { "MEMBER" })
    public void basicFlow_createsPendingSignupRequest() throws Exception {
        Tournament t = tournament(organizer, LocalDate.now().plusDays(30), (short) 5);

        mockMvc.perform(post("/tournaments/" + t.getId() + "/signup").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tournaments/" + t.getId() + "?success"));

        List<SignupRequest> requests = signupRequestRepository.findByMember(alice);
        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getStatus()).isEqualTo(RequestStatus.PENDING);
        assertThat(requests.get(0).getTournament().getId()).isEqualTo(t.getId());
    }

    @Test
    @WithMockUser(username = "alice", roles = { "MEMBER" })
    public void alt2a_deadlinePassed_returnsConflict() throws Exception {
        Tournament t = tournament(organizer, LocalDate.now().minusDays(1), (short) 5);

        mockMvc.perform(post("/tournaments/" + t.getId() + "/signup").with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(view().name("error/conflict"));

        assertThat(signupRequestRepository.findByMember(alice)).isEmpty();
    }

    @Test
    @WithMockUser(username = "alice", roles = { "MEMBER" })
    public void alt3a_duplicateRequest_returnsConflict() throws Exception {
        Tournament t = tournament(organizer, LocalDate.now().plusDays(30), (short) 5);
        pendingSignup(alice, t);

        mockMvc.perform(post("/tournaments/" + t.getId() + "/signup").with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(view().name("error/conflict"));

        assertThat(signupRequestRepository.findByMember(alice)).hasSize(1);
    }
}
