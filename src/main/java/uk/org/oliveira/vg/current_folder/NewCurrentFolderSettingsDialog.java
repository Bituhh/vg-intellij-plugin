package uk.org.oliveira.vg.current_folder;

import com.intellij.openapi.actionSystem.ActionToolbarPosition;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nullable;
import uk.org.oliveira.vg.NotificationManager;

import javax.swing.*;
import java.nio.file.Path;

public class NewCurrentFolderSettingsDialog extends DialogWrapper {
    private final Project project;
    private final Path projectPath;
    private final NewCurrentFolderSettings settings = new NewCurrentFolderSettings();
    private final JBList<VirtualFile> filesList = new JBList<>();
    private final ToolbarDecorator decorator = ToolbarDecorator.createDecorator(this.filesList);
    private final JBCheckBox scriptCheckbox = new JBCheckBox("Add 'scripts' Folder", true);

    protected NewCurrentFolderSettingsDialog(Project project, Path projectPath) {
        super(project, true);
        this.project = project;
        this.projectPath = projectPath;
        init();
        setTitle("Current Folder Settings");
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        this.setupScriptsCheckbox();
        this.setupFilesList();

        return FormBuilder.createFormBuilder()
                .setAlignLabelOnRight(true)
                .addLabeledComponent("Baseline files", this.decorator.createPanel(), true)
                .addSeparator()
                .addComponent(scriptCheckbox)
                .getPanel();
    }

    private void setupScriptsCheckbox() {
        this.scriptCheckbox.addActionListener(e -> {
            this.settings.shouldCreateScriptsFolder = this.scriptCheckbox.isSelected();
        });
    }

    private void setupFilesList() {
        this.filesList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> new JBLabel(value.getName()));

        this.decorator.setToolbarPosition(ActionToolbarPosition.RIGHT);
        this.decorator.setAddActionName("Add Baseline Files");
        this.decorator.setRemoveActionName("Remove Selected Baseline File");
        this.decorator.setAddAction(action -> {
            FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, true);
            VirtualFile schemaFolder = LocalFileSystem.getInstance().findFileByNioFile(projectPath.resolve("postgres/schema"));
            VirtualFile[] files;
            if (schemaFolder == null) {
                NotificationManager.notifyWarning("Unable to open at schema folder, opening in project root.");
            }
            files = FileChooser.chooseFiles(descriptor, project, schemaFolder);
            this.settings.setSelectedFiles(files);
            this.filesList.setListData(files);
        }).setRemoveAction(action -> {
            int index = this.filesList.getSelectedIndex();
            this.settings.getSelectedFiles().remove(index);
            VirtualFile[] files = new VirtualFile[this.settings.getSelectedFiles().size()];
            for (int i = 0; i < files.length; i++) {
                files[i] = this.settings.getSelectedFiles().get(i);
            }
            this.filesList.setListData(files);
        }).disableUpDownActions();
    }

    public NewCurrentFolderSettings getSettings() {
        return settings;
    }
}
