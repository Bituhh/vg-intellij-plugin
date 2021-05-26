package uk.org.oliveira.vg;

import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class VGUtils {
    public static final String relativePostgresFolderPath = "postgres";
    public static final String relativeReleaseFolderPath = "postgres/release";
    public static final String relativeSchemaFolderPath = "postgres/schema";
    public static final String relativeCurrentFolderPath = "postgres/release/current";


    public enum ProjectStatus {
        MISSING_RELEASE_FOLDER,
        MISSING_POSTGRES_FOLDER,
        MISSING_SCHEMA_FOLDER,
        MISSING_CURRENT_FOLDER,
        FULLY_SETUP,
    }

    @Nullable
    public static VirtualFile getProjectRootFileFrom(String path) {
        String projectRootPath = getProjectRootPathFrom(path);
        if (projectRootPath == null) {
            return null;
        }
        return LocalFileSystem.getInstance().findFileByNioFile(Path.of(projectRootPath));
    }

    @Nullable
    public static String getProjectRootPathFrom(String path) {
        path = path.replace("\\", "/");
        int rootIndex = path.indexOf("/postgres");
        if (rootIndex == -1) {
            Path siblingFolderPath = Path.of(path, "../postgres");
            if (Files.exists(Path.of(path, "/postgres"))) { // check if current path is root
                return path;
            } else if (Files.exists(siblingFolderPath)) { // check if one path before is root. case current is sibling folder of ./postgres
                VirtualFile file = LocalFileSystem.getInstance().findFileByNioFile(siblingFolderPath);
                if (file != null) {
                    return file.getParent().getPath();
                }
                return null;
            } else {
                return null;
            }
        }
        return path.substring(0, rootIndex);
    }

    public static void refreshProjectRootFrom(String path, boolean asynchronous) {
        VirtualFile projectRootFile = getProjectRootFileFrom(path);
        if (projectRootFile != null) {
            projectRootFile.refresh(asynchronous, true);
        }
    }

    public static void refreshProjectRootFrom(String path) {
        refreshProjectRootFrom(path, true);
    }

    public static ProjectStatus getProjectStructure(@NotNull Path projectPath) {
        return getProjectStructure(projectPath, new ArrayList<>());
    }

    public static ProjectStatus getProjectStructure(@NotNull Path projectPath, @NotNull List<ProjectStatus> notifyErrorOn) {
        if (Files.notExists(projectPath.resolve(relativePostgresFolderPath))) {
            if (notifyErrorOn.contains(ProjectStatus.MISSING_POSTGRES_FOLDER)) {
                NotificationManager.notifyStatus(ProjectStatus.MISSING_POSTGRES_FOLDER);
            }
            return ProjectStatus.MISSING_POSTGRES_FOLDER;
        }
        if (Files.notExists(projectPath.resolve(relativeReleaseFolderPath))) {
            if (notifyErrorOn.contains(ProjectStatus.MISSING_RELEASE_FOLDER)) {
                NotificationManager.notifyStatus(ProjectStatus.MISSING_RELEASE_FOLDER);
            }
            return ProjectStatus.MISSING_RELEASE_FOLDER;
        }
        if (Files.notExists(projectPath.resolve(relativeSchemaFolderPath))) {
            if (notifyErrorOn.contains(ProjectStatus.MISSING_SCHEMA_FOLDER)) {
                NotificationManager.notifyStatus(ProjectStatus.MISSING_SCHEMA_FOLDER);
            }
            return ProjectStatus.MISSING_SCHEMA_FOLDER;
        }
        if (Files.notExists(projectPath.resolve(relativeCurrentFolderPath))) {
            if (notifyErrorOn.contains(ProjectStatus.MISSING_CURRENT_FOLDER)) {
                NotificationManager.notifyStatus(ProjectStatus.MISSING_CURRENT_FOLDER);
            }
            return ProjectStatus.MISSING_CURRENT_FOLDER;
        }
        if (notifyErrorOn.contains(ProjectStatus.FULLY_SETUP)) {
            NotificationManager.notifyStatus(ProjectStatus.FULLY_SETUP);
        }
        return ProjectStatus.FULLY_SETUP;
    }

    @Nullable
    public static String getFileLinesFromResource(@NotNull String path) throws IOException {
        InputStream resourceStream = VGUtils.class.getResourceAsStream(path);
        if (resourceStream != null) {
            InputStreamReader streamReader = new InputStreamReader(resourceStream);
            BufferedReader reader = new BufferedReader(streamReader);
            String line;
            String allLines = "";
            while ((line = reader.readLine()) != null) {
                allLines = allLines.concat(line.concat("\n"));
            }
            return allLines;
        }
        return null;
    }
}
