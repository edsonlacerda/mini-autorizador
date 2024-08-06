package com.vr.mini_autorizador.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

import java.math.BigDecimal;

//@Document(collection = "cartoes")
@Entity
public class Cartao {

    @Id
    private String numeroCartao;
    private String senha;
    private BigDecimal saldo = BigDecimal.valueOf(500.00);

    @Version
    private long version = 0L;

    public String getNumeroCartao() {
        return numeroCartao;
    }

    public void setNumeroCartao(String numeroCartao) {
        this.numeroCartao = numeroCartao;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
