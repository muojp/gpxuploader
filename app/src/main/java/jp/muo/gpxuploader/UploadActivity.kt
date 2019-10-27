package jp.muo.gpxuploader

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UploadActivity : AppCompatActivity() {

    companion object {
        const val STRAVA_AUTH_URI = "https://www.strava.com/oauth/mobile/authorize"
        const val STRAVA_API_BASE = "https://www.strava.com/api/v3/"
        const val TAG = "upload-activity"
    }

    private val prefs: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(
            applicationContext
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uris = extractUrisFromIntent()
        setContentView(R.layout.activity_main)
        if (uris.isNotEmpty()) {
            upload(uris)
        }
    }

    private fun extractUrisFromIntent(): List<Uri> {
        val uris = ArrayList<Uri>()
        when (intent?.action) {
            Intent.ACTION_SEND_MULTIPLE -> {
                intent.getParcelableArrayListExtra<Parcelable>(Intent.EXTRA_STREAM)?.let { lst ->
                    lst.forEach { uris.add(it as Uri) }
                }
            }
            Intent.ACTION_VIEW -> intent.data?.let { uris.add(it) }
        }
        return uris
    }

    private fun isAccessTokenAvailable(): Boolean {
        return !prefs.getString("accessToken", "").isNullOrEmpty()
    }
    private suspend fun upload(uris: List<Uri>) = withContext(Dispatchers.IO) {
        if (!isAccessTokenAvailable()) {
            authorize()
        }
        else {
            refreshAccessKeyIfExpired()
            if (!isAccessTokenAvailable()) {
                Toast.makeText(applicationContext, "Needs re-authorization", Toast.LENGTH_LONG).show()
                return@withContext
            }
            uris.forEach {
                uploadFile(it)
            }
        }
    }

    private fun authorize() {
        val intentUri = Uri.parse(STRAVA_AUTH_URI).buildUpon()
    }

    private fun refreshAccessKeyIfExpired() {

    }

    private fun uploadFile(uri: Uri) {
        val inStream = contentResolver.openInputStream(uri)
        inStream?.readBytes()?.let {
            // TODO: gzip (Strava API supports *.gpx.gz files)

        }
    }
}