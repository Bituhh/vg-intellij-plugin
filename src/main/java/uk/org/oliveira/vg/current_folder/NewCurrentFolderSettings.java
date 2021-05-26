package uk.org.oliveira.vg.current_folder;

import com.intellij.openapi.vfs.VirtualFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewCurrentFolderSettings {
    private final List<VirtualFile> selectedFiles = new ArrayList<>();
    public boolean shouldCreateScriptsFolder = true;

    protected NewCurrentFolderSettings() {
    }

    public void setSelectedFiles(VirtualFile[] files) {
        selectedFiles.addAll(Arrays.asList(files));
    }

    public List<VirtualFile> getSelectedFiles() {
        return selectedFiles;
    }
}
