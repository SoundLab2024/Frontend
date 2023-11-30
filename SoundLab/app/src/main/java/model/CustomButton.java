package model;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

public class CustomButton extends AppCompatButton {
    public CustomButton(@NonNull Context context){
        super(context);
        init();
    }
    public CustomButton(@NonNull Context context, @Nullable AttributeSet attrs){
        super(context, attrs);
        init();
    }

    public CustomButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (getContentDescription() == null || getContentDescription().toString().isEmpty()) {
            setContentDescription("Clickable area");
        }
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
