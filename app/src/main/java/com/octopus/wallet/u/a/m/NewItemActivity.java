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

package com.octopus.wallet.u.a.m;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.octopus.wallet.R;
import com.octopus.wallet.m.v.a.ButtonAnimator;
import com.octopus.wallet.u.a.BActivity;
import com.octopus.wallet.u.a.mo.ExportActivity;
import com.octopus.wallet.u.a.mo.NewShoppingListActivity;
import com.octopus.wallet.u.a.t.ReceiptActivity;
import com.octopus.wallet.u.a.t.TransactionActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.yoctopus.cac.anim.Anim;
import me.yoctopus.cac.anim.AnimDuration;
import me.yoctopus.cac.anim.Animator;


public class NewItemActivity extends BActivity {
    @InjectView(R.id.record_icon)
    ImageView record;
    @InjectView(R.id.shopping_icon)
    ImageView shopping;
    @InjectView(R.id.receipt_icon)
    ImageView receipt;
    @InjectView(R.id.report_icon)
    ImageView report;
    @InjectView(R.id.fab)
    FloatingActionButton fab;
    @InjectView(R.id.ad_view)
    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);
        ButterKnife.inject(this);
        record.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doAction(
                                new Action() {
                                    @Override
                                    public void action() {
                                        startActivity(
                                                TransactionActivity.class,
                                                null);
                                    }
                                },
                                record);

                    }
                });

        shopping.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doAction(
                                new Action() {
                                    @Override
                                    public void action() {
                                        startActivity(
                                                NewShoppingListActivity.class,
                                                null);
                                    }
                                },
                                shopping);

                    }
                });

        receipt.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doAction(
                                new Action() {
                                    @Override
                                    public void action() {
                                        startActivity(
                                                ReceiptActivity.class,
                                                null);
                                    }
                                },
                                receipt);

                    }
                });

        report.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doAction(
                                new Action() {
                                    @Override
                                    public void action() {
                                        startActivity(
                                                ExportActivity.class,
                                                null);
                                    }
                                },
                                report);

                    }
                });
        fab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        exit(view);
                    }
                });
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        adView.loadAd(adRequest);

    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }


    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
        new ButtonAnimator(
                record)
                .addAnim(Anim.Zoom.zoomIn())
                .addAnimatorListener(
                        new Animator.AnimatorListener() {
                            @Override
                            public void onStartAnimator(
                                    Animator animator) {

                            }

                            @Override
                            public void onRepeatAnimator(
                                    Animator animator) {

                            }

                            @Override
                            public void onStopAnimator(
                                    Animator animator) {
                                animator.setWaitDuration(
                                        AnimDuration.ofSecond());
                                animator.setView(
                                        shopping);
                                animator.setAnimatorListener(
                                        new Animator.AnimatorListener() {
                                            @Override
                                            public void onStartAnimator(
                                                    Animator animator) {

                                            }

                                            @Override
                                            public void onRepeatAnimator(
                                                    Animator animator) {

                                            }

                                            @Override
                                            public void onStopAnimator(
                                                    Animator animator) {
                                                animator.setView(
                                                        receipt);
                                                animator.setAnimatorListener(
                                                        new Animator.AnimatorListener() {
                                                            @Override
                                                            public void onStartAnimator(
                                                                    Animator animator) {

                                                            }

                                                            @Override
                                                            public void onRepeatAnimator(
                                                                    Animator animator) {

                                                            }

                                                            @Override
                                                            public void onStopAnimator(
                                                                    Animator animator) {
                                                                animator.setView(
                                                                        report);
                                                                animator.setAnimatorListener(
                                                                        null);
                                                                animator.animate();
                                                            }
                                                        });
                                                animator.animate();
                                            }
                                        });
                                animator.animate();
                            }
                        })
                .addAnimDuration(
                        AnimDuration.ofSecond())
                .addWaitDuration(
                        AnimDuration.standard())
                .animate();
    }

    private void doAction(final Action action,
                          View view) {
        new ButtonAnimator(view)
                .addAnimatorListener(
                        new Animator.AnimatorListener() {
                            @Override
                            public void onStartAnimator(
                                    Animator animator) {

                            }

                            @Override
                            public void onRepeatAnimator(
                                    Animator animator) {

                            }

                            @Override
                            public void onStopAnimator(
                                    Animator animator) {
                                new Animator()
                                        .addView(
                                                getView(
                                                        android.R.id.content))
                                        .addAnim(
                                                Anim.Fade.fadeOut())
                                        .addAnimatorListener(
                                                new Animator.AnimatorListener() {
                                                    @Override
                                                    public void onStartAnimator(
                                                            Animator animator) {

                                                    }

                                                    @Override
                                                    public void onRepeatAnimator(
                                                            Animator animator) {

                                                    }

                                                    @Override
                                                    public void onStopAnimator(
                                                            Animator animator) {
                                                        NewItemActivity.super.finish();
                                                        action.action();
                                                    }
                                                })
                                        .addAnimDuration(
                                                AnimDuration.ofSecond())
                                        .addWaitDuration(
                                                AnimDuration.ofHalfSecond())
                                        .animate();
                            }
                        })
                .animate();

    }

    private void exit(View view) {
        new ButtonAnimator(view)
                .addAnimatorListener(
                        new Animator.AnimatorListener() {
                            @Override
                            public void onStartAnimator(
                                    Animator animator) {

                            }

                            @Override
                            public void onRepeatAnimator(
                                    Animator animator) {

                            }

                            @Override
                            public void onStopAnimator(
                                    Animator animator) {
                                finish();
                            }
                        })
                .animate();
    }

    @Override
    public void finish() {
        new Animator()
                .addView(getView(
                        android.R.id.content))
                .addAnim(
                        Anim.Fade.fadeOut())
                .addAnimatorListener(
                        new Animator.AnimatorListener() {
                            @Override
                            public void onStartAnimator(
                                    Animator animator) {

                            }

                            @Override
                            public void onRepeatAnimator(
                                    Animator animator) {

                            }

                            @Override
                            public void onStopAnimator(
                                    Animator animator) {
                                NewItemActivity.super.finish();

                            }
                        })
                .addAnimDuration(
                        AnimDuration.ofSecond())
                .addWaitDuration(
                        AnimDuration.ofHalfSecond())
                .animate();
    }

    private interface Action {
        void action();
    }

}
