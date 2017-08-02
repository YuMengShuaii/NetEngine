/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.enation.javashop.net.engine.lib.rxandroid;

import android.os.Looper;

import com.enation.javashop.net.engine.lib.rxandroid.schedulers.AndroidSchedulers;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.disposables.Disposable;

public abstract class MainThreadDisposable implements Disposable {
    /**
     * Verify that the calling thread is the Android main thread.
     * <p>
     * Calls to this method are usually preconditions for subscription behavior which instances of
     * this class later undo. See the class documentation for an example.
     *
     * @throws IllegalStateException when called from any other thread.
     */
    public static void verifyMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException(
                "Expected to be called on the main thread but was " + Thread.currentThread().getName());
        }
    }

    private final AtomicBoolean unsubscribed = new AtomicBoolean();

    @Override
    public final boolean isDisposed() {
        return unsubscribed.get();
    }

    @Override
    public final void dispose() {
        if (unsubscribed.compareAndSet(false, true)) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                onDispose();
            } else {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                    @Override public void run() {
                        onDispose();
                    }
                });
            }
        }
    }

    protected abstract void onDispose();
}
