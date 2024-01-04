package io.diveni.backend.service.projectmanagementproviders.gitlab;

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
public class GitlabServiceTest {

  @InjectMocks @Spy private GitlabService gitlabService;

  @Test
  void getProjects() {
    HttpHeaders mockedHeaders = new HttpHeaders();
    mockedHeaders.setContentType(MediaType.APPLICATION_JSON);
    String repoURL = "{\"id\":\"owner\",\"username\":\"TEST_USERNAME\"}";

    String jsonReturnValue =
        "[{\"id\":"
            + " 1,\"description\":\"Test_Description\",\"name\":\"Diveni1\",\"path_with_namespace\":\"user/Diveni1\"},{\"id\":"
            + " 2,\"description\":\"Test_Descriptio2n\",\"name\":\"Diveni2\",\"path_with_namespace\":\"user/Diveni2\"},{\"id\":"
            + " 3,\"description\":\"null\",\"name\":\"Diveni3\",\"path_with_namespace\":\"user/Diveni3\"}]";

    String jsonGroupReturn = "[\n" +
      "    {\n" +
      "        \"id\": 1,\n" +
      "        \"web_url\": \"https://gitlab.com/groups/testGroup\",\n" +
      "        \"name\": \"testGroup\",\n" +
      "        \"path\": \"testGroup\",\n" +
      "        \"description\": \"\",\n" +
      "        \"visibility\": \"public\"}]";

    String jsonReturnValueGroups =
      "[{\"id\":"
        + " 5,\"description\":\"Test_Description\",\"name\":\"Diveni1\",\"path_with_namespace\":\"user/Diveni1\"},{\"id\":"
        + " 6,\"description\":\"Test_Descriptio2n\",\"name\":\"Diveni2\",\"path_with_namespace\":\"user/Diveni2\"},{\"id\":"
        + " 8,\"description\":\"null\",\"name\":\"Diveni3\",\"path_with_namespace\":\"user/Diveni3\"}]";

    ResponseEntity<String> mockedRepoReturnValue =
        new ResponseEntity<>(repoURL, mockedHeaders, HttpStatus.valueOf(200));

    ResponseEntity<String> mockedReturnValue =
        new ResponseEntity<>(jsonReturnValue, mockedHeaders, HttpStatus.valueOf(200));

    ResponseEntity<String> mockedRepoReturnValueForGroups =
      new ResponseEntity<>(jsonGroupReturn, mockedHeaders, HttpStatus.valueOf(200));

    ResponseEntity<String> mockedReturnValueForGroups =
      new ResponseEntity<>(jsonReturnValueGroups, mockedHeaders, HttpStatus.valueOf(200));

    Mockito.doReturn(mockedRepoReturnValue, mockedReturnValue, mockedRepoReturnValueForGroups, mockedReturnValueForGroups)
        .when(gitlabService)
        .executeRequest(any(), any(), any(), any());

    List<Project> projectList = gitlabService.getProjects("accessToken");
    Assertions.assertEquals(6, projectList.size());

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
        "[{\"web_url\":\"https://gitlab.com/owner/repo/-/issues/1\","
            + "\"time_stats\":{\"time_estimate\": 18000,"
            + "\"total_time_spent\": 0, "
            + "\"human_time_estimate\": \"5h\","
            + "\"human_total_time_spent\": \"null\""
            + "},"
            + "\"id\": 1932132185,"
            + "\"iid\":1,"
            + "\"project_id\":13213213213,"
            + "\"title\":\"TEST_ISSUE\","
            + "\"description\":\"TEST_DESCRIPTION\"},"
            + "{\"web_url\":\"https://gitlab.com/owner/repo/-/issues/2\","
            + "\"time_stats\":{\"time_estimate\": \"null\","
            + "\"total_time_spent\": 0, "
            + "\"human_time_estimate\": \"null\","
            + "\"human_total_time_spent\": \"null\""
            + "},"
            + "\"id\": 1932132184,"
            + "\"iid\": 2,"
            + "\"project_id\":13213213213,"
            + "\"title\":\"TEST_ISSUE_2\","
            + "\"description\":\"\"}"
            + "]";
    ResponseEntity<String> mockedReturnValue =
        new ResponseEntity<>(jsonReturnValue, mockedHeaders, HttpStatus.valueOf(200));

    Mockito.doReturn(mockedReturnValue)
        .when(gitlabService)
        .executeRequest(any(), any(), any(), any());

    List<UserStory> userStories = gitlabService.getIssues("accessToken", "Diveni-Issue-Tracker");
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
