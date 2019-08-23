package com.han.hive.demo;


import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

public class HiveFunction extends UDF {

        public Text evaluate(final Text str){
            String tmp_str = str.toString();
            if(str != null && !tmp_str.equals("")){
                String str_ret =   tmp_str.substring(0, 1).toUpperCase() + tmp_str.substring(1);
                return  new Text(str_ret);
            }
            return  new Text("");
        }


}
