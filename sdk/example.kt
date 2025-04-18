
// Kotlin example of how to go from an initial jwt to a device

val byJwt = ""
val deviceDescription = ""
val deviceSpec = ""
val appVersion = "0.0.0"


// set sdk limits
val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager?
val maxMemoryMib = activityManager?.memoryClass?.toLong() ?: 16
// target 3/4 of the max memory for the sdk
Sdk.setMemoryLimit(3 * maxMemoryMib * 1024 * 1024 / 4)



// initialize and upgrade the network space
networkSpaceManager = Sdk.newNetworkSpaceManager(filesDirPath)

networkSpaceManagerProvider.init(filesDir.absolutePath)

val networkSpaceManager = networkSpaceManagerProvider.getNetworkSpaceManager()

val key = Sdk.newNetworkSpaceKey("bringyour.com", "main")
val networkSpace = networkSpaceManager?.updateNetworkSpace(key) { values ->
    // migrate specific bundled fields to the latest from the build
    values.envSecret = ""
    values.bundled = true
    // security settings
    // more security can mean fewer connectivity options and slower connectivity in some regions
    values.netExposeServerIps = true
    values.netExposeServerHostNames = true
    // server settings
    values.linkHostName = ""
    values.migrationHostName = ""
    // third party settings
    values.store = ""
    values.wallet = ""
    values.ssoGoogle = false
}

val api = networkSpace.api
api?.setByJwt(byJwt)



// get a client jwt and create a device

val authArgs = AuthNetworkClientArgs()
authArgs.description = deviceDescription
authArgs.deviceSpec = deviceSpec

app.api?.authNetworkClient(authArgs) { result, err ->
    runBlocking(Dispatchers.Main.immediate) {
        if (err != null) {
            callback(err.message)
        } else if (result.error != null) {
            callback(result.error.message)
        } else if (result.byClientJwt.isNotEmpty()) {
            
            val device = Sdk.newDeviceLocalWithDefaults(
                networkSpace,
                result.byClientJwt,
                deviceDescription,
                deviceSpec,
                appVersion,
                Sdk.NewId(),
                false
            )

            device.provideWhileDisconnected = true

        } else {
            callback(getString(R.string.login_client_error))
        }
    }
}


        

