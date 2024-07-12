package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

import io.restassured.http.ContentType;

public class MovieControllerRA {
	
	private String titleName;
	private Long existingMovieId, nonExistingMovieId;
	
	private String clientUsername, clientPassword, adminUsername, adminPassword;
	private String clientToken, adminToken, invalidToken;
	
	private Map<String, Object> postMovieInstance;
	
	@BeforeEach
	public void setUp() throws JSONException {
		
		baseURI = "http://localhost:8080";
		
		titleName = "Django";
		
		clientUsername = "alex@gmail.com";
		clientPassword = "123456";
		adminUsername = "maria@gmail.com";
		adminPassword = "123456";
		
		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
		invalidToken = adminToken + "xpto"; // Simulates wrong password 
		
		postMovieInstance = new HashMap<>();
		postMovieInstance.put("title", "Test Movie");
		postMovieInstance.put("score", 0.0);
		postMovieInstance.put("count", 0);
		postMovieInstance.put("image", "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");
	}
	
	@Test
	public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {
		
		given()
			.get("/movies")
		.then()
			.statusCode(200)
			.body("content.id[0]", is(1))
			.body("content.title[0]", equalTo("The Witcher"))
			.body("content.score[0]", is(4.5F));
	}
	
	@Test
	public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {		
	
		given()
			.get("/movies?title={titleName}", titleName)
		.then()
			.statusCode(200)
			.body("content.id[0]", is(6))
			.body("content.title[0]", equalTo("Django Livre"));
	}
	
	@Test
	public void findByIdShouldReturnMovieWhenIdExists() {		
		
		existingMovieId = 1L;
		
		given()
			.get("/movies/{id}", existingMovieId)
		.then()
			.statusCode(200)
			.body("id", is(1))
			.body("title", equalTo("The Witcher"))
			.body("score", is(4.5F));
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {	
	
		nonExistingMovieId = 100L;
		
		given()
			.get("/movies/{id}", nonExistingMovieId)
		.then()
			.statusCode(404);
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {		
	
		postMovieInstance.put("title", "   ");
		
		JSONObject newMovie = new JSONObject(postMovieInstance);
		
		given()
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + adminToken)
			.body(newMovie)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post("/movies")
		.then()
			.statusCode(422)
			.body("errors.fieldName[0]", equalTo("title"))
			.body("errors.message[0]", equalTo("Campo requerido"))
			.body("errors.fieldName[1]", equalTo("title"))
			.body("errors.message[1]", equalTo("Tamanho deve ser entre 5 e 80 caracteres"));
	}
	
	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {
		
		JSONObject newMovie = new JSONObject(postMovieInstance);
		
		given()
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + clientToken)
			.body(newMovie)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post("/movies")
		.then()
			.statusCode(403);
	}
	
	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
		
		JSONObject newMovie = new JSONObject(postMovieInstance);
		
		given()
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + invalidToken)
			.body(newMovie)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post("/movies")
		.then()
			.statusCode(401);
	}
}
