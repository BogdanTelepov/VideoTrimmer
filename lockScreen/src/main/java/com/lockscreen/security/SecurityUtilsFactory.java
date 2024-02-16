package com.lockscreen.security;

public class SecurityUtilsFactory {

    public static ISecurityUtils getPFSecurityUtilsInstance() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            return SecurityUtils.getInstance();
        } else {
            return SecurityUtilsOld.getInstance();
        }
    }

}
