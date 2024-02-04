package kg.dev.videoeditor.utils;

import android.content.Context;
import android.view.View;

import androidx.annotation.IdRes;

import java.lang.ref.WeakReference;

public class BaseUtils {

    private static final String ERROR_INIT = "Initialize BaseUtils with invoke init()";

    private static WeakReference<Context> mWeakReferenceContext;

    /**
     * init in Application
     */
    public static void init(Context ctx) {
        mWeakReferenceContext = new WeakReference<>(ctx);
        //something to do...
    }

    public static Context getContext() {
        if (mWeakReferenceContext == null) {
            throw new IllegalArgumentException(ERROR_INIT);
        }
        return mWeakReferenceContext.get().getApplicationContext();
    }

    public static <T> T findRequiredViewAsType(View source, @IdRes int id, String who, Class<T> cls) {
        View view = findRequiredView(source, id, who);
        return castView(view, id, who, cls);
    }

    public static View findRequiredView(View source, @IdRes int id, String who) {
        View view = source.findViewById(id);
        if (view != null) {
            return view;
        } else {
            String name = getResourceEntryName(source, id);
            throw new IllegalStateException("Required view '" + name + "' with ID " + id + " for " + who + " was not found. If this view is optional add '@Nullable' (fields) or '@Optional' (methods) annotation.");
        }
    }

    private static String getResourceEntryName(View view, @IdRes int id) {
        return view.isInEditMode() ? "<unavailable while editing>" : view.getContext().getResources().getResourceEntryName(id);
    }

    public static <T> T castView(View view, @IdRes int id, String who, Class<T> cls) {
        try {
            return cls.cast(view);
        } catch (ClassCastException var6) {
            String name = getResourceEntryName(view, id);
            throw new IllegalStateException("View '" + name + "' with ID " + id + " for " + who + " was of the wrong type. See cause for more info.", var6);
        }
    }

}
