package uk.org.oliveira.vg.current_folder;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import uk.org.oliveira.vg.NotificationManager;
import uk.org.oliveira.vg.VGUtils;
import uk.org.oliveira.vg.version_json.VersionJsonManager;
import uk.org.oliveira.vg.baseline.BaselineManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static uk.org.oliveira.vg.VGUtils.*;


public class NewCurrentFolderAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        VirtualFile selectedFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (selectedFile != null) {
            String projectRootPath = VGUtils.getProjectRootPathFrom(selectedFile.getPath());
            if (projectRootPath != null) {
                boolean notSetup = VGUtils.getProjectStructure(Path.of(projectRootPath)) != ProjectStatus.FULLY_SETUP;
                e.getPresentation().setEnabled(notSetup);
            } else {
                e.getPresentation().setEnabled(true);
            }
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            project.save();
        }
        VirtualFile selectedFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (selectedFile != null) {
            String projectRootPath = VGUtils.getProjectRootPathFrom(selectedFile.getPath());
            if (projectRootPath != null) {
                List<ProjectStatus> notifyOn = new ArrayList<>();
                notifyOn.add(ProjectStatus.MISSING_POSTGRES_FOLDER);
                notifyOn.add(ProjectStatus.MISSING_RELEASE_FOLDER);
                notifyOn.add(ProjectStatus.MISSING_SCHEMA_FOLDER);
                notifyOn.add(ProjectStatus.FULLY_SETUP); // Never going to be notified because method 'update' checks it already. Leaving just in case!

                if (VGUtils.getProjectStructure(Path.of(projectRootPath), notifyOn) == ProjectStatus.MISSING_CURRENT_FOLDER) {
                    try {
                        this.createCurrentFolder(project, Path.of(projectRootPath));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            } else {
                NotificationManager.notifyError("It appears you select a folder that doesn't contains the './postgres' folder!");
            }
        } else {
            NotificationManager.notifyError("It appears you haven't select a folder, please select a root folder that contains './postgres' folder and try again!");
        }
    }

    private void createCurrentFolder(Project project, Path projectRootPath) throws IOException {
        NewCurrentFolderSettingsDialog dialog = new NewCurrentFolderSettingsDialog(project, projectRootPath);
        if (dialog.showAndGet()) {
            NewCurrentFolderSettings settings = dialog.getSettings();
            Path currentFolderPath = projectRootPath.resolve(relativeCurrentFolderPath);
            Path schemaFolderPath = currentFolderPath.resolve("schema");
            Path scriptsFolderPath = currentFolderPath.resolve("scripts");

            Files.createDirectory(currentFolderPath);
            Files.createDirectory(schemaFolderPath);

            if (settings.shouldCreateScriptsFolder) {
                Files.createDirectory(scriptsFolderPath);
            }

            List<VirtualFile> selectedFiles = settings.getSelectedFiles();
            if (selectedFiles.size() > 0) {
                for (VirtualFile file : selectedFiles) {
                    BaselineManager.moveFileToCurrentFolder(file.getPath());
                }
            }

            // Need to refresh because files were just created.
            VGUtils.refreshProjectRootFrom(projectRootPath.toString());

            List<VirtualFile> versionJsonFiles = VersionJsonManager.getVersionFiles(currentFolderPath);
            VersionJsonManager.createJsonFile(currentFolderPath, versionJsonFiles);

            VGUtils.refreshProjectRootFrom(projectRootPath.toString());
        }
    }
}
