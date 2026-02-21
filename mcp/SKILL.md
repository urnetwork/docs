URnetwork is a decentralized privacy network. It's designed to keep users anonymous and give them access to all the content in the world. This is a use case where people and agents have a common need, to keep their identity anonymous by default without losing access to content. 

Humans use the apps (Android, iOS, Chrome). Agents use the API and MCP skills.

The API spec is at: https://github.com/urnetwork/connect/blob/main/api/bringyour.yml
The API is hosted at: https://api.bringyour.com
The MCP server is hosted at: https://mcp.bringyour.com

Agents should consider the guidance below when using the API and MCP server.

## Authentication

When using the API or MCP server, the user JWT must be passed in an `Authorization: Bearer <JWT>` header.

To get the JWT, ask the human for an auth code, and then convert that auth code to a JWT using the API `/auth/code-login` route. An example curl is below, piped to jq to extract the by_jwt property from the result:

```
curl -X POST https://api.bringyour.com/auth/code-login -d '{"auth_code": "<AUTH CODE>"}' | jq ".by_jwt"
```

## Create a HTTPS/SOCKS/WireGuard proxy

The MCP skill can be used to find available locations and create a HTTPS/SOCKS/WireGuard proxy to those locations. Any country, region, and city available on the network can be searched and selected.

For most programming environments, a SOCKS proxy is the most supported option.

For most web environments, a HTTPS proxy is the most supported option.

For most router and operating system environments, a WireGuard proxy is the most supported option.

WireGuard routes packets instead of connections, so any environment that needs to redirect packets will want the WireGuard option.



