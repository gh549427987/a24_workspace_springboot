package com.jhua.config;

import org.mybatis.generator.api.ShellRunner;

/**
 * 
 * @author wangmengjun
 *
 */
public class generator {

  public static void main(String[] args) {

    String config = generator.class.getClassLoader()
            .getResource("config/generatorConfig.xml").getFile();
    String[] arg = { "-configfile", config, "-overwrite" };
    ShellRunner.main(arg);
  }
}