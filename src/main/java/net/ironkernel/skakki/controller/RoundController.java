package net.ironkernel.skakki.controller;

import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import net.ironkernel.skakki.entity.Member;
import net.ironkernel.skakki.entity.Round;
import net.ironkernel.skakki.entity.Tournament;
import net.ironkernel.skakki.service.AuthenticationService;
import net.ironkernel.skakki.service.RoundService;
import net.ironkernel.skakki.service.TournamentAuthorizationService;
import net.ironkernel.skakki.service.TournamentService;

@RequiredArgsConstructor
@Controller
public class RoundController {
    private final TournamentService tournamentService;
    private final RoundService roundService;
    private final AuthenticationService authenticationService;
    private final TournamentAuthorizationService tournamentAuthorizationService;

    @PostMapping("/tournaments/{id}/rounds/create")
    public String createRound(@PathVariable Long id,
            @RequestParam(required = false) LocalDate playDate) {
        Tournament tournament = tournamentService.getTournament(id);
        tournamentAuthorizationService.requireArbiter(tournament);
        roundService.createRound(tournament, playDate);
        return "redirect:/tournaments/" + id + "?success";
    }

    @GetMapping("/tournaments/{id}/rounds/{roundId}")
    public String viewRoundDetailsPage(@PathVariable Long id, @PathVariable Long roundId, Model model) {
        Member currentMember = authenticationService.getLoggedInMember();
        Tournament tournament = tournamentService.getTournament(id);
        Round round = roundService.getRound(roundId);
        model.addAttribute("tournament", tournament);
        model.addAttribute("round", round);
        model.addAttribute("matches", round.getMatches());
        model.addAttribute("isArbiter", tournament.isArbiter(currentMember));
        return "round-details";
    }

    @PutMapping("/tournaments/{id}/rounds/{roundId}")
    public String updateRound(@PathVariable Long id, @PathVariable Long roundId,
            @RequestParam(required = false) LocalDate playDate) {
        Tournament tournament = tournamentService.getTournament(id);
        tournamentAuthorizationService.requireArbiter(tournament);
        roundService.updateRound(roundId, playDate);
        return "redirect:/tournaments/" + id + "/rounds/" + roundId + "?success";
    }

    @DeleteMapping("/tournaments/{id}/rounds/{roundId}")
    public String deleteRound(@PathVariable Long id, @PathVariable Long roundId) {
        Tournament tournament = tournamentService.getTournament(id);
        tournamentAuthorizationService.requireArbiter(tournament);
        roundService.deleteRound(roundId);
        return "redirect:/tournaments/" + id + "?success";
    }
}
