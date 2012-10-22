import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

class field {
    public String fieldName;
    public String type;
    public String encKey;
}
class table {
    public String tableName;
    public int fieldNum;
    public field []fids;
    table(String name, field []f) {
        tableName = name;
        fieldNum = f.length;
        fids = new field[fieldNum];
        for(int i = 0; i < fieldNum; ++i)
            fids[i] = f[i];
    }
    public field getField(String name) { //get field with field_name
        for(int i = 0; i < fieldNum; ++i)
            if(fids[i].fieldName.equals(name))
                return fids[i];
        return null;
    }
    public String toInfoString() { //generate the info fields in DB
        String res = "";
        res += String.valueOf(fieldNum) + " ";
        for(int i = 0; i < fieldNum; ++i) {
            res += fids[i].fieldName + " ";
            res += fids[i].type + " ";
            res += fids[i].encKey + " ";
        }
        return res;
    }
    public void updateDB(DBconnecter st) {
        updateDB(st, true);
    }
    public void updateDB(DBconnecter st, boolean have) { //update record
        try {
            if(have) { //need delete
                String sql = "delete from encinfo where tabName = \'" + tableName + "\' limit 1";
                st.executeSQL(sql, null, null, true);
            }
            //insert
            String info = toInfoString();
            String sql = "insert into encinfo(tabName,info,exInfo) values(\'" + tableName + "\',\'" + info + "\',\'\');";
            st.executeSQL(sql, null, null, true);
        }
        catch(Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}
class dbInfo { //store dbtables with hashmap
    public HashMap<String, table> tableMap;
    dbInfo() {
        tableMap = new HashMap<String, table>();
    }
    public void addTable(table ta) { //add table to the hashmap
        tableMap.put(ta.tableName, ta);
    }
    public table getTable(String name) {
        return (table) tableMap.get(name);
    }
    public field getField(String tname, String fil) {
        return ((table) tableMap.get(tname)).getField(fil);
    }
    public void initFromDB(DBconnecter db) {
        try {
            String sql = "select * from encinfo";
            Vector<String> result = db.executeSQL(sql, null, null, true);
            int size = result.size();
            for(int i = 2; i < size - 1; ++i) {
                StringTokenizer str = new StringTokenizer(result.get(i), " ");
                String tName = str.nextToken();
                int fieldNum = Integer.parseInt(str.nextToken());
                field fids[] = new field[fieldNum];
                for(int j = 0; j < fieldNum; ++j) {
                    fids[j] = new field();
                    fids[j].fieldName = str.nextToken();
                    fids[j].type = str.nextToken();
                    fids[j].encKey = str.nextToken();
                }
                table tab = new table(tName, fids);
                addTable(tab);
            }
        }
        catch(Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}
