package com.comet_000.myapplication;

/**
 * Created by comet_000 on 03/02/2015.
 */
public class Extra {
    public static boolean checkDuplicatedStrings(String str, String str2)
    {
        if (str.toLowerCase().equals(str2.toLowerCase()))
            return  true;
        return  false;
    }
}
