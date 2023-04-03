package io.diveni.backend.service.projectmanagementproviders.azuredevops;

import io.diveni.backend.model.Project;
import io.diveni.backend.model.UserStory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class AzureDevOpsServiceTest {

  @InjectMocks @Spy private AzureDevOpsService azureDevOpsService;

  @Test
  void getProjects() {
    HttpHeaders mockedHeaders = new HttpHeaders();
    mockedHeaders.setContentType(MediaType.APPLICATION_JSON);
    String jsonReturnValue =
        "{\"value\":[{\"id\":\"1\",\"name\":\"Diveni-Issue-Tracker\"},{\"id\":\"2\",\"name\":\"Diveni-Issue-Tracker-2\"},{\"id\":\"3\",\"name\":\"Diveni-Issue-Tracker-3\"}]}";
    ResponseEntity<String> mockedReturnValue =
        new ResponseEntity<>(jsonReturnValue, mockedHeaders, HttpStatus.valueOf(200));
    Mockito.doReturn(mockedReturnValue)
        .when(azureDevOpsService)
        .executeRequest(any(), any(), any(), any());

    List<Project> projectList = azureDevOpsService.getProjects("accessToken");
    Assertions.assertEquals(3, projectList.size());

    Assertions.assertEquals("1", projectList.get(0).getId());
    Assertions.assertEquals("Diveni-Issue-Tracker", projectList.get(0).getName());

    Assertions.assertEquals("2", projectList.get(1).getId());
    Assertions.assertEquals("Diveni-Issue-Tracker-2", projectList.get(1).getName());

    Assertions.assertEquals("3", projectList.get(2).getId());
    Assertions.assertEquals("Diveni-Issue-Tracker-3", projectList.get(2).getName());
  }

  @Test
  void getIssues() {
    HttpHeaders mockedHeaders = new HttpHeaders();
    mockedHeaders.setContentType(MediaType.APPLICATION_JSON);
    String firstJsonReturnValue = "{\"workItems\":[{\"id\":1},{\"id\":2},{\"id\":3}]}";
    ResponseEntity<String> mockedFirstReturnValue =
        new ResponseEntity<>(firstJsonReturnValue, mockedHeaders, HttpStatus.valueOf(200));
    String secondJsonReturnValue =
        "{\"value\":[{\"id\":1,\"fields\":{\"System.Title\":\"New User"
            + " Story\",\"Microsoft.VSTS.Scheduling.StoryPoints\":1,\"System.Description\":\"<div>Description"
            + " <\\/div>\"}},{\"id\":2,\"fields\":{\"System.Title\":\"New User Story"
            + " 2\"}},{\"id\":3,\"fields\":{\"System.Title\":\"New User Story"
            + " 3\",\"System.Description\":\"<div> <\\/div>\"}}]}";
    ResponseEntity<String> mockedSecondReturnValue =
        new ResponseEntity<>(secondJsonReturnValue, mockedHeaders, HttpStatus.valueOf(200));

    Mockito.doReturn(mockedFirstReturnValue, mockedSecondReturnValue)
        .when(azureDevOpsService)
        .executeRequest(any(), any(), any(), any());

    List<UserStory> userStories =
        azureDevOpsService.getIssues("accessToken", "Diveni-Issue-Tracker");
    Assertions.assertEquals(3, userStories.size());

    Assertions.assertEquals("1", userStories.get(0).getId());
    Assertions.assertEquals("New User Story", userStories.get(0).getTitle());
    Assertions.assertEquals("Description", userStories.get(0).getDescription());
    Assertions.assertEquals("1", userStories.get(0).getEstimation());

    Assertions.assertEquals("2", userStories.get(1).getId());
    Assertions.assertEquals("New User Story 2", userStories.get(1).getTitle());
    Assertions.assertEquals("", userStories.get(1).getDescription());
    Assertions.assertNull(userStories.get(1).getEstimation());

    Assertions.assertEquals("3", userStories.get(2).getId());
    Assertions.assertEquals("New User Story 3", userStories.get(2).getTitle());
    Assertions.assertEquals("", userStories.get(2).getDescription());
    Assertions.assertNull(userStories.get(2).getEstimation());
  }
}
