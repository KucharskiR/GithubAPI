package com.githubapi.githubapi.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.githubapi.githubapi.exception.GithubApiException;
import com.githubapi.githubapi.model.Branch;
import com.githubapi.githubapi.model.BranchDetails;
import com.githubapi.githubapi.model.GithubRepository;
import com.githubapi.githubapi.model.RepositoryResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubService {

    private final ObjectMapper objectMapper;
    private static final String GITHUB_API_URL = "https://api.github.com";
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public List<RepositoryResponse> getUserRepositories(String username) {
        try {
            var userReposUrl = GITHUB_API_URL + "/users/" + username + "/repos";
            var request = HttpRequest.newBuilder().uri(URI.create(userReposUrl)).build();
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                if (response.statusCode() == 404) {
                    throw new GithubApiException("Użytkownik GitHub nie istnieje.", HttpStatus.NOT_FOUND);
                }
                throw new GithubApiException("Wystąpił błąd podczas pobierania repozytoriów.", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            List<GithubRepository> repositories = objectMapper.readValue(response.body(), new TypeReference<>() {});

            if (repositories == null) {
                return List.of();
            }

            var nonForkRepos = repositories.stream().filter(repo -> !repo.isFork()).toList();

            return nonForkRepos.stream().map(repo -> {
                var branchesUrl = GITHUB_API_URL + "/repos/" + username + "/" + repo.getName() + "/branches";
                var branchesRequest = HttpRequest.newBuilder().uri(URI.create(branchesUrl)).build();
                try {
                    var branchesResponse = httpClient.send(branchesRequest, HttpResponse.BodyHandlers.ofString());

                    if (branchesResponse.statusCode() != 200) {
                        log.error("Error retrieving branches for repository: {}", repo.getName());
                        return RepositoryResponse.builder().name(repo.getName()).owner(repo.getOwner().getLogin()).branches(List.of()).build();
                    }

                    List<Branch> branches = objectMapper.readValue(branchesResponse.body(), new TypeReference<>() {});

                    var branchDetails = branches.stream().map(branch -> BranchDetails.builder().name(branch.getName())
                            .lastCommitSha(branch.getCommit().getSha()).build()).toList();

                    return RepositoryResponse.builder().name(repo.getName()).owner(repo.getOwner().getLogin())
                            .branches(branchDetails).build();
                } catch (Exception e) {
                    log.error("Error retrieving branches for repository: {}", repo.getName(), e);
                    return RepositoryResponse.builder().name(repo.getName()).owner(repo.getOwner().getLogin()).branches(List.of()).build();
                }
            }).toList();

        } catch (Exception e) {
            log.error("Error retrieving GitHub repositories for user: {}", username, e);
            throw new GithubApiException("Wystąpił błąd podczas pobierania repozytoriów.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
