/*
 * Copyright 2015 Priyesh Patel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.priyesh.hexatime

import android.content.Context

public val KEY_ENABLE_24_HOUR: String = "enable_24_hour"
public val KEY_ENABLE_NUMBER_SIGN: String = "enable_number_sign"
public val KEY_CLOCK_DIVIDER: String = "clock_divider"
public val KEY_CLOCK_SIZE: String = "clock_size"
public val KEY_DISABLE_CLOCK: String = "enable_clock"

public fun Context.getPixels(dpValue: Int): Float =
        (dpValue * getResources().getDisplayMetrics().density + 0.5f)