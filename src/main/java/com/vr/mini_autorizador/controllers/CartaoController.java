package com.vr.mini_autorizador.controllers;

import com.vr.mini_autorizador.models.Cartao;
import com.vr.mini_autorizador.models.dto.CartaoResponseDTO;
import com.vr.mini_autorizador.services.CartaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/cartoes")
public class CartaoController {

    @Autowired
    private CartaoService cartaoService;

    @PostMapping
    public ResponseEntity<CartaoResponseDTO> criarCartao(@RequestBody Cartao cartao) {
        return cartaoService.criarCartao(cartao)
                .map(c -> new ResponseEntity<>(new CartaoResponseDTO(c.getNumeroCartao(), c.getSenha()), HttpStatus.CREATED))
                .orElseGet(() -> new ResponseEntity<>(new CartaoResponseDTO(cartao.getNumeroCartao(), cartao.getSenha()), HttpStatus.UNPROCESSABLE_ENTITY));
    }

    @GetMapping("/{numeroCartao}")
    public ResponseEntity<BigDecimal> obterSaldo(@PathVariable String numeroCartao) {
        Optional<BigDecimal> saldo = cartaoService.obterSaldo(numeroCartao);
        return saldo.map(s -> new ResponseEntity<>(s, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}

