package com.vr.mini_autorizador.controllers;

import com.vr.mini_autorizador.controllers.api.TransacaoAPI;
import com.vr.mini_autorizador.services.CartaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController implements TransacaoAPI {

    @Autowired
    private CartaoService cartaoService;

    @Override
    public ResponseEntity<String> autorizarTransacao(@RequestBody Map<String, String> transacao) {
        try {
            String numeroCartao = transacao.get("numeroCartao");
            String senhaCartao = transacao.get("senhaCartao");
            BigDecimal valor = new BigDecimal(transacao.get("valor"));
            String resultado = cartaoService.autorizarTransacao(numeroCartao, senhaCartao, valor);
            return new ResponseEntity<>(resultado, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (IllegalStateException e) {
            if ("CONCORRENCIA".equals(e.getMessage())) {
                return new ResponseEntity<>("CONCORRENCIA", HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
