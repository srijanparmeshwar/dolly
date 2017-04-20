package srijanparmeshwar.dolly;

import android.os.Handler;
import android.os.HandlerThread;

/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modifications 2017 Srijan Parmeshwar
 */
public class HandlerUtils {
    private static Handler backgroundHandler;

    public static Handler getHandler() {
        if (backgroundHandler == null) {
            HandlerThread thread = new HandlerThread("Background");
            thread.start();
            backgroundHandler = new Handler(thread.getLooper());
        }
        return backgroundHandler;
    }

    public static void stop() {
        if (backgroundHandler != null) {
            backgroundHandler.getLooper().quitSafely();
            backgroundHandler = null;
        }
    }
}
