package net.ironkernel.skakki.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.ironkernel.skakki.dto.RegistrationDto;
import net.ironkernel.skakki.service.MemberService;

@RequiredArgsConstructor
@Controller
public class RegistrationController {
    private final MemberService memberService;

    @GetMapping("/register")
    public String viewRegisterPage(Model model) {
        model.addAttribute("registrationDto", new RegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registrationDto") RegistrationDto registrationFields,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        memberService.registerNewMember(registrationFields);
        return "redirect:/register?success";
    }
}
