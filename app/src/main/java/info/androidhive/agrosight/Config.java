package info.androidhive.agrosight;

public class Config {
    public static class URLs{
        static String domain = "10.0.2.2:5000";
        static String prefix = "http://";

        static String loginUrl = prefix+domain+"/user/login";
        static String registerUrl = prefix+domain+"/user/register";
    }
}
