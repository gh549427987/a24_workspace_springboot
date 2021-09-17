package com.jhua.utils;

import com.jhua.constants.Constants;
import com.jhua.Singleton.UserSingleton;
import joinery.DataFrame;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;


public class UserOperationUtil {

    //非正常的账号列表
    public static ArrayList<String > unnormalAccountList() throws IOException {
        /*
         * @Author liu-miss
         * @Description //TODO 读取李程给的xlsx，获取非正常的账号list
         * @Date  2021/3/16
         * @Param []
         * @return java.util.ArrayList<org.apache.poi.ss.usermodel.Cell>
         **/
        XSSFWorkbook wb = new XSSFWorkbook(Constants.USER_DIR + Constants.ALL_ACCOUNTS_AFTER_SELECT_PATH);
        Sheet sheet3 = wb.getSheet("非正常账号");

        //生成非正常的所有账号的列
        ArrayList<String> unnormalAccounts = new ArrayList<>();
        for (int i = 1; i < sheet3.getLastRowNum()+1; i++) {
            unnormalAccounts.add(sheet3.getRow(i).getCell(2).toString());
        }

        return unnormalAccounts;
    }

    //返回User所有user_id 和 email的对应关系
    public static HashMap<String, String> getUserIdAndItsEmailMap() {
        return null;
    }

    //返回User所有的测试账号UserId
    public static ArrayList<String > getTestAccountsUserIdList() throws IOException, SQLException {
        UserSingleton.getInstance();
        DataFrame<Object> dataFrame = UserSingleton.raw_dataFrameOfUser(); //获取完整user表
        Map<String, Integer> sim = DataDealer.columns_index(dataFrame);

        ArrayList<String> test_user_id = new ArrayList<>();
        ListIterator<List<Object>> iterrows = dataFrame.iterrows();

        while (iterrows.hasNext()) {
            Object[] next = iterrows.next().toArray();

            String userid = (String) next[sim.get("user_id")];
            String email = (String) next[sim.get("email")];

            if (!(email == null) && (email.contains("web_qa") || email.contains("ad_nsqa"))) {
                test_user_id.add(userid);
            }
        }

        return test_user_id;
    }

    //去除非正常的账号
    public static DataFrame<Object> reduceUnnormalAccounts(DataFrame<Object> dataFrame) throws IOException, SQLException {
        /*
         * @Author liu-miss
         * @Description //TODO 去除非正常账号（测试账号或者其他CP账号）采用两种办法：
         *                 1. 通过李程给的数据手动去除一部分
         *                  2. 通过此处的代码，进行进一步的筛除测试账号
         * @Date  2021/3/17
         * @Param [dataFrame]
         * @return joinery.DataFrame<java.lang.Object>
         **/
        //去除所有非正常的账号

        ArrayList<String > unnormalAccountList = unnormalAccountList();
        DataFrame<Object> dataFrame1 = new DataFrame<>(dataFrame.columns());

        // 先确认“user_id"的具体索引在哪里
        int key_index = 0;
        for (Object column : dataFrame.columns()) {
            if (((String)column).equals("user_id")) {
                break;
            }
            key_index++;
        }

        //第一种办法， 李程数据，对user_id去重
        ListIterator<List<Object>> iterrows = dataFrame.iterrows();
        while (iterrows.hasNext()) {
            List<Object> next = iterrows.next();

            // 如果在非正常账号范围内，跳过不执行
            String AccountID = (String) next.get(key_index);
            if (unnormalAccountList.contains(AccountID)) {
                continue;
            }

            dataFrame1.append(next);
        }

        //第二种办法，手动去除未来可能持续产生的web和ad_nsqa系列的测试账号;通过对email字段，进行去除 【不能通过email去重，需要转换成对应的user_id才可以，否则不具有普适性】
        Map<String, Integer> sim_df = DataDealer.columns_index(dataFrame);

        ArrayList<String> testAccountsUserIdList = getTestAccountsUserIdList(); //获取所有测试账号的list
        dataFrame1 = dataFrame1.select(value -> {
            Integer index_df_userid = sim_df.get("user_id");
            return !testAccountsUserIdList.contains(value.get(index_df_userid));
        });

        return dataFrame1;
    }

}
