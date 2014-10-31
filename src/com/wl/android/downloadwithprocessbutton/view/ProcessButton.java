package com.wl.android.downloadwithprocessbutton.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;

import com.wl.android.downloadwithprogressbutton.R;

public abstract class ProcessButton extends FlatButton {

    private int mProgress;
    private int mMaxProgress;
    private int mMinProgress;
    
    private StateListDrawable mCompleteDrawable;
	private StateListDrawable mErrorDrawable;
	
    private GradientDrawable mDisabledDrawable;
    private GradientDrawable mProgressDrawable;

    private CharSequence mLoadingText;
    private CharSequence mCompleteText;
    private CharSequence mErrorText;

    public ProcessButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public ProcessButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ProcessButton(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        mMinProgress = 0;
        mMaxProgress = 100;

        mProgressDrawable = (GradientDrawable) getDrawable(R.drawable.rect_progress).mutate();
        mProgressDrawable.setCornerRadius(getCornerRadius());
        mCompleteDrawable = new StateListDrawable();
		mErrorDrawable = new StateListDrawable();
      /*  mCompleteDrawable = (GradientDrawable) getDrawable(R.drawable.rect_complete).mutate();
        mCompleteDrawable.setCornerRadius(getCornerRadius());

        mErrorDrawable = (GradientDrawable) getDrawable(R.drawable.rect_error).mutate();
        mErrorDrawable.setCornerRadius(getCornerRadius());*/
        
        mDisabledDrawable = (GradientDrawable) getDrawable(R.drawable.rect_diabled).mutate();
        mDisabledDrawable.setCornerRadius(getCornerRadius());

        if (attrs != null) {
            initAttributes(context, attrs);
        }
    }

    private void initAttributes(Context context, AttributeSet attributeSet) {
        TypedArray attr = getTypedArray(context, attributeSet, R.styleable.ProcessButton);

        if (attr == null) {
            return;
        }

        try {
            mLoadingText = attr.getString(R.styleable.ProcessButton_pb_textProgress);
            mCompleteText = attr.getString(R.styleable.ProcessButton_pb_textComplete);
            mErrorText = attr.getString(R.styleable.ProcessButton_pb_textError);

            int purple = getColor(R.color.green_complete);
            int colorProgress = attr.getColor(R.styleable.ProcessButton_pb_colorProgress, purple);
            mProgressDrawable.setColor(colorProgress);
            
        	mCompleteDrawable.addState(
					new int[] { android.R.attr.state_pressed },
					createCompletePressedDrawable(attr));
			mCompleteDrawable
					.addState(new int[] {}, createCompleteNormalDrawable(attr));

			mErrorDrawable.addState(new int[] { android.R.attr.state_pressed },
					createErrorPressedDrawable(attr));
			mErrorDrawable.addState(new int[] {}, createErrorNormalDrawable(attr));

          /*  int green = getColor(R.color.green_complete);
            int colorComplete = attr.getColor(R.styleable.ProcessButton_pb_colorComplete, green);
            mCompleteDrawable.setColor(colorComplete);

            int red = getColor(R.color.red_error);
            int colorError = attr.getColor(R.styleable.ProcessButton_pb_colorError, red);
            mErrorDrawable.setColor(colorError);*/

        } finally {
            attr.recycle();
        }
    }

    public void setProgress(int progress) {
        mProgress = progress;

        if (mProgress == mMinProgress) {
            onNormalState();
        } else if (mProgress == mMaxProgress) {
            onCompleteState();
        } else if (mProgress < mMinProgress){
            onErrorState();
        } else {
            onProgress();
        }

        invalidate();
    }
    
    public void setDisabled(boolean type){
    	if (type) {
    		setEnabled(false);
    		onDisabledState();
		}else {
			setEnabled(true);
//			onNormalState();
			
		}
    	
    	invalidate();
    	
    }

    protected void onErrorState() {
        if(getErrorText() != null) {
            setText(getErrorText());
        }
        setBackgroundCompat(getmErrorDrawable());
    }

    protected void onProgress() {
        if(getLoadingText() != null) {
            setText(getLoadingText());
        }
        setBackgroundCompat(getNormalDrawable());
    }

