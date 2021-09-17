package com.jhua.generator;

import org.mybatis.generator.api.ShellRunner;

/**
 * 
 * @author wangmengjun
 *
 */
public class generator {

  public static void main(String[] args) {

    String config = generator.class.getClassLoader()
            .getResource("generator/generatorConfig.xml").getFile();
//    String[] arg = { "-configfile", config, "-overwrite" };
    String[] arg = { "-configfile", config };
    ShellRunner.main(arg);
  }



}