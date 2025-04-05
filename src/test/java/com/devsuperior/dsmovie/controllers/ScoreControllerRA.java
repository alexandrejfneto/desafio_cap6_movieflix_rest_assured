package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

import io.restassured.http.ContentType;


public class ScoreControllerRA {
	
	private Long existingMovieId, nonExistingMovieId, validScore;
	private Map<String, Long> scoreInstance = new HashMap<>();
	
	private String adminUsername, adminPassword;
	private String adminToken;
	
	@BeforeEach
	public void setup() throws Exception {
		
		baseURI = "http://localhost:8080";
		
		existingMovieId = 1L;
		nonExistingMovieId = 100L;
		validScore = 4L;
		
		scoreInstance.put("movieId", existingMovieId);
		scoreInstance.put("score", validScore);
		
		adminUsername = "maria@gmail.com";
		adminPassword = "123456";
		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
		
		
	}
	
	@Test
	public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {
		scoreInstance.replace("movieId", nonExistingMovieId);
		
		JSONObject newScore = new JSONObject(scoreInstance);
		
		given()
		.header("Content-type", "application/json")
		.header("Authorization", "Bearer " + adminToken)
		.body(newScore)
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
	.when()
		.put("/scores")
	.then()
		.statusCode(404)
		.body("status", equalTo(404))
		.body("error", equalTo("Recurso não encontrado"))
		;
		
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {
		
		scoreInstance.replace("movieId", null);
		
		JSONObject newScore = new JSONObject(scoreInstance);
		
		given()
		.header("Content-type", "application/json")
		.header("Authorization", "Bearer " + adminToken)
		.body(newScore)
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
	.when()
		.put("/scores")
	.then()
		.statusCode(422)
		.body("status", is(422))
		.body("error", equalTo("Dados inválidos"))
		.body("errors.fieldName[0]", equalTo("movieId"))
		.body("errors.message[0]", equalTo("Campo requerido"))
		;
		
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {
		
		scoreInstance.replace("score", -1L);
		
		JSONObject newScore = new JSONObject(scoreInstance);
		
		given()
		.header("Content-type", "application/json")
		.header("Authorization", "Bearer " + adminToken)
		.body(newScore)
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
	.when()
		.put("/scores")
	.then()
		.statusCode(422)
		.body("status", is(422))
		.body("error", equalTo("Dados inválidos"))
		.body("errors.fieldName[0]", equalTo("score"))
		.body("errors.message[0]", equalTo("Valor mínimo 0"))
		;
		
	}
}
