//package com.mission.schedule.view;
//
//import com.mission.schedule.R;
//
//import android.animation.Animator;
//import android.animation.Animator.AnimatorListener;
//import android.animation.AnimatorSet;
//import android.animation.ObjectAnimator;
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewTreeObserver;
//import android.view.ViewTreeObserver.OnGlobalLayoutListener;
//import android.widget.FrameLayout;
//import android.widget.RelativeLayout;
//
//public class LeftRightDeleteView extends FrameLayout {
//
//    private boolean isLeftOpen = false;
//    private boolean isRightOpen = false;
//    /**
//     * 单条滑动是否结束,防止连续点击
//     */
//    private boolean isOperationDone = true;
//    /**
//     * 动画执行的时间
//     */
//    private int duration = 250;
//
//    private RelativeLayout leftButton, rightButton;
//    private FrameLayout content;
//
//    private onLeftDeleteConform onConform;
//    
//    public LeftRightDeleteView(Context context) {
//        super(context);
//        initView(context);
//    }
//
//    public LeftRightDeleteView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        initView(context);
//    }
//
//    public LeftRightDeleteView(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        initView(context);
//    }
//    
//    /**
//     * 左边按钮打开右边删除试图事件
//     * @author milanoouser
//     *
//     */
//    public interface onLeftDeleteConform
//    {
//        public void onLeftDeleteButtonClick(View view);
//    }
//    
//    /**
//     * 初始化
//     * 
//     * @param context
//     */
//    private void initView(Context context)
//    {
//        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = mInflater.inflate(R.layout.adapter_edittag, this, true);
//
//        content = (FrameLayout) view.findViewById(R.id.content);
//        content.setVisibility(View.VISIBLE);
//        leftButton = (RelativeLayout) view.findViewById(R.id.delete_rl);
//        leftButton.setVisibility(View.INVISIBLE);
//        initViewDisplayPosition(leftButton);
//        rightButton = (RelativeLayout) view.findViewById(R.id.rightButton);
//        rightButton.setVisibility(View.INVISIBLE);
//        initViewDisplayPosition(rightButton);
//        
////        ((ImageButton)view.findViewById(R.id.leftDelete)).setOnClickListener(new OnClickListener() {
////            
////            @Override
////            public void onClick(View v) {
////                
////                ToggleRight();
////            }
////        });
//    }
//
//    /**
//     * 设置显示的内容
//     * 
//     * @param view
//     */
//    public void setContextView(View view)
//    {
//        content.removeAllViews();
//        content.addView(view);
//    }
//
//    /**
//     * 动态设置视图的布局
//     * 
//     * @param view
//     */
//    private void initViewDisplayPosition(final View view)
//    {
//        ViewTreeObserver vto2 = view.getViewTreeObserver();
//        vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                // 执行移动动画
//                int position = 0;
//                AnimatorSet set = new AnimatorSet();
//                if (view.getId() == R.id.delete_rl) {
//                    position = -view.getWidth();
//                } else {
//                    position = view.getWidth();
//                }
//                set.play(ObjectAnimator.ofFloat(view, "translationX", 0, position));
//                set.setDuration(1).start();
//            }
//        });
//    }
//    /**
//     * 打开右边删除视图
//     */
//    public void ToggleRight()
//    {
//        if (!isOperationDone) {
//            return;
//        }
//        
//        if (!isRightOpen) {
//            if (!isLeftOpen) {
//                //执行移动动画
//                AnimatorSet set = new AnimatorSet();
//                set.play(ObjectAnimator.ofFloat(content, "translationX", 0,-rightButton.getWidth()));
//                set.addListener(new AnimatorListener() {
//                    
//                    @Override
//                    public void onAnimationStart(Animator animation) {
//                        isOperationDone = false;
//                        rightButton.setVisibility(View.VISIBLE);
//                        //执行移动动画
//                        AnimatorSet set = new AnimatorSet();
//                        set.play(ObjectAnimator.ofFloat(rightButton, "translationX", rightButton.getWidth(),0));
//                        set.setDuration(duration).start();
//                    }
//                    
//                    @Override
//                    public void onAnimationRepeat(Animator animation) {
//                        
//                    }
//                    
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        isOperationDone = true;
//                        isRightOpen = true;
//                        isLeftOpen = false;
//                    }
//                    
//                    @Override
//                    public void onAnimationCancel(Animator animation) {
//                        
//                    }
//                });
//                set.setDuration(duration).start();
//            }else {
//                //执行移动动画
//                AnimatorSet set = new AnimatorSet();
//                set.play(ObjectAnimator.ofFloat(leftButton, "translationX", 0,-leftButton.getWidth()));
//                set.addListener(new AnimatorListener() {
//                    
//                    @Override
//                    public void onAnimationStart(Animator animation) {
//                        isOperationDone = false;
//                        //执行移动动画
//                        AnimatorSet set = new AnimatorSet();
//                        set.play(ObjectAnimator.ofFloat(content, "translationX", leftButton.getWidth(), - rightButton.getWidth()));
//                        set.addListener(new AnimatorListener() {
//                            
//                            @Override
//                            public void onAnimationStart(Animator animation) {
//                                rightButton.setVisibility(View.VISIBLE);
//                                AnimatorSet set = new AnimatorSet();
//                                set.play(ObjectAnimator.ofFloat(rightButton, "translationX", rightButton.getWidth(),0));
//                                set.setDuration(duration).start();
//                            }
//                            
//                            @Override
//                            public void onAnimationRepeat(Animator animation) {
//                                
//                            }
//                            
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                isOperationDone = true;
//                            }
//                            
//                            @Override
//                            public void onAnimationCancel(Animator animation) {
//                                
//                            }
//                        });
//                        set.setDuration(duration).start();
//                    }
//                    
//                    @Override
//                    public void onAnimationRepeat(Animator animation) {
//                        
//                    }
//                    
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        isRightOpen = true;
//                        isLeftOpen = false;
//                    }
//                    
//                    @Override
//                    public void onAnimationCancel(Animator animation) {
//                        
//                    }
//                });
//                set.setDuration(duration).start();
//            }
//        }else {
//            AnimatorSet set = new AnimatorSet();
//            set.play(ObjectAnimator.ofFloat(rightButton, "translationX",0, rightButton.getWidth()));
//            set.addListener(new AnimatorListener() {
//                
//                @Override
//                public void onAnimationStart(Animator animation) {
//                    isOperationDone = false;
//                    AnimatorSet set = new AnimatorSet();
//                    set.play(ObjectAnimator.ofFloat(content, "translationX", -rightButton.getWidth(),0));
//                    set.setDuration(duration).start();
//                }
//                
//                @Override
//                public void onAnimationRepeat(Animator animation) {
//                    
//                }
//                
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    isRightOpen = false;
//                    isOperationDone = true;
//                }
//                
//                @Override
//                public void onAnimationCancel(Animator animation) {
//                    
//                }
//            });
//            set.setDuration(duration).start();
//        }
//    }
//    /**
//     * 显示或不显示左边的内容
//     */
//    public void ToggleLeft()
//    {
//        if (!isOperationDone) {
//            return;
//        }
//        
//        if (isRightOpen) {
//            AnimatorSet set = new AnimatorSet();
//            set.play(ObjectAnimator.ofFloat(rightButton, "translationX", 0, rightButton.getWidth()));
//            set.addListener(new AnimatorListener() {
//                
//                @Override
//                public void onAnimationStart(Animator animation) {
//                   isOperationDone = false;
//                   AnimatorSet set = new AnimatorSet();
//                   set.play(ObjectAnimator.ofFloat(content, "translationX", -rightButton.getWidth(),leftButton.getWidth()));
//                   set.setDuration(duration).start();
//                   
//                   leftButton.setVisibility(View.VISIBLE);
//                   AnimatorSet set1 = new AnimatorSet();
//                   set1.play(ObjectAnimator.ofFloat(leftButton, "translationX", -leftButton.getWidth(), 0));
//                   set1.setDuration(duration).start();
//                }
//                
//                @Override
//                public void onAnimationRepeat(Animator animation) {
//                    
//                }
//                
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    isOperationDone = true;
//                    isRightOpen = false;
//                    isLeftOpen = true;
//                }
//                
//                @Override
//                public void onAnimationCancel(Animator animation) {
//                    
//                }
//            });
//            set.setDuration(duration).start();
//            
//        }else {
//            // 执行移动动画
//            AnimatorSet set = new AnimatorSet();
//            if (!isLeftOpen) {
//                set.play(ObjectAnimator.ofFloat(content, "translationX", 0, leftButton.getWidth()));
//            } else {
//                set.play(ObjectAnimator.ofFloat(content, "translationX", leftButton.getWidth(), 0));
//            }
//
//            set.addListener(new AnimatorListener() {
//                @Override
//                public void onAnimationStart(Animator animation) {
//                    isOperationDone = false;
//                    if (!isLeftOpen) {
//                        leftButton.setVisibility(View.VISIBLE);
//                        AnimatorSet set = new AnimatorSet();
//                        set.play(ObjectAnimator.ofFloat(leftButton, "translationX", -leftButton.getWidth(), 0));
//                        set.setDuration(duration).start();
//                    } else {
//                        AnimatorSet set = new AnimatorSet();
//                        set.play(ObjectAnimator.ofFloat(leftButton, "translationX", 0, -leftButton.getWidth()));
//                        set.setDuration(duration).start();
//                    }
//                }
//
//                @Override
//                public void onAnimationRepeat(Animator animation) {
//                }
//
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    isOperationDone = true;
//                    if (isLeftOpen) {
//                        leftButton.setVisibility(View.INVISIBLE);
//                    }
//                    isLeftOpen = !isLeftOpen;
//                }
//
//                @Override
//                public void onAnimationCancel(Animator animation) {
//
//                }
//            });
//            set.setDuration(duration).start();
//        }
//    }
//    /**
//     * 复位显示
//     */
//    public void RestView()
//    {
//        if (!isOperationDone) {
//            return;
//        }
//        //左边打开 关闭左边
//        if (isLeftOpen) {
//           ToggleLeft(); 
//        }
//        //右边打开 关闭右边
//        if (isRightOpen) {
//            ToggleRight();
//        }
//    }
//    
//    public boolean isLeftOpen() {
//        return isLeftOpen;
//    }
//    
//    public boolean isRightOpen() {
//        return isRightOpen;
//    }
//    
//    public void setDuration(int duration) {
//        this.duration = duration;
//    }
//    
//    public void setonleftButtonClickListener(onLeftDeleteConform  MonConform) {
//        onConform = MonConform;
//    }
//}
