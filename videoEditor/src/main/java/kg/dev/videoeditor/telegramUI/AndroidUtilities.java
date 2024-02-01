package kg.dev.videoeditor.telegramUI;

public class AndroidUtilities {

    public static float density = 1;
    public static int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(density * value);
    }


}
