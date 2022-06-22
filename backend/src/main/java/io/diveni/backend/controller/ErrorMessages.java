package io.diveni.backend.controller;

public class ErrorMessages {

    public static final String notAnAdminErrorMessage = "not an admin";

    public static String sessionNotFoundErrorMessage = "session not found";

    public static String memberExistsErrorMessage = "member already exists";

    public static String memberNotExistsErrorMessage = "member does not exist";

    public static String wrongPasswordMessage = "no or wrong password given";

    public static String failedToRetrieveRequestTokenErrorMessage = "failed to retrieve request token";

    public static String failedToRetrieveAccessTokenErrorMessage = "failed to retrieve access token";

    public static String failedToRetrieveProjectsErrorMessage = "failed to retrieve projects";

    public static String failedToEditIssueErrorMessage = "failed to edit issue";

    public static String failedToDeleteIssueErrorMessage = "failed to edit issue";

    public static String failedToRetrieveUsernameErrorMessage = "failed to retrieve current user";

    private ErrorMessages() {
    }

}
