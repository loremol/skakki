package net.ironkernel.skakki.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.ironkernel.skakki.dto.UpdateTournamentDto;
import net.ironkernel.skakki.entity.Tournament;
import net.ironkernel.skakki.service.TournamentService;

@RequiredArgsConstructor
@Controller
public class TournamentManagementController {
    private final TournamentService tournamentService;

    @GetMapping("/admin/tournaments/{id}")
    public String viewTournamentManagementPage(@PathVariable Long id, Model model) {
        Tournament tournament = tournamentService.getTournament(id);
        model.addAttribute("tournament", tournament);
        model.addAttribute("tournamentFields", new UpdateTournamentDto(tournament));
        return "admin/tournament-manage";
    }

    @PutMapping("/admin/tournaments/{id}")
    public String updateTournament(@PathVariable Long id,
            @Valid @ModelAttribute("tournamentFields") UpdateTournamentDto fields,
            BindingResult bindingResult,
            Model model) {
        if (!id.equals(fields.getId())) {
            throw new IllegalArgumentException("L'id nella richiesta di aggiornamento non corrisponde all'id del torneo");
        }
        if (bindingResult.hasErrors()) {
            Tournament tournament = tournamentService.getTournament(id);
            model.addAttribute("tournament", tournament);
            return "admin/tournament-manage";
        }
        tournamentService.updateTournament(id, fields);
        return "redirect:/admin/tournaments/" + id + "?success";
    }

    @DeleteMapping("/admin/tournaments/{id}")
    public String deleteTournament(@PathVariable Long id) {
        tournamentService.deleteTournament(id);
        return "redirect:/tournaments";
    }
}
