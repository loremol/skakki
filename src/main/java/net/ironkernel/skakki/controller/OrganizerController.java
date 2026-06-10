package net.ironkernel.skakki.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.ironkernel.skakki.dto.NewTournamentDto;
import net.ironkernel.skakki.entity.Tournament;
import net.ironkernel.skakki.entity.ArbiterApplication;
import net.ironkernel.skakki.entity.TournamentRequest;
import net.ironkernel.skakki.entity.SignupRequest;
import net.ironkernel.skakki.service.ArbiterApplicationService;
import net.ironkernel.skakki.service.AuthenticationService;
import net.ironkernel.skakki.service.SignupRequestService;
import net.ironkernel.skakki.service.TournamentAuthorizationService;
import net.ironkernel.skakki.service.TournamentService;

@RequiredArgsConstructor
@Controller
@RequestMapping("/organize")
public class OrganizerController {
    private final TournamentService tournamentService;
    private final AuthenticationService authenticationService;
    private final SignupRequestService signupRequestService;
    private final ArbiterApplicationService arbiterApplicationService;
    private final TournamentAuthorizationService tournamentAuthorizationService;

    @GetMapping("/create-tournament")
    public String viewNewTournamentPage(Model model) {
        model.addAttribute("newTournamentDto", new NewTournamentDto());
        return "organize/create-tournament";
    }

    @PostMapping("/create-tournament")
    public String createNewTournament(@Valid @ModelAttribute("newTournamentDto") NewTournamentDto fields,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "organize/create-tournament";
        }
        fields.setOrganizer(authenticationService.getLoggedInMember());
        tournamentService.createTournament(fields);
        return "redirect:/organize/create-tournament?success";
    }

    @GetMapping("/tournament/{id}")
    public String viewTournamentRequestsPage(@PathVariable Long id, Model model) {
        Tournament tournament = tournamentService.getTournament(id);
        tournamentAuthorizationService.requireOrganizer(tournament);
        List<SignupRequest> signupRequests = signupRequestService.getTournamentRequests(tournament);
        List<ArbiterApplication> arbiterApplications = arbiterApplicationService
                .getArbiterApplications(tournament);
        model.addAttribute("tournament", tournament);
        model.addAttribute("signupRequests", signupRequests);
        model.addAttribute("arbiterApplications", arbiterApplications);
        return "organize/tournament-requests";
    }

    @PostMapping("/tournament/{tournamentId}/accept-signup/{signupRequestId}")
    public String acceptSignupRequest(@PathVariable Long tournamentId, @PathVariable Long signupRequestId) {
        tournamentAuthorizationService.requireOrganizer(tournamentService.getTournament(tournamentId));
        SignupRequest request = signupRequestService.getRequest(signupRequestId);
        requireRequestOwnership(request, tournamentId);
        signupRequestService.accept(request);
        return "redirect:/organize/tournament/" + tournamentId + "?success";
    }

    @PostMapping("/tournament/{tournamentId}/accept-arbiter/{applicationId}")
    public String acceptArbiterApplication(@PathVariable Long tournamentId, @PathVariable Long applicationId) {
        tournamentAuthorizationService.requireOrganizer(tournamentService.getTournament(tournamentId));
        ArbiterApplication application = arbiterApplicationService.getApplication(applicationId);
        requireRequestOwnership(application, tournamentId);
        arbiterApplicationService.accept(application);
        return "redirect:/organize/tournament/" + tournamentId + "?success";
    }

    @PostMapping("/tournament/{tournamentId}/reject-signup/{signupRequestId}")
    public String rejectSignupRequest(@PathVariable Long tournamentId, @PathVariable Long signupRequestId) {
        tournamentAuthorizationService.requireOrganizer(tournamentService.getTournament(tournamentId));
        SignupRequest request = signupRequestService.getRequest(signupRequestId);
        requireRequestOwnership(request, tournamentId);
        signupRequestService.reject(request);
        return "redirect:/organize/tournament/" + tournamentId + "?success";
    }

    @PostMapping("/tournament/{tournamentId}/reject-arbiter/{applicationId}")
    public String rejectArbiterApplication(@PathVariable Long tournamentId, @PathVariable Long applicationId) {
        tournamentAuthorizationService.requireOrganizer(tournamentService.getTournament(tournamentId));
        ArbiterApplication application = arbiterApplicationService.getApplication(applicationId);
        requireRequestOwnership(application, tournamentId);
        arbiterApplicationService.reject(application);
        return "redirect:/organize/tournament/" + tournamentId + "?success";
    }

    private void requireRequestOwnership(TournamentRequest request, Long tournamentId) {
        if (!request.getTournament().getId().equals(tournamentId)) {
            throw new IllegalArgumentException("La richiesta non appartiene a questo torneo");
        }
    }

    @DeleteMapping("/tournament/{id}")
    public String deleteTournament(@PathVariable Long id) {
        tournamentAuthorizationService.requireOrganizer(tournamentService.getTournament(id));
        tournamentService.deleteTournament(id);
        return "redirect:/tournaments";
    }
}
