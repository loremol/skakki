package net.ironkernel.skakki.controller;

import java.util.List;

import net.ironkernel.skakki.service.ArbiterApplicationService;
import net.ironkernel.skakki.service.SignupRequestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.RequiredArgsConstructor;
import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.entity.Tournament;
import net.ironkernel.skakki.service.AuthenticationService;
import net.ironkernel.skakki.service.TournamentService;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class TournamentController {
    private final TournamentService tournamentService;
    private final SignupRequestService signupRequestService;
    private final ArbiterApplicationService arbiterApplicationService;
    private final AuthenticationService authenticationService;

    @GetMapping("/tournaments")
    public String viewTournamentListPage(Model model) {
        List<Tournament> clubTournaments = tournamentService.getAllTournaments();
        model.addAttribute("tournaments", clubTournaments);
        return "tournaments";
    }

    @GetMapping("/tournaments/{id}")
    public String viewTournamentDetailsPage(@PathVariable Long id, Model model) {
        Member currentMember = authenticationService.getLoggedInMember();
        Tournament tournament = tournamentService.getTournament(id);

        model.addAttribute("isArbiter", tournament.isArbiter(currentMember));
        model.addAttribute("isOrganizer", tournament.isOrganizer(currentMember));
        model.addAttribute("isParticipant", tournament.isParticipant(currentMember));
        model.addAttribute("tournament", tournament);
        model.addAttribute("rounds", tournament.getRounds());
        model.addAttribute("leaderboard", tournamentService.getLeaderboard(tournament));
        return "tournament-details";
    }

    @PostMapping("/tournaments/{id}/signup")
    public String registerForTournament(@PathVariable Long id) {
        Member currentMember = authenticationService.getLoggedInMember();
        Tournament tournament = tournamentService.getTournament(id);
        signupRequestService.submit(currentMember, tournament);
        return "redirect:/tournaments/" + id + "?success";
    }

    @PostMapping("/tournaments/{id}/request-arbiter-role")
    public String requestArbiterRole(@PathVariable Long id) {
        Member currentMember = authenticationService.getLoggedInMember();
        Tournament tournament = tournamentService.getTournament(id);
        arbiterApplicationService.submit(currentMember, tournament);
        return "redirect:/tournaments/" + id + "?success";
    }
}
