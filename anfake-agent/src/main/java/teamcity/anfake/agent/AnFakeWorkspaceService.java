package teamcity.anfake.agent;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.runner.BuildServiceAdapter;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.util.StringUtil;
import jetbrains.buildServer.vcs.VcsRootEntry;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Executes 'AnFake.Integration.TfWorkspacer.exe &lt;server-path> &lt;local-path> Version=&lt;version-spec>' command.
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
        String tfsPath = vcsEntry.getProperties().get("tfs-root");

        if (StringUtil.isEmptyOrSpaces(tfsUri) || StringUtil.isEmptyOrSpaces(tfsPath)) {
            throw new RunBuildException("AnFake requires first VCS root to be TFS root.");
        }

        String executable = getWorkspacerExe();
        List<String> args = new ArrayList<>();
        args.add(tfsPath);
        args.add(build.getCheckoutDirectory().getAbsolutePath());
        args.add("Version=" + build.getBuildCurrentVersion(vcsEntry.getVcsRoot()));
        args.add("Comment=AnFake+TeamCity: " + build.getBuildTypeExternalId());
        args.add("Tfs.Uri=" + tfsUri);

        return createProgramCommandline(executable, args);
    }

    private String getWorkspacerExe() throws RunBuildException {
        File workspacerExe = FileUtil.getCanonicalFile(new File("../plugins/anfake-agent/AnFake.Integration.TfWorkspacer.exe"));
        if (!workspacerExe.exists()) {
            throw new RunBuildException(
                String.format("AnFake.Integration.TfWorkspacer not found. Search path: %s", workspacerExe.getAbsolutePath()));
        }
        return workspacerExe.getAbsolutePath();
    }
}
