package io.diveni.backend.service.projectmanagementproviders.github;

import io.diveni.backend.model.Project;
import io.diveni.backend.model.UserStory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class GithubServiceTest {

  @InjectMocks @Spy private GithubService githubService;

  @Test
  void getProjects() {
    HttpHeaders mockedHeaders = new HttpHeaders();
    mockedHeaders.setContentType(MediaType.APPLICATION_JSON);
    String repoURL =
        "{\"login\":\"owner\",\"repos_url\":\"https://api.github.com/users/owner/repos\"}";

    String jsonReturnValue =
        "[{\"id\":"
            + " 1,\"node_id\":\"R_kgiurheiwhri_6g\",\"name\":\"Diveni1\",\"full_name\":\"user/Diveni1\"},{\"id\":"
            + " 2,\"node_id\":\"R_kgiwqeheiwhri_6g\",\"name\":\"Diveni2\",\"full_name\":\"user/Diveni2\"},{\"id\":"
            + " 3,\"node_id\":\"R_kgiurhorwhri_6g\",\"name\":\"Diveni3\",\"full_name\":\"user/Diveni3\"}]";

    ResponseEntity<String> mockedRepoReturnValue =
        new ResponseEntity<>(repoURL, mockedHeaders, HttpStatus.valueOf(200));

    ResponseEntity<String> mockedReturnValue =
        new ResponseEntity<>(jsonReturnValue, mockedHeaders, HttpStatus.valueOf(200));

    Mockito.doReturn(mockedRepoReturnValue, mockedReturnValue)
        .when(githubService)
        .executeRequest(any(), any(), any(), any());

    List<Project> projectList = githubService.getProjects("accessToken");
    Assertions.assertEquals(3, projectList.size());

    Assertions.assertEquals("1", projectList.get(0).getId());
    Assertions.assertEquals("Diveni1", projectList.get(0).getName());

    Assertions.assertEquals("2", projectList.get(1).getId());
    Assertions.assertEquals("Diveni2", projectList.get(1).getName());

    Assertions.assertEquals("3", projectList.get(2).getId());
    Assertions.assertEquals("Diveni3", projectList.get(2).getName());
  }

  @Test
  void getIssues() {
    HttpHeaders mockedHeaders = new HttpHeaders();
    mockedHeaders.setContentType(MediaType.APPLICATION_JSON);

    String jsonReturnValue =
        "[{\"url\":\"https://api.github.com/repos/owner/repo/issues/1\","
            + "\"repository_url\":\"https://api.github.com/repos/owner/repo\","
            + "\"id\": 1932132185,"
            + "\"node_id\":\"I_kwDOG_EiFs525KCB\","
            + "\"number\":1,"
            + "\"title\":\"TEST_ISSUE\","
            + "\"body\":\"TEST_DESCRIPTION\\n Story Points voted on Diveni.io: **5**\"},"
            + "{\"url\":\"https://api.github.com/repos/owner/repo/issues/2\","
            + "\"repository_url\":\"https://api.github.com/repos/owner/repo\","
            + "\"id\": 199321385,"
            + "\"node_id\":\"I_kwDOG_EiFs525KCB\","
            + "\"number\":2,"
            + "\"title\":\"TEST_ISSUE_2\","
            + "\"body\":\"\"}"
            + "]";
    ResponseEntity<String> mockedReturnValue =
        new ResponseEntity<>(jsonReturnValue, mockedHeaders, HttpStatus.valueOf(200));

    Mockito.doReturn(mockedReturnValue)
        .when(githubService)
        .executeRequest(any(), any(), any(), any());

    List<UserStory> userStories = githubService.getIssues("accessToken", "Diveni-Issue-Tracker");
    Assertions.assertEquals(2, userStories.size());

    Assertions.assertEquals("1", userStories.get(0).getId());
    Assertions.assertEquals("TEST_ISSUE", userStories.get(0).getTitle());
    Assertions.assertEquals("TEST_DESCRIPTION", userStories.get(0).getDescription());
    Assertions.assertEquals("5", userStories.get(0).getEstimation());

    Assertions.assertEquals("2", userStories.get(1).getId());
    Assertions.assertEquals("TEST_ISSUE_2", userStories.get(1).getTitle());
    Assertions.assertEquals("", userStories.get(1).getDescription());
    Assertions.assertNull(userStories.get(1).getEstimation());
  }
}
