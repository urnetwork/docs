# Router testing notes

We're using this space to collect feedback on how we can improve with running the provider on router hardware.

Below is a table of router and device models, architecture, and notes when running. Please add notes and links to gists/repos of any relevant scaffolding to set up the provider in the section below. Over time we will fold these into [the provider docs](https://docs.ur.io/provider).

## Provider binary

Download the `urnetwork-provider-XXX.tar.gz` from [releases](https://github.com/urnetwork/build/releases) to get binaries for many architectures:

```
linux/
    386
    amd64
    arm
    arm64
    mips
    mips64
windows/
    amd64
    arm64
darwin/
    amd64
    arm64
```

Or use the community docker image:

```
docker pull bringyour/community-provider:latest
docker run -ti bringyour/community-provider:latest --help
```

## Notes

Please submit PRs to edit this.

| Model | Architecture | Notes |
|-------|--------------|-------|
| Raspberry Pi | arm64 | Setting up the provider to run with systemctl works well. It also works to run the provider inside of docker. |
