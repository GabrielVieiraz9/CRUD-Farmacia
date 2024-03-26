package com.generation.farmacia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.generation.farmacia.model.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

	public List<Categoria> findAllByNomeContainingIgnoreCase(@Param("nome") String nome);

	public List<Categoria> findByNome(String nome);

	@Query("SELECT c FROM Categoria c LEFT JOIN FETCH c.produto WHERE c.nome = :nome")
	public Categoria findByNomeWithProdutos(@Param("nome") String nome);
}