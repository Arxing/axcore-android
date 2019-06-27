package org.arxing.axutils_android;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.lang.reflect.Field;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/8/2
 *     desc  : 鍵盤相關工具類
 * </pre>
 */
public class KeyboardUtils {

    private KeyboardUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 避免輸入法面板遮擋
     * <p>在manifest.xml中activity中設置</p>
     * <p>android:windowSoftInputMode="adjustPan"</p>
     */

    /**
     * 動態隱藏軟鍵盤
     *
     * @param activity activity
     */
    public static boolean hideSoftInput(Activity activity) {
        View view = activity.getWindow().getDecorView();
        if (view == null) {
            return false;
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return false;
        }
        return imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    /**
     * 解決軟鍵盤造成的記憶體洩漏
     * 網站說明 : https://zhuanlan.zhihu.com/p/20828861
     * 解決網站 : http://blog.csdn.net/sodino/article/details/32188809
     *
     * @param destContext
     */
    public static void fixInputMethodManagerLeak(Context destContext) {
        if (destContext == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) destContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }

        String[] arr = new String[]{"mCurRootView", "mServedView", "mNextServedView"};
        Field f = null;
        Object obj_get = null;
        for (int i = 0; i < arr.length; i++) {
            String param = arr[i];
            try {
                f = imm.getClass().getDeclaredField(param);
                if (f.isAccessible() == false) {
                    f.setAccessible(true);
                } // author: sodino mail:sodino@qq.com
                obj_get = f.get(imm);
                if (obj_get != null && obj_get instanceof View) {
                    View v_get = (View) obj_get;
                    if (v_get.getContext() == destContext) { // 被InputMethodManager持有引用的context是想要目標銷毀的
                        f.set(imm, null); // 置空，破壞掉path to gc節點
                    } else {
                        // 不是想要目標銷毀的，即為又進了另一層界面了，不要處理，避免影響原邏輯,也就不用繼續for循環了
                        //                        if (QLog.isColorLevel()) {
                        //                            QLL.d(ReflecterHelper.class.getSimpleName(), QLog.CLR, "fixInputMethodManagerLeak
                        // break, context is not suitable, get_context=" + v_get.getContext()+" dest_context=" + destContext);
                        //                        }
                        break;
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    /**
     * 點擊屏幕空白區域隱藏軟鍵盤
     * <p>根據EditText所在坐標和用戶點擊的坐標相對比，來判斷是否隱藏鍵盤</p>
     * <p>需重寫dispatchTouchEvent</p>
     * <p>參照以下註釋代碼</p>
     */
    //    public static void clickBlankArea2HideSoftInput() {
        /*
        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                View v = getCurrentFocus();
                if (isShouldHideKeyboard(v, ev)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 根據EditText所在坐標和用戶點擊的坐標相對比，來判斷是否隱藏鍵盤
        private boolean isShouldHideKeyboard(View v, MotionEvent event) {
            if (v != null && (v instanceof EditText)) {
                int[] l = {0, 0};
                v.getLocationInWindow(l);
                int left = l[0],
                        top = l[1],
                        bottom = top + v.getHeight(),
                        right = left + v.getWidth();
                return !(event.getX() > left && event.getX() < right
                        && event.getY() > top && event.getY() < bottom);
            }
            return false;
        }
        */
    //    }

    /**
     * 動態顯示軟鍵盤
     *
     * @param edit 輸入框
     */
    public static void showSoftInput(EditText edit, Context context) {
        edit.setFocusable(true);
        edit.setFocusableInTouchMode(true);
        edit.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edit, 0);
    }

    /**
     * 切換鍵盤顯示與否狀態
     */
    public static void toggleSoftInput(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}