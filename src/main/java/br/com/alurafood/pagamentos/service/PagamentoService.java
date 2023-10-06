package br.com.alurafood.pagamentos.service;

import br.com.alurafood.pagamentos.dto.PagamentoDto;
import br.com.alurafood.pagamentos.enums.Status;
import br.com.alurafood.pagamentos.model.Pagamento;
import br.com.alurafood.pagamentos.repository.PagamentoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PagamentoService {
    @Autowired
    private PagamentoRepository pagamentoRepository;
    @Autowired
    private ModelMapper modelMapper;
    public Page<PagamentoDto> findAll(Pageable pageable) {
        return pagamentoRepository.findAll(pageable).map(p -> modelMapper.map(p, PagamentoDto.class));
    }

    public PagamentoDto findById(Long id) {
        Pagamento p = pagamentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Pagamento não encontrado ."));
        return modelMapper.map(p,PagamentoDto.class);
    }

    public PagamentoDto criarPagamento(PagamentoDto pagamentoDto) {
        Pagamento pagamento = modelMapper.map(pagamentoDto,Pagamento.class);
        pagamento.setStatus(Status.CRIADO);
        pagamentoRepository.save(pagamento);

        return modelMapper.map(pagamento, PagamentoDto.class);
    }

    public PagamentoDto atualizarPagamento(Long id,PagamentoDto pagamentoDto) {
        Pagamento pagamento = modelMapper.map(pagamentoDto,Pagamento.class);
        pagamento.setId(pagamento.getId());
        pagamento = pagamentoRepository.save(pagamento);
        return modelMapper.map(pagamento, PagamentoDto.class);
    }

    public void excluir(Long id) {
        pagamentoRepository.deleteById(id);
    }
}
