package com.githubapi.githubapi.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.githubapi.githubapi.controller.GithubController;
import com.githubapi.githubapi.exception.GithubApiException;
import com.githubapi.githubapi.model.RepositoryResponse;
import com.githubapi.githubapi.service.GithubService;

@WebMvcTest(GithubController.class)
public class GithubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GithubService gitHubService;

    @Test
    public void testGetUserRepositories_Success() throws Exception {
        // Given
        String username = "testuser";
        RepositoryResponse repo = new RepositoryResponse();
        repo.setName("test-repo");
        repo.setOwner("testuser");
        repo.setBranches(Collections.emptyList());
        
        when(gitHubService.getUserRepositories(username)).thenReturn(List.of(repo));

        // When & Then
        mockMvc.perform(get("/api/repos/{username}", username)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("test-repo"))
                .andExpect(jsonPath("$[0].owner").value("testuser"));
    }

    @Test
    public void testGetUserRepositories_UserNotFound() throws Exception {
        // Given
        String username = "nonexistentuser";
        when(gitHubService.getUserRepositories(username))
                .thenThrow(new GithubApiException("Użytkownik GitHub nie istnieje.", HttpStatus.NOT_FOUND));

        // When & Then
        mockMvc.perform(get("/api/repos/{username}", username)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Użytkownik GitHub nie istnieje."));
    }
}
