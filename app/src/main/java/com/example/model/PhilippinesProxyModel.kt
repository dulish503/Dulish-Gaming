package com.example.model

enum class ProxyProtocol(val label: String) {
  HTTPS("HTTPS"),
  HTTP("HTTP"),
  SOCKS5("SOCKS5"),
  WEB_RELAY("PH Web Relay")
}

enum class ProxyRoutingMode(val title: String, val description: String) {
  DIRECT_PH_PROXY(
    "Direct PH Proxy (Socket)",
    "Routes TCP/HTTP socket through Philippines proxy server IP using Java Proxy API."
  ),
  WEB_FETCH_RELAY(
    "Philippines Web Gateway",
    "Routes via Manila gateway with verified Philippines IP headers (X-Forwarded-For, CF-IPCountry: PH)."
  ),
  AUTO_FALLBACK(
    "Smart Dual Route (Recommended)",
    "Attempts direct Manila HTTPS proxy, seamlessly falling back to PH Web Gateway if public node drops."
  )
}

data class PhilippinesProxyNode(
  val id: String,
  val name: String,
  val host: String,
  val port: Int,
  val protocol: ProxyProtocol = ProxyProtocol.HTTPS,
  val city: String = "Manila",
  val region: String = "Metro Manila (NCR)",
  val country: String = "Philippines",
  val countryCode: String = "PH",
  val isp: String = "PLDT Enterprise",
  val simulatedIp: String = "112.198.115.114",
  val latencyMs: Int = 42,
  val isVerified: Boolean = true,
  val anonymLevel: String = "Elite / High Anonymous",
)

data class ProxyFetchResult(
  val url: String,
  val statusCode: Int,
  val statusMessage: String,
  val durationMs: Long,
  val resolvedIp: String,
  val country: String,
  val city: String,
  val isp: String,
  val timezone: String,
  val headers: Map<String, String>,
  val bodyExcerpt: String,
  val isSuccess: Boolean,
  val proxyUsed: String,
  val timestampText: String,
  val errorMessage: String? = null,
)

data class IpGeoDetails(
  val ip: String = "112.198.115.114",
  val country: String = "Philippines",
  val countryCode: String = "PH",
  val region: String = "Metro Manila (National Capital Region)",
  val city: String = "Manila",
  val latitude: Double = 14.5995,
  val longitude: Double = 120.9842,
  val timezone: String = "Asia/Manila",
  val isp: String = "Philippine Long Distance Telephone (PLDT)",
  val asn: String = "AS9299 Philippine Long Distance Telephone Co.",
)
