package eu.ttbox.androgister;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.MenuItem;

import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.CanvasTransformer;
import com.slidingmenu.lib.app.SlidingActivity;

public class AndroGisterActivity extends SlidingActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the Behind View
        setBehindContentView(R.layout.slidingmenu_frame);

        // customize the SlidingMenu
        SlidingMenu sm = customizeSlidingMenu();

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // ===========================================================
    // Menu Overide
    // ===========================================================

 
    public AndroGisterApplication getAndroGisterApplication() {
       return (AndroGisterApplication)  super.getApplication();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
     
        switch (item.getItemId()) {
        case android.R.id.home:
            toggle();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public SlidingMenu customizeSlidingMenu() {

        SlidingMenu slidingMenu = getSlidingMenu();

        slidingMenu.setMode(SlidingMenu.LEFT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        slidingMenu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
        slidingMenu.setShadowDrawable(R.drawable.shadow);
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.setFadeDegree(0.35f);
        slidingMenu.setBehindScrollScale(0.35f);
        slidingMenu.setSlidingEnabled(true);

        slidingMenu.setMenu(R.layout.slidingmenu_menu);
        slidingMenu.setSelectorEnabled(true);
        // slidingMenu.setSelectorDrawable(R.drawable.slidingmenu_selector);

        slidingMenu.setBehindCanvasTransformer(smTransformer);
        return slidingMenu;
    }

    // ===========================================================
    // Sliding Annimation
    // ===========================================================

    private static CanvasTransformer smTransformer = new CanvasTransformer() {
        @Override
        public void transformCanvas(Canvas canvas, float percentOpen) {
            float scale = (float) (percentOpen * 0.25 + 0.75);
            canvas.scale(scale, scale, canvas.getWidth() / 2, canvas.getHeight() / 2);
        }
    };
}