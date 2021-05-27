package uk.org.oliveira.vg.version_json;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import net.minidev.json.parser.ParseException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class FileChangeListener implements BulkFileListener {
    private enum FileChangeOperation {
        ADD, REMOVE
    }

    @Override
    public void after(@NotNull List<? extends VFileEvent> events) {
        for (VFileEvent vFileEvent : events) {
            boolean isCreated = vFileEvent.toString().contains("create");
            boolean isDeleted = vFileEvent.toString().contains("deleted");
            boolean isChangedFromOutside = vFileEvent.isFromRefresh() || vFileEvent.isFromSave();

            if ((isCreated || isDeleted) && !isChangedFromOutside) {
                VirtualFile virtualFile = vFileEvent.getFile();
                if (virtualFile != null) {
                    if (isCreated) {
                        operateOnCurrentFolder(
                                virtualFile.getName().concat(" was created! Do you wish to add this file to 'version.json'?"),
                                FileChangeOperation.ADD,
                                virtualFile
                        );
                    }
                    if (isDeleted) {
                        operateOnCurrentFolder(
                                virtualFile.getName().concat(" was deleted! Do you wish to remove this file from 'version.json'?"),
                                FileChangeOperation.REMOVE,
                                virtualFile
                        );
                    }
                }
            }

        }
    }

    private static void operateOnCurrentFolder(@NotNull String message, @NotNull FileChangeOperation operation, @NotNull VirtualFile file) {
        if (file.getPath().contains("/postgres/release/current") && !file.getName().equals("version.json")) {
            ApplicationManager.getApplication().invokeLater(() -> {
                String title = "Current Folder Edited";
                String[] buttons = new String[]{Messages.getYesButton(), Messages.getNoButton()};
                boolean isYes = Messages.showDialog(message, title, buttons, 0, Messages.getQuestionIcon()) == 0;
                if (isYes) {
                    try {
                        switch (operation) {
                            case ADD:
                                VersionJsonManager.addFile(file);
                                break;
                            case REMOVE:
                                VersionJsonManager.removeFile(file);
                                break;
                        }
                    } catch (ParseException | IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
