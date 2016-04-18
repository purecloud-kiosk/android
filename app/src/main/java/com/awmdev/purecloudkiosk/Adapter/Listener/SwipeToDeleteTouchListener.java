package com.awmdev.purecloudkiosk.Adapter.Listener;

import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

public class SwipeToDeleteTouchListener implements View.OnTouchListener
{
    private OnSwipeDetectedListener onSwipeDetectedListener;
    private VelocityTracker velocityTracker;
    private int minFlingVelocity;
    private int maxFlingVelocity;
    private View parentView;
    private boolean swiping;
    private int viewHeight;
    private int touchSlop;
    private int viewWidth;
    private float downX;
    private float downY;

    public interface OnSwipeDetectedListener
    {
        void onSwipeComplete();
    }

    public SwipeToDeleteTouchListener(View parentView)
    {
        //save the view that is being swiped
        this.parentView = parentView;
        //grab the window configuration to determine min and max swipe velocity
        ViewConfiguration vc = ViewConfiguration.get(parentView.getContext());
        //grab the max and min swipe velocities
        minFlingVelocity = vc.getScaledMinimumFlingVelocity();
        maxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        //grab the slop from the vc
        touchSlop = vc.getScaledTouchSlop();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN: {
                //obtain the position to use for tracking and translation
                downX = event.getRawX();
                downY = event.getRawY();
                //set swiping to false since it hasn't started yet
                swiping = false;
                //obtain a velocity tracker
                velocityTracker = VelocityTracker.obtain();
                //add the motion event
                velocityTracker.addMovement(event);
                //grab the width and height of the view
                viewWidth = parentView.getWidth();
                viewHeight = parentView.getHeight();
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                //check to confirm that we have a velocity tracker, if not break
                if (velocityTracker == null)
                    break;
                //add the current movement to the tracker
                velocityTracker.addMovement(event);
                //grab the delta values for y and x
                float deltaX = event.getRawX() - downX;
                float deltaY = event.getRawY() - downY;
                //determine if the difference is enough for a swipe event
                if (Math.abs(deltaX) > touchSlop && Math.abs(deltaY) < Math.abs(deltaX) / 2)
                {
                    //swipe was greater than slop, so we are now swiping
                    swiping = true;
                    //calculate the swiping slop
                    float swipingSlop = deltaX > 0 ? touchSlop : -touchSlop;
                    //translate the view by the delta x
                    parentView.setTranslationX(deltaX - swipingSlop);
                    //change the view visibility based to the distance
                    parentView.setAlpha((Math.max(0f, Math.min(1f,
                            1f - 2f * Math.abs(deltaX) / viewWidth))));
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                if (velocityTracker == null)
                    break;
                //add the current event to the velocity tracker
                velocityTracker.addMovement(event);
                //compute the velocity in pixels per second
                velocityTracker.computeCurrentVelocity(1000);
                //grab the current velocity for the x and y axis
                float velocityX = Math.abs(velocityTracker.getXVelocity());
                float velocityY = Math.abs(velocityTracker.getYVelocity());
                //determine the new delta position for x
                float deltaX = downX - event.getRawX();
                float deltaY = downY - event.getRawY();
                //boolean to determine if we should preform the swipe
                boolean preformSwipe = false;
                //boolean to determine the direction of the swipe
                boolean swipeRight = false;
                //determine if the view was dragged far enough off screen
                if (Math.abs(deltaX) > (viewWidth/2))
                {
                    //condition satisfied, preform the swipe
                    preformSwipe = true;
                    //determine the direction
                    swipeRight = deltaX > 0;
                }
                else
                {
                    //determine is the velocity is enough for a swipe action
                    if (velocityX >= minFlingVelocity && velocityX <= maxFlingVelocity
                            && velocityY < velocityX && swiping)
                    {
                        //check to see if the velocity and direction are the same
                        if((velocityX > 0 && deltaX > 0) || (velocityX < 0 && deltaX < 0))
                        {
                            //a fling gesture has been satisfied
                            preformSwipe = true;
                            //determine the direction of the fling
                            swipeRight = (velocityTracker.getXVelocity() > 0);
                        }
                    }
                }
                //check to determine if conditions have been met to swipe
                if(preformSwipe)
                {
                    //animate the view outside of the recycler
                    parentView.animate()
                            .translationX(swipeRight ? viewWidth : -viewWidth)
                            .alpha(0)
                            .setDuration(1000)
                            .setListener(null);
                    //notify the view that the item has been successfully swiped.
                    if(onSwipeDetectedListener != null)
                        onSwipeDetectedListener.onSwipeComplete();
                }
                else
                {
                    //animate the view back to its original position
                    parentView.animate()
                            .translationX(0)
                            .alpha(1)
                            .setDuration(1000)
                            .setListener(null);
                    //check to see if this is considered a click event
                    if(Math.abs(deltaX) < touchSlop && Math.abs(deltaY) < touchSlop)
                    {
                        //user did not slide the view, dispatch a click event
                        parentView.performClick();
                    }
                }
                velocityTracker.recycle();
                velocityTracker = null;
                swiping = false;
                downX = 0;
                downY = 0;
                break;
            }

            case MotionEvent.ACTION_CANCEL:{
                if(swiping)
                {
                    parentView.animate()
                            .translationX(0)
                            .alpha(1)
                            .setDuration(1000)
                            .setListener(null);
                }
                velocityTracker.recycle();
                velocityTracker = null;
                swiping = false;
                downX = 0;
                downY = 0;
                break;
            }
        }
        //return true since were handling all touch events
        return true;
    }

    public void setSwipeDetectedListener(OnSwipeDetectedListener onSwipeDetectedListener)
    {
        this.onSwipeDetectedListener = onSwipeDetectedListener;
    }

}