package info.androidhive.agrosight;

public class Config {
    public static class URLs{
        static String domain = "10.0.2.2:5000";  // For Emulator
//        static String domain = "192.168.29.99:5000";
        static String prefix = "http://";

        static String loginUrl = prefix+domain+"/user/login";
        static String PostQuestion =prefix+domain+"/qa/add-question";
        static String registerUrl = prefix+domain+"/user/register";
        static String fetchQuestionsUrl = prefix+domain+"/qa/get-paged-questions/";
    }
}