    protected void onCompleteState() {
        if(getCompleteText() != null) {
            setText(getCompleteText());
        }
        setBackgroundCompat(getmCompleteDrawable());
    }

    protected void onNormalState() {
        if(getNormalText() != null) {
            setText(getNormalText());
        }
        setBackgroundCompat(getNormalDrawable());
    }
    
    protected void onDisabledState() {
       /**
        * 不设定文字提示，在代码中设置
        */
    	
        setBackgroundCompat(getmDisabledDrawable());
    }
    
    

    @Override
    protected void onDraw(Canvas canvas) {
        // progress
        if(mProgress > mMinProgress && mProgress < mMaxProgress) {
            drawProgress(canvas);
        }

        super.onDraw(canvas);
    }

    public abstract void drawProgress(Canvas canvas);

    public int getProgress() {
        return mProgress;
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    public int getMinProgress() {
        return mMinProgress;
    }

    public GradientDrawable getProgressDrawable() {
        return mProgressDrawable;
    }



    public CharSequence getLoadingText() {
        return mLoadingText;
    }

    public CharSequence getCompleteText() {
        return mCompleteText;
    }

    /**   
	 * mDisabledDrawable   
	 *   
	 * @return the mDisabledDrawable   
	 *
	 */
	
	public GradientDrawable getmDisabledDrawable() {
		return mDisabledDrawable;
	}

	/**   
	 * @param mDisabledDrawable the mDisabledDrawable to set   
	 */
	
	public void setmDisabledDrawable(GradientDrawable mDisabledDrawable) {
		this.mDisabledDrawable = mDisabledDrawable;
	}

	public void setProgressDrawable(GradientDrawable progressDrawable) {
        mProgressDrawable = progressDrawable;
    }

    

    public void setLoadingText(CharSequence loadingText) {
        mLoadingText = loadingText;
    }

    public void setCompleteText(CharSequence completeText) {
        mCompleteText = completeText;
    }

 

    public CharSequence getErrorText() {
        return mErrorText;
    }

    public void setErrorText(CharSequence errorText) {
        mErrorText = errorText;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.mProgress = mProgress;

        return savedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            SavedState savedState = (SavedState) state;
            mProgress = savedState.mProgress;
            super.onRestoreInstanceState(savedState.getSuperState());
            setProgress(mProgress);
        } else {
            super.onRestoreInstanceState(state);
        }
    }

	private GradientDrawable createCompleteNormalDrawable(TypedArray attr) {
		
		GradientDrawable drawableTop = (GradientDrawable) getDrawable(
				R.drawable.rect_complete).mutate();


		return drawableTop;
	}

	private Drawable createCompletePressedDrawable(TypedArray attr) {
		GradientDrawable drawablePressed = (GradientDrawable) getDrawable(
				R.drawable.rect_complete_pressed).mutate();
		

		return drawablePressed;
	}
	
	private GradientDrawable createErrorNormalDrawable(TypedArray attr) {
		GradientDrawable drawableNormal = (GradientDrawable) getDrawable(
				R.drawable.rect_error).mutate();	
	
		return drawableNormal;
	}

	private Drawable createErrorPressedDrawable(TypedArray attr) {
		GradientDrawable drawablePressed = (GradientDrawable) getDrawable(
				R.drawable.rect_error_pressed).mutate();		
		return drawablePressed;
	}
	/**
	 * mCompleteDrawable
	 * 
	 * @return the mCompleteDrawable
	 * 
	 */

	public StateListDrawable getmCompleteDrawable() {
		return mCompleteDrawable;
	}

	/**
	 * mErrorDrawable
	 * 
	 * @return the mErrorDrawable
	 * 
	 */

	public StateListDrawable getmErrorDrawable() {
		return mErrorDrawable;
	}

    /**
     * A {@link android.os.Parcelable} representing the {@link com.dd.processbutton.ProcessButton}'s
     * state.
     */
    public static class SavedState extends BaseSavedState {

        private int mProgress;

        public SavedState(Parcelable parcel) {
            super(parcel);
        }

        private SavedState(Parcel in) {
            super(in);
            mProgress = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mProgress);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
