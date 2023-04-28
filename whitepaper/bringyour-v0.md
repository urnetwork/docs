
# BringYour White Paper [very rough draft]

| Version | Change notes |
|---------|--------------|
| Version 1 | Initial draft |

| Resource | URL |
|----------|-----|
| Website | [bringyour.com](https://bringyour.com) |
| Admin | iOS, Android |
| Network identities | <code>you.bringyour.network</code> globally unique IPv6 subnet |

Aliases not used today but may be used in the future:
- (World Wide Access) ww.dev
- vpn.dev


## Mission

Be the fastest way to solve network security and privacy needs for all people.

- Connect people and services globally with a peer-to-peer network that co-mingles normal web traffic with secure transport. Work across international boundaries and regional restrictions.
- Give visibility and control on the digital footprint of your data: where it goes, what it is, what it means, actions to limit or stop.
- Allow seamless collaboration of networks and identity.


## Business plan


BringYour is an overlay ISP that adds security and privacy value to network usage. The business model is pay per use (GiB transfer), with an emphasis on web standards and peer to peer to keep fixed costs low, and a freemium core. The technology is build with a secure core managed by the company that manages users, contracts, payment, and the network; and extenders that handle peer to peer and provide additional IPs and hostnames. All peer to peer traffic is secured end to end with TLS hence the extenders can facilitate the traffic without eavesdropping.

There are many underutilized network resources today. BringYour will help make those more efficiently used to delivery value among people.

BringYour is built on a network to facilitate web standard end to end encryption between:
- endpoint to endpoint
- endpoint to subnet
- subnet to endpoint
- subnet to subnet

Product development is to start with consumer mobile devices, and then expand into prosumer and business networks. The product roadmap is:

Phase 1 Consumer: decentralized VPN with distributed providers and extenders, to compete in the same segment as the top VPN apps on the app stores, etc. Uses excess network capacity of people which gives the most realistic egress and extensive reach. Build on peer to peer web standards that also allows freemium sharing with self, friends and family. e.g. share your network with self and family. Packet inspection on each packet sent and API allows visibility and control of data. Administered from a mobile app with emphasis on speed of setup and ease of use.

Phase 2 Pro: add additional device suppport and features relevant to prosumers and business use cases. Example features are access control management and super peer. Super peer exposes endpoints and subnets from a host to self, friends, and family. This focuses on prosumer and smb use cases like local servers, smart home and iot devices. Key innovation is to transparently map a local subnet to a peer to peer connection from user to host to the resource, and to maintain a directory of resources per user. Example additional devices are desktop, router, and key open source integrations.

Phase 3 B2B: focus on business networks that use peer to peer routing to make connections to endpoints as fast as possible. Focus on SSO, SOC2, ease of setup, and interconnectivity and compliance use cases. Be the Slack of networks. 

Phase 1 Pricing:

Beat low usage cost versus popular consumer VPNs ($10/month)

The standard pay per use price is $.1 per gb. The monthly revenue for different levels of sustained transfer are below:

A typical 4k video stream is 20mbps. A typical user watches 4k video 0.5-2 hours per week. 1 hour of 4k video costs $.9

100mbps $3.2k
1gbps $32k
2.5gbps $83k ($1m arr)
10gbps $324k


The total data transferred per month on the internet in 2016 is estimated around 100 exabytes (0.000308641975312ebps or 308640gbps) [https://en.m.wikipedia.org/wiki/Zettabyte_Era]   

A contract is for 4gb of data ($.4). Bring Your must process around 11k contracts per hour per 10gbps assuming a contract is on average 10% filled.

The MVP of BringYour is to deliver a faster, cheaper, more capabable consumer VPN experience than exists today.


## Overview

Local internet egress is a popular feature with many use cases [^1]. Typically this is served by a trusted party who provides in (supposed) confidence. The goal of this project is to enable an efficient market to provide local internet egress using modern web standards, while maximizing choice for speed and anonymity. This will allow more efficient usage of excess capacity that exsits in bandwidth allocation. It may also help to anonymize traffic for servers to the ISP by adding noise to a bandwidth allocation. WebRTC gives the user control to choose between speed (peer to peer) and their own trusted confidential parties (TURN servers).

This market is built on a common protocol described in the protocol section. The protocol connects a currency contract for data transfer with a provider to a peer to peer connection to enable the data transfer, including closing the contract and disputes.

The market is a layer 2 network that spans potentially several layer 1 blockchains. Each contract on the market enables data transfer between a user (client) and service provider (server). The contract reserves a market price of a currency for the maximum contract GiB value from a market wallet into an escrow wallet to ensure that, when closed, each contract can be fully paid. The transfer into the market wallet may incur a network fee and an exchange fee. The fees and rates are exposed in the market stats API, and all transaction on the market can reference a fixed market stat to ensure price transparency.

For example, the network sets 1GiB of transfer at $0.20 with a network fee of 5%. A user sends 1XCH to their transfer wallet, 0.05XCH is taken by the market as a fee, and 0.95XCH is deposited into their market wallet. Each contract moves XCH from the market wallet into the escrow wallet equal to a market rate, (contract GiB value * $0.20)/(current dollar exchange of XCH). The value reserved in escrow is called the escrow value of the contract. When the contract closes, the agreed consumed prorated escrow value is transferred to the service provider and the rest returned to the market wallet, or a dispute is initiated following the dispute process described in the dispute section.

As soon as a contract is closed, the respective balances are updated and the contract meta data is deleted. The network retains no historical record of past contracts.


## Extenders

BringYour is built on end to end encryption between:
- endpoint to endpoint
- endpoint to subnet
- subnet to endpoint
- subnet to subnet

Extenders facilitate the end to end connection as either a peer to peer connection or a tunneled connection.

Peer to peer traffic may need turn servers and/or protocol translation to facilitate in different network environments. Additionally a decentralized network benefits from additional ips and/or hostnames. The security model of extenders is TLS, where clients only trust the core apis with certificates from Bring Your, and peer to peer traffic is always encrypted end to end with TLS.

There are two types of extenders: ip and url. 

IP extenders sit on a public IP and relay https/wss api traffic to api.bringyour.com. The identity of the extender is the public ip. Additionally they facilitate peer to peer traffic.

Url extenders sit on a public web server and do a wrapped tls relay of traffic from some path to api.bringyour.com. For example, https://foobar.com/bringyour would use the wrapped tls protocol to connect to api.bringyour.com and identify itself as the extender. Additionally they facilitate peer to peer traffic from a hostname, foobar.com .

An extender must be registered with a user (user.bringyour.network). Also an extender must open incoming tcp traffic to port 443 and tcp and udp traffic to the peer to peer ports.

An extender can also run as a free extender, which will allow facilitating non-paid traffic. Users have an option to choose free extenders. By default the client will choose free extenders when there is no remaining balance, and choose paid extenders otherwise.

When a client connects to Bring Your, it must first choose an extender. The extended can be set manually to a known good extended. Otherwise this is done via the "find closest extender" api route, using the root api route. The root api route is api.bringyour.com by default, but it can be changed to use a known good extended for example. The client will typically maintain a list of N good extenders, and switch between them as needed.


## Joining

Becoming a user or service provider on the market is tied to creating a VPN connection. The WW VPN uses an ECDA key pair for identity management, by default stored on per computer user at ~/.ww/key or in the local secure storage for the application.

Joining the network starts by creating a root account, which is associated with a network (you.bringyour.network). The root account and additional user accounts are managed with standard SSO (Apple, Google), email+password, and MFA.

Additional users and devices can be added through a secret phrase process. A candidate will generate a secret phrase with the API associated with their local key, and share that secret phrase with a root account user. The root account enters the secret phase to associate the key with the account.

All administration is done via the admin portals on web, iOS, and Android.


## Wallets

Each user of the market has four wallets per layer 1 blockchain: 

1. Transfer wallet
2. Market wallet
3. Escrow wallet
4. Dispute wallet

The only real wallet address on the blockchain will is the transfer wallet. The currency will be held there until settlement or transfer out. The other wallets are maintained as part of the layer 2 meta data to enable fast contract handling and reduce blockchain fees.

Currency are put into the transfer wallet either by direct transfer from the blockchain or ACH purchase. In direct transfer currency minus network fees are moved into the market wallet. ACH purchases exchange dollar to currency at the market rate and put currency into the market wallet minus network fees and exchange fees. Currency can be transferred out of the market wallet using the set transfer API, which may incur a blockchain fee. 

The sum of tokens across all users of `(market wallet + market transfers out + escrow wallet + dispute wallet)` remains constant during usage of the market. No fees are applied during usage of the market. 


## Protocol

Communication to the platform is via HTTPS. Communication to peer is via WebRTC data channel. Data transfer follows the QUIC protocol where the transmit buffered is indexed, and the frame of transfer is a message. Messages in the buffer are processed in order of acknowledged index.

The reference for the peer to peer transfer protocol is the WW vpn project, which has implementations in Go.

The client maintains a list of servers identified by public key. The arbiter of the protocol is the API. For any number, client requests a new contract with the server. The arbiter may deny the request for any reason (insufficient funds, ban, etc). On success, the arbiter returns a signed contract Id using the secret from the server session. If not already connected, client initializes an ice connect via the arbiter using the public key. The arbiter may deny the ice connect if there are no active contracts between parties. Client maintains a peer connection with message buffer. The first message is start contract with the signed contract id. The client sends messages until contract expires or done. Client sends an end contract message. Client send arbiter a close contract with the stats. Only one open contract per connection is allowed.

[Ice connect shares public key of client to server. Can ban public key at any time.￼]

The client must send a open contract message before starting transfer on a contract. The client must end a contract before reaching a transfer exception in the contract, such as exceeded data. The client sends the close contract stats to the API and also a close contract message to the server. When the server receives the close contract message, it sends close contract stats to the API. The server may keep the data channel open and will expect a new open contract message before transfer.

The protocol supports IPv4 and IPv6. If IPv4 or IPv6 is not routable on the server, data transfer will still be charged. The arbiter may track servers by the amount of data left unused on contracts to find routing errors. Future extensions may allow the API to surface these quality rankings.



## Security

Only public networks are allowed as destinations. Sending data to addresses in private networks is not allowed. Attempting to send to an address in a private network will result in an immediate close of the contract with a dispute status of malicious client. Correct server code will enforce this, and correct client code will filter traffic to private networks on the client side.

Listing of private networks for IPv4 and IPv6 are below.


### IPv4

| IPv4 Private Subnet [^2] | Description |
|---------------------|-------------|
| 0.0.0.0/8 | |
| 10.0.0.0/8 | |
| 127.0.0.0/8 | |
| 169.254.0.0/16 | |
| 172.16.0.0/12 | |
| 192.0.0.0/24 | |
| 192.0.2.0/24 | |
| 192.88.99.0/24 | |
| 192.168.0.0/16 | |
| 198.18.0.0/15 | |
| 198.51.100.0/24 | |
| 203.0.113.0/24 | |
| 224.0.0.0/4 | |
| 240.0.0.0/4 | |
| 255.255.255.255 | |

[^2]: https://datatracker.ietf.org/doc/html/rfc5735


### IPv6

| IPv6 Private Subnet [^3] [^4] | Description |
|---------------------|-------------|
| fc00::/7 | |
| ::/128 | |
| ::1/128 | |
| ff00::/8 | |
| fe80::/10 | |

[^3]: https://datatracker.ietf.org/doc/html/rfc4193
[^4]: https://datatracker.ietf.org/doc/html/rfc4291








## Contract Disputes

The WW arbiter connects client to server with a maximum transfer (value) for each contract. The value of the contract is locked in the escrow where only the used portion of the contract is transferred from client to server. At the end of the contract, the client and server both send a summary to the arbiter of the contract according to the contract protocol. Typically a contract is a small value, and the client automatically negotiates follow on contracts as needed. A dispute happens if at the end of the contract the client and server send a different summary to the arbiter.

A dispute can happen for several (non exclusive) reasons:

- a malicious client
- a malicious server
- a client does not follow the protocol (disappear)
- a server does not follow the protocol (disappear)
- technical errors in either the client or server that are not the fault of either

The design of disputes deters repeated abuse by removing the incentives from each of these cases. In a dispute, the value of the contract remains locked in dispute escrow and registered as a dispute in both the client and server wallets. The server is not paid for any transferred data. No further contracts between a client and server are allowed while a dispute is active between them.

A dispute resolution process assesses the pending disputes on a regular basis, and makes a decision:

- was it a malicious client
- was it a malicious server
- client did not follow protocol
- server did not follow protocol
- was it a technical error and no fault

If a malicious client, the client is permanently banned from the network and the escrow amount is released to the server. If a malicious server, the server is permanently banned from the network and the escrow amount is released to the client. If it was no fault, the value reported by the server is is deducted from the escrow account and released to the server, and the remaining value (if any) is released to the client.



# Identity

Joining the network gives you an identity on the network. Inter-network routing maintains exposes the identity of the sender. The identity is known as a network name, `you.bringyour.network`.


# Service provider accounts payable

BringYour is  a saas product. It sells gib transfer on a metered pay as you go package based on $ per gib. These are stored in a balance of gbio per account. 

Customers can pay using dollars (stripe, Apple Pay, android pay, etc) or crypto (xch or sol). Wwa maintains a balance per customer in gib available backed by a blend of assets - $ and crypto. The current available gib is given based on the blend and current market prices. Transfer out of the assets is not allowed however in the future it may be possible to transfer out or in a by a gibio coin.

When making transfer contracts on the network, the payout depends on the backing asset. For $ backing a credit is added to the vendor for each context based on gib value. For crypto asset the value at the time of contract is converted to gib and that amount is credited.

Ap runs twice a month and converts the credits to a payout. Each crypto is paid to a linked wallet for each network. If there is no linked wallet for a network, a notice is sent to the vendor and the vendor must add a wallet within 30 days or forfeit the payout. For $ the credited amount is converted to the primary payout wallet (xch) at a determined market price. Then the converted amount is distributed to the linked wallet.

Conversion of $ to crypto for payout follows a human in the loop process. Ap is notified that a dollar amount needs to be converted to coins on a network for each network. Ap does the conversion based on the best available market price. Ap enters the net converted amount (minus network and exchange fees) for each network. The payout then happens based on the net converted amount. The payout network is chosen to minimize the network and exchange fees. For example defi xch currently has 0 network and exchange fees using peer to peer offers.


# Validation

The usefulness of the network increases with provider stability. The network incorporates incentives for providers to maximum uptime and service level called validation. The network incorporates an active and passive assessment of stability.

Active assessment is spot checking done as a user would against an internet target owned or authorized by the network. The test measures include throughout, connection statistics, and total time to complete. The tests create real load and pay the provider as any user would.

Passive measurements happen as part of the contract. These include the percentage of contract accepted and percentage of contracts that end with an error.

The active and passive statistics are combined for 90 days to create a validation score. Providers with higher scores rise to the top of matchmaking and recommendation in the network. 

Providers who keep their system perfectly up for 90 days will have the highest validation score.


# Leases

A user can star a provider to add them to the top of their personal matching and recommendation. Additionally a user can commit a minimum contract amount per month to a provider. This functions as a star with an additional incentive to the provider. The monthly amount credits the usage of that provider up to that amount, and over usage will be charged as normal. When the lease amount is not fully used, the amount is paid out to the provider prorated by the validation score. For example a provider with a .5 validation score will get only .5 of the committed lease amount.

The lease allows users to give incentive to the providers they care about to remain stable . The provider can see how much they are losing by not achieving maximum validation score.



# Synthetic load

The network can drive synthetic contracts to any provider. These contracts test the provider to network link and are used for stress testing the environment. Synthetics are built into the protocol.

















