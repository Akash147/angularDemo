package com.akash.db;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

import java.io.File;

/**
 * Created by i81269 on 1/7/2016.
 */
public class KeyValueDAO {
    private DB db;
    private HTreeMap<String, String> map;

    private static KeyValueDAO mInstance;

    static{
        mInstance = new KeyValueDAO();
    }

    public KeyValueDAO() {
        db = DBMaker.newFileDB(new File("testdb"))
                .closeOnJvmShutdown()
                .encryptionEnable("password")
                .make();
        map = db.getHashMap("keyvalue");
    }

    public static void insertKeyValue(String key, String value) {
//        if(!mInstance.map.containsKey(key)){
            mInstance.map.put(key, value);
            mInstance.db.commit();
//        }
    }

    public static KeyValue getKeyValue(String serverKey) {
        if(mInstance.map.containsKey(serverKey)){
            KeyValue kv = new KeyValue();
            kv.setKey(serverKey);
            kv.setValue(mInstance.map.get(serverKey));
            return kv;
        }
        return null;
    }


}
