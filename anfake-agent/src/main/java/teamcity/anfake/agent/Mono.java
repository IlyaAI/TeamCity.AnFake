package teamcity.anfake.agent;

import com.intellij.openapi.util.text.StringUtil;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildRunnerContext;

import java.io.File;

public final class Mono {
    private Mono() {}

    public static boolean isEnabled(BuildRunnerContext ctx) throws RunBuildException {
        return "true".equals(ctx.getRunnerParameters().get("Mono"));
    }

    public static File getJit(BuildRunnerContext ctx) throws RunBuildException {
        String monoJit = ctx.getConfigParameters().get("Mono");
        if (StringUtil.isEmptyOrSpaces(monoJit)) {
            throw new RunBuildException("Unable to find config parameter Mono.");
        }
        return new File(monoJit);
    }
}
