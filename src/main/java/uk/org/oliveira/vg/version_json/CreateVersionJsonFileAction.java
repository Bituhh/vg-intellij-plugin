package uk.org.oliveira.vg.version_json;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import uk.org.oliveira.vg.NotificationManager;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class CreateVersionJsonFileAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        VirtualFile selectedFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (selectedFile != null) {
            e.getPresentation().setEnabled(selectedFile.getPath().contains("/postgres/release/current/version.json"));
        } else {
            e.getPresentation().setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            project.save();
        }
        VirtualFile selectedFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (selectedFile != null && selectedFile.getName().equals("version.json")) {
            try {
                getVersionJsonFiles(Path.of(selectedFile.getParent().getPath()));
                selectedFile.refresh(true, true);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } else {
            NotificationManager.notifyError("Oops, It appears you haven't select a version.json file!");
        }
    }

    private static void getVersionJsonFiles(Path currentFolderPath) throws IOException {
        List<VirtualFile> filesList = VersionJsonManager.getVersionFiles(currentFolderPath);
        CreateVersionJsonFileDialog dialog = new CreateVersionJsonFileDialog(filesList);
        if (dialog.showAndGet()) {
            VersionJsonManager.createJsonFile(currentFolderPath, dialog.virtualFiles);
        }
    }
}
