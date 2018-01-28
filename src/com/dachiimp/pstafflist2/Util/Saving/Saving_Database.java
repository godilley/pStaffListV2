package com.dachiimp.pstafflist2.Util.Saving;

import com.dachiimp.pstafflist2.StaffList;
import com.dachiimp.pstafflist2.Util.ClassSaving.StaffMember;
import com.dachiimp.pstafflist2.Util.Logger;
import com.dachiimp.pstafflist2.Util.MySQL.MySQL;
import com.dachiimp.pstafflist2.Util.MySQL.SQLManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;

/**
 * Created by George on 16/01/2017. For SL
 */
public class Saving_Database {

    private StaffList staffList;

    public Saving_Database(StaffList staffList) {
        this.staffList = staffList;
    }

    public void executeSave() {
        Connection connection = MySQL.openConnection();

        if (!MySQL.checkConnection()) {
            Logger.severe("Error opening connection to save data. Manually save with /stafflist save");
            return;
        }

        HashMap<Integer, StaffMember> map = staffList.getStaffMembersMap();

        int saved = 0;
        int failed = 0;
        int total = map.size();

        for (Integer index : map.keySet()) {
            saved++;
            StaffMember staffMember = map.get(index);
            final String query = "REPLACE INTO `staff` (`player`, `uuid`, `position`, `rank`,`lastlogin`) VALUES (?,?,?,?,?)";
            HashMap<Integer, Object> values = new HashMap<>();
            values.put(1, staffMember.getName());
            String uuid = "Unknown";
            if (staffMember.getUUID() != null)
                uuid = staffMember.getUUID().toString();
            values.put(2, uuid);
            values.put(3, staffMember.getPosition());
            values.put(4, staffMember.getRank());
            String lastlogin = "Unknown";
            if (staffMember.getLastlogin() != null)
                lastlogin = staffList.set_simpleDateFormat.format(staffMember.getLastlogin());
            values.put(5, lastlogin);

            boolean executed = SQLManager.update(query, values, false);
            if (!executed) {
                failed++;
                Logger.warn("Error putting " + staffMember.getName() + " into database. Manually save with /stafflist save");
            }
        }

        List<String> notRemoved = new ArrayList<>();

        for (String str : staffList.removeFromDatabase) {
            final String query = "DELETE FROM staff WHERE player=?";
            HashMap<Integer, Object> values = new HashMap<>();
            values.put(1, str);

            boolean executed = SQLManager.update(query, values, false);
            if (!executed) {
                Logger.warn("Error removing " + str + " from database. Retry with /stafflist save");
                notRemoved.add(str);
            }
        }

        // if they don't get removed try again
        staffList.removeFromDatabase.clear();
        staffList.removeFromDatabase.addAll(notRemoved);

        MySQL.closeConnection();

        if (saved >= total) {
            Logger.log("Saved all staff members to database");
        } else {
            Logger.warn("Error: Saved " + saved + " out of " + total + " staff members to database [" + failed + " failed]");
        }
    }

    public void executeLoad() {
        final String query = "SELECT * FROM staff";
        HashMap<Integer, Object> values = new HashMap<>();
        final ResultSet rs = SQLManager.query(query, values);
        // Done query stuff
        try {
            int count = 0;
            int failed = 0;
            while (rs.next()) {
                count++;
                String rank = rs.getString("rank");
                String name = rs.getString("player");
                String suuid = rs.getString("uuid");
                String slastlogin = rs.getString("lastlogin");
                int position = rs.getInt("position");

                UUID uuid = null;
                if (!suuid.equalsIgnoreCase("Unknown"))
                    uuid = UUID.fromString(suuid);

                Date lastlogin = null;
                if (!slastlogin.equalsIgnoreCase("Unknown")) {
                    try {
                        lastlogin = staffList.set_simpleDateFormat.parse(slastlogin);
                    } catch (ParseException e) {
                        Logger.warn("Error parsing date for " + name + ". Received was " + slastlogin);
                    }
                }

                StaffMember member = new StaffMember(uuid, name, rank, position, lastlogin);
                boolean done = staffList.addStaffMemberToMap(position, member);
                if (!done) {
                    Logger.warn("Error adding " + name + " to map with position " + position);
                    failed++;
                }
            }
            Logger.log("Loaded " + (count-failed) + " staff of " + count);
        } catch (SQLException e) {
            Logger.severe("Error while loading data. Stacktrace:");
            e.printStackTrace();
        }
    }
}
