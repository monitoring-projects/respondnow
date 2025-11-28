package io.respondnow.service.slack;

import com.slack.api.Slack;
import com.slack.api.bolt.App;
import com.slack.api.bolt.context.builtin.GlobalShortcutContext;
import com.slack.api.bolt.request.builtin.GlobalShortcutRequest;
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest;
import com.slack.api.methods.SlackApiException;
import com.slack.api.model.Conversation;
import com.slack.api.model.event.AppHomeOpenedEvent;
import io.respondnow.model.incident.SlackIncidentType;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * No-op implementation of SlackService when Slack integration is disabled.
 * All methods are no-ops that log a warning and return safe default values.
 */
@Service
@Slf4j
@ConditionalOnProperty(name = "slack.enabled", havingValue = "false", matchIfMissing = true)
public class SlackServiceNoOp implements SlackService {

  private static final String SLACK_DISABLED_MSG = "Slack integration is disabled. Set SLACK_ENABLED=true to enable.";

  public SlackServiceNoOp() {
    log.info("Slack integration is DISABLED. Using no-op implementation.");
  }

  @Override
  public void setBotUserIDAndName() {
    log.debug(SLACK_DISABLED_MSG);
  }

  @Override
  public String getBotUserId() {
    log.debug(SLACK_DISABLED_MSG);
    return null;
  }

  @Override
  public void addBotUserToIncidentChannel(String botUserID, String channelID) {
    log.debug(SLACK_DISABLED_MSG);
  }

  @Override
  public List<String> listAllMembersOfChannel(String channelId) {
    log.debug(SLACK_DISABLED_MSG);
    return Collections.emptyList();
  }

  @Override
  public List<String> listUsers(String channelID) {
    log.debug(SLACK_DISABLED_MSG);
    return Collections.emptyList();
  }

  @Override
  public List<Conversation> listChannels() {
    log.debug(SLACK_DISABLED_MSG);
    return Collections.emptyList();
  }

  @Override
  public void handleAppHome(AppHomeOpenedEvent event) {
    log.debug(SLACK_DISABLED_MSG);
  }

  @Override
  public String getIncidentChannelID() {
    return null;
  }

  @Override
  public String getBotToken() {
    return null;
  }

  @Override
  public String getAppToken() {
    return null;
  }

  @Override
  public boolean isBotInChannel(String botUserID, String channelID) {
    log.debug(SLACK_DISABLED_MSG);
    return false;
  }

  @Override
  public void startApp() {
    log.info(SLACK_DISABLED_MSG);
  }

  @Override
  public Slack getSlackClient() {
    return null;
  }

  @Override
  public App getSlackApp() {
    return null;
  }

  @Override
  public void createIncident(GlobalShortcutRequest req, GlobalShortcutContext ctx) {
    log.debug(SLACK_DISABLED_MSG);
  }

  @Override
  public void listIncidents(GlobalShortcutContext ctx, SlackIncidentType status) {
    log.debug(SLACK_DISABLED_MSG);
  }

  @Override
  public void createIncident(ViewSubmissionRequest viewSubmission) {
    log.debug(SLACK_DISABLED_MSG);
  }

  @Override
  public void handleIncidentSummaryViewSubmission(ViewSubmissionRequest viewSubmission)
      throws SlackApiException, IOException {
    log.debug(SLACK_DISABLED_MSG);
  }

  @Override
  public void handleIncidentCommentViewSubmission(ViewSubmissionRequest viewSubmission)
      throws SlackApiException, IOException {
    log.debug(SLACK_DISABLED_MSG);
  }

  @Override
  public void handleIncidentRolesViewSubmission(ViewSubmissionRequest viewSubmission)
      throws SlackApiException, IOException {
    log.debug(SLACK_DISABLED_MSG);
  }

  @Override
  public void handleIncidentStatusViewSubmission(ViewSubmissionRequest viewSubmission)
      throws SlackApiException, IOException {
    log.debug(SLACK_DISABLED_MSG);
  }

  @Override
  public void handleIncidentSeverityViewSubmission(ViewSubmissionRequest viewSubmission)
      throws SlackApiException, IOException {
    log.debug(SLACK_DISABLED_MSG);
  }
}
