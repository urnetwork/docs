# Welcome to URnetwork for Agents

URnetwork is a decentralized privacy network. It's designed to keep users anonymous by default and give them access to all the content in the world.

Humans use the apps (Android, iOS, Chrome). Agents use the API and MCP skills:

- The API spec is at: https://github.com/urnetwork/connect/blob/main/api/bringyour.yml
- The API is hosted at: https://api.bringyour.com
- The MCP server is hosted at: https://mcp.bringyour.com

Agents should consider the guidance below when using the API and MCP server.

## Authentication

The API and the MCP server authenticate differently. Use the right one for the surface you are calling.

### MCP server: OAuth

The MCP server is an OAuth 2.1 protected resource. A network JWT or an API key is **not** accepted there — only an access token issued by URnetwork's own authorization server.

Most MCP clients do this for you: connect to `https://mcp.bringyour.com`, and the client will discover the authorization server and walk the user through signing in. If you are implementing the flow yourself:

1. Call the MCP server without a token. It answers `401` with a `WWW-Authenticate` header naming the protected resource metadata.
2. Fetch that metadata at `https://mcp.bringyour.com/.well-known/oauth-protected-resource` to find the authorization server, `https://auth.bringyour.com`.
3. Fetch `https://auth.bringyour.com/.well-known/oauth-authorization-server` (or `/.well-known/openid-configuration`) for its endpoints.
4. Run the authorization code flow with PKCE. Include the `resource` parameter set to `https://mcp.bringyour.com`, which is what binds the token to this server.
5. Send the resulting access token as `Authorization: Bearer <ACCESS TOKEN>` on every MCP request.

Scopes:

| Scope | Grants |
| --- | --- |
| `mcp:read` | `providerLocations` |
| `mcp:fetch` | `fetch` — opens an egress client billed to the network |
| `openid` | an ID token identifying the user |
| `offline_access` | a refresh token, so the connection survives without re-prompting |

Access tokens last one hour. Request `offline_access` and use the refresh token rather than sending the user back through the browser. If a tool reports that it needs a scope you do not hold, re-authorize requesting that scope **in addition to** the ones you already have, or you will lose the others.

### API: JWT

When using the API (`https://api.bringyour.com`), the user JWT is passed in an `Authorization: Bearer <JWT>` header.

To get the JWT, ask the human for an auth code, and then convert that auth code to a JWT using the API `/auth/code-login` route. An example curl is below, piped to jq to extract the by_jwt property from the result:

```
curl -X POST https://api.bringyour.com/auth/code-login -d '{"auth_code": "<AUTH CODE>"}' | jq ".by_jwt"
```

The JWT can be stored and reused. If the human asks to refresh the JWT, repeat the process starting with asking for an auth code.

When choosing a proxy technology, consider the following use cases:

| Use Case | Recommended Protocol | Requirement |
| --- | --- | --- |
| **Scraping / Web Browsing** | **HTTPS** | Best for TCP/Web. Always use HTTPS proxy unless there is a specific need to use HTTP. HTTP is usally only needed for specific test environments that do not support HTTPS. From the /network/auth-client response, inside the proxy_config_result object, use the https_proxy_url. No additional username or password are needed. |
| **Low-level Sockets / UDP** | **SOCKS** | Supports TCP+UDP sockets with SOCKS5. From the /network/auth-client response, inside the proxy_config_result object, use the socks_proxy_url or proxy_host and proxy_port, with the username access_token (empty password). The server supports remote DNS resolution (SOCKS5H). |
| **System-wide / OS Level** | **WireGuard** | Routes all IP packets. In the /network/auth-client request, proxy_config.enable_wg must be explicitly set to true. In the response, inside the proxy_config_result object, use the wg_config.config as the complete WireGuard config file. |

When using the /network/find-locations route to query locations, always filter the returned locations array by the desired location_type (city, region, or country) to ensure the location_id matches the user's intent.

| Location Type | Requirement |
| --- | --- |
| country | For countries. |
| region | For states, provinces, administrative regions, and metro areas. |
| city | For cities. |


## Using the MCP server

The MCP server exposes two tools.

### providerLocations

Finds available locations. Any country, region, or city on the network can be searched with a query string; an empty query returns the available countries. Requires `mcp:read`.

Ask the human what country, region, or city they want. If there are no matches, ask them to broaden to a region, then to a country. If there are still no matches, list the available countries and suggest one of the top 10.

### fetch

Loads a URL as if browsing from a chosen location, and optionally returns the images, stylesheets, scripts, and media the page references. Requires `mcp:fetch`, because each new location opens an egress client billed to the network.

