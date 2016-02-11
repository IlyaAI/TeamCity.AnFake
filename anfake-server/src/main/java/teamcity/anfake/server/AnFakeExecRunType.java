package teamcity.anfake.server;

import jetbrains.buildServer.requirements.Requirement;
import jetbrains.buildServer.requirements.RequirementType;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.RunType;
import jetbrains.buildServer.serverSide.RunTypeRegistry;
import jetbrains.buildServer.util.StringUtil;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Provides AnFake runner meta-info.
 *
 * @author IlyaAI 17.06.2015
 */
public final class AnFakeExecRunType extends RunType {
    private final PluginDescriptor descriptor;

    public AnFakeExecRunType(
            @NotNull RunTypeRegistry reg,
            @NotNull PluginDescriptor descriptor) {
        this.descriptor = descriptor;
        reg.registerRunType(this);
    }

    @NotNull
    @Override
    public String getType() {
        return "AnFakeExec";
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "AnFake Runner";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Runs AnFake build script";
    }

    @Override
    public PropertiesProcessor getRunnerPropertiesProcessor() {
        return new PropertiesProcessor() {
            public Collection<InvalidProperty> process(Map<String, String> properties) {
                Collection<InvalidProperty> errors = new ArrayList<InvalidProperty>();

                if (StringUtil.isEmptyOrSpaces(properties.get("Targets"))) {
                    errors.add(new InvalidProperty("Targets", "At least one target should be specified"));
                }
                if (StringUtil.isEmptyOrSpaces(properties.get("Wrapper"))) {
                    errors.add(new InvalidProperty("Wrapper", "Wrapper batch file should be specified"));
                }
                if (StringUtil.isEmptyOrSpaces(properties.get("Script"))) {
                    errors.add(new InvalidProperty("Script", "Build script path should be specified"));
                }

                return errors;
            }
        };
    }

    @Override
    public String getEditRunnerParamsJspFilePath() {
        return descriptor.getPluginResourcesPath("editAnFakeExec.jsp");
    }

    @Override
    public String getViewRunnerParamsJspFilePath() {
        return descriptor.getPluginResourcesPath("viewAnFakeExec.jsp");
    }

    @Override
    public Map<String, String> getDefaultRunnerProperties() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("Targets", "Build");
        map.put("Script", "build.fsx");
        map.put("Properties", "");
        map.put("Wrapper", "anf.cmd");
        map.put("Mono", "");

        return map;
    }

    @NotNull
    @Override
    public String describeParameters(@NotNull Map<String, String> parameters) {
        StringBuilder sb = new StringBuilder();
        if ("true".equals(parameters.get("Mono"))) {
            sb.append("mono ");
        }

        sb.append("AnFake.exe ")
            .append(parameters.get("Script"))
            .append(' ')
            .append(parameters.get("Targets"));

        String props = parameters.get("Properties");
        if (!StringUtil.isEmptyOrSpaces(props)) {
            sb.append(' ').append(props);
        }

        return sb.toString();
    }

    @NotNull
    @Override
    public List<Requirement> getRunnerSpecificRequirements(@NotNull Map<String, String> parameters) {
        List<Requirement> spec = new ArrayList<>();

        if ("true".equals(parameters.get("Mono"))) {
            spec.add(new Requirement("Mono", null, RequirementType.EXISTS));
        } else {
            spec.add(new Requirement("DotNetFramework4.0_x86", null, RequirementType.EXISTS));
        }

        return spec;
    }
}
