package com.vr.mini_autorizador.repositories;

import com.vr.mini_autorizador.models.Cartao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartaoRepository extends JpaRepository<Cartao, String> {
}
