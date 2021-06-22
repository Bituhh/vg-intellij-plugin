package uk.org.oliveira.vg.version_json;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class RebuildVersionJsonFileDialog extends DialogWrapper {
    private final JBList<VirtualFile> filesList = new JBList<>();
    private final ToolbarDecorator decorator = ToolbarDecorator.createDecorator(this.filesList);
    public List<VirtualFile> virtualFiles;

    private enum Direction {
        UP, DOWN
    }

    protected RebuildVersionJsonFileDialog(List<VirtualFile> virtualFiles) {
        super(true);
        init();
        setTitle("Organize Files");
        this.virtualFiles = virtualFiles;
        this.filesList.setListData(virtualFiles.toArray(new VirtualFile[0]));
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        this.setupFilesList();
        return FormBuilder.createFormBuilder()
                .addLabeledComponent("Please remove any files that you do not which to go to 'version.json' and reorder them if needed.", this.decorator.createPanel(), true)
                .getPanel();
    }

    private void setupFilesList() {
        this.filesList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> new JBLabel(value.getName()));

        this.decorator.setRemoveActionName("Remove Selected File From 'version.json'");
        this.decorator.disableAddAction();
        this.decorator.setRemoveAction(action -> {
            int index = this.filesList.getSelectedIndex();
            this.virtualFiles.remove(index);
            this.setList();
        }).setMoveUpAction(action -> moveFile(Direction.UP))
                .setMoveDownAction(action -> moveFile(Direction.DOWN));
    }

    private void moveFile(Direction direction) {
        int i = this.filesList.getSelectedIndex() - 1;
        if (direction == Direction.DOWN) {
            i = this.filesList.getSelectedIndex() + 1;
        }
        int j = this.filesList.getSelectedIndex();
        VirtualFile v = this.virtualFiles.get(i);
        this.virtualFiles.set(i, this.virtualFiles.get(j));
        this.virtualFiles.set(j, v);
        this.setList();
        this.filesList.setSelectedIndex(i);
    }

    private void setList() {
        VirtualFile[] files = new VirtualFile[this.virtualFiles.size()];
        for (int i = 0; i < files.length; i++) {
            files[i] = this.virtualFiles.get(i);
        }
        this.filesList.setListData(files);
    }
}
