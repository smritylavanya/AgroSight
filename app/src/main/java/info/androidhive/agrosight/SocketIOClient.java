package info.androidhive.agrosight;


import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class SocketIOClient {
    private static Socket mSocket;
    public static Socket getmSocket(String token) throws URISyntaxException{
        if (mSocket == null){
            try {
                IO.Options options = new IO.Options();
                options.query = "auth="+token;
                mSocket = IO.socket(Config.URLs.chatSocketUrl,options);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        return mSocket;
    }
}
