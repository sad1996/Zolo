package xyz.sadaksapp.zolo;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Sadakathulla on 22-07-2017.
 */

@IgnoreExtraProperties
public class User {

    public String name;
    public String phone;
    public String email;

    public User() {
    }

    public User(String name, String phone, String email) {
        this.email = email;
        this.name = name;
        this.phone = phone;
    }
    @Exclude
    public String getName() {return name;
    }
    @Exclude
    public String getPhone() {return phone;
    }
    @Exclude
    public String getEmail() {return email;
    }
    @Exclude
    public void setName(String name) {
        this.name = name;
    }
    @Exclude
    public void setPhone(String phone) {
        this.phone = phone;
    }
    @Exclude
    public void setEmail(String email) {
        this.email = email;
    }



}

