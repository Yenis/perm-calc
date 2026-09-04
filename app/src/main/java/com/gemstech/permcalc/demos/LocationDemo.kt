package com.gemstech.permcalc.demos

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import kotlin.coroutines.resume

/** Gets the current location (fresh fix, falling back to last known) and reverse-geocodes it. */
@SuppressLint("MissingPermission")
suspend fun runLocationDemo(context: Context): LocationResult {
    val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    val lastKnown = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
        .mapNotNull { p -> runCatching { lm.getLastKnownLocation(p) }.getOrNull() }
        .maxByOrNull { it.time }

    val fresh = withTimeoutOrNull(12_000) { requestSingleFix(lm) }
    val location = fresh ?: lastKnown
        ?: throw IllegalStateException("No location available")

    val address = reverseGeocode(context, location.latitude, location.longitude)

    return LocationResult(
        latitude = location.latitude,
        longitude = location.longitude,
        accuracy = if (location.hasAccuracy()) location.accuracy else null,
        altitude = if (location.hasAltitude()) location.altitude else null,
        provider = location.provider,
        timeMillis = location.time.takeIf { it > 0 },
        address = address,
    )
}

@SuppressLint("MissingPermission")
private suspend fun requestSingleFix(lm: LocationManager): Location? =
    suspendCancellableCoroutine { cont ->
        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
            .filter { runCatching { lm.isProviderEnabled(it) }.getOrDefault(false) }
        if (providers.isEmpty()) {
            cont.resume(null)
            return@suspendCancellableCoroutine
        }

        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (cont.isActive) {
                    lm.removeUpdates(this)
                    cont.resume(location)
                }
            }

            @Deprecated("Deprecated in API 29")
            override fun onStatusChanged(provider: String?, status: Int, extras: android.os.Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        providers.forEach { p ->
            runCatching { lm.requestLocationUpdates(p, 0L, 0f, listener, Looper.getMainLooper()) }
        }

        cont.invokeOnCancellation { lm.removeUpdates(listener) }
    }

private suspend fun reverseGeocode(context: Context, lat: Double, lng: Double): AddressInfo? =
    withContext(Dispatchers.IO) {
        try {
            @Suppress("DEPRECATION")
            val results = Geocoder(context, Locale.getDefault()).getFromLocation(lat, lng, 1)
            val r = results?.firstOrNull() ?: return@withContext null
            fun clean(s: String?) = s?.trim()?.ifBlank { null }
            val street = listOfNotNull(clean(r.subThoroughfare), clean(r.thoroughfare))
                .joinToString(" ").ifBlank { null }
            AddressInfo(
                fullLine = clean(r.getAddressLine(0)),
                street = street,
                neighborhood = clean(r.subLocality),
                city = clean(r.locality) ?: clean(r.subAdminArea),
                postalCode = clean(r.postalCode),
                district = clean(r.subAdminArea),
                region = clean(r.adminArea),
                country = clean(r.countryName),
            )
        } catch (_: Exception) {
            null
        }
    }
