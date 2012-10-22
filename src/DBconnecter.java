import java.io.*;
import java.sql.*;
import java.util.Vector;
import java.util.regex.Pattern;

class DBconnecter {
    enum operate {select, insert, create, drop, delete, update, use,source};
    enum type {string, integer};
    public static Pattern mat;
    String dbServer = "";
    String username = "";
    String password = "";
    String database = "";
    Statement dbstat = null;
    Connection dbcon = null;
    DBconnecter() {
        // match 'encinfo' ,'idx'
        mat = Pattern.compile(".*\\sencinfo$|.*\\sencinfo\\s.*|.*\\sencinfo\\(.*|.*\\sidx$|.*\\sidx\\s.*|.*\\sidx\\(.*|.*\\suser$|.*\\suser\\s.*|.*\\suser\\(.*", Pattern.CASE_INSENSITIVE | Pattern.COMMENTS);
    }
    public void setInfo(String user, String pw, String db, String server) {
        username = user;
        password = pw;
        database = db;
        dbServer = server;
    }
    public Statement connect() { //connect DB
        try {
            String url = "jdbc:mysql://127.0.0.1:3306/" + database;
            Class.forName("com.mysql.jdbc.Driver");
            dbcon = DriverManager.getConnection(url, username, password);
            if(dbcon == null) {
                System.out.println("DB connect failed!");
                return null;
            }
            System.out.println("DB connected!");
            dbstat = dbcon.createStatement();
        }
        catch(Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        return dbstat;
    }
    public Vector<String> getSection(String exp) { //parse section query expression
        int st = 0;
        String temStrs[];
        Vector<String> res = new Vector<String>();
        if((st = exp.indexOf("<=", st)) > 0) {
            temStrs = exp.split("<=", 2);
            if(temStrs[0].trim().matches("\\d*")) {
                temStrs[1] += ">=" + temStrs[0];
                return getSection(temStrs[1]);
            }
            res.add(temStrs[0].trim());
            res.add("head");
            res.add(temStrs[1].trim());
            return res;
        }
        else if((st = exp.indexOf("<", st)) > 0) {
            temStrs = exp.split("<", 2);
            if(temStrs[0].trim().matches("\\d*")) {
                temStrs[1] += ">" + temStrs[0];
                return getSection(temStrs[1]);
            }
            res.add(temStrs[0].trim());
            res.add("head");
            res.add(String.valueOf(Integer.parseInt(temStrs[1].trim()) - 1));
            return res;
        }
        else if((st = exp.indexOf(">=", st)) > 0) {
            temStrs = exp.split(">=", 2);
            if(temStrs[0].trim().matches("\\d*")) {
                temStrs[1] += "<=" + temStrs[0];
                return getSection(temStrs[1]);
            }
            res.add(temStrs[0].trim());
            res.add(temStrs[1].trim());
            res.add("end");
            return res;
        }
        else if((st = exp.indexOf(">", st)) > 0) {
            temStrs = exp.split(">", 2);
            if(temStrs[0].trim().matches("\\d*")) {
                temStrs[1] += "<" + temStrs[0];
                return getSection(temStrs[1]);
            }
            res.add(temStrs[0].trim());
            res.add(String.valueOf(Integer.parseInt(temStrs[1].trim()) + 1));
            res.add("end");
            return res;
        }
        return res;
    }
    public String min(String a, String b) { //section compare
        if(a.equals("head") || b.equals("head"))
            return "head";
        else if(a.equals("end"))
            return b;
        else if(b.equals("end"))
            return a;
        else
            return Integer.parseInt(a) < Integer.parseInt(b) ? a : b;
    }
    public String max(String a, String b) { //section compare
        if(a.equals("end") || b.equals("end"))
            return "end";
        else if(a.equals("head"))
            return b;
        else if(b.equals("head"))
            return a;
        else
            return Integer.parseInt(a) > Integer.parseInt(b) ? a : b;
    }
    public Vector<String> andSection(Vector<String> a, Vector<String>  b) { //and 2 section
        if(!a.get(0).equals(b.get(0))) {
            System.out.println("Sec error!");
            return null;
        }
        a.set(1, max(a.get(1), b.get(1)));
        a.set(2, min(a.get(2), b.get(2)));
        return a;
    }
    public Vector<Vector<String>> orSection(Vector<String> a, Vector<String>  b) { //or 2 section
        if(!a.get(0).equals(b.get(0))) {
            System.out.println("Sec error!");
            return null;
        }
        Vector<Vector<String>> res = new Vector<Vector<String>>();
        String ta = max(a.get(2), b.get(1));
        String tb = max(a.get(1), b.get(2));
        String tc = min(a.get(1), b.get(1));
        String td = max(a.get(2), b.get(2));
        if(ta.equals(b.get(1)) && !ta.equals(a.get(2))) {
            res.add(a);
            res.add(b);
            return res;
        }
        else if(tb.equals(a.get(1)) && !tb.equals(b.get(2))) {
            res.add(a);
            res.add(b);
            return res;
        }
        else {
            a.set(1, tc);
            a.set(2, td);
            res.add(a);
            b.set(0, "none");
            res.add(b);
            return res;
        }
    }
    public String getLogic(String str, int from) { //get the first logic "and" ... "or"
        int andPos = str.indexOf("and", from);
        int orPos = str.indexOf("or", from);
        if(andPos == -1 && orPos == -1)
            return "";
        if(andPos == -1)
            return "or";
        if(orPos == -1)
            return "and";
        return andPos < orPos ? "and" : "or";
    }
    public Vector<String> executeSQL(String sql) {
        return executeSQL(sql, null, null, false);
    }
    public Vector<String> executeSQL(String sql, dbInfo infoDB, indexSet idx) {
        return executeSQL(sql, infoDB, idx, false);
    }
    //main process
    public Vector<String> executeSQL(String sql, dbInfo infoDB, indexSet idx, boolean admin) {
        Vector<String> res = new Vector<String>();
        try {
            //check
            if(!admin && mat.matcher(sql).matches()) {
                res.add("SQL denied!");
                return res;
            }
            //get operation  select insert delete create .....
            String temStrs[] = sql.split("\\s", 2);
            operate op = operate.valueOf(temStrs[0].trim().toLowerCase());
            String temp = "";
            switch(op) {
                case insert:
                    //fields(...)
                    /*System.out.println(sql);*/
                    int st = sql.indexOf("(") + 1;
                    int end = sql.indexOf(")");
                    temp = sql.substring(st, end);
                    String oriStr = sql.substring(0, end);
                    String preStr = "values(";
                    String hvals = "";
                    Vector<String> fNames = func.resolve(temp);
                    //table name
                    end = sql.indexOf("into") + 4;
                    String tableName = sql.substring(end, st - 1).trim();
                    //values(...)
                    st = sql.indexOf("values(") + 7;
                    end = sql.indexOf(")", st);
                    temp = sql.substring(st, end);
                    Vector<String> vals = func.resolve(temp);
                    int cnt = fNames.size();
                    table temTable = null;
                    field temfield = null;
                    if(infoDB != null) { //get table info
                        temTable = infoDB.getTable(tableName);
                        if(temTable == null) {
                            System.out.println("Table not found!");
                            return res;
                        }
                    }
                    for(int i = 0; i < cnt; ++i) {
                        //default admin type="string" & key
                        type fType = type.valueOf("string");
                        String key = Client.encInfoKey;
                        if(admin && tableName.equals("user")) { //user table
                            key = "none";
                        }
                        if(infoDB != null) { // get field Type & Key
                            temfield = temTable.getField(fNames.get(i));
                            fType = type.valueOf(temfield.type);
                            key = temfield.encKey;
                        }
                        switch(fType) {
                            case integer:
                                preStr += func.Encrypt(vals.get(i), "integer", key);
                                //update index
                                index tempidx = idx.getIndex(tableName + "." + temfield.fieldName);
                                tempidx.insertItem(Integer.parseInt(vals.get(i)));
                                tempidx.updateDB(this);
                                break;
                            case string:
                                preStr += "\'" + func.Encrypt(vals.get(i), "string", key) + "\'";
                                if(infoDB != null) {
                                    oriStr += "," + temfield.fieldName + "_hval";
                                    hvals += "," + String.valueOf(strMap.hashVal(vals.get(i)));
                                    System.out.println("hval= " + strMap.hashVal(vals.get(i)));
                                }
                                break;
                            default:
                        }
                        preStr += ","; //the last will be removed
                    }
                    oriStr += ")" + preStr.substring(0, preStr.length() - 1) + hvals + ")";
                    /*System.out.println(oriStr);*/
                    int affectNum = dbstat.executeUpdate(oriStr);
                    temp = "SQL has been executed!\n" + affectNum + " Rows affected!";
                    res.add(temp);
                    break;
                case select:            // section -> like -> equation    like with OR is a problem
                    /*System.out.println(sql);*/
                    temTable = null;
                    tableName = null;
                    st = sql.indexOf("from") + 4;
                    temStrs = sql.substring(st).trim().split("\\s", 2);
                    tableName = temStrs[0].trim();   //get table info
                    if(infoDB != null) {
                        temTable = infoDB.getTable(tableName);
                        if(temTable == null) {
                            System.out.println("Table not found!");
                            return res;
                        }
                    }
                    if(infoDB != null && sql.matches(".*(\\*).*")) { // hval filter for select * from
                        String colname = temTable.fids[0].fieldName;
                        for(int j = 1; j < temTable.fieldNum; ++j)
                            colname += "," + temTable.fids[j].fieldName;
                        int star = sql.indexOf("*");
                        String tmp = sql.substring(0, star) + colname + sql.substring(star + 1);
                        sql = tmp;
                    }
                    st = sql.indexOf("where"); //condition
                    if(st > 0) {
                        st += 5;
                        int tst = st;
                        temp = sql.substring(st);
                        String fieldName = "";
                        String extraExp = " ";
                        if(temp.matches(".*(<=|<|>=|>).*")) { //section query
                            Vector<Integer> intsec = new Vector<Integer>();
                            // seems to be 2 section from same field with and operate
                            if((st = temp.indexOf("and")) > 0 && temp.substring(st + 3).matches(".*(<=|<|>=|>).*")) {
                                temStrs = temp.split("(and|or)", 3);
                                if(temStrs.length == 3) { //have extra expression
                                    extraExp += temStrs[2] + getLogic(temp, st + 3);
                                }
                                Vector<String> sec1 = getSection(temStrs[0]); //first section
                                Vector<String> sec2 = getSection(temStrs[1]); //second
                                if(!sec1.get(0).equals(sec2.get(0))) { //two different field I'm wrong
                                    extraExp = " " + temStrs[1] + extraExp + " and ";
                                    break;
                                }
                                Vector<String> fsec = andSection(sec1, sec2); // A and B
                                fieldName = fsec.get(0); //field Name
                                index temIndex = idx.getIndex(tableName + "." + fieldName); //get index
                                intsec.addAll(temIndex.toInts(fsec.get(1), fsec.get(2)));  //add ints
                            }
                            // seems to be 2 section from same field with or operate
                            else if((st = temp.indexOf("or")) > 0 && temp.substring(st + 2).matches(".*(<=|<|>=|>).*")) {
                                temStrs = temp.split("(and|or)", 3);
                                if(temStrs.length == 3) {// have extra expression
                                    extraExp += temStrs[2] + getLogic(temp, st + 2);
                                }
                                Vector<String> sec1 = getSection(temStrs[0]); //first section
                                Vector<String> sec2 = getSection(temStrs[1]); //second
                                if(!sec1.get(0).equals(sec2.get(0))) { //two different field
                                    extraExp = " " + temStrs[1] + extraExp + " or ";
                                    break;
                                }
                                Vector<Vector<String>> secs = orSection(sec1, sec2); //A or B
                                fieldName = secs.get(0).get(0); //field Name
                                index temIndex = idx.getIndex(tableName + "." + fieldName); //get index
                                intsec.addAll(temIndex.toInts(secs.get(0).get(1), secs.get(0).get(2)));//add first ints
                                if(!secs.get(1).get(0).equals("none")) //more ints
                                    intsec.addAll(temIndex.toInts(secs.get(1).get(1), secs.get(1).get(2))); //add
                            }
                            //only one section expression
                            else {
                                temStrs = temp.split("(and|or)", 2);
                                if(temStrs.length == 2) { //extra expression
                                    extraExp += temStrs[1] + getLogic(temp, 0);
                                }
                                Vector<String> sec = getSection(temStrs[0]);
                                fieldName = sec.get(0);
                                index temIndex = idx.getIndex(tableName + "." + fieldName);
                                intsec.addAll(temIndex.toInts(sec.get(1), sec.get(2))); //add ints
                            }
                            //form sql
                            sql = sql.substring(0, tst) + extraExp + " " + fieldName + "=";
                            int n = intsec.size();
                            cnt = 0;
                            for(int i = 0; i < n; ++i) { //generate sqls & execute SQL
                                Vector<String> tres = executeSQL(sql + intsec.get(i), infoDB, idx, false);
                                int tsize = tres.size();
                                cnt += tsize - 3;
                                int start = i > 0 ? 2 : 0;
                                for(int j = start; j < tsize - 1; ++j) //add result
                                    res.add(tres.get(j));
                            }
                            res.add("SQL has been executed!\n" + cnt + " Rows founded!");
                            return res;
                        }
                        else {
                            st = 0;
                            String exps[] = temp.split("(and|or)"); //split expressions
                            sql = sql.substring(0, tst) + " "; //pre sql
                            if(exps.length > 0 && exps[0].matches(".*\\slike\\s.*")) { //like query
                                temStrs = exps[0].split("like", 2);
                                temStrs[0] = temStrs[0].trim();
                                temStrs[1] = temStrs[1].trim();
                                temStrs[1] = temStrs[1].substring(1, temStrs[1].length() - 1);
                                if(exps.length > 1) {
                                    String log = getLogic(temp, 0);
                                    int tt = temp.indexOf(log);
                                    sql += temp.substring(tt + 3) + " " + log + " ";
                                }
                                sql += temStrs[0] + "_hval & ";
                                String tval = String.valueOf(strMap.hashVal(temStrs[1]));
                                sql += tval + "=" + tval;
                                Vector<String> tres = executeSQL(sql, infoDB, idx, admin);// first phrase result
                                /*System.out.println(tres);*/
                                String names[] = tres.get(0).trim().split("\\t"); //column names
                                int target = 0;
                                for(int j = 0; j < names.length; ++j) {
                                    if(names[j].trim().equals(temStrs[0])) {
                                        target = j;
                                        break;
                                    }
                                }
                                res.add(tres.get(0));
                                res.add(tres.get(1));
                                for(int j = 2; j < tres.size() - 1; ++j) {
                                    names = tres.get(j).split("\\s");
                                    if(names[target].indexOf(temStrs[1]) != -1) {
                                        res.add(tres.get(j));
                                    }
                                }
                                temp = "SQL has been executed!\n" + (res.size() - 2) + "/" + (tres.size() - 3) + " Rows founded!"; // ---- res[n-1]
                                res.add(temp);
                                return res;
                            }
                            //simple equation query
                            for(int i = 0; i < exps.length; ++i) { //convert sql
                                temStrs = exps[i].trim().split("=", 2); //split expression
                                temStrs[0] = temStrs[0].trim(); //field
                                temStrs[1] = temStrs[1].trim(); //value
                                field temField = null;
                                if(temTable != null)
                                    temField = temTable.getField(temStrs[0]); //get field
                                type fType;
                                String key;
                                if(temField == null) { // this is the hval field
                                    fType = type.valueOf("integer");
                                    key = "none";
                                    /*System.out.println("In where condition: field not found!");
                                      return res;*/
                                }
                                else {
                                    fType = type.valueOf(temField.type); //get type
                                    key = temField.encKey;
                                }
                                if(admin && tableName.equals("user")) { //user table
                                    fType = type.valueOf("string");
                                    key = "none";
                                }
                                sql += temStrs[0] + "="; //add "field = "
                                switch(fType) { //encypt value
                                    case integer:
                                        sql += func.Encrypt(temStrs[1], "integer", key);
                                        break;
                                    case string:
                                        temStrs[1] = temStrs[1].substring(1, temStrs[1].length() - 1);
                                        sql += "\'" + func.Encrypt(temStrs[1], "string", key) + "\'";
                                        break;
                                }
                                // add logic
                                String log = getLogic(temp, st);
                                st = temp.indexOf(log, st) + 3;
                                sql += " " + log + " ";
                            }
                        }
                    }
                    temp = "";
                    ResultSet rset = dbstat.executeQuery(sql);
                    ResultSetMetaData rsetMeta = rset.getMetaData();
                    int colCount = rsetMeta.getColumnCount();
                    String ColName[] = new String[colCount];
                    for(int i = 0; i < colCount; ++i) { // col names    -------  res[0]
                        ColName[i] = rsetMeta.getColumnName(i + 1);
                        temp += ColName[i] + " \t"; //head title
                    }
                    res.add(temp);
                    temp = "";
                    for(int i = 1; i <= colCount; ++i)
                        temp += "--------"; //<hr/>                    --------- res[1]
                    res.add(temp);
                    field fids[] = new field[colCount];
                    type ftypes[] = new type[colCount];
                    // types  & keys
                    if(infoDB != null) {
                        for(int i = 0; i < colCount; ++i) {
                            fids[i] = temTable.getField(ColName[i]);
                            if(fids[i] == null) {
                                ftypes[i] = type.valueOf("integer");
                                fids[i] = new field();
                                fids[i].encKey = "none";
                            }
                            else
                                ftypes[i] = type.valueOf(fids[i].type);
                        }
                    }
                    else {//admin
                        for(int i = 0; i < colCount; ++i) {
                            fids[i] = new field();
                            fids[i].encKey = tableName.equals("user") ? "none" : Client.encInfoKey;
                            ftypes[i] = type.valueOf("string");
                        }
                    }
                    while(rset.next()) { //resolve data
                        temp = "";
                        //decrypt values
                        for(int i = 0; i < colCount; ++i) {
                            switch(ftypes[i]) {
                                case integer:
                                    temp += func.Decrypt(rset.getString(i + 1), "integer", fids[i].encKey) + " ";
                                    break;
                                case string:
                                    temp += func.Decrypt(rset.getString(i + 1), "string", fids[i].encKey) + " ";
                                    break;
                            }
                        }
                        res.add(temp);
                    }
                    temp = "SQL has been executed!\n" + (res.size() - 2) + " Rows founded!"; // ---- res[n-1]
                    res.add(temp);
                    break;
                case delete:
                    /*System.out.println(sql);*/
                    temTable = null;
                    tableName = null;
                    boolean needinit = false;
                    st = sql.indexOf("from") + 4;
                    temStrs = sql.substring(st).trim().split("\\s", 2);
                    tableName = temStrs[0].trim();
                    if(infoDB != null) {
                        temTable = infoDB.getTable(tableName);
                        if(temTable == null) {
                            System.out.println("Table not found!");
                            return res;
                        }
                    }
                    st = sql.indexOf("where"); //condition query
                    String limit = " "; //limit
                    if(st > 0) {
                        st += 5;
                        int tst = st;
                        st = sql.indexOf("limit");
                        if(st > 0) {
                            temp = sql.substring(tst, st);
                            limit += sql.substring(st);
                        }
                        else
                            temp = sql.substring(tst);
                        String fieldName = "";
                        String extraExp = " ";
                        if(temp.matches(".*(<=|<|>=|>).*")) { //section query
                            Vector<Integer> intsec = new Vector<Integer>();
                            if((st = temp.indexOf("and")) > 0 && temp.substring(st + 3).matches(".*(<=|<|>=|>).*")) {
                                temStrs = temp.split("(and|or)", 3);
                                if(temStrs.length == 3) {
                                    extraExp += temStrs[2] + getLogic(temp, st + 3);
                                }
                                Vector<String> sec1 = getSection(temStrs[0]);
                                Vector<String> sec2 = getSection(temStrs[1]);
                                if(!sec1.get(0).equals(sec2.get(0))) {
                                    extraExp = " " + temStrs[1] + extraExp + " and ";
                                    break;
                                }
                                Vector<String> fsec = andSection(sec1, sec2);
                                fieldName = fsec.get(0);
                                index temIndex = idx.getIndex(tableName + "." + fieldName);
                                intsec.addAll(temIndex.toInts(fsec.get(1), fsec.get(2)));
                            }
                            else if((st = temp.indexOf("or")) > 0 && temp.substring(st + 2).matches(".*(<=|<|>=|>).*")) {
                                temStrs = temp.split("(and|or)", 3);
                                if(temStrs.length == 3) {
                                    extraExp += temStrs[2] + getLogic(temp, st + 2);
                                }
                                Vector<String> sec1 = getSection(temStrs[0]);
                                Vector<String> sec2 = getSection(temStrs[1]);
                                if(!sec1.get(0).equals(sec2.get(0))) {
                                    extraExp = " " + temStrs[1] + extraExp + " or ";
                                    break;
                                }
                                Vector<Vector<String>> secs = orSection(sec1, sec2);
                                fieldName = secs.get(0).get(0);
                                index temIndex = idx.getIndex(tableName + "." + fieldName);
                                intsec.addAll(temIndex.toInts(secs.get(0).get(1), secs.get(0).get(2)));
                                if(!secs.get(1).get(0).equals("none"))
                                    intsec.addAll(temIndex.toInts(secs.get(1).get(1), secs.get(1).get(2)));
                            }
                            else {
                                temStrs = temp.split("(and|or)", 2);
                                if(temStrs.length == 2) {
                                    extraExp += temStrs[1] + getLogic(temp, 0);
                                }
                                Vector<String> sec = getSection(temStrs[0]);
                                fieldName = sec.get(0);
                                index temIndex = idx.getIndex(tableName + "." + fieldName);
                                intsec.addAll(temIndex.toInts(sec.get(1), sec.get(2)));
                            }
                            sql = sql.substring(0, tst) + extraExp + " " + fieldName + "=";
                            int n = intsec.size();
                            cnt = 0;
                            for(int i = 0; i < n; ++i) {
                                Vector<String> tres = executeSQL(sql + intsec.get(i) + limit, infoDB, idx, false);
                                st = tres.get(0).indexOf("\n");
                                temp = tres.get(0).substring(st + 1).trim();
                                temStrs = temp.split("\\s", 2);
                                cnt += Integer.parseInt(temStrs[0]);
                            }
                            res.add("SQL has been executed!\n" + cnt + " Rows affected!");
                            return res;
                        }
                        else {
                            st = 0;
                            String exps[] = temp.split("(and|or)");
                            sql = sql.substring(0, tst) + " ";
                            for(int i = 0; i < exps.length; ++i) {
                                temStrs = exps[i].trim().split("=", 2);
                                temStrs[0] = temStrs[0].trim();
                                temStrs[1] = temStrs[1].trim();
                                sql += temStrs[0] + "=";
                                type fType = type.valueOf("string");
                                String key = Client.encInfoKey;
                                if(admin && tableName.equals("user")) { //user table
                                    key = "none";
                                }
                                field temField = null;
                                if(!admin) {
                                    temField = temTable.getField(temStrs[0]);
                                    if(temField == null) {
                                        System.out.println("In where condition: field not found!");
                                        return res;
                                    }
                                    fType = type.valueOf(temField.type);
                                    key = temField.encKey;
                                }
                                switch(fType) {
                                    case integer:
                                        sql += func.Encrypt(temStrs[1], "integer", key);
                                        //update index
                                        index temIndex = idx.getIndex(temTable.tableName + "." + temField.fieldName);
                                        temIndex.deleteItem(Integer.parseInt(temStrs[1]));
                                        temIndex.updateDB(this);
                                        break;
                                    case string:
                                        needinit = true;
                                        temStrs[1] = temStrs[1].substring(1, temStrs[1].length() - 1);
                                        sql += "\'" + func.Encrypt(temStrs[1], "string", key) + "\'";
                                        break;
                                }
                                String log = getLogic(temp, st);
                                st = temp.indexOf(log, st) + 3;
                                sql += " " + log + " ";
                            }
                        }
                    }
                    affectNum = dbstat.executeUpdate(sql + limit);
                    if(!admin && needinit && temTable != null) {
                        fids = temTable.fids;
                        for(int i = 0; i < fids.length; ++i)
                            if(fids[i].type.equals("integer")) {
                                index temIndex = new index(temTable.tableName + "." + fids[i].fieldName);
                                temIndex.initFromTable(infoDB, this);
                                temIndex.updateDB(this);
                            }
                    }
                    res.add("SQL has been executed!\n" + affectNum + " Rows affected!");
                    break;
                case update:
                    /*System.out.println(sql);*/
                    temTable = null;
                    tableName = null;
                    needinit = false;
                    if(infoDB != null) {
                        st = sql.indexOf("update") + 6;
                        temStrs = sql.substring(st).trim().split("\\s", 2);
                        tableName = temStrs[0].trim();
                        temTable = infoDB.getTable(tableName);
                        if(temTable == null) {
                            System.out.println("Table not found!");
                            return res;
                        }
                    }
                    st = sql.indexOf("where"); //condition query
                    limit = " "; //limit
                    if(st > 0) {
                        st += 5;
                        int tst = st;
                        st = sql.indexOf("limit");
                        if(st > 0) {
                            temp = sql.substring(tst, st);
                            limit += sql.substring(st);
                        }
                        else
                            temp = sql.substring(tst);
                        String fieldName = "";
                        String extraExp = " ";
                        if(temp.matches(".*(<=|<|>=|>).*")) { //section query
                            Vector<Integer> intsec = new Vector<Integer>();
                            if((st = temp.indexOf("and")) > 0 && temp.substring(st + 3).matches(".*(<=|<|>=|>).*")) {
                                temStrs = temp.split("(and|or)", 3);
                                if(temStrs.length == 3) {
                                    extraExp += temStrs[2] + getLogic(temp, st + 3);
                                }
                                Vector<String> sec1 = getSection(temStrs[0]);
                                Vector<String> sec2 = getSection(temStrs[1]);
                                if(!sec1.get(0).equals(sec2.get(0))) {
                                    extraExp = " " + temStrs[1] + extraExp + " and ";
                                    break;
                                }
                                Vector<String> fsec = andSection(sec1, sec2);
                                fieldName = fsec.get(0);
                                index temIndex = idx.getIndex(tableName + "." + fieldName);
                                intsec.addAll(temIndex.toInts(fsec.get(1), fsec.get(2)));
                            }
                            else if((st = temp.indexOf("or")) > 0 && temp.substring(st + 2).matches(".*(<=|<|>=|>).*")) {
                                temStrs = temp.split("(and|or)", 3);
                                if(temStrs.length == 3) {
                                    extraExp += temStrs[2] + getLogic(temp, st + 2);
                                }
                                Vector<String> sec1 = getSection(temStrs[0]);
                                Vector<String> sec2 = getSection(temStrs[1]);
                                if(!sec1.get(0).equals(sec2.get(0))) {
                                    extraExp = " " + temStrs[1] + extraExp + " or ";
                                    break;
                                }
                                Vector<Vector<String>> secs = orSection(sec1, sec2);
                                fieldName = secs.get(0).get(0);
                                index temIndex = idx.getIndex(tableName + "." + fieldName);
                                intsec.addAll(temIndex.toInts(secs.get(0).get(1), secs.get(0).get(2)));
                                if(!secs.get(1).get(0).equals("none"))
                                    intsec.addAll(temIndex.toInts(secs.get(1).get(1), secs.get(1).get(2)));
                            }
                            else {
                                temStrs = temp.split("(and|or)", 2);
                                if(temStrs.length == 2) {
                                    extraExp += temStrs[1] + getLogic(temp, 0);
                                }
                                Vector<String> sec = getSection(temStrs[0]);
                                fieldName = sec.get(0);
                                index temIndex = idx.getIndex(tableName + "." + fieldName);
                                intsec.addAll(temIndex.toInts(sec.get(1), sec.get(2)));
                            }
                            sql = sql.substring(0, tst) + extraExp + " " + fieldName + "=";
                            int n = intsec.size();
                            cnt = 0;
                            for(int i = 0; i < n; ++i) {
                                Vector<String> tres = executeSQL(sql + intsec.get(i) + limit, infoDB, idx, false);
                                st = tres.get(0).indexOf("\n");
                                temp = tres.get(0).substring(st + 1).trim();
                                temStrs = temp.split("\\s", 2);
                                cnt += Integer.parseInt(temStrs[0]);
                            }
                            res.add("SQL has been executed!\n" + cnt + " Rows affected!");
                            return res;
                        }
                        else {
                            st = 0;
                            String exps[] = temp.split("(and|or)");
                            sql = sql.substring(0, tst) + " ";
                            for(int i = 0; i < exps.length; ++i) {
                                temStrs = exps[i].trim().split("=", 2);
                                temStrs[0] = temStrs[0].trim();
                                temStrs[1] = temStrs[1].trim();
                                sql += temStrs[0] + "=";
                                type fType = type.valueOf("string");
                                String key = Client.encInfoKey;
                                field temField = null;
                                if(!admin) {
                                    temField = temTable.getField(temStrs[0]);
                                    if(temField == null) {
                                        System.out.println("In where condition: field not found!");
                                        return res;
                                    }
                                    fType = type.valueOf(temField.type);
                                    key = temField.encKey;
                                }
                                switch(fType) {
                                    case integer:
                                        sql += func.Encrypt(temStrs[1], "integer", key);
                                        //update index
                                        index temIndex = idx.getIndex(temTable.tableName + "." + temField.fieldName);
                                        temIndex.deleteItem(Integer.parseInt(temStrs[1]));
                                        temIndex.updateDB(this);
                                        break;
                                    case string:
                                        needinit = true;
                                        temStrs[1] = temStrs[1].substring(1, temStrs[1].length() - 1);
                                        sql += "\'" + func.Encrypt(temStrs[1], "string", key) + "\'";
                                        break;
                                }
                                String log = getLogic(temp, st);
                                st = temp.indexOf(log, st) + 3;
                                sql += " " + log + " ";
                            }
                        }
                    }
                    st = sql.indexOf("set"); //set expressions
                    String extraExp = "";
                    if(st > 0) {
                        st += 3;
                        preStr = sql.substring(0, st) + " ";
                        temp = sql.substring(st);
                        temStrs = temp.split("where", 2);
                        if(temStrs.length == 2)
                            extraExp = " where " + temStrs[1];
                        Vector<String> sets = func.resolve(temStrs[0]);
                        for(int i = 0; i < sets.size(); ++i) {
                            temStrs = sets.get(i).split("=", 2);
                            preStr += temStrs[0] + "=";
                            type fType = type.valueOf("string");
                            String key = Client.encInfoKey;
                            field temField = null;
                            if(!admin) {
                                temField = temTable.getField(temStrs[0].trim());
                                if(temField == null) {
                                    fType = type.valueOf("integer");
                                    key = "none";
                                    /*System.out.println("In set expression: field not found!");
                                      return res;*/
                                }
                                else {
                                    fType = type.valueOf(temField.type);
                                    key = temField.encKey;
                                }
                            }
                            switch(fType) {
                                case integer:
                                    preStr += func.Encrypt(temStrs[1].trim(), "integer", key);
                                    break;
                                case string:
                                    temStrs[1] = temStrs[1].substring(1, temStrs[1].length() - 1);
                                    preStr += "\'" + func.Encrypt(temStrs[1], "string", key) + "\'";
                                    break;
                            }
                            preStr += ", ";
                        }
                        preStr = preStr.substring(0, preStr.length() - 2) + extraExp;
                        sql = preStr;
                    }
                    affectNum = dbstat.executeUpdate(sql + limit);
                    if(!admin && needinit && temTable != null) {  //update index
                        fids = temTable.fids;
                        for(int i = 0; i < fids.length; ++i)
                            if(fids[i].type.equals("integer")) {
                                index temIndex = new index(temTable.tableName + "." + fids[i].fieldName);
                                temIndex.initFromTable(infoDB, this);
                                temIndex.updateDB(this);
                            }
                    }
                    res.add("SQL has been executed!\n" + affectNum + " Rows affected!");
                    break;
                case create:
                    st = sql.indexOf("table") + 5;
                    end = sql.indexOf("(");
                    tableName = sql.substring(st, end).trim();
                    if(infoDB != null) {
                        temTable = infoDB.getTable(tableName);
                        if(temTable != null) {
                            System.out.println("Table is already in DB!");
                            return res;
                        }
                    }
                    st = sql.indexOf(")", end);
                    temp = sql.substring(end + 1, st);
                    fNames = func.resolve(temp); //field names
                    cnt = fNames.size();
                    fids = new field[cnt];
                    for(int i = 0; i < cnt; ++i) {
                        fids[i] = new field();
                        temStrs = fNames.get(i).split("\\s", 2);
                        fids[i].fieldName = temStrs[0];
                        if(temStrs[1].matches(".*int.*")) {
                            fids[i].type = "integer";
                            //Create index for integer
                            index temIndex = new index(tableName + "." + fids[i].fieldName);
                            temIndex.updateDB(this);
                        }
                        else
                            fids[i].type = "string";
                    }
                    st = sql.indexOf("keys(");
                    if(st < 0) { // key gen
                        for(int i = 0; i < cnt; ++i) {
                            String pw = Client_UI.password;
                            String  rand = String.valueOf((int)(java.lang.Math.random() * 1000000));
                            fids[i].encKey = rand + pw;
                        }
                        /*System.out.println("Need keys() !");
                        return res;*/
                    }
                    else { // manual keys
                        st += 5;
                        end = sql.indexOf(")", st);
                        temp = sql.substring(st, end);
                        sql = sql.substring(0, st - 5);
                        fNames = func.resolve(temp); //field keys
                        for(int i = 0; i < cnt; ++i) {
                            if(fNames.get(i).isEmpty())
                                fids[i].encKey = "none";
                            else
                                fids[i].encKey = fNames.get(i);
                        }
                    }
                    temTable = new table(tableName, fids);
                    temTable.updateDB(this, false);
                    infoDB.addTable(temTable);
                    dbstat.executeUpdate(sql);
                    res.add("Table Created!");
                    break;
                case source:
                    String fileName=sql.substring(6).trim();
                    BufferedReader fin = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
                    String head="insert into _tableName_(_f1_,_f2_,_f3_) values( ",data="";
                    while((data=fin.readLine())!=null){
                    	String dat[] =data.split(";");
                    	String SQL=head;
                    	SQL+=dat[0]+",";
                    	SQL+="'" + dat[1] +"',";
                    	
                    	SQL+=dat[2] + ")";
                    	executeSQL(SQL, infoDB, idx, false);
                    }
                    res.add("import done!");
                    break;
                default:
                    affectNum = dbstat.executeUpdate(sql);
                    res.add("SQL has been executed!\n" + affectNum + " Rows affected!");
            }
        }
        catch(Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        return res;
    }
}
