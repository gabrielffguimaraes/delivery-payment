package br.com.alurafood.pagamentos.controller;

import br.com.alurafood.pagamentos.dto.PagamentoDto;
import br.com.alurafood.pagamentos.service.PagamentoService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.servlet.ServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;


@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {
    private final RabbitTemplate rabbitTemplate;
    private final PagamentoService pagamentoService;

    public PagamentoController(RabbitTemplate rabbitTemplate, PagamentoService pagamentoService) {
        this.rabbitTemplate = rabbitTemplate;
        this.pagamentoService = pagamentoService;
    }

    @GetMapping
    public ResponseEntity<?> findAll(@PageableDefault Pageable pageable) {
        return ok(pagamentoService.findAll(pageable));
    }

    @GetMapping("/porta")
    public ResponseEntity<?> porta(ServletRequest request) {
        return ok(request.getLocalPort());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable @NotNull Long id) {
        return ok(pagamentoService.findById(id));
    }



    @PostMapping
    public ResponseEntity<?> save (@RequestBody @Valid PagamentoDto pagamentoDto, UriComponentsBuilder uriComponentsBuilder) {
        PagamentoDto pagamento = pagamentoService.criarPagamento(pagamentoDto);

        URI endereco = uriComponentsBuilder.path("/pagamentos/{id}").buildAndExpand(pagamento.getId()).toUri();
        Message message = new Message(("Criei um pagamento com o id " + pagamento.getId()).getBytes());
        rabbitTemplate.convertAndSend("pagamentos.ex","",pagamento);
        return created(endereco).body(pagamento);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable @NotNull Long id,@RequestBody @Valid PagamentoDto dto) {
        PagamentoDto atualizado = pagamentoService.atualizarPagamento(id,dto);
        return ResponseEntity.ok(atualizado);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable @NotNull Long id) {
        pagamentoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
    @CircuitBreaker(name = "atualizaPedido", fallbackMethod = "pagamentoAutorizadoComIntegracaoPendente")
    @PatchMapping("/{id}/confirmar")
    public void confirmarPagamento(@PathVariable @NotNull Long id){
        pagamentoService.confirmarPagamento(id);
    }


    public void pagamentoAutorizadoComIntegracaoPendente(Long id, Exception e){
        pagamentoService.alteraStatus(id);
    }
}
