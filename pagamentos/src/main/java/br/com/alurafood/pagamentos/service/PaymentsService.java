package br.com.alurafood.pagamentos.service;

import java.time.LocalDate;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.alurafood.pagamentos.dto.PaymentsDto;
import br.com.alurafood.pagamentos.http.OrderClient;
import br.com.alurafood.pagamentos.model.Payments;
import br.com.alurafood.pagamentos.model.Status;
import br.com.alurafood.pagamentos.repository.PaymentsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentsService {

    private final PaymentsRepository paymentsRepository;

    private final ModelMapper modelMapper;

    private final OrderClient orderClient;

    public Page<PaymentsDto> getAll(Pageable pagination) {
        return (Page<PaymentsDto>) this.paymentsRepository.findAll(pagination)
                .map(p -> modelMapper.map(p, PaymentsDto.class));
    }

    public PaymentsDto getById(Long id) {
        var payment = this.paymentsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException());
        return this.modelMapper.map(payment, PaymentsDto.class);
    }

    public PaymentsDto create(PaymentsDto paymentsDto) {
        Payments payments = this.modelMapper.map(paymentsDto, Payments.class);
        payments.setStatus(Status.CRIADO);
        this.paymentsRepository.save(payments);
        return this.modelMapper.map(payments, PaymentsDto.class);
    }

    public PaymentsDto update(Long id, PaymentsDto paymentsDto) {
        this.paymentsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException());
        Payments payments = this.modelMapper.map(paymentsDto, Payments.class);
        payments = this.paymentsRepository.save(payments);
        return this.modelMapper.map(payments, PaymentsDto.class);
    }

    public void delete(Long id) {
        var payment = this.paymentsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException());
        payment.setRemoveAt(LocalDate.now());
    }

    public void confirmPayment(Long id) {
        Optional<Payments> payment = this.paymentsRepository.findById(id);
        if (!payment.isPresent()) {
            throw new EntityNotFoundException();
        }
        payment.get().setStatus(Status.CONFIRMADO);
        paymentsRepository.save(payment.get());
        orderClient.updatePayment(payment.get().getOrderId());
    }

    public void updateStatus(Long id) {
        Optional<Payments> payment = this.paymentsRepository.findById(id);
        if (!payment.isPresent()) {
            throw new EntityNotFoundException();
        }
        payment.get().setStatus(Status.CONFIRMADO_NAO_INTEGRADO);
        paymentsRepository.save(payment.get());
    }
}
