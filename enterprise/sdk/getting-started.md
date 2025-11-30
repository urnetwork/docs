# URnetwork SDK Getting Started


## Supported Versions

The following platform versions are supported by the URnetwork SDK:

- Android SDK 26+ (8)
- iOS 16.6+
- macOS 13.5+
- Windows 10+ (coming soon)
- Chrome Extension v3+ (coming soon)

## Packages

The following packages are used in the apps:

- URnetworkAndroidClient.aar - for Kotlin/Java Android apps
- URnetworkAppleClient - Swift package for Swift iOS/macOS apps
- URnetworkWindowsClient.dll (coming soon) - C++ package for C++ Windows apps
- URnetworkWebClient.js (coming soon) - JS package for Chrome Extensions and hybrid UIs

## Overview

Usage of the URnetwork SDK is done in two parts:

1. Add the supporting service (Android), network extension (iOS/macOS), or network component (Windows, coming soon) to the app. We will collectively call these "the tunnel". This base types will come from the URnetwork Client SDK. There are separate steps for each platform to add the correct permissions and declarations to the package meta data (see below).

2. Integrate the URnetwork Client SDK into the app which will create a `Device` instance to manage the tunnel. The device is wrapper around a native library that allows the app code to interact with the tunnel and receive events in real time. For Android, the SDK is packaged as a `aar` file with `x86`, `x64`, `arm32`, and `arm64` architectures (the build can decide which of these to ultimate keep or filter out from the final app). For iOS, the SDK is packaged as a Swift package that wraps a binary `XCframework` with `arm64` architectures.  For Windows (coming soon) the SDK is packaged as a `dll`. For Chrome Extension (coming soon), the SDK is packaged as a `js` that wraps a `wasm` binary. 

See the [URnetworkSDK.md](URnetworkSDK.md) doc for the configuration and events available for the tunnel.


## Android meta data

Reference application: https://github.com/urnetwork/android

Key permissions in `AndroidManifest.xml`:

```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />

<service
    android:name=".MyVpnService"
    android:permission="android.permission.BIND_VPN_SERVICE"
    android:exported="true">

    <intent-filter>
        <action android:name="android.net.VpnService"/>
    </intent-filter>
    <meta-data android:name="android.net.VpnService.SUPPORTS_ALWAYS_ON"
        android:value="true"/>

</service>
```

These permissions are for *client only* and not provider. There are additional power management permissions that help for providers that are not included here.

## iOS meta data

Reference application: https://github.com/urnetwork/apple

The application will need to create a new Network Extension in the Apple Developer Portal, with a new bundle ID. For example, "my.bundle.extension". The network extension will need to configure with capabilities:

```
Network Extension
- DNS Proxy
- DNS Settings
- Packet Tunnel
```

## Android Basic configuration

On Android, because the tunnel shares memory with the app, the device should live inside the application, and the application should implement the `UrNetworkEnabled` interface to expose the device to the service. The app should define this code:

```
class MyApplication : Application(), UrNetworkEnabled {

	let urNetworkState = UrNetworkState()

	init {
		urNetworkState.device = Sdk.newDeviceLocalWithDefaults(
            networkSpace,
            clientJwt,
            deviceDescription,
            deviceSpec,
            appVersion,
            instanceId,
            false
        )
	}

	// URnetworkEnabled
	fun getUrNetworkState(): UrNetworkState {
		return urNetworkState
	}
}
```

Define a service that extends the `UrNetworkTunnelService` base.

```
class MyVpnService : UrNetworkTunnelService() {
}
```

An additional `URnetworkAndroidClient` aar will be provided that contains these types.


## iOS Basic configuration

On iOS, the app should create a new `RemoteDevice` to connect and manage the tunnel.

```

var newDeviceError: NSError?

let networkSpace = SdkNetworkSpaceUrNetworkMain()
// each memory instance should have a new instance id
let instanceId = SdkNewId()

let device = SdkNewDeviceRemoteWithDefaults(
    networkSpace,
    clientJwt,
    instanceId,
    &newDeviceError
)
```

An additional `URnetworkAppleClient` swift package will be provided that contains these types, with `StartTunnel` and `StopTunnel` functions, which require the network extension bundle ID from your app and assist with configuring and launching the tunnel.

Inside the network extension, extend the `UrNetworkPacketTunnelProvider` base.

```
class MyPacketTunnelProvider: UrNetworkPacketTunnelProvider {
}
```

## Authentication

Each `Device` needs to be instantiated with a JWT for its network. For enterprise users, the network and JWT should be created on the backend using the enterprise API and sent to the client. The JWT should be refreshed every 30 days unless configured otherwise. The enterprise API can also expire JWTs and get a list of active sessions per network.

See the [API docs](https://bringyour.com/api).


### Migration and Dogfooding Plan

The official URnetwork applications will migrate to the `URnetwork*Client` packages so they will be examples and tests of how to use fully use the client SDKs. This will be rolled out to an active user base for testing.

