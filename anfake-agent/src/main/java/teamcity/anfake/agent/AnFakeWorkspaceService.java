package teamcity.anfake.agent;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.runner.BuildServiceAdapter;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.util.StringUtil;
import jetbrains.buildServer.vcs.VcsRootEntry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Executes 'AnFake [AnFakeExtras]/teamcity-tf.fsx GetSpecific -p &lt;local-path> &lt;version-spec>' command.
 *
 * @author IlyaAI
 */
public final class AnFakeWorkspaceService extends BuildServiceAdapter {
    @NotNull
    @Override
    public ProgramCommandLine makeProgramCommandLine() throws RunBuildException {
        BuildRunnerContext ctx = getRunnerContext();
        AgentRunningBuild build = ctx.getBuild();

        if (build.getVcsRootEntries().isEmpty()) {
            throw new RunBuildException("AnFake requires at least one VCS root to be specified.");
        }

        VcsRootEntry vcsEntry = build.getVcsRootEntries().get(0);
        String tfsUri = vcsEntry.getProperties().get("tfs-url");

        if (StringUtil.isEmptyOrSpaces(tfsUri)) {
            throw new RunBuildException("AnFake requires first VCS root to be TFS root.");
        }

        String executable = AnFake.getExe(ctx);
        List<String> args = new ArrayList<>();
        args.add("[AnFakeExtras]/teamcity-tf.fsx");
        args.add("GetSpecific");
        args.add("Tfs.Uri=" + tfsUri);
        args.add("-p");
        args.add(build.getCheckoutDirectory().getAbsolutePath());
        args.add(build.getBuildCurrentVersion(vcsEntry.getVcsRoot()));

        return createProgramCommandline(executable, args);
    }
}
