package uk.org.oliveira.vg.settings;

import com.intellij.openapi.actionSystem.ActionToolbarPosition;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import uk.org.oliveira.vg.VGState;

import javax.swing.*;
import javax.swing.plaf.PanelUI;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class VGSettingsComponent {
    private final List<String> typeSuggestions = new ArrayList<>();
    final private JBList<String> typeSuggestionsList = new JBList<>();
    final private ToolbarDecorator typeSuggestionsDecorator = ToolbarDecorator.createDecorator(this.typeSuggestionsList);

    private final List<String> rolesSuggestions = new ArrayList<>();
    final private JBList<String> rolesSuggestionsList = new JBList<>();
    final private ToolbarDecorator rolesSuggestionsDecorator = ToolbarDecorator.createDecorator(this.rolesSuggestionsList);

    public JPanel getPanel() {
        VGState state = VGState.getInstance();

        // Types
        this.typeSuggestions.addAll(Arrays.asList(state.typeSuggestions));
        this.typeSuggestionsList.setListData(state.typeSuggestions);

        // Roles
        this.rolesSuggestions.addAll(Arrays.asList(state.rolesSuggestions));
        this.rolesSuggestionsList.setListData(state.rolesSuggestions);

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0));
        return FormBuilder.createFormBuilder()
                .addLabeledComponent("Type suggestions", getTypeSuggestionsPanel(), true)
                .addLabeledComponent("Roles suggestions", getRolesSuggestionsPanel(), true)
                .getPanel();
    }

    private JPanel getTypeSuggestionsPanel() {
        this.typeSuggestionsDecorator.setToolbarPosition(ActionToolbarPosition.RIGHT);
        this.typeSuggestionsDecorator.setAddAction(a -> {
            String input = Messages.showInputDialog("Which type would you like to add to suggestions?", "New Type", Messages.getQuestionIcon(), null, new InputValidator() {
                @Override
                public boolean checkInput(@NlsSafe String inputString) {
                    return !inputString.isEmpty();
                }

                @Override
                public boolean canClose(@NlsSafe String inputString) {
                    return !inputString.isEmpty();
                }
            });
            this.typeSuggestions.add(input.toUpperCase());
            this.typeSuggestionsList.setListData(this.typeSuggestions.toArray(String[]::new));
        }).setRemoveAction(a -> {
            int i = this.typeSuggestionsList.getSelectedIndex();
            this.typeSuggestions.remove(i);
            this.typeSuggestionsList.setListData(this.typeSuggestions.toArray(String[]::new));
        }).disableUpDownActions();

        return this.typeSuggestionsDecorator.createPanel();
    }

    private JPanel getRolesSuggestionsPanel() {
        this.rolesSuggestionsDecorator.setToolbarPosition(ActionToolbarPosition.RIGHT);
        this.rolesSuggestionsDecorator.setAddAction(a -> {
            String input = Messages.showInputDialog("Which role would you like to add to suggestions?", "New Role", Messages.getQuestionIcon(), null, new InputValidator() {
                @Override
                public boolean checkInput(@NlsSafe String inputString) {
                    return !inputString.isEmpty();
                }

                @Override
                public boolean canClose(@NlsSafe String inputString) {
                    return !inputString.isEmpty();
                }
            });
            this.rolesSuggestions.add(input);
            this.rolesSuggestionsList.setListData(this.rolesSuggestions.toArray(String[]::new));
        }).setRemoveAction(a -> {
            int i = this.rolesSuggestionsList.getSelectedIndex();
            this.rolesSuggestions.remove(i);
            this.rolesSuggestionsList.setListData(this.rolesSuggestions.toArray(String[]::new));
        }).disableUpDownActions();

        return this.rolesSuggestionsDecorator.createPanel();
    }

    public String[] getTypeSuggestions() {
        return this.typeSuggestions.toArray(String[]::new);
    }

    public String[] getRolesSuggestions() {
        return this.rolesSuggestions.toArray(String[]::new);
    }
}
