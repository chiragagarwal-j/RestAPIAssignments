package com.spring.learningRest.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spring.learningRest.entity.CycleStock;
import com.spring.learningRest.model.RegistrationForm;
import com.spring.learningRest.repository.CycleStockRepository;
import com.spring.learningRest.service.DomainUserService;

import jakarta.annotation.PostConstruct;

@Controller
public class CycleController {

    @Autowired
    private DomainUserService domainUserService;

     @Autowired
    private CycleStockRepository cycleStockRepo;

    private List<CycleStock> cycleList;

    @PostConstruct
    public void init() {
        cycleList = new ArrayList<>();
        cycleStockRepo.findAll().forEach(c -> cycleList.add(c));

    }

    @GetMapping("/cycleStock")
    public String rent(Model model) {
        model.addAttribute("cycleList", cycleList);
        return "cycleStock";
    }

    @PostMapping("/borrow")
    public String borrowCycle(@RequestParam int id) {
        Optional<CycleStock> optionalCycle = cycleStockRepo.findById(id);
        if (optionalCycle.isPresent()) {
            CycleStock cycle = optionalCycle.get();
            int currentAvailableCycles = cycle.getAvailableCycles();
            if (currentAvailableCycles > 0) {
                cycle.setAvailableCycles(currentAvailableCycles - 1);
                cycleStockRepo.save(cycle);
                updateCycleList();
            }
        }
        return "redirect:/cycleStock";
    }

    @PostMapping("/return")
    public String returnCycle(@RequestParam int id) {
        Optional<CycleStock> optionalCycle = cycleStockRepo.findById(id);
        if (optionalCycle.isPresent()) {
            CycleStock cycle = optionalCycle.get();
            int currentAvailableCycles = cycle.getAvailableCycles();
            if (currentAvailableCycles > 0) {
                cycle.setAvailableCycles(currentAvailableCycles + 1);
                cycleStockRepo.save(cycle);
                updateCycleList();
            }
        }
        return "redirect:/cycleStock";
    }

    @PostMapping("/restock")
    public String restockCycle(@RequestParam("brandId") int id, @RequestParam("restockQuantity") int restockQuantity) {
        Optional<CycleStock> optionalCycle = cycleStockRepo.findById(id);

        if (optionalCycle.isPresent()) {
            CycleStock cycle = optionalCycle.get();
            int currentAvailableCycles = cycle.getAvailableCycles();
            int newAvailableCycles = currentAvailableCycles + restockQuantity;
            cycle.setAvailableCycles(newAvailableCycles);
            cycleStockRepo.save(cycle);
            updateCycleList();
        }

        return "redirect:/cycleStock";
    }

    private void updateCycleList() {
        cycleList.clear();
        cycleStockRepo.findAll().forEach(c -> cycleList.add(c));
    }

    @GetMapping("/register")
    public String getRegistrationForm(Model model) {
        if (!model.containsAttribute("registrationForm")) {
            model.addAttribute("registrationForm", new RegistrationForm());
        }
        return "/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("registrationForm") RegistrationForm registrationForm,
            BindingResult bindingResult,
            RedirectAttributes attr) {
        if (bindingResult.hasErrors()) {
            attr.addFlashAttribute("org.springframework.validation.BindingResult.registrationForm", bindingResult);
            attr.addFlashAttribute("registrationForm", registrationForm);
            return "redirect:/register";
        }
        if (!registrationForm.isValid()) {
            attr.addFlashAttribute("message", "Passwords must match");
            attr.addFlashAttribute("registrationForm", registrationForm);
            return "redirect:/register";
        }
        System.out.println(registrationForm);
        domainUserService.save(registrationForm.getUsername(), registrationForm.getPassword());
        attr.addFlashAttribute("result", "Registration success!");
        return "redirect:/login";
    }
}
