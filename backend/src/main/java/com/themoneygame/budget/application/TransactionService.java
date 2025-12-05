package com.themoneygame.budget.application;

import com.themoneygame.auth.domain.User;
import com.themoneygame.budget.domain.Transaction;
import com.themoneygame.budget.infrastructure.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> getAll(User user) {
        return transactionRepository.findAllByUser(user);
    }

    public List<Transaction> getByPeriod(User user, LocalDate start, LocalDate end) {
        return transactionRepository.findAllByUserAndDateBetween(user, start, end);
    }

    public Transaction save(Transaction tx) {
        return transactionRepository.save(tx);
    }
}
