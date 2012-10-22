import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import java.util.Vector;


public class Client {
    static String encInfoKey = "paroid";
    public static void main(String[] args) {
        try {
            DBconnecter db = new DBconnecter();
            db.setInfo("root", "paroid", "test", "127.0.0.1");
            db.connect();
            dbInfo dbTable = new dbInfo();
            dbTable.initFromDB(db);
            indexSet idx = new indexSet();
            /*index in = new index("tab.id");
              in.initFromTable(dbTable, db);
              in.updateDB(db);*/
            idx.initFromDB(db);
            /*strMap.makeHval(db, dbTable, "tab");*/
            /*System.out.println(func.getMD5("abcdefghijklmnopqrstuvwxyz".getBytes()));*/
            /*user tu=new user();
            tu.setInfo("admin","paroid");
            tu.updateDB(db);            */
            NetConnecter net = new NetConnecter();
            while(true) {
                net.connect();
                System.out.println("console connected!");
                user usr = new user();
                while(true) {
                    net.writeLine("username & password?");
                    String uname = net.readLine();
                    String pw = net.readLine();
                    usr.setInfo(uname, pw);
                    if(usr.checkIn(db)) {
                        net.writeLine("Welcome!");
                        break;
                    }
                    net.writeLine("Wrong username or password !");
                    System.out.println("login failed! with  " + uname + " & " + pw);
                }
                System.out.println("user login!");
                //System.out.println(idx.getIndex("tab.id").count);
                System.out.println("Total: " + dbTable.tableMap.size() + " table.   " + idx.indexMap.size() + " index.");
                net.writeLine(String.valueOf(idx.getIndex("tab.id").count));
                net.writeLine("Total: " + dbTable.tableMap.size() + " table. " + idx.indexMap.size() + " index");
                while(true) {
                    /*System.out.println(">>>>");
                      String sql = func.getString();*/
                    net.writeLine(">>>>");
                    String sql = net.readLine();
                    System.out.println(sql);
                    if(sql.equals("exit")) {
                        System.out.println("Session Over!");
                        net.writeLine("Goodbye!");
                        net.client.close();
                        net.ss.close();
                        break;
                    }
                    if(sql.equals("init")) {//re-init system
                        dbTable.initFromDB(db);
                        idx.initFromDB(db);
                        System.out.println("System Initialized!\n" + "Total: " + dbTable.tableMap.size() + " table.  " + idx.indexMap.size() + " index.");
                        net.writeLine("System Initialized!\n" + "Total: " + dbTable.tableMap.size() + " table.  " + idx.indexMap.size() + " index.");
                        continue;
                    }
                    long start = System.nanoTime(); //timer start
                    Vector<String> result = db.executeSQL(sql, dbTable, idx);
                    long finish = System.nanoTime(); //timer end
                    System.out.println("time cost: " + String.valueOf((double)(finish - start) / 1000000) + " ms");
                    /*net.writeLine("time cost: "+String.valueOf((double)(finish-start)/1000000)+" ms");*/
                    int size = result.size();
                    if(size >= 3) { //query
                        if(size > 3) {
                            System.out.println(result.get(0));
                            System.out.println(result.get(1));
                            /*net.writeLine();*/
                            net.writeLine(result.get(0));
                            net.writeLine(result.get(1));
                            for(int i = 2; i < size - 1; ++i) {
                                StringTokenizer str = new StringTokenizer(result.get(i), " ");
                                while(str.hasMoreTokens()) {
                                    String tre = str.nextToken();
                                    System.out.print(tre + "\t");
                                    net.write(tre + " \t");
                                }
                                System.out.println();
                                net.writeLine();
                            }
                            System.out.println(result.get(1));
                            System.out.println(result.get(size - 1));
                            net.writeLine(result.get(1));
                        }
                        net.writeLine(result.get(size - 1));
                    }
                    else {
                        for(int i = 0; i < size; ++i) {
                            System.out.println(result.get(i));
                            net.writeLine(result.get(i));
                        }
                    }
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
class NetConnecter {
    ServerSocket ss = null;
    int port;
    Socket client = null;
    BufferedReader in = null;
    BufferedWriter out = null;
    NetConnecter() {
        port = 1990;
    }
    NetConnecter(int po) {
        port = po;
        try {
            ss = new ServerSocket(port);
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public String connect() {
        try {
            if(ss == null)
                return "";
            client = ss.accept();
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            return client.getInetAddress() + " @ " + client.getPort();
        }
        catch(Exception e) {
            System.out.println(e.getLocalizedMessage());
            //e.printStackTrace();
        }
        return "";
    }
    public String readLine() {
        String res = "";
        try {
            res = in.readLine();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
    public void write(String str) {
        try {
            out.write(str);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writeLine() {
        writeLine("");
    }
    public void writeLine(String str) {
        try {
            out.write(str);
            out.newLine();
            out.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}

