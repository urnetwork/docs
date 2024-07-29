# Economic model

BringYour earns revenue from user premium subscriptions. We build and operate the protocol that connects users to a global network of egress providers, which are access points to localized internets all around the world. Our goal is to have the largest network of egress providers, so that traffic from our network is indistinguishable from normal internet traffic. We designed our economic model to both 1. reward people who help run and grow the network, and 2. build a great business.

Because the number of egress providers grows with the number of users, our data center costs are almost fixed. We're able to offer access for free because, simply, you’ve earned it by participating in the network to provide security and privacy to other users. This is a completely new design that lets us build a free, fast, private VPN in a transparent and ethical way.

Traditional VPNs need to spend money on bandwidth and operations. To pay for this, traditional free VPNs monetize their users - your data, ads, scraping, malware, botnets. Because in our network users run the egress providers and trade bandwidth with each other, we do not have to spend money on bandwidth and operations. Instead we spend money on building the protocol, automated processes, and simple apps to scale the network.

We’re focused on making the network safe, scalable, and fast. We’re continually improving our protocol to thrive in the realities of the global internet. We're also focused on premium features that matter to you, like private sharing, third-party blocking, anti-censorship, and data management. Because we charge a small subscription for premium, we're able to spend more on bandwidth and servers for our premium users.

We see a future where a significant portion of internet users switch away from sketchy traditional VPNs to us, because we work better, more private, and cheaper. It’s important that we have an economic model that rewards the community and leads to a great business as we scale. If we didn't have a realistic model that leads to a great business at scale, one could conclude we also plan to monetize our users. For this reason we want to be transparent with our economic model.

Our current model factors in three components:
- Paid transfer (the old way)
- Subsidized transfer (the new way)
- Referral bonuses

Note that with subsidized transfer, paid transfer trends to zero. We keep it in the model since that was the operation of the network from public beta launch in H1 2024 to “v0” launch in H2 2024.


## Payments to the community

We set our subsidy payment as 10% of premium revenue with a guaranteed minimum amount of $.1 per MAU. MAU does not include automation and bots. The payout amount per device is determined by the amount of data routed, weighted 75% against all global devices and 25% against devices in the same country, where all countries are weighted equally. The guaranteed minimum amount will adjust every year after launch. [^1]

We set our referral bonus as 50% of the earnings of the referred user and 50% of their referral bonus.

This means that we’re paying out up to 20% of our premium revenue back to the community, with a minimum amount up to $.2 per MAU.

This also means that we’re targeting margins between 70-80% at scale, which is where we want to be as a business. This is competitive in the market and allows us to run as an independent company.

The revenue share of paid transfer is 50%. However, the amount of paid traffic trends to zero after our launch in H2 2024.

The payouts to the community are sent to each network’s active wallet, as USDC, either on Polygon or Solana. The active wallet is managed in the app, and a default user-controlled wallet can be created directly in the app.

Payouts to the active wallet subtract the gas fee from the payout. The payout is held and combined with future payouts until the value minus the gas fee is positive.

There is no cost to participate as a provider.


## Phases of the company

The launch of the network and community payments creates four phases of the company:

0. Repeatable network
1. Repeatable premium product
2. Scale
3. Profitable company

In phase 0, we will focus on a network that is better than other  consumer VPNs, and scaling the network in a cost efficient way. The exit is a scalable network with almost fixed operating costs.

In phase 1, the premium revenue is not enough to subsidize the community payments. We’ll be in this phase as long as premium conversion per MAU is less than .2/premium price [^2]. At our current premium price of $5/month, the break-even exit of phase 1 is at 4% conversion of users to premium users. In this phase the company will focus on a repeatable premium offering that converts above the break-even rate to exit.

In phase 2, the company can scale revenue with an almost fixed operating cost by scaling MAU. The company will scale faster if the conversion to premium is higher than the break-even. In this phase the company will focus on volume to cover operating costs and exit.

In phase 3, the company will be profitable as it scales. At this point we will look at expanding our TAM with new premium products starting at phase 1.


## Payout sweep

Payouts are done weekly and the subsidy is computed from the previous week’s revenue and the previous month’s MAU.


## Privacy

Payment on our network is private.

Transfer between a user and provider is preceded by a contract. A contract combines payment information, access control, and priority so that a user and provider can directly communicate and apply the right rules. Both user and provider close a contract, and if they agree, the contract is settled and added to the payout.

Settled contracts used for a payout are removed one week after the payout. This is in line with our approach of aggressively deleting user information from our system. If there are any issues with a payout, participants must contact us within one week. 


[^1]: As a formula, the subsidized payout to a single provider is `0.75 * sum(all contracts serviced by provider)/sum(all contracts) + (0.25 / NumberOfCountries) * sum(all contracts serviced by provider)/sum(all contracts in provider's country)`

[^2]: `PremiumPrice/month * PremiumConversion * MAU = .2 * MAU/month`
