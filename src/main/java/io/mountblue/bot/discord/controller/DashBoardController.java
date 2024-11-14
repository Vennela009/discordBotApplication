package io.mountblue.bot.discord.controller;

import io.mountblue.bot.discord.entity.Interaction;
import io.mountblue.bot.discord.service.InteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.List;

@Controller
public class DashBoardController {
    @Autowired
    private InteractionService interactionService;

    @GetMapping("/")
    public String showDashBoard(Model model){
        List<Interaction> interactionList = interactionService.fetchAllInteraction();
        Collections.reverse(interactionList);

        model.addAttribute("userInteractionList",interactionList);

        return "dashBoard";
    }
}