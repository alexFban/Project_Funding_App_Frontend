package com.example.ps2024.utils

import android.os.Handler
import android.os.Looper
import com.squareup.otto.Bus

object CommunicationBus {
    var instance: Bus? = null
        /**
         * Returns the shared instance of the bus.
         * @return single tone instance of Bus
         */
        get() {
            if (field == null) {
                field = MainThreadBus()
            }
            return field
        }
        private set

    /**
     * Custom bus that allows posting events from another thread than the main one.
     */
    internal class MainThreadBus : Bus() {
        private val mHandler = Handler(Looper.getMainLooper())
        override fun register(`object`: Any) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                super.register(`object`)
            } else {
                mHandler.post { super@MainThreadBus.register(`object`) }
            }
        }

        override fun post(event: Any) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                super.post(event)
            } else {
                mHandler.post { super@MainThreadBus.post(event) }
            }
        }
    }
}
