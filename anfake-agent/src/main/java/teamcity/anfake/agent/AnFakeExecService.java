package teamcity.anfake.agent;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.runner.BuildServiceAdapter;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine;
import jetbrains.buildServer.messages.serviceMessages.PublishArtifacts;
import jetbrains.buildServer.util.StringUtil;
import jetbrains.buildServer.vcs.VcsRootEntry;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Executes 'AnFake &lt;target> &lt;properties>' command.
 *
 * @author IlyaAI
 */
public final class AnFakeExecService extends BuildServiceAdapter {
    //private static final Logger Log = Logger.getInstance(AnFakeService.class.getName());

    @NotNull
    @Override
    public ProgramCommandLine makeProgramCommandLine() throws RunBuildException {
        File executable;
        List<String> args = new ArrayList<>();

        BuildRunnerContext ctx = getRunnerContext();
        File anfWrapper = AnFake.getWrapper(ctx);
        File anfExe = AnFake.getExe(anfWrapper);

        if (Mono.isEnabled(ctx)) {
            executable = Mono.getJit(ctx);
            args.add(anfExe.getPath());
        } else {
            executable = anfExe;
        }

        args.add(new File(getWorkingDirectory(), getScript()).getPath());
        args.addAll(getTargets());
        args.addAll(getProperties());

        List<VcsRootEntry> vcsRoots = getBuild().getVcsRootEntries();
        if (!vcsRoots.isEmpty()) {
            VcsRootEntry vcsEntry = vcsRoots.get(0);
            args.add("TeamCity.BuildVcsNumber=" + getBuild().getBuildCurrentVersion(vcsEntry.getVcsRoot()));

            String tfsUri = vcsEntry.getProperties().get("tfs-url");
            if (!StringUtil.isEmptyOrSpaces(tfsUri)) {
                args.add("Tfs.Uri=" + tfsUri);
            }
        }

        args.add("TeamCity.BuildCounter=" + getConfigParameters().get("build.counter"));
        args.add("TeamCity.BuildNumber=" + getBuild().getBuildNumber());

        String serverUrl = getConfigParameters().get("teamcity.serverUrl");
        if (!StringUtil.isEmptyOrSpaces(serverUrl)) {
            args.add("TeamCity.Uri=" + serverUrl);
            args.add("TeamCity.BuildId=" + getBuild().getBuildId());
            args.add("TeamCity.BuildTypeId=" + getBuild().getBuildTypeExternalId());
            args.add("TeamCity.CheckoutFolder=" + getCheckoutDirectory().getPath());
            args.add("TeamCity.User=" + getBuild().getAccessUser());
            args.add("TeamCity.Password=" + getBuild().getAccessCode());
        }

        return new SimpleProgramCommandLine(
            ctx.getBuildParameters().getEnvironmentVariables(),
            anfWrapper.getParent(),
            executable.getPath(),
            args);
    }

    @Override
    public void afterProcessFinished() throws RunBuildException {
        File workingDir = AnFake.getWrapper(getRunnerContext()).getParentFile();
        File anfLog = new File(workingDir, "AnFake.log");
        if (anfLog.exists()) {
            getBuild().getBuildLogger().message(
                new PublishArtifacts(String.format("%s=>.teamcity/logs", anfLog.getPath())).asString()
            );
        }
    }

    @NotNull
    @Override
    public BuildFinishedStatus getRunResult(int exitCode) {
        switch (exitCode) {
            case 0:
            case 1:
                return BuildFinishedStatus.FINISHED_SUCCESS;
            case 2:
                return BuildFinishedStatus.FINISHED_WITH_PROBLEMS;
            default:
                return super.getRunResult(exitCode);
        }
    }

    private String getScript() throws RunBuildException {
        String script = getRunnerParameters().get("Script");
        if (StringUtil.isEmptyOrSpaces(script)) {
            throw new RunBuildException("Build script not specified.");
        }
        return script;
    }

    private Collection<String> getTargets() throws RunBuildException {
        String targets = getConfigParameters().get("anfake.targets");
        if (StringUtil.isEmptyOrSpaces(targets)) {
            targets = getRunnerParameters().get("Targets");
            if (StringUtil.isEmptyOrSpaces(targets)) {
                throw new RunBuildException("Target not specified.");
            }
        }

        return StringUtil.splitHonorQuotes(targets);
    }

    private Collection<String> getProperties() throws RunBuildException {
        String props = getRunnerParameters().get("Properties");

        List<String> propsList;
        if (StringUtil.isEmptyOrSpaces(props)) {
            propsList = new ArrayList<>();
        } else {
            propsList = StringUtil.splitHonorQuotes(props);
        }

        for (Map.Entry<String, String> param: getConfigParameters().entrySet()) {
            if (param.getKey().startsWith("anfake.prop.")) {
                propsList.add(
                    String.format("\"%s=%s\"", param.getKey().substring(12), param.getValue())
                );
            }
        }

        return propsList;
    }
}
