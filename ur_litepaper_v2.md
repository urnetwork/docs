# $UR Token Litepaper

**URnetwork Protocol**
*Own Your Network. Own Your Privacy.*

Version 2.0 | February 2026

---

## Executive Summary

URnetwork is decentralized privacy infrastructure. Instead of trusting a VPN company with your traffic, URnetwork distributes it across a global network of independent providers. No single node sees both source and destination. The architecture makes surveillance technically meaningless.

The $UR token coordinates this network. It enables access, rewards contributors, and aligns incentives — without the inflationary mechanics that have killed every comparable DePIN project.

$UR has a fixed supply of 1,000,000,000 tokens. All exist at genesis. There is no inflation. Contributor rewards come from network usage fees and a finite pre-allocated pool, not token printing. A 2.5% protocol absorption mechanism permanently removes tokens from circulation with every transaction. Dual-sided staking locks additional supply by giving users discounts and providers reward multipliers.

The result: a token designed for durability, not hype.

---

## The Problem

### VPNs Require Trust That Gets Broken

Every VPN asks users to trust them. Trust that they don't log traffic. Trust that their servers aren't compromised. Trust that the company behind them won't sell data or cave to government pressure.

This trust is routinely violated. VPN providers have been caught logging traffic, selling user data, and cooperating with surveillance programs — all while marketing "no-log" policies. The centralized architecture makes this inevitable. When all traffic flows through servers owned by one company, that company is a single point of failure, compromise, and coercion.

### DePIN Tokenomics Create Death Spirals

Most Decentralized Physical Infrastructure Networks follow the same path to failure. They launch tokens, pay contributors with newly minted emissions, attract speculators instead of users, and watch prices collapse when sell pressure overwhelms thin demand.

The pattern is consistent. 84.7% of tokens launched in 2024-2025 trade below their TGE price within 12 months. The median drawdown is 71%. Projects with real technology and real teams saw their tokens crater because tokenomics designed for growth hacking don't survive contact with markets.

The core failure: paying contributors with newly printed tokens creates constant sell pressure. When prices fall, projects must increase emissions to maintain dollar-denominated rewards. This accelerates the death spiral.

---

## The Solution

### Trustless Privacy Architecture

URnetwork eliminates trust by distributing traffic across independent providers worldwide. The protocol uses multi-hop routing — traffic passes through multiple providers before reaching its destination. No single provider sees both the user's identity and the content they're accessing.

Key architectural properties:

**Egress-only design.** Providers only handle outbound traffic at the final hop. They never see the originating user.

**End-to-end encryption.** All traffic is encrypted with multiple layers. Each hop can only decrypt its own routing instructions, not the payload.

**Stateless coordinators.** The matching infrastructure that connects users to providers retains no session data. There is nothing to subpoena.

**Performance Auction.** The protocol continuously selects the fastest, most reliable paths for each session. If one path degrades, traffic seamlessly shifts to the next-best option without dropping the connection.

**Default Safe.** The protocol embeds trust and safety at the protocol level — blocking abuse-prone ports, enforcing encrypted-only protocols, rate-limiting connections — so providers can participate safely without exposure to legal liability.

### Sustainable Token Economics

$UR takes a fundamentally different approach to DePIN tokenomics. Instead of printing tokens to pay contributors, the protocol uses a fixed supply with three value accrual mechanisms that create genuine demand tied to real usage.

---

## How the Token Works

### The Economic Loop

```
Users & Enterprises pay USD for network access
        ↓
Network Operator (BringYour, Inc.) converts USD → $UR on open market
        ↓
Protocol receives $UR → 2.5% permanently absorbed → 97.5% distributed
        ↓
Contributors earn $UR for providing bandwidth
        ↓
Contributors can hold, stake for multipliers, or sell
        ↓
(Sold tokens return to market → available for next usage cycle)
```

