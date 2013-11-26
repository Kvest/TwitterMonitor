package com.kvest.twittermonitor.network;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.util.LruCache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.kvest.twittermonitor.core.TwitterMonitorApplication;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 24.11.13
 * Time: 19:49
 * To change this template use File | Settings | File Templates.
 */
public class VolleyHelper {
    private static final float DEFAULT_MEM_CACHE_PERCENT = 0.6f;

    private static VolleyHelper instance;
    private static final Object lock = new Object();

    private RequestQueue requestQueue;
    private ImageLoader imageLoader;

    public static VolleyHelper getInstance() {
        synchronized (lock) {
            if (instance == null) {
                instance = new VolleyHelper();
            }
        }

        return instance;
    }

    private VolleyHelper() {
        requestQueue = Volley.newRequestQueue(TwitterMonitorApplication.getContext());// thread pool(4)
        imageLoader = new ImageLoader(requestQueue, new LruBitmapCache(DEFAULT_MEM_CACHE_PERCENT));
    }

    public Request addRequest(Request request) {
        if (requestQueue != null) {
            return requestQueue.add(request);
        } else {
            return null;
        }
    }

    public void cancelAll(final Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    private static class LruBitmapCache implements ImageLoader.ImageCache {
        private LruCache<String, Bitmap> cache;

        public LruBitmapCache(float memCachePercent) {
            int memCacheSize = Math.round(memCachePercent * Runtime.getRuntime().maxMemory() / 1024);
            cache = new LruCache<String, Bitmap>(memCacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    final int bitmapSize = getBitmapSize(value) / 1024;
                    return bitmapSize == 0 ? 1 : bitmapSize;
                }
            };
        }

        @Override
        public Bitmap getBitmap(String url) {
            return cache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            cache.put(url, bitmap);
        }

        public static int getBitmapSize(Bitmap bitmap) {
            if (hasHoneycombMR1()) {
                return bitmap.getByteCount();
            }

            // Pre HC-MR1
            return bitmap.getRowBytes() * bitmap.getHeight();
        }

        public static boolean hasHoneycombMR1() {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
        }
    }
}
