package com.kirer;

import android.content.Context;

/**
 * Created by 信文波 on 2016/9/23.
 */

public class K {

    private K(){

    }

    private static class SingletonHolder {
        private static final K INSTANCE = new K();
    }

    public static K getInstance() {
        return K.SingletonHolder.INSTANCE;
    }

    private Context appContext;
    public  void init(Context context){
        this.appContext = context;
    }
    public Context getAppContext(){
        return appContext;
    }

}
