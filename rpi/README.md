
# URnetwork on Raspberry Pi

URnetwork can integrate with Raspberry Pi devices to provide and route traffic on the network.

There are several popular adblocking and VPN projects for Raspberry Pi. URnetwork works with these projects to add additional networking options.

[PiHole](#pi-hole)

[PiVPN](#pivpn)


## Provide

Follow the steps in [the provider doc](https://docs.ur.io/provider) to build a binary provider for Linux arm64.

TODO integrate to start automatically


## Route traffic on the network

Follow the steps in [the tether doc](https://docs.ur.io/tether) to build a binary tether for Linux arm64.

TODO integrate to start automatically and route traffic

TODO steps to set up a local Wifi AP that routes traffic to URnetwork


## Pi-hole

[Pi-hole](https://pi-hole.net/) is a great choice for a network-wide adblocker. The following steps set up URnetwork to run inside a Pi-hole deployment to route traffic post-blocking.


## PiVPN

[PiVPN](https://www.pivpn.io/) is a good project to run a self-managed Wireguard server. It is possible to send all egress traffic for connected clients to URnetwork following the steps below.

If you want to route only some clients to URnetwork, URnetwork has a Wireguard server mode that can be deployed side-by-side to create custom configurations that route to URnetwork. Follow the steps below.