Enterprise clients pay in fiat via standard SaaS contracts. They never touch crypto. The Network Operator handles the conversion, creating consistent buy pressure from real commercial demand — not speculation.

### Network Pricing

Network usage is priced in USD. The amount of $UR required fluctuates based on market price:

- If $UR price increases → fewer tokens needed per transaction → less selling pressure per dollar of revenue
- If $UR price decreases → more tokens needed per transaction → more buy pressure per dollar of revenue

This creates natural price stabilization without algorithmic intervention.

---

## Value Accrual Mechanisms

### 1. Protocol Absorption — 2.5% Supply Reduction Per Transaction

Every time $UR flows through the protocol for network usage, 2.5% is transferred to a protocol-controlled absorption address and permanently removed from active circulation.

This is not a performative buyback-and-burn. It's triggered by every transaction, correlated 1:1 with actual usage, and visible on-chain in real time.

**Impact at scale:**

| Annual Revenue | $UR Absorbed | % of Total Supply |
|---|---|---|
| $1M | 833K | 0.08% |
| $10M | 8.3M | 0.83% |
| $50M | 41.7M | 4.17% |

At higher revenue levels, tokens cycle through circulation multiple times per year, amplifying the absorption effect beyond single-cycle estimates.

The 2.5% rate was designed in collaboration with Chris Jenkins (Director, Pocket Network Foundation), who designed the Shannon tokenomics model. It's high enough to create meaningful deflation at scale, low enough to preserve contributor economics.

### 2. Dual-Sided Staking

**User Staking — Lock for Discounts**

Users can stake $UR to unlock premium features and reduced costs. Staked tokens are removed from circulating supply for the duration of the stake.

| Tier | Stake Required | Benefit |
|---|---|---|
| Basic | 0 $UR | Pay-per-month/year |
| Member | 1,000 $UR | 25% discount |
| Pro | 5,000 $UR | 50% discount |
| Unlimited | 25,000 $UR | Unlimited access |

Developer API tiers follow the same structure with usage fee discounts up to 95%.

**Provider Staking — Lock for Multipliers**

Providers stake $UR to increase their share of epoch reward pools. This creates demand from the supply side of the network.

| Tier | Stake Required | Reward Multiplier |
|---|---|---|
| Standard | 0 $UR | 1.0x |
| Verified | 10,000 $UR | 1.25x |
| Professional | 50,000 $UR | 1.5x |
| Enterprise | 100,000+ $UR | 2.0x |

Multipliers do not create inflation. The total epoch reward pool is fixed per period. Staked providers receive larger proportional shares from the same pool. Unstaked providers still earn — they just earn a smaller slice.

### 3. BME (Burn-Mint Equivalent) Reward Model

Contributor rewards come from two sources that shift over time:

**Source 1: Contributor Rewards Pool.** 40% of total supply (400M $UR) is pre-allocated for contributor rewards, released on a decaying schedule across 10+ years. This is a finite drawdown, not inflation — the tokens already exist.

**Source 2: Network Usage Fees.** Real revenue from paying users and enterprises, flowing through the protocol after the 2.5% absorption.

In early stages, most rewards come from the pre-allocated pool. As usage grows, fees increasingly replace pool releases. The target steady state is ~90% of rewards funded by usage fees by Year 5.

No new tokens are ever minted. The 1B supply is fixed at genesis. The shift from pool to fees is the protocol transitioning from subsidized bootstrapping to self-sustaining economics.

### The Combined Flywheel

These mechanisms reinforce each other:

Users stake for discounts → supply removed from circulation → network usage increases → Network Operator buys $UR on market → protocol absorbs 2.5% per transaction → supply permanently decreases → providers stake for multipliers → more supply locked → cycle repeats with less circulating supply and more demand.

---

## Token Distribution

**Total Supply: 1,000,000,000 $UR (fixed at genesis)**

