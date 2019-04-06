package com.matrixfrats.syntex;

/**
 * Created by DELL on 21-03-2018.
 */

public class Trialgettersetter {

    private String msearchimage;
    private String msearchname,mnamecode;

    public Trialgettersetter(String searchimage,String searchname,String namecode) {

        msearchimage = searchimage;
        msearchname = searchname;
        mnamecode = namecode;
    }

    public String getMsearchimage(){
        return msearchimage;
    }

    public String getMsearchname(){
        return msearchname;
    }

    public String getMsearchcode(){
        return mnamecode;
    }
}
