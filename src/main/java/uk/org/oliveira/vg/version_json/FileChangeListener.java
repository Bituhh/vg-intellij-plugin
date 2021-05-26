package uk.org.oliveira.vg.version_json;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.PsiTreeChangeListener;
import net.minidev.json.parser.ParseException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class FileChangeListener implements PsiTreeChangeListener {
    @Override
    public void beforeChildAddition(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void beforeChildRemoval(@NotNull PsiTreeChangeEvent event) {
        editCurrentFolderBasedOnFile(event, "Do you wish to remove this file from 'version.json'?", file -> {
            try {
                VersionJsonManager.removeFile(file);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void beforeChildReplacement(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void beforeChildMovement(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void beforeChildrenChange(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void beforePropertyChange(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void childAdded(@NotNull PsiTreeChangeEvent event) {
        editCurrentFolderBasedOnFile(event, "Do you wish to add this file to 'version.json'?", file -> {
            try {
                VersionJsonManager.addFile(file);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void childRemoved(@NotNull PsiTreeChangeEvent event) {
    }

    @Override
    public void childReplaced(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void childrenChanged(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void childMoved(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void propertyChanged(@NotNull PsiTreeChangeEvent event) {

    }

    private interface FileChangeCallback {
        void run(VirtualFile file) throws IOException;
    }

    private static void editCurrentFolderBasedOnFile(@NotNull PsiTreeChangeEvent event, String message, FileChangeCallback operation) {
        PsiElement child = event.getChild();
        PsiFile containingFile = child.getContainingFile();
        if (containingFile != null) {
            VirtualFile file = containingFile.getVirtualFile();
            if (file != null) {
                if (file.getPath().contains("/postgres/release/current") && !file.getName().equals("version.json")) {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        String title = "Current Folder Edited";
                        String[] buttons = new String[]{Messages.getYesButton(), Messages.getNoButton()};
                        boolean isYes = Messages.showDialog(message, title, buttons, 0, Messages.getQuestionIcon()) == 0;
                        if (isYes) {
                            try {
                                operation.run(file);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }
    }
}
