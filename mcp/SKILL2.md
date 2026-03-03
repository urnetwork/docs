# Optimized Skill Document: URnetwork for AI Agents

## Overview

URnetwork is a decentralized privacy network. AI Agents use this skill to provision anonymous network egress via SOCKS, HTTPS, or WireGuard proxies.

* **API Base:** `https://api.bringyour.com`
* **MCP Server:** `https://mcp.bringyour.com`
* **Spec:** `https://github.com/urnetwork/connect/blob/main/api/bringyour.yml`

## 1. Authentication Protocol

Agents must maintain a valid User JWT.

1. **Trigger:** If no JWT exists or 401 Unauthorized occurs.
2. **Action:** Prompt human: *"Please provide a URnetwork auth code to initialize the proxy connection."*
3. **Exchange:** Call `POST /auth/code-login` with `{"auth_code": "<CODE>"}`.
4. **Storage:** Extract `by_jwt`. Use in all headers as `Authorization: Bearer <JWT>`.

## 2. Proxy Technology Selection Logic

Before calling `/network/auth-client`, the Agent **must** determine the protocol based on the environment:

| Use Case | Recommended Protocol | Requirement |
| --- | --- | --- |
| **Scraping / Web Browsing** | **HTTPS** | Best for TCP/Web. Use `https_proxy_url`. |
| **Low-level Sockets / UDP** | **SOCKS** | Supports TCP+UDP. Use `socks_proxy_url`. User: `access_token`, Pass: (empty). |
| **System-wide / OS Level** | **WireGuard** | Routes all IP packets. Set `proxy_config.enable_wg: true`. Use `wg_config.config` as a `.conf` file. |

## 3. Location Discovery & Selection Workflow

When a user requests a location (e.g., "Give me a London IP"), follow this recursive logic:

### Step 1: Search

Call `POST /network/find-locations` with `{"query": "<USER_QUERY>"}`.

### Step 2: Validation & Fallback

* **If 1+ results:** Present top matches to the human to confirm `location_type` (City vs. Country).
* **If 0 results:** 1.  Broaden search (e.g., if "London City" fails, search "United Kingdom").
2.  If broadening fails, fetch all available countries and suggest the top 10.
* **Conflict Resolution:** If the user is vague, ask: *"Would you like to connect to the City, Region, or Country level?"*

## 4. Implementation Patterns (API)

### Pattern A: Connecting by Country Code (Simplest)

Use this when only the country is specified.

* **Route:** `/network/auth-client`
* **Payload Fragment:**

```json
{
  "proxy_config": {
    "initial_device_state": {
      "country_code": "US"
    }
  }
}

```

### Pattern B: Connecting by Specific Location ID

Use this for specific cities or regions discovered in Step 3.

* **Path:** `proxy_config.initial_device_state.location.connect_location_id.location_id`
* **Payload Fragment:**

```json
{
  "proxy_config": {
    "initial_device_state": {
      "location": {
        "connect_location_id": { "location_id": "12345" }
      }
    }
  }
}

```

### Pattern C: Egress IP Rotation (Provider Level)

Use this if the user requires multiple unique IPs in the same location.

1. Call `POST /network/find-providers2` using the `location_id`.
2. Loop through the `providers` list.
3. For each `client_id`, call `/network/auth-client` using:
`proxy_config.initial_device_state.location.connect_location_id.client_id`

## 5. Agent Constraints

* **Persistence:** Do not request a new Auth Code unless the JWT expires or the user explicitly asks to "re-login."
* **Security:** Never expose the JWT in logs or chat output.
* **Protocol:** Always prefer HTTPS over HTTP unless testing is specified.
