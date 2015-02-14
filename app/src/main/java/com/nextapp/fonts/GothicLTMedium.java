package com.nextapp.fonts;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class GothicLTMedium extends TextView
{

    Context context;
    
    public GothicLTMedium(Context context, AttributeSet attrs, int defStyle)
    {
	super(context, attrs, defStyle);
	this.context = context;
	inite();
    }

    public GothicLTMedium(Context context, AttributeSet attrs)
    {
	super(context, attrs);
	this.context = context;
	inite();
    }

    public GothicLTMedium(Context context)
    {
	super(context);
	this.context = context;
	inite();
    }

    private void inite()
    {
	if (!isInEditMode())
	{
	    // Typeface face = Typeface.createFromAsset(context.getAssets(),
	    // "Bariol_Regular.otf");
	    // this.setTypeface(face);

	    this.setTypeface(TypeFaceProvider.get(context, "ITC Avant Garde Gothic LT Medium_1.ttf"));

	}
    }
}
