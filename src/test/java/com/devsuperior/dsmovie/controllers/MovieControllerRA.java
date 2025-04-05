package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

import io.restassured.http.ContentType;

public class MovieControllerRA {
	
	private Long existingMovieId, nonExistingMovieId;
	
	private String adminUsername, adminPassword, clientUsername, clientPassword;
	private String adminToken, clientToken, invalidToken;
	
	private Map <String, Object> movieInstance = new HashMap <>();
	
	@BeforeEach
	public void setup() throws Exception {
		
		baseURI = "http://localhost:8080";
		
		existingMovieId = 1L;
		nonExistingMovieId = 100L;
		
		adminUsername = "maria@gmail.com";
		adminPassword = "123456";
		clientUsername = "alex@gmail.com";
		clientPassword = "123456";
		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		invalidToken = adminToken +"XPTO";
		
		movieInstance.put("title", "Test Movie");
		movieInstance.put("score", 0.0);
		movieInstance.put("count", 0);
		movieInstance.put("image", "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");
		
	}
	
	@Test
	public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {
		
		given()
	.when()
		.get("/movies")
	.then()
		.statusCode(200)
		.body("content.title", hasItems("The Witcher", "Venom: Tempo de Carnificina"))
		;
		
	}
	
	@Test
	public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {
		
		String title = "Venom";
		
		given()
		.when()
			.get("/movies?title={title}", title)
		.then()
			.statusCode(200)
			.body("content.title", hasItems("Venom: Tempo de Carnificina"))
			;
		
	}
	
	@Test
	public void findByIdShouldReturnMovieWhenIdExists() {	

		given()
		.when()
			.get("/movies/{id}", existingMovieId)
		.then()
			.statusCode(200)
			.body("id", is(1))
			.body("title", equalTo("The Witcher"))
			.body("score", equalTo(4.33F))
			.body("count", is(3))
			.body("image", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg"))
			;
		
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {	
		
		given()
		.when()
			.get("/movies/{id}", nonExistingMovieId)
		.then()
			.statusCode(404)
			.body("status", is(404))
			.body("error", equalTo("Recurso não encontrado"))
			;
		
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {	
		
		movieInstance.replace("title", "");
		JSONObject newMovie = new JSONObject(movieInstance);
		
		given()
		.header("Content-type", "application/json")
		.header("Authorization", "Bearer " + adminToken)
		.body(newMovie)
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
	.when()
		.post("/movies")
	.then()
		.statusCode(422)
		.body("status", is(422))
		.body("error", equalTo("Dados inválidos"))
		.body("errors.fieldName[0]", equalTo("title"))
		.body("errors.message[0]", equalTo("Campo requerido"))
		.body("errors.message[1]", equalTo("Tamanho deve ser entre 5 e 80 caracteres"))
		;
		
	}
	
	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {
		
		JSONObject newMovie = new JSONObject(movieInstance);
		
		given()
		.header("Content-type", "application/json")
		.header("Authorization", "Bearer " + clientToken)
		.body(newMovie)
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
	.when()
		.post("/movies")
	.then()
		.statusCode(403)
		;
		
	}
	
	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
		
		JSONObject newMovie = new JSONObject(movieInstance);
		
		given()
		.header("Content-type", "application/json")
		.header("Authorization", "Bearer " + invalidToken)
		.body(newMovie)
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
	.when()
		.post("/movies")
	.then()
		.statusCode(401)
		;
		
	}
}
