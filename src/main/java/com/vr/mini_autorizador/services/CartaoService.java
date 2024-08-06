package com.vr.mini_autorizador.services;

import com.vr.mini_autorizador.models.Cartao;
import com.vr.mini_autorizador.repositories.CartaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class CartaoService {

    @Autowired
    private CartaoRepository cartaoRepository;

    public Optional<Cartao> criarCartao(Cartao cartao) {
        return Optional.of(cartao)
                .filter(c -> !cartaoRepository.existsById(c.getNumeroCartao()))
                .map(c -> {
                    c.setSaldo(BigDecimal.valueOf(500.00));
                    return cartaoRepository.save(c);
                });
    }

    public Optional<BigDecimal> obterSaldo(String numeroCartao) {
        return cartaoRepository.findById(numeroCartao)
                .map(Cartao::getSaldo);
    }

    @Transactional
    public String autorizarTransacao(String numeroCartao, String senha, BigDecimal valor) {
        Cartao cartao = cartaoRepository.findById(numeroCartao)
                .orElseThrow(exceptionSupplier("CARTAO_INEXISTENTE"));

        validarSenha(cartao, senha)
                .orElseThrow(exceptionSupplier("SENHA_INVALIDA"));

        validarSaldo(cartao, valor)
                .orElseThrow(exceptionSupplier("SALDO_INSUFICIENTE"));

        return debitarSaldo(cartao, valor);
    }

    private Optional<Cartao> validarSenha(Cartao cartao, String senha) {
        return Optional.of(cartao)
                .filter(c -> c.getSenha().equals(senha))
                .or(() -> Optional.empty());
    }

    private Optional<Cartao> validarSaldo(Cartao cartao, BigDecimal valor) {
        return Optional.of(cartao)
                .filter(c -> c.getSaldo().compareTo(valor) >= 0)
                .or(() -> Optional.empty());
    }

    private String debitarSaldo(Cartao cartao, BigDecimal valor) {
        try {
            cartao.setSaldo(cartao.getSaldo().subtract(valor));
            cartaoRepository.save(cartao);
            return "OK";
        } catch (OptimisticLockingFailureException e) {
            throw new IllegalStateException("CONCORRENCIA", e);
        }
    }

    private Supplier<RuntimeException> exceptionSupplier(String mensagem) {
        return () -> new IllegalArgumentException(mensagem);
    }
}
