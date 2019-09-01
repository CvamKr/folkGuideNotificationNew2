package com.creation.android.receivenoti;

public class FolkBoyData {
    String fb_berth_pref, fb_email, fb_message;
    String fb_id;

    public FolkBoyData(String fb_berth_pref, String _fb_email, String _fb_message, String fb_id) {
        this.fb_berth_pref = fb_berth_pref;
        this.fb_email = _fb_email;
        this.fb_message = _fb_message;
        this.fb_id = fb_id;
    }

    public FolkBoyData() {
    }

    public String getFb_berth_pref() {
        return fb_berth_pref;
    }

    public void setFb_berth_pref(String fb_berth_pref) {
        this.fb_berth_pref = fb_berth_pref;
    }

    public String getFb_email() {
        return fb_email;
    }

    public void setFb_email(String fb_email) {
        this.fb_email = fb_email;
    }

    public String getFb_message() {
        return fb_message;
    }

    public void setFb_message(String fb_message) {
        this.fb_message = fb_message;
    }


    public String getFb_id(){
        return fb_id;
    }

    public void setFb_id(String fb_id){
        this.fb_id= fb_id;
    }
}