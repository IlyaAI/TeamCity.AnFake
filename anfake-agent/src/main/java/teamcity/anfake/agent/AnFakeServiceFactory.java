package teamcity.anfake.agent;

import jetbrains.buildServer.agent.AgentBuildRunnerInfo;
import jetbrains.buildServer.agent.BuildAgentConfiguration;
import jetbrains.buildServer.agent.runner.CommandLineBuildService;
import jetbrains.buildServer.agent.runner.CommandLineBuildServiceFactory;
import org.jetbrains.annotations.NotNull;

public final class AnFakeServiceFactory implements CommandLineBuildServiceFactory, AgentBuildRunnerInfo {
    @NotNull
    @Override
    public CommandLineBuildService createService() {
        return new AnFakeService();
    }

    @NotNull
    @Override
    public AgentBuildRunnerInfo getBuildRunnerInfo() {
        return this;
    }

    @NotNull
    @Override
    public String getType() {
        return "AnFake";
    }

    @Override
    public boolean canRun(@NotNull BuildAgentConfiguration agentConfiguration) {
        return true;
    }
}