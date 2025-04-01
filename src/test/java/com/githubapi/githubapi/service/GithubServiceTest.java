package com.githubapi.githubapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.githubapi.githubapi.exception.GithubApiException;
import com.githubapi.githubapi.model.Branch;
import com.githubapi.githubapi.model.Commit;
import com.githubapi.githubapi.model.GithubRepository;
import com.githubapi.githubapi.model.RepositoryResponse;

@ExtendWith(MockitoExtension.class)
public class GithubServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GithubService gitHubService;

    private GithubRepository[] repositories;
    private Branch[] branches;

    @BeforeEach
    public void setUp() {
        // Mock repository data
        GithubRepository repo1 = new GithubRepository();
        repo1.setName("repo1");
        repo1.setOwner(new GithubRepository.Owner("testuser"));
        repo1.setFork(false);

        GithubRepository repo2 = new GithubRepository();
        repo2.setName("repo2");
        repo2.setOwner(new GithubRepository.Owner("testuser"));
        repo2.setFork(true); // This is a fork and should be filtered out

        repositories = new GithubRepository[] { repo1, repo2 };

        // Mock branch data
        Branch branch1 = new Branch();
        branch1.setName("main");
        branch1.setCommit(new Commit("abc123"));

        Branch branch2 = new Branch();
        branch2.setName("develop");
        branch2.setCommit(new Commit("def456"));

        branches = new Branch[] { branch1, branch2 };
    }

    @Test
    public void testGetUserRepositories_Success() {
        // Given
        String username = "testuser";
        when(restTemplate.getForObject(
                eq("https://api.github.com/users/testuser/repos"), 
                eq(GithubRepository[].class)))
                .thenReturn(repositories);

        when(restTemplate.getForObject(
                eq("https://api.github.com/repos/testuser/repo1/branches"), 
                eq(Branch[].class)))
                .thenReturn(branches);

        // When
//        List<RepositoryResponse> result = GithubService.getUserRepositories(username);
        List<RepositoryResponse> result = gitHubService.getUserRepositories(username);

        // Then
        assertEquals(1, result.size());
        assertEquals("repo1", result.get(0).getName());
        assertEquals("testuser", result.get(0).getOwner());
        assertEquals(2, result.get(0).getBranches().size());
        assertEquals("main", result.get(0).getBranches().get(0).getName());
        assertEquals("abc123", result.get(0).getBranches().get(0).getLastCommitSha());
    }

    @Test
    public void testGetUserRepositories_UserNotFound() {
        // Given
        String username = "nonexistentuser";
        when(restTemplate.getForObject(
                anyString(), 
                eq(GithubRepository[].class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // When & Then
        GithubApiException exception = assertThrows(GithubApiException.class, () -> {
            gitHubService.getUserRepositories(username);
        });
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Użytkownik GitHub nie istnieje.", exception.getMessage());
    }
}
