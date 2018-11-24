package com.team4.capstone.voiceofcau;

public class StatisticItem {
    private String dateStr ;
    private String scoreStr ;
    private String songnameStr ;
    private String useridStr ;


    public void setdate(String date) {
        dateStr = date ;
    }
    public void setscore(String score) {
        scoreStr = score ;
    }

    public void setsongname(String songname) {
        songnameStr = songname ;
    }

    public void setuserid(String userid) {
        useridStr = userid ;
    }


    public String getdate() {
        return this.dateStr ;
    }
    public String getscore() {
        return this.scoreStr ;
    }
    public String getsongname() {
        return this.songnameStr ;
    }
    public String getuserid() {
        return this.useridStr ;
    }

}
