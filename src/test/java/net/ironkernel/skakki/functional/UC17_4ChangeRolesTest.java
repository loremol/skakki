package net.ironkernel.skakki.functional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.entity.Role;
import net.ironkernel.skakki.support.AbstractFunctionalTest;

class UC17_4ChangeRolesTest extends AbstractFunctionalTest {

    protected Member alice;
    protected Member bob;

    @BeforeEach
    void setUp() {
        alice = member("alice", Role.ADMIN, Role.MEMBER);
        bob = member("bob", Role.MEMBER);
    }

    @Test
    @WithMockUser(username = "alice", roles = { "ADMIN" })
    public void basicFlow_updatesRoles() throws Exception {
        mockMvc.perform(put("/admin/members/" + bob.getId())
                .with(csrf())
                .param("id", bob.getId().toString())
                .param("email", bob.getEmail())
                .param("username", bob.getUsername())
                .param("firstName", bob.getFirstName())
                .param("lastName", bob.getLastName())
                .param("dateOfBirth", bob.getDateOfBirth().toString())
                .param("gender", bob.getGender().name())
                .param("countryCode", bob.getCountryCode())
                .param("roles", Role.MEMBER.name(), Role.ORGANIZER.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/members/" + bob.getId() + "?success"));

        Member reloaded = memberRepository.findById(bob.getId()).orElseThrow();
        assertThat(reloaded.getRoles()).containsExactlyInAnyOrder(Role.MEMBER, Role.ORGANIZER);
    }

    @Test
    @WithMockUser(username = "alice", roles = { "ADMIN" })
    public void alt5a_invalidFields_rerendersForm() throws Exception {
        mockMvc.perform(put("/admin/members/" + bob.getId())
                .with(csrf())
                .param("id", bob.getId().toString())
                .param("email", "bad")
                .param("username", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/member-manage"))
                .andExpect(model().attributeHasFieldErrors("memberFields", "email", "username"));
    }

    @Test
    @WithMockUser(username = "alice", roles = { "ADMIN" })
    public void alt6a_emailInUse_returnsBadRequest() throws Exception {
        Member charlie = member("charlie", Role.MEMBER);

        mockMvc.perform(put("/admin/members/" + bob.getId())
                .with(csrf())
                .param("id", bob.getId().toString())
                .param("email", charlie.getEmail())
                .param("username", bob.getUsername())
                .param("firstName", bob.getFirstName())
                .param("lastName", bob.getLastName())
                .param("dateOfBirth", bob.getDateOfBirth().toString())
                .param("gender", bob.getGender().name())
                .param("countryCode", bob.getCountryCode())
                .param("roles", Role.MEMBER.name()))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("error/bad-request"));

        Member reloaded = memberRepository.findById(bob.getId()).orElseThrow();
        assertThat(reloaded.getEmail()).isEqualTo("bob@test.local");
    }
}
