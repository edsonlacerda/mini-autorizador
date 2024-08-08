package com.vr.mini_autorizador.controllers.api;

import com.vr.mini_autorizador.models.Cartao;
import com.vr.mini_autorizador.models.dto.CartaoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RequestMapping("/cartoes")
@Tag(name = "Cartões")
public interface CartaoAPI {

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Criar Novo Cartão")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown"),
    })
    ResponseEntity<CartaoResponseDTO> criarCartao(@RequestBody Cartao cartao);

    @GetMapping(
            value = "/{numeroCartao}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Obter Saldo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown"),
    })
    ResponseEntity<BigDecimal> obterSaldo(@PathVariable String numeroCartao);

}