State is threaded back through you. Every result includes a `next_step` saying exactly what to carry forward:

- **`signed_proxy_id`** — the egress that served the request. Pass it back on follow-on loads to reuse the same location instead of opening a new client each time. Keep passing `location` too, so the egress can be re-established if it expired. Reuse guarantees the same **location**, not the same exit IP.
- **`cookies`** — the site session, so logins and consent banners survive across calls. Opaque; pass it back unchanged and never edit it.
- **`continuation`** — present when the page referenced more resources than fit in one call. Call again passing it to collect the rest; `url` is not needed then.
- **`payment_required`** — present when the network is at its plan's concurrent client limit and payment can settle it. Sign the payment it describes and repeat the identical call with `payment` set to the signed payment. Do not start a separate purchase flow.

Resource discovery is static: the HTML is parsed for references. Content a page loads with JavaScript is not seen.

### Creating a proxy for direct use

To get an HTTPS/SOCKS/WireGuard proxy you drive yourself, rather than fetching through the MCP server, use the API flow below.


## Using the API to create a HTTPS/SOCKS/WireGuard proxy for a country

The API can be used directly to create a HTTPS/SOCKS/WireGuard proxy for a country.

Step 1, search for a list of locations using the route /network/find-locations. A curl example is below, piped to jq to extract the locations list.

```
curl -X POST -H 'Authorization: Bearer <JWT>' https://api.bringyour.com/network/find-locations -d '{"query": "<LOCATION NAME>"}' | jq '.locations'
```

Step 2, choose the location of interest and save the country_code property.

Step 3, create a proxy using the saved country code using the /network/auth-client route and setting the proxy_config.initial_device_state to have country_code.

```
curl -X POST -H 'Authorization: Bearer <JWT>' https://api.bringyour.com/network/auth-client -d '{"proxy_config": {"initial_device_state": {"country_code": "<COUNTRY CODE>"}}}'
```


## Using the API to create a HTTPS/SOCKS/WireGuard proxy for a search location

The API can be used directly to search for a location and create a HTTPS/SOCKS/WireGuard proxy. A decision will have to be made to choose the location result that is most desired. Each location has a location_id that is fixed and can be saved in code.


Step 1, search for a list of locations using the route /network/find-locations. A curl example is below, piped to jq to extract the locations list.

```
curl -X POST -H 'Authorization: Bearer <JWT>' https://api.bringyour.com/network/find-locations -d '{"query": "<LOCATION NAME>"}' | jq '.locations'
```

Step 2, choose the location of interest and save the location_id property.

Step 3, create a proxy using the saved location_id using the /network/auth-client route and setting the the proxy_config.initial_device_state.location to have connect_location_id.location_id.

```
curl -X POST -H 'Authorization: Bearer <JWT>' https://api.bringyour.com/network/auth-client -d '{"proxy_config": {"initial_device_state": {"location": {"connect_location_id":{"location_id": "<LOCATION ID>"}}}}}'
```


## Using the API to create a HTTPS/SOCKS/WireGuard proxy for a search location, enumerating all the egress IPs in that location

The API can be used directly to search for a location, enumerate the providers (egress IPs) in that location, and create a HTTPS/SOCKS/WireGuard proxy for each egress IP. A decision will have to be made to choose the location result that is most desired. Each location has a location_id that is fixed and can be saved in code. Additionally each provider has a client_id that is fixed and can be saved in code.

Step 1, search for a list of locations using the route /network/find-locations. A curl example is below, piped to jq to extract the locations list.

```
curl -X POST -H 'Authorization: Bearer <JWT>' https://api.bringyour.com/network/find-locations -d '{"query": "<LOCATION NAME>"}' | jq '.locations'
```

Step 2, choose the location of interest and save the location_id.

Step 3, fetch a ranked list of providers (egress IPs) for the location_id using the route /network/find-providers2. The sample size can be set to however many unique providers are needed. A curl example is below, piped to jq to extract the providers list.

```
curl -X POST -H 'Authorization: Bearer <JWT>' https://api.bringyour.com/network/find-providers2 -d '{"specs": [{"client_id": "<CLIENT ID>"}], "count": <COUNT>}' | jq '.providers'
```

Step 4, by looping over each client_id in the list, create a proxy using the client_id using the /network/auth-client route and setting the the proxy_config.initial_device_state.location to have connect_location_id.client_id.

```
curl -X POST -H 'Authorization: Bearer <JWT>' https://api.bringyour.com/network/auth-client -d '{"proxy_config": {"initial_device_state": {"location": {"connect_location_id":{"client_id": "<CLIENT ID>"}}}}}'
```



