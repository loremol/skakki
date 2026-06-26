package net.ironkernel.skakki.functional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.entity.RequestStatus;
import net.ironkernel.skakki.entity.Role;
import net.ironkernel.skakki.entity.SignupRequest;
import net.ironkernel.skakki.entity.Tournament;
import net.ironkernel.skakki.support.AbstractFunctionalTest;

class UC12_1AcceptSignupTest extends AbstractFunctionalTest {

    protected Member alice;
    protected Member bob;

    @BeforeEach
    void setUp() {
        alice = member("alice", Role.ORGANIZER, Role.MEMBER);
        bob = member("bob", Role.MEMBER);
    }

    @Test
    @WithMockUser(username = "alice", roles = { "ORGANIZER", "MEMBER" })
    public void basicFlow_marksAcceptedAndAddsParticipant() throws Exception {
        Tournament t = tournament(alice, LocalDate.now().plusDays(30), (short) 5);
        SignupRequest req = pendingSignup(bob, t);

        mockMvc.perform(post("/organize/tournament/" + t.getId() + "/accept-signup/" + req.getId())
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organize/tournament/" + t.getId() + "?success"));

        SignupRequest reloaded = signupRequestRepository.findById(req.getId()).orElseThrow();
        Assertions.assertThat(reloaded.getStatus()).isEqualTo(RequestStatus.ACCEPTED);
        Tournament tReloaded = tournamentRepository.findById(t.getId()).orElseThrow();
        Assertions.assertThat(tReloaded.getParticipants())
                .extracting(Member::getId)
                .contains(bob.getId());
    }

    @Test
    @WithMockUser(username = "alice", roles = { "ORGANIZER", "MEMBER" })
    public void alt4a_notOrganizer_returnsForbidden() throws Exception {
        Member other = member("charlie", Role.ORGANIZER, Role.MEMBER);
        Tournament t = tournament(other, LocalDate.now().plusDays(30), (short) 5);
        SignupRequest req = pendingSignup(bob, t);

        mockMvc.perform(post("/organize/tournament/" + t.getId() + "/accept-signup/" + req.getId())
                .with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(view().name("error/forbidden"));

        SignupRequest reloaded = signupRequestRepository.findById(req.getId()).orElseThrow();
        Assertions.assertThat(reloaded.getStatus()).isEqualTo(RequestStatus.PENDING);
    }
}
