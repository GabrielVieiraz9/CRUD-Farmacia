package com.generation.farmacia.teste.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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

import com.generation.farmacia.model.Categoria;
import com.generation.farmacia.repository.CategoriaRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CategoriaControllerTest {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Autowired
	private CategoriaRepository categoriaRepository;

	@BeforeAll
	void start() {
		categoriaRepository.deleteAll();

		Categoria novaCategoria1 = new Categoria(null, "Categoria1", null);
		categoriaRepository.save(novaCategoria1);

		Categoria novaCategoria2 = new Categoria(null, "Categoria2", null);
		categoriaRepository.save(novaCategoria2);

		Categoria novaCategoria3 = new Categoria(null, "Categoria3", null);
		categoriaRepository.save(novaCategoria3);
	}

	@Test
	@DisplayName("Testar se é possível obter todas as categorias")
	void testGetAllCategorias() {
		ResponseEntity<Categoria[]> response = testRestTemplate.exchange("/categorias", HttpMethod.GET, null,
				Categoria[].class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(3, response.getBody().length);
	}

	@Test
	@DisplayName("Testar se é possível obter uma categoria pelo nome")
	void testGetCategoriaByNome() {
		categoriaRepository.deleteAll();
		categoriaRepository.save(new Categoria(null, "Categoria1", null)); // Insere uma categoria no banco de dados

		List<Categoria> categorias = categoriaRepository.findAll(); // Obtém todas as categorias do banco de dados
		assertFalse(categorias.isEmpty(), "Não há categorias salvos no banco de dados"); // Verifica se há categorias no
																							// banco de dados

		String nomeCategoria = "Categoria1";

		ResponseEntity<String> response = testRestTemplate.getForEntity("/categorias/nome/" + nomeCategoria,
				String.class); // Faz a chamada para obter a categoria pelo ID

		assertEquals(HttpStatus.OK, response.getStatusCode(), "A solicitação para obter a categoria pelo nome falhou"); // Verifica
																														// se
																														// a
																														// solicitação
																														// foi
																														// bem-sucedida
	}

	@Test
	@DisplayName("Testar se é possível adicionar uma nova categoria")
	void testAdicionarCategoria() {
		ResponseEntity<Categoria> response = testRestTemplate.postForEntity("/categorias",
				new Categoria(null, "Nova Categoria", null), Categoria.class);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals("Nova Categoria", response.getBody().getNome());
	}

	@Test
	@DisplayName("Testar se é possível atualizar uma categoria existente")
	void testAtualizarCategoria() {
		Categoria categoriaExistente = categoriaRepository.findByNome("Categoria1").get(0);
		categoriaExistente.setNome("Categoria 1 Atualizada");
		ResponseEntity<Categoria> response = testRestTemplate.exchange("/categorias", HttpMethod.PUT,
				new HttpEntity<>(categoriaExistente), Categoria.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Categoria 1 Atualizada", response.getBody().getNome());
	}

	@Test
	@DisplayName("Testar se é possível excluir uma categoria existente")
	void testExcluirCategoria() {
		categoriaRepository.deleteAll();
		categoriaRepository.save(new Categoria(null, "Categoria2", null)); // Insere uma categoria no banco de dados

		List<Categoria> categorias = categoriaRepository.findByNome("Categoria2"); // Obtém a categoria recém-inserida
		assertFalse(categorias.isEmpty(), "Não há categorias com o nome 'Categoria2'"); // Verifica se a categoria foi
																						// encontrada

		Long categoriaId = categorias.get(0).getId(); // Obtém o ID da categoria

		ResponseEntity<Void> response = testRestTemplate.exchange("/categorias/id/" + categoriaId, HttpMethod.DELETE,
				null, Void.class); // Faz a chamada para excluir a categoria pelo ID

		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode()); // Verifica se a exclusão foi bem-sucedida
	}
}