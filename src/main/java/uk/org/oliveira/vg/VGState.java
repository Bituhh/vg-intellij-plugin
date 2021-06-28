package uk.org.oliveira.vg;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@State(
        name = "uk.org.oliveira.vg.VGState",
        storages = {@Storage("VGPlugin.xml")}
)
public class VGState implements PersistentStateComponent<VGState> {
    public String[] typeSuggestions = {"BOOLEAN", "INTEGER", "TEXT", "JSON", "JSONB", "CHARACTER VARYING", "CHARACTER", "VOID"};
    public String[] rolesSuggestions = {};

    public static VGState getInstance() {
        return ServiceManager.getService(VGState.class);
    }

    public void addTypeSuggestion(String suggestion) {
        suggestion = suggestion.toUpperCase();
        List<String> tempList = new ArrayList<>(Arrays.asList(this.typeSuggestions));
        if (!tempList.contains(suggestion)) {
            tempList.add(suggestion);
            this.typeSuggestions = tempList.toArray(String[]::new);
        }
    }

    public void addRoleSuggestion(String suggestion) {
        List<String> tempList = new ArrayList<>(Arrays.asList(this.rolesSuggestions));
        if (!tempList.contains(suggestion)) {
            tempList.add(suggestion);
            this.rolesSuggestions = tempList.toArray(String[]::new);
        }
    }

    @Override
    public @Nullable VGState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull VGState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
