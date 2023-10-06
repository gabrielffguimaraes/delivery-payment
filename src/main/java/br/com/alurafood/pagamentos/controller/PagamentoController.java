package br.com.alurafood.pagamentos.controller;

import br.com.alurafood.pagamentos.dto.PagamentoDto;
import br.com.alurafood.pagamentos.service.PagamentoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @GetMapping
    public ResponseEntity<?> findAll(@PageableDefault Pageable pageable) {
        return ok(pagamentoService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable @NotNull Long id) {
        return ok(pagamentoService.findById(id));
    }
    @PostMapping
    public ResponseEntity<?> save (@RequestBody @Valid PagamentoDto pagamentoDto, UriComponentsBuilder uriComponentsBuilder) {
        PagamentoDto pagamento = pagamentoService.criarPagamento(pagamentoDto);

        URI endereco = uriComponentsBuilder.path("/pagamentos/{id}").buildAndExpand(pagamento.getId()).toUri();
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
}
