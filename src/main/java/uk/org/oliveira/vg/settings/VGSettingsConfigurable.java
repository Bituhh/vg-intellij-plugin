package uk.org.oliveira.vg.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;
import uk.org.oliveira.vg.VGState;

import javax.swing.*;
import java.util.Arrays;

public class VGSettingsConfigurable implements Configurable {

    VGSettingsComponent component;

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "VG";
    }

    @Override
    public @Nullable JComponent createComponent() {
        this.component = new VGSettingsComponent();
        return this.component.getPanel();
    }

    @Override
    public boolean isModified() {
        VGState state = VGState.getInstance();
        return !Arrays.equals(this.component.getTypeSuggestions(), state.typeSuggestions) ||
                !Arrays.equals(this.component.getRolesSuggestions(), state.rolesSuggestions);
    }

    @Override
    public void apply() throws ConfigurationException {
        VGState state = VGState.getInstance();
        state.typeSuggestions = this.component.getTypeSuggestions();
        state.rolesSuggestions = this.component.getRolesSuggestions();
    }
}
