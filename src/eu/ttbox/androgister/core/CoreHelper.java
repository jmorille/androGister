package eu.ttbox.androgister.core;

import android.util.Log;

public class CoreHelper {

    private static final String TAG = "CoreHelper";
    
    public static int[] getTwoFragmentOr(int sumZise) {
        final float nombreOr = 1.6180339887f;
        float a = (sumZise * nombreOr) / (1 + nombreOr);
        int valA = Math.round(a);
        int valB = sumZise - valA;
        Log.i(TAG, String.format("The Size %s divided in A=%s et B=%s", sumZise, valA, valB));
        return new int[] { valA, valB };
    }
    
}