| Category | % | Tokens | Vesting |
|---|---|---|---|
| Contributor Rewards | 40% | 400,000,000 | 10+ year decaying release |
| Team & Advisors | 15% | 150,000,000 | 3-year vest, 1-year cliff |
| Investors (Seed) | 15% | 150,000,000 | 24-month linear, 12-month cliff |
| Treasury | 15% | 150,000,000 | Controlled release, governance-gated |
| Public Sale | 10% | 100,000,000 | 100% unlocked at TGE |
| Early Network Rewards | 5% | 50,000,000 | 6-month linear vest |

### Pricing

| Round | Price | FDV |
|---|---|---|
| Seed | $0.01 | $10,000,000 |
| Public (TGE) | $0.03 | $30,000,000 |

### Initial Circulating Supply

At TGE, approximately 13.3% of total supply will be circulating:

- Public sale: 100M tokens (100% unlocked)
- Treasury LP allocation: ~25M tokens (liquidity seeding)
- Early Network Rewards: ~8.3M tokens (first month of 6-month vest)

Seed investors and team are fully locked at TGE (12-month cliffs). Zero insider sell pressure on day one.

---

## The Network

### Protocol Architecture

URnetwork runs on an open-source, multi-transport protocol designed to scale to millions of providers per network operator. The protocol supports:

**Multi-hop routing** via TCP transports with plans for WebRTC, XRay, and WireGuard stream upgrades.

**N-TLS encryption** (N≥2) with SNI spoofing for censorship resistance. Each outer encryption uses a self-signed certificate for any hostname to an intermediary IP, making traffic indistinguishable from normal HTTPS.

**Performance-based matching** that samples providers and ranks them by reliability and client score, with Sybil resistance guaranteed by the property that reliability scores sum to ≤1 per IP subnet.

**Extender network** for censored regions. Anyone can host an extender on any domain, providing an entry point for users in restrictive environments. Extenders receive protocol-level incentive allocations.

### Trust and Safety — Default Safe

URnetwork's approach to trust and safety is embedded at the protocol level, not bolted on as policy. The system combines automated frontline protections with a privacy-preserving abuse audit process.

**Frontline (automated):**
- Only public unicast addresses are routable (no local network access)
- Unencrypted protocols disabled (plain DNS, HTTP, unencrypted mail)
- Restricted ports blocked except those needed for standard web, communication, and app traffic
- Abuse-prone protocols blocked (BitTorrent, IRC)
- Connection rate limits and parallel connection limits per source
- Contract priority weighted toward paying users to resist bot activity

**Backline (abuse response):**
- Encrypted audit logs where providers opt-in to send encrypted records
- Providers throw away their decryption key after submission
- Only a mostly-complete abuse report (including destination IP and TLS client random hash) can decrypt a record
- The company cannot decode user data without a legitimate external abuse report
- 24-month retention with computational difficulty by design

This architecture protects providers from legal liability while maintaining the network's ability to respond to legitimate abuse reports. It's why URnetwork calls its approach "Default Safe" — privacy and safety coexist through protocol design, not policy promises.

**Warrant canary** and **quarterly transparency reports** are published at docs.ur.io/trust-and-safety.

### Enterprise Integration

Enterprise clients interact with URnetwork through fiat-denominated SaaS contracts. They never need to hold tokens, manage wallets, or understand blockchain infrastructure.

**Proxy network:** Residential proxies across 100+ countries and 3,000+ cities. SOCKS5 and HTTPS support with geographic targeting, sticky sessions, and rotating IPs. Used for web scraping, ad verification, market research, SEO tracking, and AI agent web access.

**White-label VPN:** Complete backend for consumer and business VPN products. SDKs for Android, iOS, macOS, Windows, and Chrome. 99.99% regional uptime SLO. Fully white-labeled.

The Network Operator (BringYour, Inc.) handles all fiat-to-crypto conversion, creating consistent $UR buy pressure from commercial demand without requiring enterprise customers to interact with tokens.

---

