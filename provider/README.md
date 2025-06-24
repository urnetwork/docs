
# URnetwork Providers

URnetwork is a decentralized VPN where egress capacity is provided by anyone who wants to participate in the network, called a provider. A provider connects to a network space, which is a distributed set of servers that manage and faciliate the network using the URnetwork [API](https://docs.ur.io/api) and [connect protocol](https://github.com/urnetwork/protocol). Users authenticate with the network space to use the network for security, privacy, anonymity, and content acccess.

The main URnetwork space follows a set of trust and safety rules and an economic model that keeps the network safe for providers, and rewards providers for participating. The network is free to use with a data cap, where users can become a supporter to get a higher data cap and priority speeds. The main URnetwork space supports email, SMS, Google, and Apple authentication.

[Trust and Safety Policy](https://docs.ur.io/trust-and-safety/trust-and-safety)

[Economic Model](https://docs.ur.io/economic-model/economic-model)


## Provide using an app

URnetwork builds apps for popular consumer platforms. Our goal is to support as much existing hardware as possible and make it "one tap" to get set up as a provider. When you install an app, select "Provide while disconnected" from the settings to enable always-on provider. Otherwise, the provider is only active while you are connected to the network.

[URnetwork apps](https://ur.io/app)


## Provide using a binary

The `provider` binary compiles to run on Linux, Windows, macOS. For information on specific IoT and router platforms see [Raspberry Pi](https://docs.ur.io/rpi), [EdgeOS](https://docs.ur.io/edgeos), and [RouterOS](https://docs.ur.io/routeros).

The binary runs as any user and does not require special permissions.

The binary can be compiled and deployed from source, or deployed with our pre-built docker container. Both methods are documented below.


### Provider binary

#### Windows

You can install the provider using the following command in your Command Prompt (cmd.exe) or PowerShell:

```
powershell -c "irm https://raw.githubusercontent.com/urnetwork/connect/refs/heads/main/scripts/Provider_Install_Win32.ps1 | iex"
```

In case if you wish to uninstall the provider, run the following:

```
powershell -c "irm https://raw.githubusercontent.com/urnetwork/connect/refs/heads/main/scripts/Provider_Uninstall_Win32.ps1 | iex"
```

#### Linux

Run the following to install the provider:

```bash
curl -fSsL https://raw.githubusercontent.com/urnetwork/connect/refs/heads/main/scripts/Provider_Install_Linux.sh | sh
```

If you wish to uninstall it:

```bash
curl -fSsL https://raw.githubusercontent.com/urnetwork/connect/refs/heads/main/scripts/Provider_Uninstall_Linux.sh | sh
```

#### macOS

You'll have to build the provider binary from source, as of now. Please refer to the next section.

#### Build from source

Run the following commands to build the `provider` binary from source. You will need `go` version of at least 1.23[^1] and `git`.

```
mkdir urnetwork
cd urnetwork
git clone https://github.com/urnetwork/connect
git clone https://github.com/urnetwork/protocol
cd connect/provider
go build
# the provider binary is now at ./provider
```

#### To initialize a provider on your network, follow the steps below.

1. Log into your network in the [web UI](https://ur.io/?auth). You can create a new network here also.
2. Once logged in, there is an button in the dialog called "Copy an Auth Code". Tap that button to copy a time-limited auth code. You'll use this in the next step to generate a local JWT file.
3. `./provider auth`. Paste the auth code at the prompt to enter the auth code.
4. You should see output that says "Saved auth to ~/.urnetwork/jwt". Now any provider that runs in your user will be authenticated to your network. You can copy the `$HOME/.urnetwork` dir to new environments to quickly set up the provider.

#### To run a provider on your network, run the command below.

```
./provider provide
# you should see the message "Provider XXX started"
```

This runs in the foreground and provides egress capacity to the network until killed. Payouts are made to the wallet set up in the network. You can use [one of the apps](https://ur.io/app) to set up your wallet.

#### To run a provider all the time in the background, follow the steps below.

*Linux*

If you have `systemd`, then the installer script should have automatically configured a systemd service when you installed the provider. The service is `urnetwork`, so you can use the usual `systemctl` commands to control it:

```bash
systemctl --user start urnetwork    # Start
systemctl --user stop urnetwork     # Stop
systemctl --user enable urnetwork   # Start on login
systemctl --user disable urnetwork  # Disable start on login
```

*macOS*

On modern macOS, background processes are managed with `launchd` using the command `launchctl` while the logs get appended to `/var/log/system.log`. The following steps set up a basic launchd unit to run the provider binary using the configuration at `$HOME/.urnetwork`.

1. Download the [latest launchd template from Github](https://github.com/urnetwork/connect/provider/launchd)
2. Edit the `/path/to/provider` to the actual provider path
3. Edit the `$USER` to the user you want to run as
4. Make sure for that user, `$HOME/.urnetwork` is in place
5. `sudo cp <EDITED urnetwork-provider.plist> /Library/LaunchAgents/urnetwork-provider.plist`
6. `sudo launchctl load /Library/LaunchAgents/urnetwork-provider.plist`
7. `sudo launchctl start /Library/LaunchAgents/urnetwork-provider.plist`
8. Verify the unit is running with `tail -f /var/log/system.log | grep -i provider`. You should see the message "Running on port XX"
9. You're all set!

*Windows*

When you run the installation script, it will ask you whether you want to add the provider program to system startup programs list. If you do, it will start in background when your system boots up.
It is possible to run the provider manually in the background:

```
powershell -NoProfile -WindowStyle Hidden -Command "Start-Process urnetwork.exe -ArgumentList 'provide' -WindowStyle Hidden"
```

### Provider binary on multiple platforms

If you want to build the binary for multiple platforms, use the Makefile.

```
mkdir urnetwork
cd urnetwork
git clone https://github.com/urnetwork/connect
git clone https://github.com/urnetwork/protocol
cd connect/provider
go build
# the provider binaries are compiled into binaries at ./build/$OS/$ARCH
# build/darwin/amd64/provider
# build/darwin/arm64/provider
# build/linux/amd64/provider
# build/linux/arm64/provider
# build/linux/arm/provider
# build/linux/386/provider
# build/windows/amd64/provider
# build/windows/arm64/provider
```

Choose the `provider` binary for your OS and architecture.

Note: `darwin` is for macOS

Note: if you have a modern Intel or AMD processor, choose `amd64`. If you have a modern Apple or Qualcomm processor, choose `arm64`. Otherwise for Linux IoT and router platforms, run `uname -m` to find your architecture.

Note: you can download pre-built binaries from the [nightly releases](https://github.com/urnetwork/connect)

### Provider container

We publish an image for following the warp convention to make running an up-to-date provider easier. We continuously update the image with 4 service blocks, where the last block (`g4`) has been tested the longest and hence is the most stable.

| Block | Latest |
|-------|--------|
| `g1` | [bringyour/community-provider:g1-latest](https://hub.docker.com/r/bringyour/community-provider) |
| `g2` | [bringyour/community-provider:g2-latest](https://hub.docker.com/r/bringyour/community-provider) |
| `g3` | [bringyour/community-provider:g3-latest](https://hub.docker.com/r/bringyour/community-provider) |
| `g4` | [bringyour/community-provider:g4-latest](https://hub.docker.com/r/bringyour/community-provider) |


Note: the images are built for amd64 and arm64 architectures only.

To initialize with the latest, most stable version, run the command below.

```
docker run bringyour/community-provider:g4-latest --mount type=bind,source=$HOME/.urnetwork,target=/root/.urnetwork auth
```

To run the latest, most stable version, run the command below.

```
docker run bringyour/community-provider:g4-latest --mount type=bind,source=$HOME/.urnetwork,target=/root/.urnetwork provide
```

You can adapt the commands from the section [To run a provider all the time in the background, follow the steps below](#to-run-a-provider-all-the-time-in-the-background-follow-the-steps-below) to use `docker run bringyour/community-provider:g4-latest --mount type=bind,source=$HOME/.urnetwork,target=/root/.urnetwork` instead of `/path/to/provider`.

To update your provider, just run `docker pull bringyour/community-provider:g4-latest` . A new g4 is published about once a month. You can [see the changelogs in the releases section of the Github repo](https://github.com/urnetwork/connect).


## Main Network Space

The main URnetwork space is below. Anyone can host a network space by deploying and maintaining the services in the server repo. The main network space is continuously deployed with the warp tools, where the continuous versions are committed as tags on GitHub for transparency.

| Server Name | Purpose |
|-------------|---------|
| api.bringyour.com | API server |
| connect.bringyour.com | Connect protocol server |
| extender.bringyour.com | Public extender server. Users can run their own extenders in a secure location to have their own IP to access the network, which is benefitial for some network access situations. |

The provider tools use the main network space by default.


[^1]: [Download and install Go](https://go.dev/doc/install)
