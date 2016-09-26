package com.kirer.utils;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.widget.Toast;


import com.kirer.KActivity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;

public class TUtil {


    /**
     * 构造一个新的ViewModule
     */
    public static <VM> VM getVM(KActivity o, ViewDataBinding binding) {
        try {
            Class[] paramTypes = {binding.getClass()};
            LUtil.d("TUtil getVM paramTypes --> " + paramTypes[0]);
            Object[] params = {binding};
            ParameterizedType parameterizedType = (ParameterizedType) o.getClass().getGenericSuperclass();
            Class<VM> tClass = (Class<VM>) parameterizedType.getActualTypeArguments()[1];
            Constructor<VM> con = tClass.getConstructor(paramTypes);
            return con.newInstance(params);
        } catch (InstantiationException e) {
            LUtil.e("TUtil getVM Exception --> " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            LUtil.e("TUtil getVM Exception --> " + e.getMessage());
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            LUtil.e("TUtil getVM Exception --> " + e.getMessage());
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            LUtil.e("TUtil getVM Exception --> " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static void showToast(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }
}
