package com.cofdet.dap8600.CompMix_java;

//阵列或数组相关处理辅助函数集


import java.util.ArrayList;
import java.util.List;

public class AryUtils{

    //得到最小ID值函数
    public static int getMinId(List<Integer> orgList){
        ArrayList<Integer> arr = new ArrayList<>(orgList);//因需要交换位置，故复制一个
        int l =0;
        int r = arr.size();
        while( l < r ){
            int lData = arr.get(l);
            if(lData == l + 1){
                l++;
            }else if((lData <=l) || (lData > r ) || (arr.get(lData - 1) == lData)){
                arr.set(l, arr.get(--r));
            }else {//互换位置
                //arr.swap[arr,l,arr.get(l)-1);
                arr.set(l, arr.get(lData - 1));
                arr.set(lData - l, lData);
            }
        }
        return l + 1;
    }
};