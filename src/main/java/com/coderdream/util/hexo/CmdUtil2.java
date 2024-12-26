package com.coderdream.util.hexo;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CmdUtil2 {
    public static void main(String[] args) {
      try {
              Runtime rt = Runtime.getRuntime();
//              Process pr = rt.exec("cmd /c ping www.baidu.com && dir");
              //Process pr = rt.exec("D:\\xunlei\\project.aspx");

        Process pr = rt.exec("D:/04_GitHub/hexo-project/Hexo-BlueLake-Blog/hexo g");
              BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream(), "GBK"));
              String line = null;
              while ((line = input.readLine()) != null) {
                  System.out.println(line);
              }
              int exitVal = pr.waitFor();
              System.out.println("Exited with error code " + exitVal);

           } catch (Exception e) {
             System.out.println(e.toString());
             e.printStackTrace();
        }
    }
}
