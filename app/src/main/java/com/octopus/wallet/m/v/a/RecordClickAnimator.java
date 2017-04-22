/*
 * Copyright 2017, Peter Vincent
 * Licensed under the Apache License, Version 2.0, Vin Budget.
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.octopus.wallet.m.v.a;

import android.view.View;

import me.yoctopus.cac.anim.Anim;
import me.yoctopus.cac.anim.AnimDuration;
import me.yoctopus.cac.anim.Animator;

/**
 * Created by octopus on 10/1/16.
 */
public class RecordClickAnimator extends Animator {
    public RecordClickAnimator(View view) {
        super(view,
                Anim.Attention.pulse(),
                AnimDuration.ofLessThanHalfSecond());

    }
}
