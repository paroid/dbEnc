import java.util.Vector;

class user {
    String userName;
    String password;
    static double saltLength = 10000;
    void setInfo(String name, String pw) {
        userName = name;
        password = pw;
    }
    boolean checkIn(DBconnecter db) {
        String sql = "select * from user where name=\'" + userName + "\'";
        Vector<String> res = db.executeSQL(sql, null, null, true);
        String vals[] = res.get(2).split("\\s");
        return vals[2].equals(func.getMD5((userName + vals[1] + password).getBytes()));
    }
    void updateDB(DBconnecter db) {
        String sql = "delete from user where name=\'" + userName + "\' limit 1";
        db.executeSQL(sql, null, null, true);
        String  salt = String.valueOf((int)(java.lang.Math.random() * saltLength));
        sql = "insert into user(name,salt,md) values(\'" + userName + "\',\'" + salt + "\',\'" + func.getMD5((userName + salt + password).getBytes()) + "\')";
        db.executeSQL(sql, null, null, true);
    }
}
