package com.githubapi.githubapi.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

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

	private final RestTemplate restTemplate;
	private static final String GITHUB_API_URL = "https://api.github.com";

	public List<RepositoryResponse> getUserRepositories(String username) {
		try {
			// Fetch all repositories for the user
			String userReposUrl = GITHUB_API_URL + "/users/" + username + "/repos";
			GithubRepository[] repositories = restTemplate.getForObject(userReposUrl, GithubRepository[].class);

			if (repositories == null) {
				return List.of();
			}

			// Filter out forks
			List<GithubRepository> nonForkRepos = Arrays.stream(repositories).filter(repo -> !repo.isFork())
					.collect(Collectors.toList());

			List<RepositoryResponse> result = new ArrayList<>();

			// For each non-fork repository, fetch branches and create response
			for (GithubRepository repo : nonForkRepos) {
				String branchesUrl = GITHUB_API_URL + "/repos/" + username + "/" + repo.getName() + "/branches";
				Branch[] branches = restTemplate.getForObject(branchesUrl, Branch[].class);

				List<BranchDetails> branchDetails = new ArrayList<>();
				if (branches != null) {
					branchDetails = Arrays.stream(branches).map(branch -> BranchDetails.builder().name(branch.getName())
							.lastCommitSha(branch.getCommit().getSha()).build()).collect(Collectors.toList());
				}

				result.add(RepositoryResponse.builder().name(repo.getName()).owner(repo.getOwner().getLogin())
						.branches(branchDetails).build());
			}

			return result;

		} catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
				log.error("GitHub user not found: {}", username);
				throw new GithubApiException("Użytkownik GitHub nie istnieje.", HttpStatus.NOT_FOUND);
			} else {
				log.error("Error retrieving GitHub repositories for user: {}", username, e);
				throw new GithubApiException("Wystąpił błąd podczas pobierania repozytoriów.",
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}
}
