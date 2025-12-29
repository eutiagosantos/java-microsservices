package br.com.alurafood.pagamentos.controller;

import java.net.URI;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.alurafood.pagamentos.dto.PaymentsDto;
import br.com.alurafood.pagamentos.service.PaymentsService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentsController {

    private final PaymentsService paymentsService;
    private final RabbitTemplate rabbitTemplate;

    @GetMapping
    public ResponseEntity<Page<PaymentsDto>> listAll(@PageableDefault(value = 10) Pageable pagination) {
        var payments = this.paymentsService.getAll(pagination);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentsDto> getById(@PathVariable @NotNull Long id) {
        var payment = this.paymentsService.getById(id);
        return ResponseEntity.ok(payment);
    }

    @PostMapping
    public ResponseEntity<PaymentsDto> create(@RequestBody @Valid PaymentsDto paymentsDto,
            UriComponentsBuilder uriBuilder) {
        var payment = this.paymentsService.create(paymentsDto);
        URI endereco = uriBuilder.path("/api/v1/payments/{id}").buildAndExpand(payment.getId()).toUri();
        Message message = new Message(("Created payment with id: " + payment.getId()).getBytes());
        rabbitTemplate.send("payments.completed", message);
        return ResponseEntity.created(endereco).body(payment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentsDto> atualizar(@PathVariable @NotNull Long id,
            @RequestBody @Valid PaymentsDto dto) {
        PaymentsDto atualizado = this.paymentsService.update(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @PatchMapping("/{id}/confirm")
    @CircuitBreaker(name = "updateOrder", fallbackMethod = "paymentAuthorizedWithIntegrationPending")
    public ResponseEntity<Void> confirmPayment(@PathVariable @NotNull Long id) {
        this.paymentsService.confirmPayment(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable @NotNull Long id) {
        this.paymentsService.delete(id);
        return ResponseEntity.noContent().build();
    }

    public void paymentAuthorizedWithIntegrationPending(Long id, Exception e) {
        this.paymentsService.updateStatus(id);
    }

}
