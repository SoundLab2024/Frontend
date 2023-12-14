package view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.cardview.widget.CardView;


public class CustomCardView extends CardView {

    public CustomCardView(Context context) {
        super(context);
    }

    public CustomCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCardView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        if (this.isPressed()) {
            this.setAlpha(0.5f);
        }
        else {
            this.setAlpha(1f);
        }
    }

}