## Entity Structure

| Entity | Type | Role |
|---|---|---|
| **URnetwork, LLC** | US LLC | Protocol foundation — token issuance, oracle management, Network Operator certification, treasury governance |
| **BringYour, Inc.** | Delaware C-Corp | Primary Network Operator — enterprise sales, fiat-to-$UR conversion, user support, app development |

Additional Network Operators may be certified as the protocol matures. The protocol is designed to support multiple operators contributing to a common incentive system.

---

## Team & Advisors

**Brien Colwell** — CEO
Engineering leadership at Palantir and Quora. CTO and Co-Founder of HeadSpin.

**Richard Guzzo** — CCO
Revenue leadership at Dell/EMC, LaunchDarkly, Teleport, and Haus.

**Stuart Kuentzel** — CTO
Product and crypto engineering.

**Advisors:**
- **Chris Jenkins** — Director, Pocket Network Foundation. Designed Shannon tokenomics (mint=burn model).
- **Daniel Andrade** — Founder, DePIN Hub. Hardware advisor (URnode) and early Helium hardware contributor.
- **Nicholas Sullivan** — Former Head of Research, Cloudflare. Created TLS ECH. Encryption advisor.

---

## Roadmap

**Q1 2026** — Seed round close. TGE preparation.

**March 2026** — Token Generation Event. Jupiter launch. Solana mainnet deployment. Public sale and exchange listings. Protocol launch with existing contributor network.

**Q2 2026** — Enterprise gateway v1. Additional Network Operator onboarding. Expanded geographic coverage.

**Q3-Q4 2026** — Governance framework. Secondary utility features (if community-approved). SDK release for third-party integration.

---

## Why $10M FDV

We analyzed every comparable privacy/VPN and DePIN token launch from 2024-2025. The data is clear:

84.7% of tokens launched in this period trade below their TGE price within 12 months. The median drawdown is 71%. Even "conservative" $50M launches get crushed. The privacy/VPN floor appears to be $25-35M — and most projects launched 10-20x above that.

We're not launching at the floor. We're launching below it.

At a $10M seed FDV and $30M public FDV, we're pricing $UR at seed-stage valuation despite having a live network with paying users, contributors currently earning USDC, an enterprise pipeline in progress, and protocol-level value accrual mechanisms.

Every comparable privacy token trades at 2.5-3.5x our public launch price. That's not a ceiling — that's where failed launches landed after crashing 90%.

---

## Risk Factors

**Regulatory risk:** Token classification and securities law continue to evolve. $UR is designed as a utility token, but regulatory interpretation varies by jurisdiction.

**Adoption risk:** Token economics depend on network usage. If adoption underperforms, protocol absorption and usage fees may not reach meaningful scale.

**Technical risk:** Smart contract vulnerabilities, oracle failures, or protocol bugs could impact token distribution and network operation.

**Liquidity risk:** Initial float of ~13% creates thin markets in early trading.

**Competition:** Centralized VPNs and competing decentralized protocols may capture market share.

---

## Conclusion

Most DePIN tokens are liabilities disguised as assets. They promise yield while quietly diluting holders into oblivion.

$UR is designed as infrastructure. Fixed supply means no dilution trap. Usage-based rewards mean contributors are paid from value created. Protocol absorption means every transaction makes the token scarcer. Enterprise abstraction means demand isn't dependent on crypto adoption. Dual-sided staking means the network's heaviest users and best providers are also its most aligned holders.

The token coordinates a real network with real users paying real money. When the network grows, the token captures that value. When it doesn't, there's no artificial mechanism propping up an unsustainable system.

Privacy is a fundamental right. $UR is how ownership works — built to last.

---

**Website:** ur.io
**Documentation:** docs.ur.io
**Contact:** team@ur.io

*This document is for informational purposes only and does not constitute an offer to sell or solicitation to buy tokens. Please consult legal and financial advisors before participating in any token sale.*
