package uk.org.oliveira.vg.store_procedure;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class NewStoreProcedureAction extends AnAction {
    @Override
    public void update(@NotNull AnActionEvent e) {
        VirtualFile selectedFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (selectedFile != null) {
            e.getPresentation().setEnabled(selectedFile.getPath().contains("/postgres/release/current/schema"));
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        VirtualFile selectedFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (selectedFile != null) {
            NewStoreProcedureSetupDialog newStoreProcedureSetupDialog = new NewStoreProcedureSetupDialog(project);
            if (newStoreProcedureSetupDialog.showAndGet()) {
                String prefix = newStoreProcedureSetupDialog.getStoreProcedurePrefix();
                String name = newStoreProcedureSetupDialog.getStoreProcedureName();
                List<String> arguments = newStoreProcedureSetupDialog.getArguments();
                String returnType = newStoreProcedureSetupDialog.getReturnType();
                String volatility = newStoreProcedureSetupDialog.getVolatility();
                List<String> userAuthorizationRoles = newStoreProcedureSetupDialog.getUserAuthorizationRoles();
                try {
                    String selectedFilePath = selectedFile.getPath();
                    String projectRootPath = VGUtils.getProjectRootPathFrom(selectedFilePath);
                    String targetDirectoryPath = selectedFilePath;
                    if (!targetDirectoryPath.contains("/postgres/release/current/schema/07-functions")) {
                        Path targetDirectory = Path.of(projectRootPath, "/postgres/release/current/schema/07-functions");
                        targetDirectoryPath = targetDirectory.toString();
                        Files.createDirectories(targetDirectory);
                    } else if (!selectedFile.isDirectory()) {
                        targetDirectoryPath = selectedFile.getParent().getPath();
                    }

                    StoreProcedure procedure = new StoreProcedure(prefix, name, arguments, returnType, volatility, userAuthorizationRoles);
                    String targetPath = procedure.writeFile(targetDirectoryPath);


                    // Need to refresh because files were just created.
                    if (projectRootPath != null) {
                        VGUtils.refreshProjectRootFrom(projectRootPath);
                    }

                    VirtualFile targetFile = LocalFileSystem.getInstance().findFileByNioFile(Path.of(targetPath));
                    if (targetFile != null) {
                        VersionJsonManager.addFile(targetFile);
                    } else {
                        NotificationManager.notifyError("Unable to get newly created file. Please add the file manually to the 'version.json'!");
                    }
                } catch (IOException | ParseException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }
}
