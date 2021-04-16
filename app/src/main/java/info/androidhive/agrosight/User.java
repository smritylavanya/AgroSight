package info.androidhive.agrosight;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class User {
    SharedPreferences sharedPreferences;
    Context context;
    private String token;
    private String userId;
    private String fName;
    private String lName;
    private String email;
    private String phone;

    public User(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public  String getToken() {
        token = sharedPreferences.getString("token","");
        return token;
    }

    public void setToken(String token) {
        sharedPreferences.edit().putString("token",token).apply();
        this.token = token;
    }

    public String getUserId() {
        userId = sharedPreferences.getString("userId","");
        return userId;
    }

    public void setUserId(String userId) {
        sharedPreferences.edit().putString("userId",userId).apply();
        this.userId = userId;
    }

    public String getfName() {
        fName = sharedPreferences.getString("fName","");
        return fName;
    }

    public void setfName(String fName) {
        sharedPreferences.edit().putString("fName",fName).apply();
        this.fName = fName;
    }

    public String getlName() {
        lName = sharedPreferences.getString("lName","");
        return lName;
    }

    public void setlName(String lName) {
        sharedPreferences.edit().putString("lName",lName).apply();
        this.lName = lName;
    }

    public String getEmail() {
        email = sharedPreferences.getString("email","");
        return email;
    }

    public void setEmail(String email) {
        sharedPreferences.edit().putString("email",email).apply();
        this.email = email;
    }

    public String getPhone() {
        phone = sharedPreferences.getString("phone","");
        return phone;
    }

    public void setPhone(String phone) {
        sharedPreferences.edit().putString("phone",phone).apply();
        this.phone = phone;
    }

    public void removeUser(){
        setEmail("");
        setToken("");
        setfName("");
        setlName("");
        setUserId("");
        setPhone("");
    }
}
