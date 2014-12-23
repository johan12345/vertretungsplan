/**
 * Adapted from http://stackoverflow.com/a/12387343/450148
 *
 * @author Anton Averin
 * @author Felipe Micaroni Lalli
 */

package com.johan.vertretungsplan.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public final class FontUtils {
    /* cache for loaded Roboto typefaces*/
    private static Map<String, Typeface> typefaceCache = new HashMap<String, Typeface>();

    private FontUtils() {
    }

    /**
     * Creates Roboto typeface and puts it into cache
     */
    private static Typeface getRobotoTypeface(Context context, String fontType, Typeface typeface) {
        String fontPath = "fonts/" + getFontName(fontType, typeface) + ".ttf";
        Log.d("vertretungsplan", fontPath);

        if (!typefaceCache.containsKey(fontType)) {
            typefaceCache.put(fontType, Typeface.createFromAsset(context.getAssets(), fontPath));
        }

        return typefaceCache.get(fontType);
    }

    private static String getFontName(String fontType, Typeface typeface) {
        if (fontType == null)
            if (typeface == null)
                return "Roboto-Regular";
            else if (typeface.isBold() && typeface.isItalic())
                return "Roboto-BoldItalic";
            else if (typeface.isBold())
                return "Roboto-Bold";
            else if (typeface.isItalic())
                return "Roboto-Italic";
            else
                return "Roboto-Regular";
        else if (fontType.equals("roboto-light"))
            if (typeface == null)
                return "Roboto-Light";
            else if (typeface.isItalic())
                return "Roboto-LightItalic";
            else
                return "Roboto-Light";
        else if (fontType.equals("roboto-condensed"))
            if (typeface == null)
                return "RobotoCondensed-Regular";
            else if (typeface.isBold() && typeface.isItalic())
                return "RobotoCondensed-BoldItalic";
            else if (typeface.isBold())
                return "RobotoCondensed-Bold";
            else if (typeface.isItalic())
                return "RobotoCondensed-Italic";
            else
                return "RobotoCondensed-Regular";
        else if (fontType.equals("roboto-slab"))
            if (typeface == null)
                return "RobotoSlab-Regular";
            else if (typeface.isBold())
                return "RobotoSlab-Bold";
            else
                return "RobotoSlab-Regular";
        else if (fontType.equals("roboto-slab-light"))
            return "RobotoSlab-Light";
        else if (fontType.equals("roboto-slab-thin"))
            return "RobotoSlab-Thin";
        else if (fontType.equals("roboto-condensed-light"))
            if (typeface == null)
                return "RobotoCondensed-Light";
            else if (typeface.isItalic())
                return "RobotoCondensed-LightItalic";
            else
                return "RobotoCondensed-Light";
        else if (fontType.equals("roboto-thin"))
            if (typeface == null)
                return "Roboto-Thin";
            else if (typeface.isItalic())
                return "Roboto-ThinItalic";
            else
                return "Roboto-Thin";
        else if (fontType.equals("roboto-medium"))
            if (typeface == null)
                return "Roboto-Medium";
            else if (typeface.isItalic())
                return "Roboto-MediumItalic";
            else
                return "Roboto-Medium";
        else if (typeface == null)
            return "Roboto-Regular";
        else if (typeface.isBold() && typeface.isItalic())
            return "Roboto-BoldItalic";
        else if (typeface.isBold())
            return "Roboto-Bold";
        else if (typeface.isItalic())
            return "Roboto-Italic";
        else
            return "Roboto-Regular";
    }

    /**
     * Walks ViewGroups, finds TextViews and applies Typefaces taking styling in consideration
     *
     * @param context - to reach assets
     * @param view    - root view to apply typeface to
     */
    public static void setRobotoFont(Context context, View view) {
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                setRobotoFont(context, ((ViewGroup) view).getChildAt(i));
            }
        } else if (view instanceof TextView) {
            Typeface currentTypeface = ((TextView) view).getTypeface();
            ((TextView) view).setTypeface(getRobotoTypeface(context, (String) view.getTag(), currentTypeface));
        }
    }
}