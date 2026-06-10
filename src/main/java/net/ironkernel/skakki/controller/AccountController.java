package net.ironkernel.skakki.controller;

import jakarta.validation.Valid;
import net.ironkernel.skakki.dto.ChangePasswordDto;
import net.ironkernel.skakki.service.AuthenticationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.RequiredArgsConstructor;
import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.service.MemberService;
import net.ironkernel.skakki.service.TournamentService;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class AccountController {
    private final MemberService memberService;
    private final TournamentService tournamentService;
    private final AuthenticationService authenticationService;

    @GetMapping("/u/{id}")
    public String viewMemberProfilePage(@PathVariable Long id, Model model) {
        Member member = memberService.getMember(id);
        model.addAttribute("member", member);
        model.addAttribute("participatedTournaments", tournamentService.getTournamentsByParticipant(member));
        model.addAttribute("organizedTournaments", tournamentService.getTournamentsByOrganizer(member));
        model.addAttribute("arbiteredTournaments", tournamentService.getTournamentsByArbiter(member));
        return "profile";
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("changePasswordDto", new ChangePasswordDto());
        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute ChangePasswordDto changePasswordDto,
            BindingResult bindingResult, Model model) {
        Member loggedInMember = authenticationService.getLoggedInMember();

        if (bindingResult.hasErrors()) {
            return "change-password";
        }

        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmPassword())) {
            model.addAttribute("error", "Le password non corrispondono");
            model.addAttribute("changePasswordDto", changePasswordDto);
            return "change-password";
        }

        memberService.changePassword(loggedInMember, changePasswordDto.getOldPassword(),
                changePasswordDto.getNewPassword());
        model.addAttribute("success", "Password cambiata con successo");
        model.addAttribute("changePasswordDto", new ChangePasswordDto());
        return "change-password";

    }
}
