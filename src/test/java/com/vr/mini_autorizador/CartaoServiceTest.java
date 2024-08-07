package com.vr.mini_autorizador;

import com.vr.mini_autorizador.models.Cartao;
import com.vr.mini_autorizador.repositories.CartaoRepository;
import com.vr.mini_autorizador.services.CartaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

//@SpringBootTest
//@Testcontainers
public class CartaoServiceTest {

    @Container
    private static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:5.7")
            .withDatabaseName("testdb")
            .withUsername("root")
            .withPassword("rootpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

    @Mock
    private CartaoRepository cartaoRepository;

    @InjectMocks
    private CartaoService cartaoService;

    private Cartao cartao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cartao = new Cartao();
        cartao.setNumeroCartao("6549873025634501");
        cartao.setSenha("1234");
        cartao.setSaldo(BigDecimal.valueOf(500.00));
    }

    @Test
    void testCriarCartao() {
        when(cartaoRepository.existsById(cartao.getNumeroCartao())).thenReturn(false);
        when(cartaoRepository.save(any(Cartao.class))).thenReturn(cartao);

        var result = cartaoService.criarCartao(cartao);

        assertTrue(result.isPresent());
        assertEquals(cartao.getNumeroCartao(), result.get().getNumeroCartao());
        assertEquals(cartao.getSenha(), result.get().getSenha());
    }

    @Test
    void testObterSaldo() {
        when(cartaoRepository.findById(cartao.getNumeroCartao())).thenReturn(Optional.of(cartao));

        var saldo = cartaoService.obterSaldo(cartao.getNumeroCartao());

        assertTrue(saldo.isPresent());
        assertEquals(cartao.getSaldo(), saldo.get());
    }

    @Test
    void testAutorizarTransacaoComSucesso() {
        when(cartaoRepository.findById(cartao.getNumeroCartao())).thenReturn(Optional.of(cartao));
        when(cartaoRepository.save(any(Cartao.class))).thenReturn(cartao);

        String resultado = cartaoService.autorizarTransacao(cartao.getNumeroCartao(), cartao.getSenha(), BigDecimal.valueOf(10.00));

        assertEquals("OK", resultado);
        assertEquals(BigDecimal.valueOf(490.00), cartao.getSaldo());
    }

    @Test
    void testAutorizarTransacaoSaldoInsuficiente() {
        cartao.setSaldo(BigDecimal.valueOf(5.00));
        when(cartaoRepository.findById(cartao.getNumeroCartao())).thenReturn(Optional.of(cartao));

        var exception = assertThrows(IllegalArgumentException.class, () -> {
            cartaoService.autorizarTransacao(cartao.getNumeroCartao(), cartao.getSenha(), BigDecimal.valueOf(10.00));
        });

        assertEquals("SALDO_INSUFICIENTE", exception.getMessage());
    }

    @Test
    void testAutorizarTransacaoSenhaInvalida() {
        when(cartaoRepository.findById(cartao.getNumeroCartao())).thenReturn(Optional.of(cartao));

        var exception = assertThrows(IllegalArgumentException.class, () -> {
            cartaoService.autorizarTransacao(cartao.getNumeroCartao(), "senhaErrada", BigDecimal.valueOf(10.00));
        });

        assertEquals("SENHA_INVALIDA", exception.getMessage());
    }

    @Test
    void testAutorizarTransacaoCartaoInexistente() {
        when(cartaoRepository.findById(cartao.getNumeroCartao())).thenReturn(Optional.empty());

        var exception = assertThrows(IllegalArgumentException.class, () -> {
            cartaoService.autorizarTransacao(cartao.getNumeroCartao(), cartao.getSenha(), BigDecimal.valueOf(10.00));
        });

        assertEquals("CARTAO_INEXISTENTE", exception.getMessage());
    }

    @Test
    void testAutorizarTransacaoConcorrencia() {
        when(cartaoRepository.findById(cartao.getNumeroCartao())).thenReturn(Optional.of(cartao));
        doThrow(OptimisticLockingFailureException.class).when(cartaoRepository).save(any(Cartao.class));

        var exception = assertThrows(IllegalStateException.class, () -> {
            cartaoService.autorizarTransacao(cartao.getNumeroCartao(), cartao.getSenha(), BigDecimal.valueOf(10.00));
        });

        assertEquals("CONCORRENCIA", exception.getMessage());
    }
}