# AnFake Runner for TeamCity

Provides TeamCity build runner which executes [AnFake](https://github.com/IlyaAI/AnFake) scripts.

## Build

```
mvn package
```

## Install to TeamCity

 1. Copy 'anfake-runner/target/anfake-runner-1.0.zip' to '[\<TeamCity Data Directory\>](https://confluence.jetbrains.com/display/TCD9/TeamCity+Data+Directory)/plugins' directory.
 1. Restart the TeamCity server and check 'Administration > Plugins List' to verify the plugin was installed correctly.

## Setup Build Configuration

 1. Add the first build step with runner 'AnFake Restore'. This step restores NuGet solution-level packages (usually this is just AnFake itself).
 1. Add the second step: 'AnFake Runner'. This step runs AnFake with specified build script (default: build.fsx) and targets (default: Build).

#### TFS

If you use TFS version control then you need some additional setup.

 1. In 'Version Control Settings > VCS checkout mode' select 'Do not checkout automatically'.
 1. Add build step 'AnFake TFS Workspacer' before 'AnFake Restore'. This step checks out the workspace definition file ('.workspace') creates TFS workspace with appropriate mappings and downloads all necessary sources.