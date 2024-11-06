# Tether: Improving Compatibility of URnetwork with the WireGuard Protocol

[WireGuard](https://www.wireguard.com/) is one of the most popular open-source VPNs due to its simple yet fast nature. In this article, we'll discuss how we integrated WireGuard into URnetwork to create a smoother and more convenient experience for users already familiar with its setup and benefits. The source code of the project can be found [here](https://github.com/bringyour/connect/tree/tether/tetherctl).

## WireGuard Crash Course

First, let's go over the basics of WireGuard and how it works in order to understand what we expect from our solution. 

WireGuard works by adding a logical (virtual) network interface, which acts as a tunnel between endpoints (`peers`). This interface can be controlled through normal means like `ip-tables` and `if-config`. A WireGuard client controls multiple interfaces, where each interface has a private and public key that is used when encrypting/decrypting traffic going through it. A peer is another WireGuard client's interface which we wish to communicate with. 

In an interface, peers are defined by their public key, a local IP, and an endpoint through which the peer can be reached. When a packet is sent through a WireGuard interface, the destination is used to figure out which peer corresponds to the desired target. Then, the packet is encrypted with that peer's public key and is sent to the endpoint of the peer. When receiving a packet, it is decrypted with our interface's private key and authenticated for a peer. Then, the source IP and port of the packet are remembered as the endpoint of the authenticated peer. Once decrypted, the payload of the packet is the plaintext packet the peer is sending through the tunnel. Additionally, each peer is allowed to use only a certain range (or list) of local tunnel IPs, and if the plaintext packet is received outside of this range for a peer, then it is simply discarded as this peer is not allowed to send in this range.

For example, let's define two configurations, one for a WireGuard interface which will act as a server:
```ini
[Interface]
ListenPort = 33336
PrivateKey = 4HOYXaS0mtLH9ZChsPaDQ3W/L7Z/rrchr8CMDqZGgXg=

[Peer]
PublicKey = 10NguWHSLJ0tUOr4AkbTtOEYHoagq1KH/PJSIJ3SwFs=
AllowedIPs = 192.168.90.1/32

[Peer]
PublicKey = vi2iosrlXoDeZT08aXlq4AxXUNKO04NDuEeCw2Z7sD0= 
AllowedIPs = 192.168.90.2/32
```

And another for a WireGuard interface which will act as a client:
```ini
[Interface]
PrivateKey = 2JB7HwgWaOnmCpKA9TlMLTLdIYeNOZMnrmy3YI7JYnk=
ListenPort = 29043

[Peer]
PublicKey = UESAy9LgT3PR77Tl1RCnHuj+ZtFvYMeWahnTIEhEvXM=
AllowedIPs = 0.0.0.0/0
Endpoint = 88.42.58.36:33336
```

So, the client wants to send its packets through the server to appear as if the server is sending them (\**VPN magic*\*). As we can see, configuration files in WireGuard are quite simple. They are `ini` formatted, and each interface has a private key (used to decrypt packets) and a port (through which packets will be sent). Each peer has a public key (with which outgoing packets meant for that peer will be encrypted) and a list of allowed IPs (only packets from these IPs will be accepted from this peer). 

In the above example, the client has a key pair `<10NguWHSLJ0tUOr4AkbTtOEYHoagq1KH/PJSIJ3SwFs=, 2JB7HwgWaOnmCpKA9TlMLTLdIYeNOZMnrmy3YI7JYnk=>` and is allowed to only send packets through its interface using IP `192.168.90.1`. When the client wants to send a packet to the server (with key pair `<UESAy9LgT3PR77Tl1RCnHuj+ZtFvYMeWahnTIEhEvXM=, 4HOYXaS0mtLH9ZChsPaDQ3W/L7Z/rrchr8CMDqZGgXg=>`), it will encrypt it with the server's private key and then send it to the known `endpoint` of the server (`88.42.58.36:33336`). When the server sends a response back to the client, it will encrypt it with the client's private key and send it to the last known endpoint of the client (which is recorded when a packet is received from the client). The client is configured to accept packets from all IPs from the server, thus making it route all of its traffic through the server. For a more detailed explanation of how WireGuard works, check this [page](https://www.wireguard.com/).

## Desired Solution

Now that we know how WireGuard works, let's define what we want to achieve. Simply put, we want **an intuitive way for users to connect existing WireGuard clients to the URnetwork**.

This entails that we want URnetwork providers to function as routers of the traffic of WireGuard clients of already existing distributions, e.g., your phone's WireGuard app. From a user perspective, a user can choose a provider that will act as a router for their WireGuard traffic and set up a WireGuard tunnel in any WireGuard distribution, allowing them to route all of their traffic in that tunnel through the chosen provider.

This article focuses on how we developed a proof of concept solution that allows anyone to create a WireGuard interface that uses a URnetwork client to add peers, create configuration files for existing WireGuard distributions, and, most importantly, route the traffic of peers.

## Task Division

Let's divide in a more concrete manner the features we expect from our solution and try to address each separately. The main goal is we want our solution to be able to use a single command to create a WireGuard interface, configure it, and make it accessible for users to add their own peers to the interface. Additionally, we want the ability to:
1. manage peers in an interface (list, add, remove, etc.);
2. get a configuration file that a peer can use in an existing WireGuard distribution;
3. manage interfaces (list, add, remove, etc.);
4. configure an interface (change its keypair, listening port, local/tunnel address space);
5. change the availability of an interface (bring it up or down).

Moreover, some other quality-of-life features which we can add are a way to:
- generate `<public, private>` key pairs;
- view the configuration of an interface;
- save the configuration of an interface so it can be started up more easily;
- run commands before or after an interface is started or stopped, similar to how some common WireGuard tools allow.

## Problem 1: WireGuard Implementation

The first dilemma we encounter when trying to build our solution is that we need to pick a WireGuard implementation that URnetwork clients will run. We can try using existing WireGuard modules; for example, since version 5.6, the Linux Kernel has built-in WireGuard support, making for an optimized kernel-level integration. However, using this approach will make our solution platform-dependent or, at the least, not available on all platforms. Additionally, we will need a way to make the WireGuard network interfaces route their traffic through a URnetwork client.

Another approach that could prove successful is to use an existing implementation and make it run in userspace, as then we abstract from the logical (virtual) interfaces that WireGuard uses, and hence we can use it on any platform. This is the approach we go for. As URnetwork is developed in `go`, we need a WireGuard implementation in `go` that a URnetwork client can run. Luckily, the developers of WireGuard already have a version which we can use: [wireguard-go](https://github.com/WireGuard/wireguard-go).

This version, however, is very bloated as it has implementations for several platforms (Linux, Windows, macOS, FreeBSD, and OpenBSD), which all use a logical interface. Thus, we will need to remove this platform dependence and re-write it to work in userspace. The way we go about this is to first leave only one implementation (in our case the Linux one since the machine we use is running Ubuntu). Then, as URnetwork runs fully in userspace, we can replace all of the logic for handling packets through a logical interface with a URnetwork client (we use a local user NAT for the proof of concept). 

Here, we encounter a problem: how to route the packets as they need to be sent through URnetwork with the public IP of the server machine, but they are received with the (local) tunnel address of the WireGuard interface. The solution is simple—a NAT table that stores which pair of `IP:port` to map to which `tunnel IP`. So, for each outgoing packet, we save its `tunnel IP` in the NAT table using the `public IP` of the server and the `source port` of the packet as the key. Then, when we receive a packet, we can check the NAT table (using `destination IP` and `port` as the key) and reverse the IP back to the `tunnel IP`. Since we are modifying each packet, we need to also recalculate the checksums to make it a valid packet. It should be noted that the current implementation only works with TCP and UDP packets, as most traffic is sent using TCP and UDP in the modern Internet. Additionally, ICMP packets are ignored as they do not have a transport layer and hence a port for the NAT table. This is not detrimental, as ICMP packets are not strictly necessary for basic Internet functionality.

The last task remains to have a way to interact with our userspace WireGuard implementation programmatically instead of the default way of textual configurations. Luckily, this was straightforward, as the official WireGuard project provides a [cross-platform userspace implementation](https://www.wireguard.com/xplatform/#interface) (CPI) for consistency in configuration and management of WireGuard interfaces. Here, we should note that, to stay consistent, in code, we refer to WireGuard interfaces as `devices` everywhere, as that is how the original implementation, the CPI, and most WireGuard tools refer to them. A device is an abstraction from the logical interface that they are built on. This abstraction is also nice for us, as we no longer use a logical interface, so there is no confusion in the naming. Hence, from now on, we use interface and device interchangeably.

Going back to the programmatic configuration, we make use of the [`wgctrl`](https://pkg.go.dev/golang.zx2c4.com/wireguard/wgctrl#section-readme) module, which enables control of WireGuard devices on multiple platforms. More specifically, we use the [`wgtypes`](https://pkg.go.dev/golang.zx2c4.com/wireguard/wgctrl/wgtypes) package, as it has objects that conform to the CPI. So, we hence transition from using the original textual configurations to using `wgtypes.Config` objects. Finally, let's quickly run through an example to showcase how to create and configure a WireGuard device using our solution.

```go
	// 1. set up logger with desired log level (available - LogLevelVerbose, LogLevelError, LogLevelSilent)
	logger := logger.NewLogger(logger.LogLevelVerbose, " (userspace-wireguard)")

	// 2. configure the public IPs of the server (it has no ipv6 so we set it to nil)
	var publicIPv4 net.IP = net.IPv4(1, 2, 3, 4)
	var publicIPv6 net.IP = nil

	// 3. create a tun device that uses userspace sockets to send packets
	utun, err := tun.CreateUserspaceTUN(logger, &publicIPv4, &publicIPv6)
	if err != nil {
		logger.Errorf("Failed to create TUN device: %v", err)
		os.Exit(1)
	}

	// 4. create the wireguard device
	device := device.NewDevice(utun, conn.NewDefaultBind(), logger)
	logger.Verbosef("Device started")

	// 5. get the private key of the server and a peer that wishes to communicate with the server
	privateKeyServer, err := wgtypes.ParseKey("4HOYXaS0mtLH9ZChsPaDQ3W/L7Z/rrchr8CMDqZGgXg=")
	if err != nil {
		logger.Errorf("Invalid server private key provided: %w", err)
		os.Exit(1)
	}
	publicKeyPeer, err := wgtypes.ParseKey("10NguWHSLJ0tUOr4AkbTtOEYHoagq1KH/PJSIJ3SwFs=")
	if err != nil {
		logger.Errorf("Invalid peer public key provided: %w", err)
		os.Exit(1)
	}

	// 6. set configuration (CPI)
	port := 33336
	config := wgtypes.Config{
		PrivateKey:   &privateKeyServer,
		ListenPort:   &port,
		ReplacePeers: true,
		Peers: []wgtypes.PeerConfig{
			{
				PublicKey:         publicKeyPeer,
				ReplaceAllowedIPs: true,
				AllowedIPs:        []net.IPNet{{IP: net.IPv4(192, 168, 90, 1), Mask: net.CIDRMask(32, 32)}},
			},
		},
	}
	if err := device.IpcSet(&config); err != nil {
		logger.Errorf("Failed to Set Config: %v", err)
		os.Exit(1)
	}
	
	// 7. start up the device
	device.AddEvent(tun.EventUp)

	//... wait for termination signal ...
	
	// Finally, clean up
	device.Close() 
```

*Voilà*, we have achieved our initial goal. We now have a way to programmatically interact with a WireGuard interface without the need to run any commands, and the **interface runs fully in userspace through URnetwork**, so it can be used on any platform. The latest version of the code can be found packaged in the [`userspace-wireguard`](https://github.com/bringyour/userspace-wireguard) module.

## Problem 2: Managing Interfaces

An easy way to enable developers to manage interfaces is through a command line interface. Thus, for our tool, we make it so that the features described in [Task Division](#task%20division) are available as commands in a CLI. Additionally, since WireGuard uses `ini` configuration files to set up interfaces, we can extend these files with some of our features to make it simpler for developers to use. Extending the existing configuration file format has the advantage of making our custom ones viable configuration files for standard WireGuard clients.

### Device Configuration Files

Let's explain how a configuration file is structured. A device can be configured using an `ini` formatted file similar to the ones used by the [`wg`](https://www.man7.org/linux/man-pages/man8/wg.8.html) kernel module. The configuration file can have two sections:
* `[Interface]` - contains the configuration for the device/interface (mandatory, exactly one). The following options are available:
  * `Address` - a comma-separated list of IPs in CIDR notation to be assigned to the interface (can appear multiple times).
  * `ListenPort` - the port on which the interface listens.
  * `PrivateKey` - the private key of the interface (mandatory).
  * `PreUp`, `PostUp`, `PreDown`, `PostDown` - bash commands which will be executed before/after bringing up/down the interface (can appear multiple times). The special string `%i` is expanded to the interface name.
  * `SaveConfig` - a `boolean` value to save the configuration of the interface when being brought down. Any changes made to the device while the interface is up will be saved to the configuration file.
* `[Peer]` - contains the configuration for a peer (optional, can appear multiple times). The following options are available:
  * `PublicKey` - the public key of the peer (mandatory).
  * `AllowedIPs` - a comma-separated list of IPs in CIDR notation that the peer is allowed to access through the interface (can appear multiple times).
  * `Endpoint` - the public IP of the server where the peer can be contacted.

As expressed before, we have kept the configuration file in the same form as normal WireGuard configuration files. This reduces the learning curve for developers who are already familiar with WireGuard, allowing them to set up and manage devices through URnetwork with minimal extra effort. From the above options, the only non-standard ones are in the interface section, namely, `Address`, `PreUp`, `PostUp`, `PreDown`, `PostDown`, and `SaveConfig`. All of these options are also available in [`wg-quick`](https://www.man7.org/linux/man-pages/man8/wg-quick.8.html), which allows for users on Linux to set up a WireGuard interface in a simple manner, further reducing the overhead of using our solution.

### CLI Commands

The commands available in the command line interface aim to mimic the workflow that developers normally go through to set up a WireGuard interface. This includes many familiar commands from [`wg-quick`](https://www.man7.org/linux/man-pages/man8/wg-quick.8.html) and the [`wg`](https://www.man7.org/linux/man-pages/man8/wg.8.html) kernel module, such as `up`, `down`, `gen-priv-key`, `gen-pub-key`, `get-config`, and `save-config`. Each command corresponds to a specific action in managing WireGuard configurations, making it intuitive for users who have experience with traditional WireGuard setups \[[1](https://markliversedge.blogspot.com/2023/09/wireguard-setup-for-dummies.html), [2](https://www.vps-mart.com/blog/how-to-set-up-wireguard-on-VPS), [3](https://blog.frehi.be/2022/06/11/setting-up-wireguard-vpn-with-ipv6/)\].

However, since our implementation operates in userspace rather than directly in the kernel, some differences arise. Additionally, while kernel-based WireGuard allows for lower latency due to its direct access to network interfaces, a userspace implementation can offer flexibility across different platforms and is often easier to extend with custom logic, such as choosing the depth of logs provided or operating devices programmatically.

The list of commands that are currently supported by the CLI tool can be found below:
```
Tether cli.

Usage:
    tethercli add [--dname=<dname>] [--log=<log>] [--ipv4=<ipv4>] [--ipv6=<ipv6>]
    tethercli remove [--dname=<dname>]
    tethercli up [--dname=<dname>] [--config=<config>]
    tethercli down [--dname=<dname>] [--config=<config>] [--new_file=<new_file>]
    tethercli get-config [--dname=<dname>] [--config=<config>]
    tethercli save-config [--dname=<dname>] [--config=<config>] [--new_file=<new_file>]
    tethercli gen-priv-key 
    tethercli gen-pub-key --priv_key=<priv_key>
    tethercli get-device-names
    tethercli get-device [--dname=<dname>]
    tethercli change-device [--dname=<dname>] [--lport=<lport>] [--priv_key=<priv_key>]
    tethercli add-peer --pub_key=<pub_key> [--dname=<dname>] [--endpoint_type=<endpoint_type>]
    tethercli remove-peer --pub_key=<pub_key> [--dname=<dname>]
    tethercli get-peer-config --pub_key=<pub_key> [--dname=<dname>] [--endpoint_type=<endpoint_type>]

Options:
    --dname=<dname>                   WireGuard device name. Keeps the last set value [initial: bywg0].
    --config=<config>                 Location of the config file in the system [default: /etc/tetherctl/].
    --log=<log>                       Log level from verbose, error, and silent [default: error].
    --ipv4=<ipv4>                     Public IPv4 address of the device.
    --ipv6=<ipv6>                     Public IPv6 address of the device.
    --new_file=<new_file>             Location where the updated config should be stored. If not specified, the original file is updated.
    --endpoint_type=<endpoint_type>   Type of endpoint to use for peer config [default: ipv4].
    --lport=<lport>                   Port to listen on for incoming connections.
    --pub_key=<pub_key>               Public key of a WireGuard peer (unique).
    --priv_key=<priv_key>             Private key of a WireGuard device.
```

The CLI tool can be found in the [`tetherctl`](https://github.com/bringyour/connect/tree/tether/tetherctl) module of URnetwork. Let's also go through a small example of how to set up a device with a configuration file using the new CLI. We will use the same keys and configuration options as in the [WireGuard Crash Course](#WireGuard%20Crash%20Course) section.

So, for the server, we define the following configuration stored in a file called `tbywg0.conf`:
```ini
[Interface]
Address = 192.168.90.0/24
ListenPort = 33336
PrivateKey = 4HOYXaS0mtLH9ZChsPaDQ3W/L7Z/rrchr8CMDqZGgXg=
SaveConfig = true

[Peer]
PublicKey = 10NguWHSLJ0tUOr4AkbTtOEYHoagq1KH/PJSIJ3SwFs=
AllowedIPs = 192.168.90.1/32
```

Then, we can start `tetherctl` in CLI mode using the `go run . cli` command in a terminal. Next, we are presented with the integrated CLI where we can run commands after a provided input prompt `>`. What we will do is:
1. Create a device called `tbywg0` (it has to match the name in the configuration file).
```sh
> add --dname=tbywg0 --log=v
```
2. Then, we bring up the device with verbose logging, providing the path to the above-created configuration file. 
```sh
> up --config=/root/connect/tetherctl/
```

**That's it!** We have a running device with a peer added to it. To make sure, we can see the device's current configuration we can use the following command:
```sh
> get-config --dname=tbywg0 --config=/root/connect/tetherctl/
```
Or if we want to see the underlying `wgtypes.Device` object, we can use:
```sh
> get-device --dname=tbywg0
```

Additionally, we can get the configuration file that the peer needs using:
```sh
> get-peer-config --dname=tbywg0 --pub_key=10NguWHSLJ0tUOr4AkbTtOEYHoagq1KH/PJSIJ3SwFs=
```

The output we get after running the command is:
```ini
# Config for public key "10NguWHSLJ0tUOr4AkbTtOEYHoagq1KH/PJSIJ3SwFs="
[Interface]
PrivateKey = __PLACEHOLDER__ # replace with your private key
Address = 192.168.90.1/32
DNS = 1.1.1.1, 8.8.8.8

[Peer]
PublicKey = UESAy9LgT3PR77Tl1RCnHuj+ZtFvYMeWahnTIEhEvXM=
AllowedIPs = 0.0.0.0/0
Endpoint = 88.42.58.36:33336
```

The output from this command can be used in any WireGuard app to connect to the device and route all of our traffic through the newly created device. The only thing that should be changed is the peer's private key, as the device does not have it. *Hooray!* We have achieved our goal of making a **fully interactable userspace WireGuard distribution that runs on URnetwork**. But how can users obtain their peer's configuration, as they cannot access the CLI?

## Problem 3: Availability for Users

As this is a proof of concept, we decide to go for an API. The API can be exposed through a simple HTTP server that users can send requests to. Thus, we add several endpoints, each serving a distinct purpose, including:  
 * `/peer/add/:endpoint_type/*pubKey` (request method: `POST`) - adds a peer with the given public key to the WireGuard device. The endpoint type specifies which type of endpoint the peer wants to communicate with the server on (available values: any, ipv4, ipv6, domain). The request has no body. The config that the peer can use to set up its own WireGuard client is returned here (essentially runs the CLI `add-peer` command).  
 * `/peer/remove/*pubKey` (request method: `DELETE`) - removes a peer with the given public key from the WireGuard device (essentially runs the CLI `remove-peer` command). The request will succeed even if the peer does not exist, meaning that if a request is accidentally repeated, the peer will only be removed once.  
* `/peer/config/:endpoint_type/*pubKey` (request method: `GET`) - returns the configuration of a peer with the given public key (essentially runs the `get-peer-config` command) and the specified endpoint type of the server (available values: any, ipv4, ipv6, domain). The configuration can be used in any existing WireGuard distribution.  

Additionally, we have added several commands so that a developer can start the API for any of its WireGuard devices through the CLI:  
```
Tether cli.

Usage:
    tethercli start-api [--dname=<dname>] [--api_url=<api_url>]
    tethercli stop-api
    
Options:
    --api_url=<api_url>               API url [default: localhost:9090].
```

The `--api_url` option defines the address and port where the API will be accessible. By default, it is set on `localhost:9090`, but if you want to expose it to be accessible through your public IP, you can set `--api_url=:9090`. As an example, let's continue from where we left off in the last section. Let's say the developer wants users to be able to access the device through the API. Then, they can run:  
```sh
> start-api --dname=tbywg0 --api_url=:9090
```

Then, a user with a peer defined by the keypair `<vi2iosrlXoDeZT08aXlq4AxXUNKO04NDuEeCw2Z7sD0=, WDxx84eTgEdpZ+ykGHSNWEtYKzFAXshtPSLLCUbr4XE=>` needs to make a `POST` request with the following URL: `http://88.42.58.36:9090/peer/add/any/vi2iosrlXoDeZT08aXlq4AxXUNKO04NDuEeCw2Z7sD0=`  

The response of the request is the configuration file that can be used by the user to create a tunnel with the device (after putting in their peer's private key). In our case, this is:  
```sh
# Config for public key "vi2iosrlXoDeZT08aXlq4AxXUNKO04NDuEeCw2Z7sD0="
[Interface]
PrivateKey = __PLACEHOLDER__ # replace with your private key
Address = 192.168.90.2/32
DNS = 1.1.1.1, 8.8.8.8
	
[Peer]
PublicKey = UESAy9LgT3PR77Tl1RCnHuj+ZtFvYMeWahnTIEhEvXM=
AllowedIPs = 0.0.0.0/0
Endpoint = 88.42.58.36:33336
```

Additionally, as we can see, the peer is given the next available IP (`192.168.90.2`) in the local address space, as defined by the device's configuration. Currently, choosing the next available IP is done in a brute-force manner by iterating through all IPs in the local tunnel subrange, checking if they are used by another peer, and if not, using it. This process can be greatly improved, however, prioritizing efficiency is not the primary goal of this proof of concept.  

Well, anyway, *job well done*! We now **provide users with a way to add their peers to a device and get a configuration file** that can be used in any WireGuard distribution to set up a tunnel.

## Problem 4: Single Command

We move on to the final problem that needs to be tackled: creating a single command for setting up a device with a desired configuration where users can add their peers to the device easily. Thankfully, having solved the previous problems makes this one quite straightforward to address.

Since our devices are fully in userspace, represented using `wgtypes.Device` objects, we can create a workflow that accommodates our needs, i.e., creates a device, then configures it using our custom/extended configuration files, and finally starts the API. This is the *culmination* of the project—using our previous solutions to accomplish the current task. The way we decide to solve this is by using the [`Builder`](https://en.wikipedia.org/wiki/Builder_pattern) design pattern as it solves the following problems:  
- *How can a class (the same construction process) create different representations of a complex object?* For us, this is the `device` object.  
- *How can a class that includes creating a complex object be simplified?* For us, this is the `configuration` process.  

The design pattern comprises a `Director` object, which manages different `Builder` objects that are the separate "workflows." For now, we have implemented only one `Builder` type, called a `DefaultBuilder`, which implements the `DeviceBuilder` interface:  
```go
type IDeviceBuilder interface {
	CreateDevice(dname string, configPath string, logLevel int) error          // creates and brings up a device
	StopDevice(dname string, configPath string) error                          // brings down a device
	ManageEndpoint(opts EndpointOptions) error                                 // add/remove/reset endpoint
	ManagePeer(dname string, opts PeerOptions) (string, error)                 // add/remove peer from device (and get config if requested)
	StartApi(dname string, apiURL string, errorCallback func(err error)) error // start API for device
	StopApi(dname string) error                                                // stop api of device
	StopClient()                                                               // closes tether client
}
```

Then, we add a command that runs through our workflow (using the functions of the `DeviceBuilder` interface):  
```sh
Tether control.

Usage:
    tetherctl default-builder --dname=<dname> --config=<config> --api_url=<api_url> [--log=<log>]
    
Options:
    --dname=<dname>         Wireguard device name.
    --config=<config>       Location of the config file in the system.
    --api_url=<api_url>     API url.
    --log=<log>             Log level from verbose(v), error(e), and silent(s) [default: error].
```

The workflow is as follows:  
1. Use the provided log level for the logger.  
2. Add an endpoint (use the public IP of the server).  
3. Create the device by the provided name using the given configuration file (here we also start the device).  
4. Start an API server at the provided URL (now users can add their peers and obtain configuration files).  
5. Wait for an interrupt signal.  
6. Stop the API, device, and WireGuard client.  

Finally, we create an automatic way to run this command on the startup of the developer's system. This will allow them to create a WireGuard device and then have it always running in the background. To limit the scope of this project, we consider only Linux. So we create a service that runs the command "`/etc/tetherctl/tetherctl default-builder --dname=%i --config=/etc/tetherctl/ --api_url=:9090`" on startup. To set up the service on your system, check the [README](https://github.com/bringyour/connect/tree/tether/tetherctl) of the project. There you can also find further notes on the previous sections as well as the source code of the project.  

So once again, *hurrah*! We solved another problem: **automating the process of creating and configuring a device through a single command**.

## Summary

This brings the end of the project, so thank you for following this article. We quickly summarize what we did and provide an extra section where we discuss pitfalls and future work.

This project aimed at integrating WireGuard into URnetwork to create a smoother and more convenient experience for users already familiar with its setup. Using a configuration file format similar to [`wg-quick`](https://www.man7.org/linux/man-pages/man8/wg-quick.8.html) and the [`wg`](https://www.man7.org/linux/man-pages/man8/wg.8.html) kernel module makes the setup intuitive, letting developers get devices and peers up and running with a minimal learning curve. The CLI commands mimic standard WireGuard functionalities and workflows while running fully in userspace through URnetwork. This integration provides a seamless experience for users while remaining flexible for developers. The source code of the project can be found [here](https://github.com/bringyour/connect/tree/tether/tetherctl).

## Pitfalls and Future Work

This project was not without its **challenges**. Initially, the exact solution wasn’t entirely clear, as we were still figuring out the requirements and the specific limitations tied to different platforms.  

The problems we defined in this article were ordered from more detrimental to the design of the final solution to less detrimental. Unfortunately, during the development of the solution, we did not tackle them in the given order; hence, we were forced to redesign the solution and refactor the code base several times.

At first, we planned to rely on the Linux kernel WireGuard module and had bash scripts to handle parts of the setup automatically. However, we quickly hit a compatibility barrier due to differences across platforms and their corresponding WireGuard implementations, which pushed us toward a userspace solution. Shifting to this approach required several rounds of refactoring, impacting everything from CLI commands to the API and the service structure. Each shift brought us closer to the final solution.

A lot of time was spent at the start researching WireGuard’s features to decide exactly what our solution should include. This helped shape our configuration format and clarified what the final setup should look like. This early research gave us confidence and a foundation to move forward with a better sense of direction.

**Looking to the future**, there are a few areas we want to expand. First, as previously mentioned, we can improve the selection of the next allowed IP, which would make the process more efficient. Also, while currently both IPv4 and IPv6 are supported, only IPv4 is tested in the full setup, so IPv6 functionality may require additional testing and adjustments to ensure full functionality within the system.

Second, our solution should be integrated more tightly with URnetwork, ideally allowing users to route their traffic through URnetwork providers rather than the current approach of using a user local NAT. We also want to give users the ability to pick specific providers to route their traffic, adding more control based on things like location or bandwidth needs.

Ultimately, we envision this project as part of the URnetwork app, where users can generate configurations compatible with any WireGuard distribution. In the URnetwork app, users should also be able to directly manage routing options and provider choices, making it easier to get everything set up the way they need. With these improvements, we’re aiming for a more adaptable, all-in-one tool for the next stages of the project as a standalone feature.