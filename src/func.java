import java.io.*;
import java.util.Vector;

class func {
    public static InputStream string2InputStream(String str) {
        return new ByteArrayInputStream(str.getBytes());
    }

    public static byte[] int2byte(int res) {
        byte[] targets = new byte[4];
        targets[0] = (byte) (res & 0xff);
        targets[1] = (byte) ((res >> 8) & 0xff);
        targets[2] = (byte) ((res >> 16) & 0xff);
        targets[3] = (byte) (res >>> 24);
        return targets;
    }

    public static int byte2int(byte[] res) {
        int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00) | ((res[2] << 24) >>> 8) | (res[3] << 24);
        return targets;
    }

    static String enctable = "0123456789abcdef";
    public static byte invDecode(char c) {
        if(c >= '0' && c <= '9')
            return (byte) (c - '0');
        return (byte) (c - 'a' + 10);
    }
    public static String byte2String(byte[] array) {
        String res = "";
        for(int i = 0; i < array.length; ++i) {
            res += enctable.charAt(array[i] & 0xf);
            res += enctable.charAt((array[i] >> 4) & 0xf);
        }
        return res;
    }

    public static byte[] string2byte(String str) {
        int len = str.length() >> 1;
        byte res[] = new byte[len];
        for(int i = 0; i < len; ++i) {
            res[i] |= (invDecode(str.charAt(i << 1))) & 0xf;
            res[i] |= ((invDecode(str.charAt((i << 1) + 1))) & 0xf) << 4;
        }
        return res;
    }


    public static boolean RC4(byte[] data, String enckey) { //ENC func RC4
        if(enckey == null || enckey.equals("none"))
            return true;
        int l = enckey.length(), i, j, k;
        byte[] ss = new byte[256];
        byte[] res = new byte[data.length];
        for(i = 0; i < 256; i++)
            ss[i] = (byte) i;
        for(i = 0; i < l; i++) {
            j = (i + ss[i] + enckey.charAt(i) + 256) % 256;
            swap(ss, i, j);
        }
        i = j = 0;
        for (k = 0; k < data.length; k++) {
            i = (i + 1) % 256;
            j = (j + ss[i] + 256) % 256;
            swap(ss, i, j);
            int t = (ss[i] + ss[j] + 256) % 256;
            res[k] = (byte) (ss[t] ^ data[k]);
        }
        System.arraycopy(res, 0, data, 0, data.length);
        return true;
    }

    enum type {string, integer};
    public static String Encrypt(String in, String t, String key) {
        if(key.equals("none"))
            return in;
        type ty = type.valueOf(t);
        String res = "";
        switch(ty) {
            case integer:
                int intval = Integer.parseInt(in);
                byte temByte[] = func.int2byte(intval);
                func.RC4(temByte, key);
                intval = func.byte2int(temByte);
                res = String.valueOf(intval);
                break;
            case string:
                try {
                    temByte = in.getBytes("utf-8");
                    func.RC4(temByte, key);
                    res = func.byte2String(temByte);
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
        }
        return res;
    }

    public static String Decrypt(String in, String t, String key) {
        if(key.equals("none"))
            return in;
        type ty = type.valueOf(t);
        String res = "";
        switch(ty) {
            case integer:
                int intval = Integer.parseInt(in);
                byte temByte[] = func.int2byte(intval);
                func.RC4(temByte, key);
                intval = func.byte2int(temByte);
                res = String.valueOf(intval);
                break;
            case string:
                try {
                    temByte = func.string2byte(in);
                    func.RC4(temByte, key);
                    res = new String(temByte, "utf-8");
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
        }
        return res;
    }

    public static void swap(byte[] data, int a, int b) {
        data[a] ^= data[b];
        data[b] ^= data[a];
        data[a] ^= data[b];
    }

    public static Vector<String> resolve(String str) { //parse String like "a*,b*,c*,d*" to Vector [a*,b*,c*,d*]
        str.trim();
        Vector<String> res = new Vector<String>();
        int st = 0, end = str.length();
        while(st < end) {
            int p = str.indexOf(",", st);
            if(p < 0)
                p = end;
            String temp = str.substring(st, p).trim();
            if(temp.charAt(0) == '\'')
                temp = temp.substring(1, temp.length() - 1);
            res.add(temp);
            st = p + 1;
        }
        return res;
    }

    public static String getString() throws IOException {
        return new BufferedReader(new InputStreamReader(System.in)).readLine();
    }
    public static String getMD5(byte[] source) {
        String s = null;
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            md.update(source);
            byte tmp[] = md.digest();
            char str[] = new char[16 * 2];
            int k = 0;
            for (int i = 0; i < 16; i++) {
                byte byte0 = tmp[i];
                str[k++] = hexDigits[byte0 >> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            s = new String(str);
        }
        catch( Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
        return s;
    }
}
