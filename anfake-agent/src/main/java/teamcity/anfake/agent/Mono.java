package teamcity.anfake.agent;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildRunnerContext;

public final class Mono {
    private Mono() {}

    public static boolean isEnabled(BuildRunnerContext ctx) throws RunBuildException {
        return "true".equals(ctx.getRunnerParameters().get("Mono"));
    }

    public static String getJit(BuildRunnerContext ctx) throws RunBuildException {
        String monoJit = ctx.getConfigParameters().get("Mono");
        if (monoJit == null) {
            throw new RunBuildException("Unable to find config parameter Mono.");
        }
        return monoJit;
    }
}
