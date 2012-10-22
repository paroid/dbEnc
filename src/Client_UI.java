import java.util.StringTokenizer;
import java.util.Vector;

class Client_UI extends Thread {
    public UI uiFrame;
    public boolean stop;
    private String serverIP;
    private String uname;
    static protected String password;
    private int port;
    protected NetConnecter net;
    private void setInfo() {
        serverIP = uiFrame.jTextField_sip.getText();
        uname = uiFrame.jTextField_username.getText();
        password = String.valueOf(uiFrame.jPasswordField_pw.getPassword());
        port = Integer.parseInt(uiFrame.jTextField_port.getText());
    }
    public void run() { //here run
        try {
            DBconnecter db = new DBconnecter();
            setInfo();
            db.setInfo(uname, password, "test", serverIP);
            db.connect();
            if(db.dbstat != null)
                uiFrame.jTextArea1.append("DB connected!\n");
            else {
                uiFrame.jTextArea1.append("DB connect failed!\n");
                return;
            }
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
            while(true) {
                net = new NetConnecter(port);
                net.connect();
                if(net.ss == null)
                    return;
                System.out.println("console connected!");
                uiFrame.jTextArea1.append("console connected!\n");
                user usr = new user();
                while(true) {
                    net.writeLine("username & password?");
                    if(net.client == null)
                        return;
                    String uname = net.readLine();
                    String pw = net.readLine();
                    usr.setInfo(uname, pw);
                    if(usr.checkIn(db)) {
                        net.writeLine("Welcome!");
                        break;
                    }
                    net.writeLine("Wrong username or password !");
                    System.out.println("login failed! with  " + uname + " & " + pw);
                    uiFrame.jTextArea1.append("login failed! with  " + uname + " & " + pw + "\n");
                }
                System.out.println("user login!");
                uiFrame.jTextArea1.append("user login!\n");
                //System.out.println(idx.getIndex("tab.id").count);
                System.out.println("Total: " + dbTable.tableMap.size() + " table.   " + idx.indexMap.size() + " index.");
                uiFrame.jTextArea1.append("Total: " + dbTable.tableMap.size() + " table.   " + idx.indexMap.size() + " index.\n");
                net.writeLine(String.valueOf(idx.getIndex("tab.id").count));
                net.writeLine("Total: " + dbTable.tableMap.size() + " table. " + idx.indexMap.size() + " index");
                while(true) {
                    //System.out.println(stop);
                    if(stop) { //stop
                        net.client.close();
                        net.ss.close();
                        return;
                    }
                    /*System.out.println(">>>>");
                      String sql = func.getString();*/
                    net.writeLine(">>>>");
                    String sql = net.readLine();
                    System.out.println(sql);
                    uiFrame.jTextArea1.append("SQL:  " + sql + "\n");
                    if(sql.equals("exit")) {
                        System.out.println("Session Over!");
                        uiFrame.jTextArea1.append("Session Over!\n");
                        net.writeLine("Goodbye!");
                        net.client.close();
                        net.ss.close();
                        break;
                    }
                    if(sql.equals("init")) {//re-init system
                        dbTable.initFromDB(db);
                        idx.initFromDB(db);
                        System.out.println("System Initialized!\n" + "Total: " + dbTable.tableMap.size() + " table.  " + idx.indexMap.size() + " index.");
                        uiFrame.jTextArea1.append("System Initialized!\n" + "Total: " + dbTable.tableMap.size() + " table.  " + idx.indexMap.size() + " index.\n");
                        net.writeLine("System Initialized!\n" + "Total: " + dbTable.tableMap.size() + " table.  " + idx.indexMap.size() + " index.");
                        continue;
                    }
                    long start = System.nanoTime(); //timer start
                    Vector<String> result = db.executeSQL(sql, dbTable, idx);
                    long finish = System.nanoTime(); //timer end
                    System.out.println("time cost: " + String.valueOf((double)(finish - start) / 1000000) + " ms");
                    uiFrame.jTextArea1.append("time cost: " + String.valueOf((double)(finish - start) / 1000000) + " ms\n");
                    /*net.writeLine("time cost: "+String.valueOf((double)(finish-start)/1000000)+" ms");*/
                    int size = result.size();
                    if(size >= 3) { //query
                        if(size > 3) {
                            System.out.println(result.get(0));
                            uiFrame.jTextArea1.append(result.get(0) + "\n");
                            System.out.println(result.get(1));
                            uiFrame.jTextArea1.append(result.get(1) + result.get(1) + "\n");
                            /*net.writeLine();*/
                            net.writeLine(String.valueOf(result.size())); //size
                            net.writeLine(result.get(0));
                            net.writeLine(result.get(1));
                            for(int i = 2; i < size - 1; ++i) {
                                StringTokenizer str = new StringTokenizer(result.get(i), " ");
                                while(str.hasMoreTokens()) {
                                    String tre = str.nextToken();
                                    System.out.print(tre + "\t");
                                    uiFrame.jTextArea1.append(tre + "\t");
                                    net.write(tre + " \t");
                                }
                                System.out.println();
                                uiFrame.jTextArea1.append("\n");
                                net.writeLine();
                            }
                            System.out.println(result.get(1));
                            uiFrame.jTextArea1.append(result.get(1) + result.get(1) + "\n");
                            System.out.println(result.get(size - 1));
                            uiFrame.jTextArea1.append(result.get(size - 1) + "\n");
                            net.writeLine(result.get(1));
                        }
                        net.writeLine(result.get(size - 1));
                    }
                    else {
                        for(int i = 0; i < size; ++i) {
                            System.out.println(result.get(i));
                            uiFrame.jTextArea1.append(result.get(i) + "\n");
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
