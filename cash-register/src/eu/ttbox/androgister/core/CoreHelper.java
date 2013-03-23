package eu.ttbox.androgister.core;

import android.util.Log;

public class CoreHelper {

    private static final String TAG = "CoreHelper";

    /**
     * For using it <br>
     * DisplayMetrics dm = new DisplayMetrics();
     * getWindowManager().getDefaultDisplay().getMetrics(dm);
     * CoreHelper.getTwoFragmentOr(dm.heightPixels);
     * 
     * @param sumZise
     * @return
     */
    public static int[] getTwoFragmentOr(int sumZise) {
        final float nombreOr = 1.6180339887f;
        float a = (sumZise * nombreOr) / (1 + nombreOr);
        int valA = Math.round(a);
        int valB = sumZise - valA;
        Log.i(TAG, String.format("The Size %s divided in A=%s et B=%s", sumZise, valA, valB));
        return new int[] { valA, valB };
    }

    public static Long[] convertToLongArray(long... checkedIds) {
        int checkedIdSize = checkedIds.length;
        Long[] deleteIds = new Long[checkedIdSize];
        for (int i = 0; i < checkedIdSize; i++) {
            deleteIds[i] = Long.valueOf(checkedIds[i]);
        }
        return deleteIds;
    }

}
