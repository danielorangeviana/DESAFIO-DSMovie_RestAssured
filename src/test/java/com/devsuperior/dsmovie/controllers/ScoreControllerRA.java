package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

import io.restassured.http.ContentType;



public class ScoreControllerRA {
	
	private String clientUsername, clientPassword;
	private String clientToken;
	
	private Map<String, Object> putScoresInstance;
	
	@BeforeEach
	public void setUp() throws Exception {
		
		clientUsername = "maria@gmail.com";
		clientPassword = "123456";
		
		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		
		putScoresInstance = new HashMap<>();
		putScoresInstance.put("movieId", 1);
		putScoresInstance.put("score", 4);
	}
	
	@Test
	public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {		
		
		putScoresInstance.put("movieId", 100);
		
		JSONObject newScore = new JSONObject(putScoresInstance);
		
		given()
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + clientToken)
			.body(newScore)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/scores")
		.then()
			.statusCode(404);
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {
		
		putScoresInstance.put("movieId", "");
		
		JSONObject newScore = new JSONObject(putScoresInstance);
		
		given()
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + clientToken)
			.body(newScore)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/scores")
		.then()
			.statusCode(422)
			.body("errors.fieldName[0]", equalTo("movieId"))
			.body("errors.message[0]", equalTo("Campo requerido"));
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {		
		
		putScoresInstance.put("score", -4);
		
		JSONObject newScore = new JSONObject(putScoresInstance);
		
		given()
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + clientToken)
			.body(newScore)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/scores")
		.then()
			.statusCode(422)
			.body("errors.fieldName[0]", equalTo("score"))
			.body("errors.message[0]", equalTo("Valor mínimo 0"));
	}
}
