package uk.org.oliveira.vg;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;

public class NotificationManager {
    public static void notifyWarning(String message) {
        Project project = ProjectManager.getInstance().getOpenProjects()[0];
        NotificationGroupManager.getInstance()
                .getNotificationGroup("VGAlerts")
                .createNotification(message, NotificationType.WARNING)
                .notify(project);
    }

    public static void notifyError(String message) {
        Project project = ProjectManager.getInstance().getOpenProjects()[0];
        NotificationGroupManager.getInstance()
                .getNotificationGroup("VGAlerts")
                .createNotification(message, NotificationType.ERROR)
                .notify(project);
    }

    public static void notifyStatus(VGUtils.ProjectStatus status) {
        switch (status) {
            case MISSING_RELEASE_FOLDER:
                NotificationManager.notifyError(
                        "Missing 'release' folder: It appears you have select a none root folder," +
                                " please select a root folder that contains './postgres/release' folder and try again!"
                );
                break;
            case MISSING_POSTGRES_FOLDER:
                NotificationManager.notifyError(
                        "Missing 'postgres' folder: It appears you have select a none root folder," +
                                " please select a root folder that contains './postgres' folder and try again!"
                );
                break;
            case MISSING_SCHEMA_FOLDER:
                NotificationManager.notifyError(
                        "Missing 'schema' folder: It appears you have select a none root folder," +
                                " please select a root folder that contains './postgres/schema' folder and try again!"
                );
                break;
            case MISSING_CURRENT_FOLDER:
                NotificationManager.notifyError(
                        "Missing 'current' folder: It appears you have select a none root folder," +
                                " please select a root folder that contains './postgres/release/current' folder and try again!"
                );
                break;
            case FULLY_SETUP:
                NotificationManager.notifyError(
                        "A current folder already exists under releases! The project is already fully setup!"
                );
                break;
        }
    }

    public static NotificationResponse fileChangedQuestion(String message) {
        String[] buttons = new String[]{Messages.getYesButton(), Messages.getNoButton()};
        return Messages.showDialog(message, "File Changed", buttons, 0, Messages.getQuestionIcon()) == 0 ? NotificationResponse.YES : NotificationResponse.NO;
    }

    public enum NotificationResponse {
        YES, NO
    }
}
