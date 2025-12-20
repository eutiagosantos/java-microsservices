package br.com.alurafood.orders.controller;

import br.com.alurafood.orders.dto.OrderDto;
import br.com.alurafood.orders.dto.StatusDto;
import br.com.alurafood.orders.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService service;

    @GetMapping()
    public List<OrderDto> listAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getById(@PathVariable @NotNull Long id) {
        OrderDto dto = service.getById(id);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/port")
    public String returnPort(@Value("local.server.port") String port) {
        return String.format("Requisição pela instancia executando na porta: %s ", port);
    }

    @PostMapping()
    public ResponseEntity<OrderDto> placeOrder(@RequestBody @Valid OrderDto dto, UriComponentsBuilder uriBuilder) {
        OrderDto placedOrder = service.createOrder(dto);

        URI uri = uriBuilder.path("/orders/{id}").buildAndExpand(placedOrder.getId()).toUri();

        return ResponseEntity.created(uri).body(placedOrder);

    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDto> updateStatus(@PathVariable Long id, @RequestBody StatusDto status) {
        OrderDto dto = service.updateStatus(id, status);

        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}/paid")
    public ResponseEntity<Void> approvePayment(@PathVariable @NotNull Long id) {
        service.approveOrderPayment(id);

        return ResponseEntity.ok().build();

    }
}
