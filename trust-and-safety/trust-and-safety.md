

# Default Safe: Our Trust and Safety Process

This page outlines our process as of May 2024. This file is versioned so that you can track changes. For input and feedback please join [our Discord](https://bringyour.com/discord).

Our trust and safety process is how we create a network for all users to safely participate with the most access and the most privacy possible. At BringYour our goal is to create a network where everyone can participate knowing they will be safe, and users are confident their traffic is uncensored, anonymous, and encrypted. 

The process we use is to combine an automated "frontline" with a catch-all "backline" abuse audit process that preserves privacy. Most issues are resolved automatically by the frontline, which involves a mix of protocol and economic incentives. It involves turning off protocols associated with botnets and copyright abuse, while keeping web and app traffic uncensored and free. All levels are opt-out, meaning there are ways for providers to change the rules. Our stance is that we want to have a safe default, but allow users to have freedom of choice.


## Warrant canary

If the [warrant canary](https://docs.ur.io/trust-and-safety/warrant-canary) exists, it means that we confirm:

- BringYour has never turned over our encryption or authentication keys or our customers' encryption or authentication keys to anyone.

- BringYour has never installed any law enforcement software or equipment anywhere on our network.

- BringYour has never provided any law enforcement organization a feed of our customers' content transiting our network.

- BringYour has never modified network content at the request of law enforcement or another third party.

- BringYour has never weakened, compromised, or subverted any of its encryption at the request of law enforcement or another third party.



## Transparency report

We publish [quarterly statistics](https://docs.ur.io/trust-and-safety/transparency-report) of the number and type of abuse reports, decryptions of the abuse logs, and type of outcomes.




## Frontline protocol and economic incentives

The frontline are automated rules embedded within the clients and providers. If the client and provider mutually agree to change the rules, it is possible to use different rules. However, it is not possible for one party to to change the rules without the other party, and a mismatch in rules will raise a contract dispute. In this way these rules are the default, but there are ways that two parties can opt-out of the rules. Our stance is that we want to have a safe default, but allow users to have freedom of choice. We explain the directions to change the rules at the end.

The current default set of frontline rules are split into protocol limits, rate limit, and contract limits.


### Protocol limits

**No local network access**

Only public unicast addresses are routable. Traffic to private or multicast addresses will not route.


**Disable unencrypted protocols**

Plain DNS and HTTP are disabled. The device must use DoH/DoT and HTTPs.

Unencrypted mail protocols are disabled. The encrypted versions of these protocols are supported.

DNS (port 53) is blocked.

HTTP (port 80) is blocked.

IMAP (port 143) is blocked.

SMTP (port 25) is blocked.

POP (port 110) is blocked.


**Block restricted ports**

The restricted port range is used by many core internet protocols which are most susceptible to abuse. Our goal is to block all restricted ports except protocols needed by mainstream users to access almost all of the web, communication, apps, and gaming.

HTTPS (port 443) is unblocked.

DoH (port 443) in unblocked.

DoT (port 853) is unblocked.

IMAP TLS (port 993) is unblocked.

SMTP TLS (ports 587 and 465) is unblocked.

POP TLS (port 995) is unblocked.

SFTP (port 990) is unblocked.


**Block protocols that commonly lead to abuse to providers**

We unblock all user ports except protocols that lead to an excess number of abuse reports for providers. It does not mean these protocols are inherently bad. Rather, they cause an a strain for providers that makes it harder to safely be a provider. Generally user ports are used widely in WebRTC, communication, and gaming.

Bittorrent (ports 6881-6889) is blocked.

IRC (port 6667) is blocked.


### Rate limits

**Connection rate limit per source**

Each source can only create a limited number of new TCP connections and UDP streams per second. The number is high enough that it does not impact normal use cases. The goal of the rate limit is to prevent traffic abuse using the network.


**Parallel connections limit per source**

The number of parallel connections per source is limited. Additionally the number of parallel connections per provider is limited by the ulimit settings of the provider device. The goal of the parallel limit is to prevent prevent traffic abuse using the network.


### Contract limits

**Contract priority**

Contracts are assigned a priority by the platform. The contract priority for a client is determined by its contribution to the network. Providing and being a premium user are considered contributions to the network. Providers may dynamically rate limit connections based on contract priority.

To make the network more resilient to bots, contract priority is weighted towards premium users and providing premium traffic. In other words, the spend per GiB is the core unit used to confer priority to providers. In this way priority is correlated with money flow and is harder to influence without spending money.

**Minimum contract close**

Minimum contract close has been deprecated in favor of contract priority. This lets us better support free users.


### Changing the rules

On our roadmap is to let providers turn off any of the default rules. The providers would do this knowing the network is not able to keep them safe. We are considering a way to let providers signal which rules are enabled, so that clients who want to choose a different set of rules can choose the providers that match those rules. In other words, users could select providers that disable rules when searching for providers.

For example providers with absolutely free internet could remove most rules and serve clients who want that.



## Backline abuse audit process that preserves privacy

We operate as a US company and want to stay and scale in the US. We take the stance that the company itself may be an adversary to user privacy, so that users must be protected with hard cryptography and properly distributed protocols. Our goal is to advance the state of art of logging with better cryptography, so that we can have proper duty of care to resond to abuse, while taking the position that the company should not know anything about the users. Our approach is detailed below on how we response to abuse reports and take action to remove abuse from network.

Any abuse request must be sent to [notice@bringyour.com](mailto:notice@bringyour.com) and tell us:
- **When**
- **Source IP and port**
- **Destination IP and port**
- **SHA256 hash of the TLS client random if the abusive connection is TLS encrypted**
- **Reporter contact email. This must be from an official domain that we can use to verify.**
- **Reporter abuse notice**

We will need to verify the identify of the reporter, as well as the validity of the abuse notice. We will typically reach out directly to the reporter contact to do the verification.

Typically an abuse notice is submitted by a provider when they receive a notice. However we also work with abuse reporting services so that notices can be filed directly to the network.

We are able to enforce abuse notices because the network maintains an encrypted audit log. Providers opt-in to send encrypted records to the log, and throw away their key. Only the destination or ISP would be able to derive the key, which is what they send in an abuse report.

This ensures extracting data is intractable unless a mostly-complete key is provided, as from an abuse notice. In this way the company cannot decode the data without a legitimate abuse report. With a mostly-complete key, finding the accounts involved in a single record is possible, but it is still computationally intensive by design. We are able to extract single records in sufficient time to feed into the trust and safety process. The technical details can be found in the audit protocol[^1].

The encrypted audit log is maintained for 24 months.

The trust and safety process involves looking at both parties involved in any incident, as well as the submitter of the incident. The result may be that any of the parties or submitter may be removed from the network. Additionally abuse patterns are considered as candidates to incorporate into the frontline system.

### Opting out

Providers may opt out of sending encrytped audit records to the platform. However in these cases we will not be able to take action on abuse reports submitted by the provider.

### SHA256 hash of the TLS client random

Today the TLS client random value is not typically logged on servers and ISPs. We believe this leads to a standard of logging that forces proxy companies to store logs in a way that compromises user privacy. Using a 256-bit hash of the TLS client random creates enough entropy in the encrypted record key such that it could not be brute forced without a mostly complete abuse report.

We require server and ISP logging where the SHA256 of the TLS client random is stored as part of the log, allowing us and other proxy companies to store user logs in a way that protects user privacy. Our standard is published in the audit protocol[^1]. We offer consulting services to implement this logging so that servers and ISPs can add a hash of the TLS client random to their logs. Please contact [security@bringyour.com](mailto:security@bringyour.com).

[^1]: See the audit protocol: https://github.com/bringyour/connect/tree/main/connect

