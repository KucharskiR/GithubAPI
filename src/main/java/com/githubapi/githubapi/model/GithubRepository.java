package com.githubapi.githubapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GithubRepository {
    private String name;
    
    private Owner owner;
    
    @JsonProperty("fork")
    private boolean isFork;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Owner {
        private String login;
    }
}
