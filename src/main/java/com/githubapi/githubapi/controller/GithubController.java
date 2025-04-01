package com.githubapi.githubapi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.githubapi.githubapi.model.RepositoryResponse;
import com.githubapi.githubapi.service.GithubService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GithubController {

    private final GithubService gitHubService;

    @GetMapping("/repos/{username}")
    public List<RepositoryResponse> getUserRepositories(@PathVariable String username) {
        return gitHubService.getUserRepositories(username);
    }
}
