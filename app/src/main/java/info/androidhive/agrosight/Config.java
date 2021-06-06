package info.androidhive.agrosight;

public class Config {
    public static class URLs{
        static String domain = "10.0.2.2:5000";  // For Emulator
//        static String domain = "192.168.101.5:5000";
        static String prefix = "http://";

        static String loginUrl = prefix+domain+"/user/login";
        static String PostQuestion =prefix+domain+"/qa/add-question";
        static String registerUrl = prefix+domain+"/user/register";
        static String fetchQuestionsUrl = prefix+domain+"/qa/get-paged-questions/";
        static String fetchAnswerUrl = prefix+domain+"/qa/get-answers/";
        static String getsingleQuestionUrl=prefix+domain+"/qa/get-question/";
        static String chatSocketUrl = prefix+domain+"/chat";
        static String getDialogMetaDataUrl = prefix+domain+"/chat/get-dialogs-metadata";
        static String getDialogUrl = prefix+domain+"/chat/get-dialog";
        static String imagePrefix = prefix+domain;
        static String qaAttachmentsUrl = prefix+domain+"/qa/get-q-attachments/";
        static String addAnswerurl = prefix+domain+"/qa/add-answer";
        static String getAllMessagesUrl = prefix+domain+"/chat/get-all-messages/";
        static String getQuestionsByTagUrl = prefix+domain+"/qa/get-questions-by-tag/";
    }
}
