import java.util.HashMap;
import java.util.Vector;

class index {
    public static int BM = 8;
    public int count;
    public String indexName;
    public BplusTree tree;
    index(String name) {
        indexName = name;
        tree = new BplusTree(BM);
        count = 0;
    }
    public void fromString(String str) { //form B+ tree from string "(key)=(count) )"
        str.trim();
        String temp[] = str.split("\\s");
        Vector<Integer> key = new Vector<Integer>();
        Vector<Integer> val = new Vector<Integer>();
        int n = temp.length;
        for(int i = 0; i < n; ++i) {
            String kv[] = temp[i].split("=", 2);
            key.add(Integer.parseInt(kv[0]));
            val.add(Integer.parseInt(kv[1]));
        }
        makeCountTree(key, val);
    }
    public void makeCountTree(Vector<Integer> k, Vector<Integer> v) { // construct B+ tree with count-item
        int n = k.size();
        for(int i = 0; i < n; ++i)
            tree.insertOrUpdate(k.get(i), v.get(i));
        count = n;
    }
    public void makeTree(Vector<Integer> v) { //construct B+ tree with ints
        int n = v.size();
        for(int i = 0; i < n; ++i)
            insertItem(v.get(i));
        count = n;
    }
    public void insertItem(Integer it) { //insert single item
        Integer temp = (Integer)tree.get(it);
        if(temp == null) {
            tree.insertOrUpdate(it, 1);
            ++count;
        }
        else
            tree.insertOrUpdate(it, temp + 1);
    }
    public void deleteItem(Integer it) { //delete item
        Integer temp = (Integer)tree.get(it);
        if(temp != null) {
            if(temp == 1) {
                tree.remove(it);
                --count;
            }
            else
                tree.insertOrUpdate(it, temp - 1);
        }
    }
    public String toString() { //form a "(key)=(count) " string
        String res = "";
        Vector<Integer> temp = tree.toInts();
        Vector<Integer> tvals = tree.getVals();
        int size = temp.size();
        for(int i = 0; i < size; ++i)
            res += temp.get(i) + "=" + tvals.get(i) + " ";
        return res;
    }
    public Vector<Integer> toInts() { //from "head" to "end"
        return tree.toInts();
    }
    public Vector<Integer> toInts(String from, String to) {
        return tree.toInts(from, to);
    }
    public void updateDB(DBconnecter db) {
        try {
            String sql = "delete from idx where name=\'" + indexName + "\' limit 1";
            db.executeSQL(sql, null, null, true);
            String intString = toString();
            sql = "insert into idx(name,indexinfo) values(\'" + indexName + "\',\'" + intString + "\')";
            db.executeSQL(sql, null, null, true);
        }
        catch(Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
    public void initFromTable(dbInfo info, DBconnecter db) {
        try {
            String temStr[] = indexName.split("\\.", 2);
            String sql = "select " + temStr[1] + " from " + temStr[0];
            Vector<String> result = db.executeSQL(sql, info, null, true);
            int size = result.size();
            tree = null;
            tree = new BplusTree(BM);
            Vector<Integer> res = new Vector<Integer>();
            for(int i = 2; i < size - 1; ++i)
                res.add(Integer.parseInt(result.get(i).trim()));
            makeTree(res);
        }
        catch(Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}
class indexSet {
    public HashMap<String, index> indexMap;
    indexSet() {
        indexMap = new HashMap<String, index>();
    }
    public index getIndex(String name) {
        return indexMap.get(name);
    }
    public void addIndex(index in) {
        indexMap.put(in.indexName, in);
    }
    public void initFromDB(DBconnecter db) {
        try {
            String sql = "select * from idx";
            Vector<String> result = db.executeSQL(sql, null, null, true);
            int size = result.size();
            for(int i = 2; i < size - 1; ++i) {
                String temStr[] = result.get(i).split("\\s", 2);
                String indexName = temStr[0];
                index idx = new index(indexName);
                idx.fromString(temStr[1]);
                addIndex(idx);
            }
        }
        catch(Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}
