package com.generation.farmacia.teste.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.farmacia.repository.ProdutoRepository;
import com.generation.farmacia.model.Produto;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProdutoControllerTest {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Autowired
	private ProdutoRepository produtoRepository;

	@BeforeAll
	void start() {
		produtoRepository.deleteAll();

		Produto novoProduto = new Produto("Creme hidratante", "0", new BigDecimal("15.90"), 88, null, false);
		produtoRepository.save(novoProduto);
	}

	@Test
	@DisplayName("Testar se é possível obter todos os produtos")
	void testGetAllProdutos() {
		produtoRepository.deleteAll();
		produtoRepository.save(new Produto("Creme hidratante", "0", new BigDecimal("15.90"), 88, null, false));

		ResponseEntity<String> response = testRestTemplate.getForEntity("/produtos", String.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	@DisplayName("Testar se é possível obter um produto por ID")
	void testGetProdutoById() {

		produtoRepository.deleteAll();
		produtoRepository.save(new Produto("Creme hidratante", "0", new BigDecimal("15.90"), 88, null, false));

		List<Produto> produtos = produtoRepository.findAll();
		assertFalse(produtos.isEmpty(), "Não há produtos salvos no banco de dados");

		Long produtoId = produtos.get(0).getId();

		ResponseEntity<String> response = testRestTemplate.getForEntity("/produtos/id/" + produtoId, String.class);

		assertEquals(HttpStatus.OK, response.getStatusCode(), "A solicitação para obter o produto por ID falhou");
	}

	@Test
	@DisplayName("Testar se é possível cadastrar um novo produto")
	void testCadastrarNovoProduto() {
		produtoRepository.deleteAll();

		Produto novoProduto = new Produto("Novo Produto", "0", new BigDecimal("10.00"), 10, null, false);
		ResponseEntity<Produto> response = testRestTemplate.postForEntity("/produtos", novoProduto, Produto.class);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
	}

	@Test
	@DisplayName("Testar se é possível atualizar um produto")
	void testAtualizarProduto() {
		produtoRepository.deleteAll();
		produtoRepository.save(new Produto("Creme hidratante", "0", new BigDecimal("15.90"), 88, null, false));

		Produto produtoExistente = produtoRepository.findByNome("Creme hidratante").get(0);
		produtoExistente.setNome("Creme hidratante atualizado");
		ResponseEntity<Produto> response = testRestTemplate.exchange("/produtos", HttpMethod.PUT,
				new HttpEntity<>(produtoExistente), Produto.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	@DisplayName("Testar se é possível excluir um produto")
	void testExcluirProduto() {
		produtoRepository.deleteAll();
		produtoRepository.save(new Produto("Creme hidratante", "0", new BigDecimal("15.90"), 88, null, false));

		List<Produto> produtos = produtoRepository.findByNome("Creme hidratante");
		if (!produtos.isEmpty()) {
			Produto produtoExistente = produtos.get(0);
			ResponseEntity<Void> response = testRestTemplate.exchange("/produtos/id/" + produtoExistente.getId(),
					HttpMethod.DELETE, null, Void.class);
			assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
		} else {
			fail("Não há produtos com o nome 'Creme hidratante'");
		}
	}

}
