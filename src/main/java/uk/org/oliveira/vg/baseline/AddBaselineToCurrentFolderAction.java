package uk.org.oliveira.vg.baseline;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import net.minidev.json.parser.ParseException;
import org.jetbrains.annotations.NotNull;
import uk.org.oliveira.vg.NotificationManager;
import uk.org.oliveira.vg.VGUtils;
import uk.org.oliveira.vg.version_json.VersionJsonManager;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;

public class AddBaselineToCurrentFolderAction extends AnAction {
    @Override
    public void update(@NotNull AnActionEvent e) {
        VirtualFile selectedFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (selectedFile != null) {
            String projectRootPath = VGUtils.getProjectRootPathFrom(selectedFile.getPath());
            if (VGUtils.getProjectStructure(Path.of(projectRootPath)) == VGUtils.ProjectStatus.FULLY_SETUP) {
                e.getPresentation().setEnabled(!selectedFile.isDirectory() && selectedFile.getPath().contains("/postgres/schema"));
            } else {
                e.getPresentation().setEnabled(false);
            }
        } else {
            e.getPresentation().setEnabled(false);
        }

    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        VirtualFile selectedFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        Project project = e.getProject();
        if (project != null) {
            project.save();
        }
        if (selectedFile != null) {
            try {
                Path target = BaselineManager.moveFileToCurrentFolder(selectedFile.getPath());

                // Need to refresh because files were just created.
                VGUtils.refreshProjectRootFrom(selectedFile.getPath(), false);

                if (NotificationManager.fileChangedQuestion("Do you want to add this file to 'version.json'") == NotificationManager.NotificationResponse.YES) {
                    VirtualFile newFile = LocalFileSystem.getInstance().findFileByNioFile(target);
                    if (newFile != null) {
                        VersionJsonManager.addFile(newFile);
                    } else {
                        NotificationManager.notifyError("Unable to get copied file. Please add this file manually to 'version.json'!");
                    }
                }
            } catch (FileAlreadyExistsException fileAlreadyExistsException) {
                NotificationManager.notifyError("This file already exist in current folder.");
            } catch (IOException | ParseException ioException) {
                ioException.printStackTrace();
            }
        } else {
            NotificationManager.notifyError("Unable to get selected file!");
        }
    }
}
