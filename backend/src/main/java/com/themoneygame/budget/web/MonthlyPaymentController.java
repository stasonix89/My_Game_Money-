package com.themoneygame.budget.web;

import com.themoneygame.auth.application.UserDetailsImpl;
import com.themoneygame.auth.domain.User;
import com.themoneygame.budget.application.MonthlyPaymentService;
import com.themoneygame.budget.application.CategoryService; // Сервис для категорий
import com.themoneygame.budget.domain.Account;
import com.themoneygame.budget.domain.Category; // Модель категории
import com.themoneygame.budget.domain.MonthlyPayment;
import com.themoneygame.budget.infrastructure.AccountRepository;
import com.themoneygame.budget.web.dto.MonthlyPaymentDto;
import com.themoneygame.budget.web.dto.CategoryDto; // DTO для категорий
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/budget/payments")
public class MonthlyPaymentController {

    private final MonthlyPaymentService monthlyPaymentService;
    private final AccountRepository accountRepository;
    private final CategoryService categoryService; // Сервис для категорий

    public MonthlyPaymentController(MonthlyPaymentService monthlyPaymentService,
                                    AccountRepository accountRepository,
                                    CategoryService categoryService) {
        this.monthlyPaymentService = monthlyPaymentService;
        this.accountRepository = accountRepository;
        this.categoryService = categoryService; // Инициализация сервиса
    }

    // -------------------------------------------------------
    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    // -------------------------------------------------------

    private User toUser(UserDetailsImpl userDetails) {
        return userDetails.toUser();
    }

    private MonthlyPaymentDto toDto(MonthlyPayment payment) {
        return MonthlyPaymentDto.fromEntity(payment);
    }

    private Account findPrimaryDebit(User userRef) {
        return accountRepository.findAllByUser(userRef).stream()
                .filter(Account::isMainForPayments)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Primary debit account not found"));
    }

    private MonthlyPayment findUserPaymentOrThrow(User userRef, Long id) {
        return monthlyPaymentService.getAll(userRef).stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    // -------------------------------------------------------
    // API
    // -------------------------------------------------------

    /**
     * Получить все платежи пользователя.
     */
    @GetMapping
    public List<MonthlyPaymentDto> getPayments(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User userRef = toUser(userDetails);
        return monthlyPaymentService.getAll(userRef).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Получить платежи конкретного месяца/года.
     */
    @GetMapping("/{year}/{month}")
    public List<MonthlyPaymentDto> getPaymentsByMonth(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable int year,
            @PathVariable int month
    ) {
        User userRef = toUser(userDetails);
        return monthlyPaymentService.getByMonth(userRef, year, month).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Добавить новый платёж.
     */
    @PostMapping
    public MonthlyPaymentDto addPayment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody MonthlyPayment payment
    ) {
        payment.setUser(toUser(userDetails));
        payment.setPaid(false);

        // Проверяем, если категория существует, если нет, то создаём
        if (payment.getCategory() != null) {
            Category category = categoryService.findByName(payment.getCategory().getName());
            if (category == null) {
                category = new Category(payment.getCategory().getName()); // Создаём новую категорию
                category = categoryService.save(category); // Сохраняем категорию в БД
            }
            payment.setCategory(category); // Устанавливаем категорию
        }

        MonthlyPayment saved = monthlyPaymentService.save(payment);
        return toDto(saved);
    }

    /**
     * Отметить платёж как оплаченный.
     */
    @PostMapping("/{id}/mark-paid")
    public MonthlyPaymentDto markPaid(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id
    ) {
        User userRef = toUser(userDetails);

        MonthlyPayment payment = findUserPaymentOrThrow(userRef, id);
        Account primaryDebit = findPrimaryDebit(userRef);

        monthlyPaymentService.markAsPaid(payment, primaryDebit);

        return toDto(payment);
    }

    /**
     * Отмена оплаты.
     */
    @PostMapping("/{id}/unmark-paid")
    public MonthlyPaymentDto unmarkPaid(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id
    ) {
        User userRef = toUser(userDetails);

        MonthlyPayment payment = findUserPaymentOrThrow(userRef, id);
        Account primaryDebit = findPrimaryDebit(userRef);

        monthlyPaymentService.unmarkAsPaid(payment, primaryDebit);

        return toDto(payment);
    }

    /**
     * Получить все категории.
     */
    @GetMapping("/categories")
    public List<CategoryDto> getCategories() {
        return categoryService.getAllCategories().stream()
                .map(CategoryDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Добавить категорию.
     */
    @PostMapping("/categories")
    public CategoryDto addCategory(@RequestBody Category category) {
        Category savedCategory = categoryService.save(category);
        return CategoryDto.fromEntity(savedCategory);
    }

    /**
     * Удалить категорию.
     */
    @DeleteMapping("/categories/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
    }
}
