package teamcity.anfake.server;

import jetbrains.buildServer.requirements.Requirement;
import jetbrains.buildServer.requirements.RequirementType;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.RunType;
import jetbrains.buildServer.serverSide.RunTypeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Provides AnFake Workspace runner meta-info.
 *
 * @author IlyaAI 17.06.2015
 */
public final class AnFakeWorkspaceRunType extends RunType {

    public AnFakeWorkspaceRunType(@NotNull RunTypeRegistry reg) {
        reg.registerRunType(this);
    }

    @NotNull
    @Override
    public String getType() {
        return "AnFakeWorkspace";
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "AnFake TFS Workspacer";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Updates TFS workspace from '.workspace' file and downloads all changes. IMPORTANT! 'VCS checkout mode' MUST BE set to 'Do not checkout automatically'.";
    }

    @NotNull
    @Override
    public String describeParameters(@NotNull Map<String, String> parameters) {
        return "AnFake.Integration.TfWorkspacer.exe %vcsroot.tfs-root% %teamcity.build.checkoutDir%";
    }

    @Override
    public PropertiesProcessor getRunnerPropertiesProcessor() {
        return new PropertiesProcessor() {
            public Collection<InvalidProperty> process(Map<String, String> properties) {
                return Collections.emptyList();
            }
        };
    }

    @Override
    public Map<String, String> getDefaultRunnerProperties() {
        return Collections.emptyMap();
    }

    @Override
    public String getEditRunnerParamsJspFilePath() {
        return null;
    }

    @Override
    public String getViewRunnerParamsJspFilePath() {
        return null;
    }

    @NotNull
    @Override
    public List<Requirement> getRunnerSpecificRequirements(@NotNull Map<String, String> parameters) {
        List<Requirement> spec = new ArrayList<>();
        spec.add(new Requirement("DotNetFramework4.0_x86", null, RequirementType.EXISTS));

        return spec;
    }
}
