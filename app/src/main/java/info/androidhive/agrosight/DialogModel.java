package info.androidhive.agrosight;

public class DialogModel{
    private final int id;
    private final String type;
    private final String data;
    private final int unseenCount;
    private final String fName;
    private final String lName;

    public DialogModel(int id, String type, String data, int unseenCount, String fName, String lName) {
        this.id = id;
        this.type = type;
        this.data = data;
        this.unseenCount = unseenCount;
        this.fName = fName;
        this.lName = lName;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public int getUnseenCount() {
        return unseenCount;
    }

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }
}
