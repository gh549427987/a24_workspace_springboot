### 
|变量|解释|<->|
|---|---|---|
||注册|---|
|DEVICE_COUNT_NEW|设备装机数（新增）||
|DEVICE_COUNT_ALL|设备装机数（累计）||
|USER_COUNT_NEW|注册账号数（新增）||
|USER_COUNT_ALL|注册账号数（累计）||
|NULL|触达商家数（新增）-定义：绑定设备数超过1个，且游戏数据不只有BS的账号||
|NULL|触达商家数（累计）-定义：绑定设备数超过1个，且游戏数据不只有BS的账号||
|STORE_COUNT_NEW|覆盖商家数-定义：填写商家信息账号数（新增）||
|STORE_COUNT_ALL|覆盖商家数-定义：填写商家信息账号数（累计）||

##环境准备
### joinery库准备
**Maven**
```xml
  <repositories>
        <repository>
            <id>joinery</id>
            <url>https://dl.bintray.com/cardillo/maven</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>joinery</groupId>
            <artifactId>joinery-dataframe</artifactId>
            <version>1.9</version>
        </dependency>

        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi</artifactId>
            <version>3.17</version>
        </dependency>
    </dependencies>
```
> - 需要添加仓库地址
> - 之前加了个<type>Pom</type>搞得我半天都加载不进来,TMD烦死了

```java
if (Max.isEmpty()) {// 暂无最大数，不清空
    Max.put(dataFrame_user_id, paymoney);
}
else { // 如果已经存在最大的user_id 和 paymoney
    Double max_paymoney = (Double) Max.values().toArray()[0];
    if (max_paymoney < paymoney) {
        // 如果比之前的大
        Max.clear();
        Max.put(dataFrame_user_id, paymoney); //更大的上位
    } else if (max_paymoney.equals(paymoney)) {
        Max.put(dataFrame_user_id, paymoney); // 如果两者相等，不清除max表,多增加一个
    } 
}
```

###CSV准备
注意事项：
- 通过GripData导出的时候，记得选择”Comma-separated (CSV)",勾选上“Add Column Header"
- 因为joinery这个比读取CSV的时候，把第一行当成是表头，我们不加的话，他会报错