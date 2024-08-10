package com.example.finalexam.web;

import com.example.finalexam.entities.Transaction;
import com.example.finalexam.repositories.TransactionRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

//Github link:

@Controller
public class TransactionController {
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping(path = "/index")
    public String salesSummary(Model model,
                               @RequestParam(name = "salesman", defaultValue = "") String salesman) {
        // Calculate totals
        Double washingMachineTotal = transactionRepository.findTotalAmountBySalesmanAndItem(salesman, "Washing Machine");
        Double refrigeratorTotal = transactionRepository.findTotalAmountBySalesmanAndItem(salesman, "Refrigerator");
        Double musicSystemTotal = transactionRepository.findTotalAmountBySalesmanAndItem(salesman, "Music System");

        model.addAttribute("salesman", salesman);
        model.addAttribute("washingMachineTotal", washingMachineTotal != null ? washingMachineTotal : 0);
        model.addAttribute("refrigeratorTotal", refrigeratorTotal != null ? refrigeratorTotal : 0);
        model.addAttribute("musicSystemTotal", musicSystemTotal != null ? musicSystemTotal : 0);

        List<Transaction> transactions = transactionRepository.findBySalesmanOrderByTransactionDateDesc(salesman);
        model.addAttribute("transactions", transactions);

        model.addAttribute("transaction", new Transaction());

        return "salesSummary";
    }

    @PostMapping(path = "/saveTransaction")
    public String saveTransaction(@ModelAttribute("transaction") Transaction transaction,
                                  BindingResult bindingResult,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            return "salesSummary"; // If validation fails, stay on the same page
        }
        transactionRepository.save(transaction);
        return "redirect:/index?salesman=" + transaction.getSalesman(); // Redirect to the summary page with the salesman name
    }

    @GetMapping("/delete")
    public String delete(Long transaction_id) {
        transactionRepository.deleteById(transaction_id);
        return "redirect:/index";
    }

    @GetMapping("/editTransaction")
    public String editStudents(Model model, Long id, HttpSession session){

        Transaction transaction = transactionRepository.findById(id).orElse(null);
        if(transaction == null) throw new RuntimeException("Student does not exist");
        model.addAttribute("transaction", transaction);
        return "editTransaction";
    }
}





