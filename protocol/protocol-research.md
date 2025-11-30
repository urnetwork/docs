# Protocol Research

The [URnetwork protocol](https://ur.io/protocol) can be run by any network operator. The protocol is a decentralized-native, multi-IP, multi-transport protocol (as opposed to a traditional site-to-site tunnel protocol). The goal is to scale to a network of millions of providers per network operator and deliver a best-in-class experience. The "Layer 2" design allows the most sensitive data to be private. However, for transparency and research, anonymized exports of the problem space inputs and outputs are made available. 

## Anonymization technique 

The payout block of the network is 7 days. The data is anonymized per block. All client ids and IP subnet hashes are replaced with simple integer counter values. Additionally city meta data is not included.

## Note to researchers/builders

The URnetwork team would like to allow users to opt-into experimental algorithms. We plan to keep this document up to the date with the current (default) algorithm along with experimental directions. For security research please follow the [Vulnerability Disclosure Program](https://ur.io/vdp). 


## Problems

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
| Current algorithm | UR-TRANSFER ([transfer.go](https://github.com/urnetwork/connect/blob/main/transfer.go)) A reliable transfer window tuned for high latency environments. Protocol retransmits into the transfer layer are disabled since the window has reliable delivery. Distributes traffic amongst available transports based on ranked performance and availability of the transport. |
| Data sets | This runs entirely on the client side and we currently  do not have an option for users to share traces with us (TODO) |


| | IP Egress |
|---|---|
| Current algorithm | UR-IP ([ip.go](https://github.com/urnetwork/connect/blob/main/ip.go)) An implementation of an IP stack that runs in minimal memory. Assumes the communicate to the peer is reliable via transfer, and hence retransmits are optimized. |
| Data set | This runs entirely on the client side and we currently  do not have an option for users to share traces with us (TODO) |


| | Points/Token allocation |
|---|---|
| Current algorithm | UR-PSUB2 ([account_payout_model_plan.go](https://github.com/urnetwork/server/blob/main/model/account_payment_model_plan.go)). Points are allocated from a "subsidy" pool and distributed every 7 days proportional to data transfer "votes", reliability scores, and referrals. The data votes prioritize traffic generated from paid subscribers to  counteract gaming the totals. Multiplier bonuses are applied for certain reliability and community incentives. |
| Data set | The inputs and outputs will be moved on chain as part of the tokenization effort. |


| | Safety |
|---|---|
| Current algorithm | UR-SEC1 ([ip_security.go](https://github.com/urnetwork/connect/blob/main/ip_security.go)) Port block list and IP block list. Does not do protocol inspection. |
| Data set | This runs entirely on the client side and we currently  do not have an option for users to share traces with us (TODO) |



 
