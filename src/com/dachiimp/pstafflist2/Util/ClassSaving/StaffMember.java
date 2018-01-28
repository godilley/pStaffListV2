package com.dachiimp.pstafflist2.Util.ClassSaving;

import java.util.Date;
import java.util.UUID;

/**
 * Created by George on 15/01/2017. For SL
 */
public class StaffMember {

    /*
        http://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
     */

    private UUID uuid;
    private String name;
    private String rank;
    private Integer position;
    private Date lastlogin;

    public StaffMember(UUID uuid, String name, String rank, Integer position, Date lastlogin) {
        this.uuid = uuid;
        this.name = name;
        this.rank = rank;
        this.position = position;
        this.lastlogin = lastlogin;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Date getLastlogin() {
        return lastlogin;
    }

    public void setLastlogin(Date lastlogin) {
        this.lastlogin = lastlogin;
    }

    @Override
    public String toString() {
        return uuid + " | " + name + " | " + rank + " | " + position + " | " + lastlogin;
    }
}
