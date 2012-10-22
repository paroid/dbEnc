import java.util.Vector;

class strMap {
    static long M = 32L; //bit-width
    static long Mod = 429496709L; //prime
    static long hashVal(String str) { // main hash function
        long res = 0L; // unsigned int 32bit
        int l = str.length();
        for(int i = 1; i < l; ++i) { //each pair mapping to 2-bit
            res |= 1L << ((((long)str.charAt(i - 1)) % M + M) % M);
            res |= 1L << (((((long)str.charAt(i - 1) * (long)str.charAt(i)) % Mod % M) + M) % M);
        }
        res |= 1L << ((((long)str.charAt(l - 1)) % M + M) % M); //last char
        /*System.out.println(res);*/
        return res;
    }
    static void makeHval(DBconnecter db, dbInfo info, String tabname) { //generate hval from table
        Vector<String> strFields = new Vector<String>();
        table tab = info.getTable(tabname);
        for(int i = 0; i < tab.fieldNum; ++i) {
            if(tab.fids[i].type.equals("string"))
                strFields.add(tab.fids[i].fieldName);
        }
        String sql = "select " + strFields.get(0);
        for(int i = 1; i < strFields.size(); ++i)
            sql += "," + strFields.get(i);
        sql += "  from  " + tab.tableName;
        Vector<String> res = db.executeSQL(sql, info, null, true);
        Vector<String> sqls = new Vector<String>();
        Vector<String> whs = new Vector<String>();
        sqls.add("update " + tab.tableName + "  set " + strFields.get(0) + "_hval=");
        whs.add("   where " + strFields.get(0) + "=");
        for(int i = 1; i < strFields.size(); ++i) {
            sqls.add(" , " + strFields.get(i) + "_hval=");
            whs.add(" and " + strFields.get(i) + "=");
        }
        for(int i = 2; i < res.size() - 1; ++i) {
            String tmp[] = res.get(i).split("\\s");
            sql = "";
            for(int j = 0; j < strFields.size(); ++j)
                sql += sqls.get(j) + String.valueOf(hashVal(tmp[j]));
            for(int j = 0; j < strFields.size(); ++j)
                sql += whs.get(j) + "\'" + tmp[j] + "\'";
            db.executeSQL(sql, info, null, false);
        }
    }
}

