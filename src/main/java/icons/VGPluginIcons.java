package icons;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;


public interface VGPluginIcons {
    Icon NewCurrentFolderAction = AllIcons.ToolbarDecorator.AddFolder;
    Icon NewStoreProcedureAction = AllIcons.Nodes.Function;
    Icon NewTableAction = AllIcons.Nodes.DataTables;
    Icon RebuildVersionJsonFileAction = AllIcons.Actions.Refresh;
    Icon AddBaselineToCurrentFolderAction = AllIcons.Actions.GroupByFile;
    Icon PluginIcon = IconLoader.getIcon("/icons/pluginIcon.svg", VGPluginIcons.class);
}
