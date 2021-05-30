package info.androidhive.agrosight;

class Answer{
    public String answer;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getUpvote() {
        return upvote;
    }

    public void setUpvote(int upvote) {
        this.upvote = upvote;
    }

    public int getDownvote() {
        return downvote;
    }

    public void setDownvote(int downvote) {
        this.downvote = downvote;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String firstname;
    public String lastname;
    public int upvote;
    public int downvote;
    public int id;
    public int uid;

    public Answer(int id,String answer,String firstname,String lastname, int upvote, int downvote) {
        this.id = id;
        this.answer= answer;
        this.firstname = firstname;
        this.lastname = lastname;
        this.upvote= upvote;
        this.downvote = downvote;
    }
}
