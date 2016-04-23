package com.hugo.myqlu.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by 田宇 on 2016-04-22.
 */
public class JsoupUtils {

    private static void setPageNumber(Context context, String PAGE_ID, int pageNumber) {
        SharedPreferences page = context.getSharedPreferences("PAGE_ID", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = page.edit();
        editor.putInt(PAGE_ID, pageNumber);
        editor.commit();
    }

    private static int getPageNumber(Context context, String PAGE_ID) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PAGE_ID", Context.MODE_WORLD_READABLE);
        return sharedPreferences.getInt(PAGE_ID, 785);
    }

    public static Document parseNewsHtmlFromUrl(Context context, String url, String PAGE_ID) {
        Document doc = null;
        try {
            int pageNumberTry = getPageNumber(context, PAGE_ID) + 1;
            doc = Jsoup.connect(url + PAGE_ID + pageNumberTry + "/list.htm").get();
            System.out.println(url + PAGE_ID + getPageNumber(context, PAGE_ID) + "/list.htm");
            setPageNumber(context, PAGE_ID, pageNumberTry);
            System.out.println(doc.title());
        } catch (IOException e) {
            doc = Jsoup.connect(url + PAGE_ID + getPageNumber(context, PAGE_ID) + "/list.htm").get();
        } finally {
            return doc;
        }
    }
}
