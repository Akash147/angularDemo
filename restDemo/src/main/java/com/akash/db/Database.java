package com.akash.db;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.util.concurrent.ConcurrentNavigableMap;

/**
 * Created by i81269 on 1/7/2016.
 */
public class Database {
    private DB db;

    public Database() {
        db = DBMaker.newFileDB(new File("testdb"))
                .closeOnJvmShutdown()
                .encryptionEnable("password")
                .make();
    }

    public void insert(String key, String value) {
        // open existing an collection (or create new)
        ConcurrentNavigableMap<String, String> map = db.createTreeMap("collectionName").

        map.put(1, "one");
        map.put(2, "two");

        db.commit();  //persist changes into disk

        map.put(3, "three");
// map.keySet() is now [1,2,3]
        db.rollback(); //revert recent changes
// map.keySet() is now [1,2]

        db.close();
    }
}
