# Protocol Research

The [URnetwork protocol](https://ur.io/protocol) can be run by any network operator. The protocol is a decentralized-native, multi-IP, multi-transport protocol (as opposed to a traditional site-to-site tunnel protocol). The goal is to scale to a network of millions of providers per network operator and deliver a best-in-class experience. The "Layer 2" design allows the most sensitive data to be private. However, for transparency and research, anonymized exports of the problem space inputs and outputs are made available. 

## Anonymization technique 

The payout block of the network is 7 days. The data is anonymized per block. All client ids and IP subnet hashes are replaced with simple integer counter values. Additionally city meta data is not included.

## Note to researchers/builders

The URnetwork team would like to allow users to opt-into experimental algorithms via federated network operators. New network operators will be able to participate in the common incentive system via root contracts to reward their providers. Providers can participate across as many network operators as they want. The main app will support the option to enter an alternate network operator domain for all users. See the [New Network Operator beta form]() to receive peering credentials and transfer allocation. We plan to keep this document up to the date with the current (default) algorithm along with experimental directions. For security research please follow the [Vulnerability Disclosure Program](https://ur.io/vdp).


## Problems

| | Performance |
|---|---|
| Current algorithm | URTRANSPORT1 ([transport.go](https://github.com/urnetwork/connect/blob/main/transport.go) and [stream_manager.go](https://github.com/urnetwork/connect/blob/main/transfer_stream_manager.go)) The current approach is focused on accessibility, so that every person in the world can connect. Multi-hop routing is done via TCP transports through a central hop. UDP transports (H3, DNS) are supported but disabled due to real world performance issues in our setup (to be resolved). There is a multi-provider hop with peer-to-peer upgrade called stream that is supported but currently disabled. The goal is to integrate tried and try protocols (WebRTC, XRay, WireGuard) as the stream upgrade. There are two cases to optimize: 1. the next hop does not have a public ip:port, and 2. the next hop does have a public ip:port. We may want to add meta data to the matching algorithm to allow selecting hops with case 2 (speed), or 1+2 (quality). |
| Data sets | This algorithm is most measurable by raw speed and latency tests |

| | Accessibility |
|---|---|
| Current algorithm | UREXTENDER1 ([net_extender.go](https://github.com/urnetwork/connect/blob/main/net_extender.go)) The core network stack (the non-stream transport) supports a N-TLS (N>=2) encryption where each outer encryption uses a self-signed cert for any host name (SNI spoofing) to an intermediary IP, which forwards to either another hop or an end-to-end TLS connection to the network operator domain. Anyone can host an extender on whatever domain they want. All users from a single extender share a common rate limit which may be adjusted on a per-case basis. Extenders get added to a grant list in the protocol, which allocates a percentage of the incentive to all extenders. The [Extender Network beta form]() is the first step to get added to the grant list. The will be able to enter the first hop extender IP and choose a host name in the app (TODO) |
| Data sets | We do not plan on collecting data of extender usage |

| | Matching clients to providers |
|---|---|
| Current algorithm | UR-FP2 ([network_client_location_model.go#FindProviders2](https://github.com/urnetwork/server/blob/main/model/network_client_location_model.go#L2708)) A sampling algorithm that loads a 10x random sample of potential providers from memory and shuffles it proportion to `reliability * client score` to a list of finalist. The fairness of the shuffle versus provider aliasing (Sybil attack) is guaranteed by the property of `sum(reliability) <= 1` per IP subnet. |
| Data sets | Traces of every function call including inputs and selected outputs are here (TODO) |


| | Multi client |
|---|---|
| Current algorithm | UR-MULTI ([ip_remote_multi_client.go](https://github.com/urnetwork/connect/blob/main/ip_remote_multi_client.go)) A heuristic sweep based algorithm to manage a window of providers. Locks traffic into providers of the top available tier. Based on transfer-thresholds and not protocol analysis. |
| Data sets | This runs entirely on the client side and we currently  do not have an option for users to share traces with us (TODO) |


| | Transfer |
|---|---|
| Current algorithm | UR-TRANSFER ([transfer.go](https://github.com/urnetwork/connect/blob/main/transfer.go)) A reliable transfer window tuned for high latency environments. Protocol retransmits into the transfer layer are disabled since the window has reliable delivery. Distributes traffic among available transports based on ranked performance and availability of the transport. |
| Data sets | This runs entirely on the client side and we currently  do not have an option for users to share traces with us (TODO) |


| | IP Egress |
|---|---|
| Current algorithm | UR-IP ([ip.go](https://github.com/urnetwork/connect/blob/main/ip.go)) An implementation of an IP stack that runs in minimal memory. Assumes the communicate to the peer is reliable via transfer, and hence retransmits are optimized. |
| Data set | This runs entirely on the client side and we currently  do not have an option for users to share traces with us (TODO) |


| | Points/Token allocation |
|---|---|
| Current algorithm | UR-PSUB2 ([account_payout_model_plan.go](https://github.com/urnetwork/server/blob/main/model/account_payment_model_plan.go)) Points are allocated from a "subsidy" pool and distributed every 7 days proportional to data transfer "votes", reliability scores, and referrals. The data votes prioritize traffic generated from paid subscribers to  counteract gaming the totals. Multiplier bonuses are applied for certain reliability and community incentives. |
| Data set | The inputs and outputs will be moved on chain as part of the tokenization effort. |


| | Permission |
|---|---|
| Current algorithm | UR-CONTRACT ([subscription_model.go](https://github.com/urnetwork/server/blob/main/model/subscription_model.go)) Transfer between two parties (the initiator and the companion) requires a contract encrypted with the secret key of the destination client. The contract includes a fixed transfer balance held in escrow, and a permission set of the relation between the two parties. The companion can create contracts paired to the initiator for return traffic. Additionally multi-hop paths will send stream open and stream close events to intermediaries. Both the initiator and companion must close the contract after use with the acknowledged byte count. If either side does not close, or there is a disagreement, the contraction resolution process forces a result. If any side reports abuse, future transfer between the two parties is not allowed. |
| Data set | Export of contracts and closure state so that the raw transfer statistics and traffic flows in the network can be analyzed. |


| | Safety |
|---|---|
| Current algorithm | UR-SEC1 ([ip_security.go](https://github.com/urnetwork/connect/blob/main/ip_security.go)) Port block list and IP block list. Does not do protocol inspection. |
| Data set | This runs entirely on the client side and we currently  do not have an option for users to share traces with us (TODO) |



 
