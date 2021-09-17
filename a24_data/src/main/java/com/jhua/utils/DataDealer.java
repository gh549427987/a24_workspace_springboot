package com.jhua.utils;

import com.jhua.constants.Constants;
import com.jhua.event.PlayRecord;
import joinery.DataFrame;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class DataDealer {
    /*
    获取临时表格的一些基本数据，比如列名的索引
    需要用啥再添加啥
     */

    // 检查输入时间，返回pattern
    public static String checkPattern(String fromDate, String toDate) throws ParseException {
        /*
         * @Author liu-miss
         * @Description //TODO 校验起始和截止时间是否合格
         * @Date  2021/3/16
         * @Param [fromDate, toDate]
         * @return void
         **/
        String pattern = null;

        Date from = new Date();
        Date to = new Date();

        // 过滤掉不合长度的东东
        if ((fromDate.length()==7 && toDate.length()==7)) {
            pattern = "yyyy-MM";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            return pattern;

        } else if (fromDate.length()==10 && toDate.length()==10) {
            pattern = "yyyy-MM-dd";
            return pattern;
        } else if (fromDate.length()==13 && toDate.length()==13) {
            pattern = "yyyy-MM-dd ww";
            return pattern;
        }

        return null;
    }

    //csv转换输出xlsx
    public static void csv2Xlsx() throws IOException {
        File resource = new File(Constants.RESOURCE_PATH);
        File[] files = resource.listFiles();
        for (File file : files) {
            if (file.getName().endsWith(".csv")) {
                String name = file.getName().replace(".csv", "");
                DataFrame<Object> fromCsv = DataFrame.readCsv(String.valueOf(file));
                DataDealer.writeXls(fromCsv, Constants.OUT_PUT_PATH + name + ".xlsx");
            }
        }
    }

    // 计算两个日期相隔多少天
    public static int differentDaysByMillisecond(Date date1,Date date2) {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
        return days;
    }

    // 时间比较， 传入pattern和两个需要比较的时间
    public static boolean equalsTime(String firstTime, String secondTime, String pattern) throws ParseException {

        SimpleDateFormat sim = new SimpleDateFormat(pattern);

        Calendar cal_first = Calendar.getInstance();
        cal_first.setFirstDayOfWeek(Calendar.MONDAY);
        Calendar cal_second = Calendar.getInstance();
        cal_second.setFirstDayOfWeek(Calendar.MONDAY);

        cal_first.setTime(sim.parse(firstTime));
        cal_second.setTime(sim.parse(secondTime));

        boolean c1 = cal_first.get(Calendar.WEEK_OF_YEAR) == cal_second.get(Calendar.WEEK_OF_YEAR);
        boolean c2 = cal_first.equals(cal_second);
        boolean c3 = cal_first.get(Calendar.YEAR) == cal_second.get(Calendar.YEAR);

        if (pattern.length() == 13)
            return c1 && c3;
        else {
            return c2 ;
        }
    }

    // 获取索引和列名的对应关系
    public static Map<String, Integer> columns_index(DataFrame<Object> dataFrame) {
        /*
         * @Author liu-miss
         * @Description //TODO 获取列名与索引的字典关系
         * @Date  2021/3/16
         * @Param [dataFrame]
         * @return java.util.Map<java.lang.String,java.lang.Integer>
         **/
        Object[] col_index = dataFrame.columns().toArray();
        HashMap<String, Integer> map = new HashMap<>();
        int count = 0;
        for (Object colIndex : col_index) {
            map.put((String) colIndex, count);
            count++;
        }
        return map;
    }

    // 格式化对应时间
    @Deprecated
    public static void format_time(DataFrame<Object> dataFrame, String formatColName, String pattern) throws ParseException {
        /*
         * @Author liu-miss
         * @Description //TODO 把dataFram下的时间全部转换为指定格式，方便groupby;pattern [yyyy-MM, yyyy-MM-dd]
         * @Date  2021/3/16
         * @Param [dataFrame, formatColName, pattern]
         * @return joinery.DataFrame<java.lang.Object>
         **/


        //调用方法获取列名与索引的关系    
        Map<String, Integer> stringIntegerMap = columns_index(dataFrame);

        // 先重新格式化所有时间为日/月/周度
        ListIterator<List<Object>> iterrows = dataFrame.iterrows();
        int row_count = 0;
        while (iterrows.hasNext()) {
            List<Object> next = iterrows.next();

            // 重新格式化日期
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            Date o = (Date) next.get(stringIntegerMap.get(formatColName));
            String s = sdf.format(o);
            dataFrame.set(row_count, stringIntegerMap.get(formatColName), s);

            row_count++;
        }
    }

    //创建基本的空时间表
    public static DataFrame<Object> format_time_dataframe(String fromDate, String toDate, Integer columnsSize) throws ParseException {
        /*
         * @Author liu-miss
         * @Description //TODO 新建一个从 from 到 to 时间段的新列表 （eg. from="2021-01" to="2021-08" or from="2021-01-01" to="2021-08-01")
         *                 columnsSize 为-1时候，可创建index和columns都是时间的空dataFrame
         * @Date  2021/3/16
         * @Param [from, to, columnsSize]
         * @return joinery.DataFrame<java.lang.Object>
         **/
        // 处理传参
        boolean isLiuCun = false;
        boolean isMonth = false;
        boolean isDate = false;
        boolean isWeek = false;
//        if (columnsSize == -1)
//            isLiuCun = true;
        String pattern = DataDealer.checkPattern(fromDate, toDate);
        // 过滤掉不合长度的东东
        assert pattern != null;
        if (pattern.length() == 7) {
            isMonth = true;
        } else if (pattern.length()==10) {
            isDate = true;
        } else if (pattern.length()==13)
            isWeek = true;
        else
            return null;
        
        DataFrame<Object> dataframe = new DataFrame("Date", "Content");


        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        Calendar start_cal = (Calendar) cal.clone();
        start_cal.setFirstDayOfWeek(Calendar.MONDAY);
        Calendar end_cal = (Calendar) cal.clone();
        end_cal.setFirstDayOfWeek(Calendar.MONDAY);

        start_cal.setTime(sdf.parse(fromDate));
        end_cal.setTime(sdf.parse(toDate));

        ArrayList<String> date_time_of_dateFrame = new ArrayList<>();
        date_time_of_dateFrame.add("Content"); //先添加第一个填充值
        date_time_of_dateFrame.add(sdf.format(start_cal.getTime())); //再添加第一个日期
        while (start_cal.before(end_cal)) {
            // 判断添加月还是日
            if (isMonth)
                start_cal.add(Calendar.MONTH, 1);
            else if (isDate)
                start_cal.add(Calendar.DAY_OF_MONTH, 1);
            else if (isWeek)
                start_cal.add(Calendar.WEEK_OF_YEAR, 1);

            date_time_of_dateFrame.add(sdf.format(start_cal.getTime()));
        }
        date_time_of_dateFrame.trimToSize();

        //创建空的列表，方便之后创建列
        if (columnsSize == -1) {
            DataFrame<Object> result = new DataFrame<>(date_time_of_dateFrame, date_time_of_dateFrame);
            result = result.drop(0);
            return result;
        } else {
            ArrayList<Integer> columns = (ArrayList<Integer>) createDetectedList(columnsSize);
            return new DataFrame<>(date_time_of_dateFrame, columns);
        }

//        if (columnsSize != -1) {
//            columns = (ArrayList<Integer>) createDetectedList(columnsSize);
//            return new DataFrame<>(date_time_of_dateFrame, columns);
//        } else {
//            date_time_of_dateFrame.set(0, "平台留存");
//            return new DataFrame<>(date_time_of_dateFrame.subList(1, date_time_of_dateFrame.size()), date_time_of_dateFrame);
//        }
    }

    // 去掉“create_time"
    public static DataFrame<Object> reduceCreateTime(DataFrame<Object> dataFrame) {
        /*
         * @Author liu-miss
         * @Description //TODO 适用于如User和Store表，删除掉create_time列，看起来更好看
         * @Date  2021/3/16
         * @Param [dataFrame]
         * @return joinery.DataFrame<java.lang.Object>
         **/
        return dataFrame.drop("create_time");
    }

    // 创建[0, 1, ..., N]的列表
    public static List<Integer> createDetectedList(Integer size) {
        ArrayList<Integer> columns = new ArrayList<>(); //创建只有一个值的列表
        for (int i = 0; i < size; i++) {
            columns.add(i);
        }
        columns.trimToSize();
        return columns;
    }

    // List去重
    public static List<Object> reduceSameThing(List<Object> list) {
        /*
         * @Author liu-miss
         * @Description //TODO 数组去重
         * @Date  2021/3/16
         * @Param [list]
         * @return java.util.List<java.lang.Object>
         **/
        List<Object> col = list;

        List<Object> col_new = new ArrayList<>();
        for (Object o : col) {
            if (!col_new.contains(o))
                col_new.add(o);
        }
        return col_new;
    }

    //创建抽离单一列名dataFrame,去重
    public static ArrayList<String> createSingleKeyDataFrame(DataFrame<Object> dataFrame, String key) {
        /*
         * @Author liu-miss
         * @Description //TODO 给予特定的key，返回一个新的以这个key为第一列的dataFrame
         * @Date  2021/3/16
         * @Param [key]
         * @return joinery.DataFrame<java.lang.Object>
         **/

        // 去重
        List<Object> col = reduceSameThing(dataFrame.col(key));
        ArrayList<String> single = new ArrayList<String>();
        for (Object o : col) {
            if (!single.contains(o)) {
                single.add((String) o);
            }
        }
        return single;


        //创建空的列表，方便之后创建列

//        List<Integer> columns = createDetectedList(columnsSize);


    }

    //select筛选指定时间区间的数据并返回dataFrame
    public static void selectDataFrameOfDetectedTime(DataFrame<Object> dataFrame, String fromDate, String toDate) throws ParseException {

        //时间转换
        String pattern = DataDealer.checkPattern(fromDate, toDate);
        assert pattern != null;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        Calendar start_cal = Calendar.getInstance();
        start_cal.setFirstDayOfWeek(Calendar.MONDAY);
        Calendar end_cal = Calendar.getInstance();
        end_cal.setFirstDayOfWeek(Calendar.MONDAY);

        Date start_date = sdf.parse(fromDate);
        Date end_date = sdf.parse(toDate);

        start_cal.setTime(start_date);
        end_cal.setTime(end_date);

        Map<String, Integer> map = DataDealer.columns_index(dataFrame);
        Calendar cal_of_dataFrame = Calendar.getInstance();
        cal_of_dataFrame.setFirstDayOfWeek(Calendar.MONDAY);
        dataFrame = dataFrame.select(value -> {

            //转换时间
            Object create_time = value.get(map.get("create_time"));

            if (create_time instanceof String) {
                try {
                    cal_of_dataFrame.setTime(sdf.parse((String) create_time));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (create_time instanceof Date) {
                Date time = (Date) value.get(map.get("create_time"));
                cal_of_dataFrame.setTime(time);
            }



            Boolean c1 = cal_of_dataFrame.after(start_cal); //在起始时间之后
            Boolean c2 = cal_of_dataFrame.before(end_cal); //在截止时间之前
            return c1 && c2;
        });
    }

    //传入指定dataFrame， column_key，contentType,返回一个result表,
    //content |         date1       | date 2 | ...
    //key1    |  (contentType)value | ...    |
    //key2    |     ...             | ...    |
    //...
    public static DataFrame<Object> getCountsResultDataFrame(DataFrame<Object> dataFrame, String fromDate, String toDate,  String column_key, int contentType) throws ParseException {
        /*
         * @Author liu-miss
         * @Description //TODO contentType : 数据写入的时候 0 - Double ; 1 - Interger
         *                 如果column_key为null， 说明不需要使用给定key，直接写入平台留存
         * @Date  2021/3/18
         * @Param [dataFrame, fromDate, toDate, column_key, contentType]
         * @return joinery.DataFrame<java.lang.Object>
         **/

        String pattern = DataDealer.checkPattern(fromDate, toDate);
        // 分发数据 先创建一个带有时间的空dataFrame
        DataFrame<Object> result = DataDealer.format_time_dataframe(fromDate, toDate, dataFrame.col(column_key).size());

        assert result != null;
        result = result.transpose();
        // 遍历并把所有的东西都写进去
        List<Object> result_userid_list = result.col(0);
        // 确定是同一行
        int row;
        for (int i = 0; i < dataFrame.col(0).size(); i++) {

            // 确定是同一行
            row = i;

            // USER_ID
            // 先判断dataFrame此处的user_id是否在result的index列表内
            String user_id = (String) dataFrame.get(i, 0);
            if (!(result_userid_list.contains(user_id))) {

                // 如果不存在这个user_id的话，就给他加上
                for (int j = 0; j < result_userid_list.size(); j++) {
                    if (result_userid_list.get(j) == null) {
                        result.set(j, 0, user_id);
                        row = j;
                        break;
                    }

                }
            } else {
                // 如果存在这个user_id,确定为同一行
                for (int j = 0; j < result_userid_list.size(); j++) {
                    if (result_userid_list.get(j).equals(user_id)) {
                        row = j;
                        break;
                    }
                }
            }

            String user_time = (String) dataFrame.get(i, 1);//时间获取
            //CREATE_TIME
            //遍历result表的时间，看是否有符合的，添加进去
            for (int j = 1; j < result.columns().size(); j++) {
                String result_time = (String) result.columns().toArray()[j]; //获取列名，硬转String

                // 先比较时间谁大谁小
                Calendar cal_result = Calendar.getInstance();
                cal_result.setFirstDayOfWeek(Calendar.MONDAY);
                Calendar cal_user = Calendar.getInstance();
                cal_user.setFirstDayOfWeek(Calendar.MONDAY);
                assert pattern != null;
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);

                Date result_time_d = sdf.parse(result_time);
                Date user_time_d = sdf.parse(user_time);

                cal_result.setTime(result_time_d);
                cal_user.setTime(user_time_d);

                // 分开 日-月比较和周比较
                if (pattern.length() != 13) {

                    // 如果子表的时间，比结果表的时间还早，那就没必要继续遍历了
                    if (cal_user.before(cal_result)) {
                        break;
                    } else if (cal_user.equals(cal_result)){
                        // 判断两个时间是否相等
                        result.set(row, j, dataFrame.get(i, 2));
                        break;
                    }
                } else {
                    // 比较周
                    int wiy_user = cal_user.get(Calendar.WEEK_OF_YEAR);
                    int wiy_result = cal_result.get(Calendar.WEEK_OF_YEAR);
                    if (wiy_user == wiy_result){
                        // result非null，说明已经有数据已经在里面，需要加起来
                        if (result.get(row, j) != null){

                            // 处理整型
                            if (contentType == 0) {
                                Integer o = (Integer) dataFrame.get(i, 2) + (Integer) result.get(row, j);
                                result.set(row, j, o);
                                break;
                            // 处理Double
                            } else if (contentType == 1) {
                                Double o = (Double) dataFrame.get(i, 2) + (Double) result.get(row, j);
                                result.set(row, j, o);
                                break;
                            }
                        } else {
                            result.set(row, j, dataFrame.get(i, 2));
                            break;
                        }
                    }
                }
            }
        }

        return result;
    }

    //创建游戏留存表,起始不太正确的
    @Deprecated
    public static DataFrame<Object> createLiuCunRawTable(String fromDate, String toDate, List<Object > keyList) throws ParseException {

        DataFrame<Object> liu_cun = format_time_dataframe(fromDate, toDate, keyList.size());

        assert liu_cun != null;
        liu_cun = liu_cun.transpose();
        // 添加第一列元素
        int row = 0;
        for (Object o : keyList) {
            String game_name = (String) o;
            if (game_name.contains("购买"))
                continue;
            liu_cun.set(row, 0, o);
            row++;
        }

        // 去掉多余行
        liu_cun = liu_cun.transpose();
        int max = liu_cun.columns().size();
        int times = max - row;
        int count = 0;
        try {
            while (count<times){
                liu_cun = liu_cun.drop(liu_cun.columns().size()-1);
                count++;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        liu_cun = liu_cun.transpose();
        return liu_cun;
    }

    // 创建正常留存表
    public static DataFrame<Object> createLiuCunTable(String fromDate, String toDate, String contentName) throws ParseException {

        String pattern = DataDealer.checkPattern(fromDate, toDate);
        // 分发数据 先创建一个带有时间的空dataFrame
        DataFrame<Object> result = DataDealer.format_time_dataframe(fromDate, toDate, -1);
        result.rename("Content", contentName);
        result = result.transpose();
//        result.set(0, 0, contentName);

        // 填入列时间
        List<Object> rows = Arrays.asList(result.index().toArray());
        for (int row = 0; row < rows.size(); row++) {
            result.set(row, 0, rows.get(row));
        }
        return result;
    }

    // 游戏周活跃商家以及占比写入
    public static DataFrame<Object> perGameHuoYueAndZhanBiWriteInMethod(DataFrame<Object> dataFrame, DataFrame<Object> base, String fromDate, String toDate) throws ParseException, IOException {

        // 处理传参
        String pattern = DataDealer.checkPattern(fromDate, toDate);

//        System.out.println("liuCunWriteInMethod ==============================");
//        System.out.println(dataFrame);
//        System.out.println(base);
//        System.out.println("liuCunWriteInMethod ==============================");

        // 设置基本参数
        Calendar cal_dataFrame = Calendar.getInstance();
        cal_dataFrame.setFirstDayOfWeek(Calendar.MONDAY);
        Calendar cal_thisDate = Calendar.getInstance();
        cal_thisDate.setFirstDayOfWeek(Calendar.MONDAY);
        assert pattern != null;
        SimpleDateFormat sim = new SimpleDateFormat(pattern);

        // 获得时间列表
        Map<String, Integer> index_sim = DataDealer.columns_index(dataFrame);
        Object[] base_columns = base.columns().toArray();

        // 先计算出每个日期下的user_id count，获得一个hashmap
        HashMap<String, Integer> userIdCount_of_thisDate = new HashMap<>();
        for (int i = 1; i < base_columns.length; i++) {
            String date = (String) base_columns[i];

            // 设置时间
            cal_thisDate.setTime(sim.parse(date));

            // 遍历dataFrame, 获得该日期下的商家数
            DataFrame<Object> store_user_count = dataFrame.select(value -> {
                String date_of_dataFrame = (String) value.get(index_sim.get("create_time"));

                // 时间比较
                try {
                    return equalsTime(date, date_of_dataFrame, pattern);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return null;
            }).groupBy("user_id", "create_time");

            // 获得user_id列，并且去重user_id
            List<Object> user_id_weiquchang = store_user_count.col("user_id");

                // 去重
            ArrayList<String> quchong = new ArrayList<>();
            for (Object o : user_id_weiquchang) {
                String user_id = (String) o;
                if (!quchong.contains(user_id)) {
                    quchong.add(user_id);
                }
            }


            // 添加到字典中去
            userIdCount_of_thisDate.put(date, quchong.size());

            // 清空去重列，继续计算
            quchong.clear();
        }

        // 遍历活跃表的每一格，每行到每列遍历
        for (int row = 0; row < base.index().size(); row++) {
            for (int col = 1; col < base.columns().size(); col++) {

                // 遍历，设置基础参数
                String date = (String) base_columns[col];
                String game = (String) base.get(row, 0);

                // 设置时间
                cal_thisDate.setTime(sim.parse(date));

                // 遍历dataFrame, 获得该日期下, 玩这个游戏的商家数
                DataFrame<Object> thisDate_user_count = dataFrame.select(value -> {
                    String date_of_dataFrame = (String) value.get(index_sim.get("create_time"));
                    String game_of_dataFrame = (String) value.get(index_sim.get("game_name"));

                    // 时间比较
                    boolean c1 = false;
                    try {
                        c1 =  equalsTime(date, date_of_dataFrame, pattern);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    // 游戏比较
                    boolean c2 = game_of_dataFrame.equals(game);

                    return c1 && c2;
                }).groupBy("user_id", "create_time");

                // 获得user_id列，并且去重user_id
                List<Object> user_id_weiquchang = thisDate_user_count.col("user_id");

                // 去重
                ArrayList<String> quchong = new ArrayList<>();
                for (Object o : user_id_weiquchang) {
                    String user_id = (String) o;
                    if (!quchong.contains(user_id)) {
                        quchong.add(user_id);
                    }
                }

                // 今天有多少人玩
                int today_game_counts = quchong.size();
                int today_all_counts = userIdCount_of_thisDate.get(date);
                double zhanbi = (double) today_game_counts/today_all_counts;

                // 插入数据
                base.set(row, col, today_game_counts);
                base.set(row+1, col, zhanbi);

//                System.out.println(base);
                quchong.clear();
            }
            row++;
        }
//        base.writeXls("base.xlsx");

        return base;
    }

    // 游戏留存写入
    @Deprecated
    public static DataFrame<Object> liuCunWriteInMethod(DataFrame<Object> dataFrame, DataFrame<Object> liuCun, String fromDate, String toDate) throws ParseException {

        // 处理传参
        String pattern = DataDealer.checkPattern(fromDate, toDate);

        DecimalFormat df = new DecimalFormat("0.000%");


//        System.out.println("liuCunWriteInMethod ==============================");
//        System.out.println(dataFrame);
//        System.out.println(liuCun);
//        System.out.println("liuCunWriteInMethod ==============================");

        // 遍历留存表的每一格，每行到每列遍历
        Map<String, Integer> index_sim = DataDealer.columns_index(dataFrame);
        Object[] liuCun_columns = liuCun.columns().toArray();
        ArrayList<String> thisDate_userId = new ArrayList<>();
        ArrayList<String> beforeDate_userId = new ArrayList<>();
        ArrayList<Object> liuCun_userId = new ArrayList<>();
        for (int row = 0; row < liuCun.index().size(); row++) {
            beforeDate_userId.clear();

            for (int col = 1; col < liuCun.columns().size(); col++) {

                thisDate_userId.clear(); // 初始化今天数据

                String game_name = (String) liuCun.get(row, 0);
                assert pattern != null;
                SimpleDateFormat sim = new SimpleDateFormat(pattern);

                // 通过dataFrame获取这一个日期的前提下，user_id的count，以及user_id的列表
                int finalCol = col;
                DataFrame<Object> sub_liucun = dataFrame.select(value -> {
                    // 时间比较
                    SimpleDateFormat sim_3 = new SimpleDateFormat(pattern);
                    Calendar cal_date = Calendar.getInstance();
                    cal_date.setFirstDayOfWeek(Calendar.MONDAY);
                    Calendar cal_liucun = Calendar.getInstance();
                    cal_liucun.setFirstDayOfWeek(Calendar.MONDAY);
                    String create_time = (String) value.get(index_sim.get("create_time"));
                    String date = liuCun_columns[finalCol].toString();
                    try {
                        cal_date.setTime(sim_3.parse(create_time));
                        cal_liucun.setTime(sim_3.parse(date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    String game_name1 = (String) value.get(index_sim.get("game_name"));
                    boolean c1 = cal_date.get(Calendar.WEEK_OF_YEAR) == cal_liucun.get(Calendar.WEEK_OF_YEAR);
                    boolean c2 = game_name1.equals(game_name);
                    return c1 && c2;
                }); //筛选出合适日期下的表


                // 统计今天的的用户到 thisDate_userId
                Map<String, Integer> index_sim2 = DataDealer.columns_index(sub_liucun);
                for (List<Object> sl : sub_liucun) {
                    thisDate_userId.add((String) sl.get(index_sim2.get("user_id"))); // 添加到列表里
                }

                // 统计留存用户到 liuCun_userId
                if (!beforeDate_userId.isEmpty()) { //如果有昨天数据
                    for (String bu : beforeDate_userId) {
                        if (thisDate_userId.contains(bu)) {
                            liuCun_userId.add(bu);
                        }
                    }
                }

                // 插入数据
                if (col <= 1) {
                    // 由于没有前日期数据，这里跳过
                    liuCun.set(row, col, "skip");
                } else if (liuCun_userId.size() == 0) {
                    liuCun.set(row, col , 0); // 无留存
                } else {
                    Double value = (double) liuCun_userId.size() / beforeDate_userId.size();
                    liuCun.set(row, col , df.format(value)); // 插入留存
                }

//                System.out.println("1");

                // 遍历结束, 把统计昨天到 beforeDate_userId
                liuCun_userId.clear(); // 初始化
//                System.out.println("2");
            }
        }
//        System.out.println(liuCun);

        return liuCun;
    }

    // 游戏新留存写入
    public static DataFrame<Object> liuCunWriteInMethodForGame(DataFrame<Object> dataFrame_pr, DataFrame<Object> liuCun, String fromDate, String toDate, String game_name) throws ParseException, IOException {

        // 处理传参
        String pattern = DataDealer.checkPattern(fromDate, toDate);

        DecimalFormat df = new DecimalFormat("0.000%");


        // 遍历留存表的每一格，每行到每列遍
//        Map<String, Integer> index_sim = DataDealer.columns_index(dataFrame_pr);
        Object[] liuCun_columns = liuCun.columns().toArray();
        ArrayList<String> thisDate_userId = new ArrayList<>();
        ArrayList<String> firstDate_userId = new ArrayList<>();
        ArrayList<Object> liuCun_userId = new ArrayList<>();

        // 提前创建时间
        Calendar cal_date = Calendar.getInstance();
        cal_date.setFirstDayOfWeek(Calendar.MONDAY);
        Calendar cal_liucun = Calendar.getInstance();
        cal_liucun.setFirstDayOfWeek(Calendar.MONDAY);

        Map<String, Integer> sim_pr = DataDealer.columns_index(dataFrame_pr);

        for (int row = 0; row < liuCun.index().size(); row++) {

            firstDate_userId.clear();
            String row_date = (String) liuCun.get(row, 0);
            boolean isGotFirstDat = false;

            for (int col = 1; col < liuCun.columns().size(); col++) {

                thisDate_userId.clear(); // 初始化今天数据
                assert pattern != null;
                SimpleDateFormat sim = new SimpleDateFormat(pattern);
                String col_date = liuCun_columns[col].toString();
                SimpleDateFormat sim_3 = new SimpleDateFormat(pattern);

                //region 计算行日期下的玩家量
                if (firstDate_userId.isEmpty() && !isGotFirstDat){
                    DataFrame<Object> sub_xinzheng = dataFrame_pr.select(value -> {
                        String create_time = (String) value.get(sim_pr.get("create_time"));
                        String game = (String) value.get(sim_pr.get("game_name"));

                        try {
                            cal_date.setTime(sim_3.parse(row_date));
                            cal_liucun.setTime(sim_3.parse(create_time));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        Integer cal_date_year = cal_date.get(Calendar.YEAR);
                        Integer cal_liucun_year = cal_liucun.get(Calendar.YEAR);

                        if (pattern.length() == 13) {
                            Integer week_date = Integer.valueOf(row_date.substring(11, 13));
                            Integer week_liucun = Integer.valueOf(create_time.substring(11, 13));
                            return week_date.equals(week_liucun) && cal_liucun_year.equals(cal_date_year) && game.equals(game_name);
                        } else {
                            return cal_date.equals(cal_liucun) && game.equals(game_name);
                        }
                    }); //筛选出合适日期下的表

                    // 统计用户到 firstDate_userId
                    Map<String, Integer> index_sim1 = DataDealer.columns_index(sub_xinzheng);
                    for (List<Object> sl : sub_xinzheng) {
                        String user_id = (String) sl.get(index_sim1.get("user_id"));
                        if (!firstDate_userId.contains(user_id)) {
                            firstDate_userId.add(user_id); // 添加到列表里
                        }
                    }

                    // 更新行信息，填上注册人数
                    String row_name_zhuce = row_date + "【" + firstDate_userId.size() + "人游玩】";
                    liuCun.set(row, 0 , row_name_zhuce); // 插入留存
//                    System.out.println("Log: " + row + "_" + col + " 的 firstDate_userId 是： " + firstDate_userId);
//                    System.out.println(sub_xinzheng.index().size() + "   查询到的大小 以及 查询到的数据:");
//                    System.out.println(sub_xinzheng);
//                    sub_xinzheng.writeXls("C:\\Users\\wb.xiejiehua\\IdeaProjects\\A24_WorkSpace\\A24_Data\\src\\main\\test_outPut\\" + row + "_" + col + "sub_xinzheng.xlsx");
                    isGotFirstDat = true;
                }
                //endregion

                //region 计算列日期下的玩家量
                int finalCol = col;
                DataFrame<Object> sub_dangTianYouWan = dataFrame_pr.select(value -> {

                    // 时间比较
                    String create_time = (String) value.get(sim_pr.get("create_time"));
                    String game = (String) value.get(sim_pr.get("game_name"));

                    try {
                        cal_date.setTime(sim_3.parse(col_date));
                        cal_liucun.setTime(sim_3.parse(create_time));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Integer cal_date_year = cal_date.get(Calendar.YEAR);
                    Integer cal_liucun_year = cal_liucun.get(Calendar.YEAR);

//                        System.out.println("用户 "+ value.get(sim_pr.get("user_id")) + " 第一天日期: " + date + " 今天日期: " + create_time + ", 两两比较周：" + cal_date.get(Calendar.WEEK_OF_YEAR) + " and " + cal_liucun.get(Calendar.WEEK_OF_YEAR) + " 结果 "+ c1);
                    if (pattern.length() == 13) {
                        Integer week_date = Integer.valueOf(col_date.substring(11, 13));
                        Integer week_liucun = Integer.valueOf(create_time.substring(11, 13));
                        return week_date.equals(week_liucun) && cal_liucun_year.equals(cal_date_year) && game.equals(game_name);
                    } else {
                        return cal_date.equals(cal_liucun) && game.equals(game_name);
                    }
                });//筛选出合适日期下的表

                // 统计用户到 thisDate_userId
                Map<String, Integer> index_sim2 = DataDealer.columns_index(sub_dangTianYouWan);
                for (List<Object> sl : sub_dangTianYouWan) {
                    String user_id = (String) sl.get(index_sim2.get("user_id"));
                    if (!thisDate_userId.contains(user_id))
                        thisDate_userId.add(user_id); // 添加到列表里
                }
//                    System.out.println(sub_dangTianYouWan.index().size() + "   查询到的大小 以及 查询到的数据:");
//                    System.out.println(sub_dangTianYouWan);
//                    sub_dangTianYouWan.writeXls("C:\\Users\\wb.xiejiehua\\IdeaProjects\\A24_WorkSpace\\A24_Data\\src\\main\\test_outPut\\" + row + "_" + col + "sub_dangTianYouWan.xlsx");
//                    System.out.println("Log: " + row + "_" + col + " 的 thisDate_userId 是： " + thisDate_userId);
                //endregion

                //region 统计留存用户到 liuCun_userId
                if (!firstDate_userId.isEmpty() && col > 1) { //如果有第一天数据
                    for (String bu : firstDate_userId) {
                        if (thisDate_userId.contains(bu)) {
                            liuCun_userId.add(bu);
                        }
                    }
                }
//                System.out.println("Log: " + row + "_" + col + " 的 liuCun_userId 是： " + liuCun_userId);
                //endregion

                //region 插入数据
                if (firstDate_userId.size() == 0) {
                    liuCun.set(row, col , 0); // 无留存
                } else {
                    Double value = (double) liuCun_userId.size() / firstDate_userId.size();
                    liuCun.set(row, col , df.format(value)); // 插入留存
                }
                //endregion
                
                //region 遍历结束, 把统计昨天到 firstDate_userId
                liuCun_userId.clear(); // 初始化
                //endregion
            }
        }

        return liuCun;
    }

    // 平台留存写入
    public static DataFrame<Object> liuCunWriteInMethodForPlatform(DataFrame<Object> dataFrame, DataFrame<Object> liuCun, String fromDate, String toDate) throws ParseException, IOException {

        // 处理传参
        String pattern = DataDealer.checkPattern(fromDate, toDate);

//        System.out.println("liuCunWriteInMethod ==============================");
//        System.out.println(dataFrame);
//        dataFrame.writeXls("/Users/jhua/IdeaProjects/a24_datadeal/A24_Data/src/main/test_outPut/dataFrame.xlsx");
//        System.out.println(liuCun);
//        System.out.println("liuCunWriteInMethod ==============================");

        // 遍历留存表的每一格，每行到每列遍
        Map<String, Integer> index_sim = DataDealer.columns_index(dataFrame);
        Object[] liuCun_columns = liuCun.columns().toArray();
        ArrayList<String> thisDate_userId = new ArrayList<>();
        ArrayList<String> firstDate_userId = new ArrayList<>();
        ArrayList<Object> liuCun_userId = new ArrayList<>();

        // 提前创建时间
        Calendar cal_date = Calendar.getInstance();
        cal_date.setFirstDayOfWeek(Calendar.MONDAY);
        Calendar cal_liucun = Calendar.getInstance();
        cal_liucun.setFirstDayOfWeek(Calendar.MONDAY);

        // 获取游戏记录
        DataFrame<Object> dataFrame_pr = PlayRecord.dataFrameOfPlayRecord(fromDate, toDate);
        DataDealer.format_time(dataFrame_pr, "create_time", pattern);

        dataFrame_pr = dataFrame_pr.retain("user_id", "create_time", "play_record_id")
                .groupBy("user_id", "create_time")
                .count()
        ;
//        dataFrame_pr.writeXls("/Users/jhua/IdeaProjects/a24_datadeal/A24_Data/src/main/test_outPut/dataFrame_pr.xlsx");

        Map<String, Integer> sim_pr = DataDealer.columns_index(dataFrame_pr);

        //region 遍历每个格子
        for (int row = 0; row < liuCun.index().size(); row++) {
            firstDate_userId.clear();// 初始化
            String row_date = (String) liuCun.get(row, 0);
            boolean isGotFirstDat = false;

            for (int col = 1; col < liuCun.columns().size(); col++) {

                thisDate_userId.clear(); // 初始化今天数据
                assert pattern != null;
                String col_date = liuCun_columns[col].toString();
                SimpleDateFormat sim_3 = new SimpleDateFormat(pattern);

                
                //region 计算行日期下的注册量
                // 如果是第一天，需要统计第一天的新增用户号，通过dataFrame获取这一个日期的前提下，user_id的count，以及user_id的列表
                // 时间比较

                if (firstDate_userId.isEmpty() && !isGotFirstDat){

                    DataFrame<Object> sub_xinzheng = dataFrame.select(value -> {
                        String create_time = (String) value.get(index_sim.get("create_time"));
                        try {
                            cal_date.setTime(sim_3.parse(row_date));
                            cal_liucun.setTime(sim_3.parse(create_time));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        Integer cal_date_year = cal_date.get(Calendar.YEAR);
                        Integer cal_liucun_year = cal_liucun.get(Calendar.YEAR);

                        if (pattern.length() == 13) {
                            Integer week_date = Integer.valueOf(row_date.substring(11, 13));
                            Integer week_liucun = Integer.valueOf(create_time.substring(11, 13));
                            return week_date.equals(week_liucun) && cal_liucun_year.equals(cal_date_year);
                        } else {
                            return cal_date.equals(cal_liucun);
                        }
                    }); //筛选出合适日期下的表

                    // 统计用户到 firstDate_userId
                    Map<String, Integer> index_sim1 = DataDealer.columns_index(sub_xinzheng);
                    for (List<Object> sl : sub_xinzheng) {
                        firstDate_userId.add((String) sl.get(index_sim1.get("user_id"))); // 添加到列表里
                    }

                    // 更新行信息，填上注册人数
                    String row_name_zhuce = row_date + "【" + firstDate_userId.size() + "人注册】";
                    liuCun.set(row, 0 , row_name_zhuce); // 插入留存

                    isGotFirstDat = true;
                }
                //endregion


                //region 计算列日期下的玩量
                DataFrame<Object> sub_dangTianYouWan = dataFrame_pr.select(value -> {

                    // 时间比较
                    String create_time = (String) value.get(sim_pr.get("create_time"));

                    try {
                        cal_date.setTime(sim_3.parse(col_date));
                        cal_liucun.setTime(sim_3.parse(create_time));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Integer cal_date_year = cal_date.get(Calendar.YEAR);
                    Integer cal_liucun_year = cal_liucun.get(Calendar.YEAR);

                    boolean c1 = cal_date.get(Calendar.WEEK_OF_YEAR) == cal_liucun.get(Calendar.WEEK_OF_YEAR);
                    boolean c2 = cal_date.equals(cal_liucun);
                    boolean c3 = cal_date.get(Calendar.YEAR) == cal_liucun.get(Calendar.YEAR);

//                    System.out.println("用户 "+ value.get(sim_pr.get("user_id")) + " 第一天日期: " + col_date + " 今天日期: " + create_time + ", 两两比较周：" + cal_date.get(Calendar.WEEK_OF_YEAR) + " and " + cal_liucun.get(Calendar.WEEK_OF_YEAR) + " 结果 "+ c1);


                    if (pattern.length() == 13) {
                        Integer week_date = Integer.valueOf(col_date.substring(11, 13));
                        Integer week_liucun = Integer.valueOf(create_time.substring(11, 13));
                        return week_date.equals(week_liucun) && cal_liucun_year.equals(cal_date_year);
                    } else {
                        return cal_date.equals(cal_liucun);
                    }

                });//筛选出合适日期下的表

                // 统计用户到 thisDate_userId
                Map<String, Integer> index_sim2 = DataDealer.columns_index(sub_dangTianYouWan);
                for (List<Object> sl : sub_dangTianYouWan) {
                    String user_id = (String) sl.get(index_sim2.get("user_id"));
                    if (!thisDate_userId.contains(user_id))
                        thisDate_userId.add(user_id); // 添加到列表里
                }
//                    System.out.println(sub_dangTianYouWan.index().size() + "   查询到的大小 以及 查询到的数据:");
                //endregion

                //region 统计留存用户到 liuCun_userId
                if (!firstDate_userId.isEmpty()) { //如果有第一天数据
                    for (String bu : firstDate_userId) {
                        if (thisDate_userId.contains(bu)) {
                            liuCun_userId.add(bu);
                        }
                    }
                }
                //endregion

                //region 插入数据
                if (liuCun_userId.size() == 0) {
                    liuCun.set(row, col , 0); // 无留存
                } else {
                    DecimalFormat df = new DecimalFormat("0.00%");
                    Double value = (double) liuCun_userId.size() / firstDate_userId.size();
                    liuCun.set(row, col , df.format(value)); // 插入留存
                }
                //endregion

                //region 遍历结束, 把统计昨天到 firstDate_userId
                liuCun_userId.clear(); // 初始化
                //endregion


//                sub_dangTianYouWan.writeXls("C:\\Users\\wb.xiejiehua\\IdeaProjects\\A24_WorkSpace\\A24_Data\\src\\main\\test_outPut\\" + row_date+"~~"+col_date+ " 游玩.xlsx");
            }
        }
        //endregion

        return liuCun;
    }

    // 获取某段时间范围内的数据，返回DataFrame
    public static DataFrame<Object> dataFrameBetweenDate(DataFrame<Object> dataFrame, Date startDate, Date endDate, String dateKey) {

        Map<String, Integer> index = DataDealer.columns_index(dataFrame);

        return dataFrame.select(value -> {
            Date create_time = (Date) value.get(index.get(dateKey));
            if (create_time.after(startDate) && create_time.before(endDate)) {
                return true;
            }
            return false;
        });
    }
    public static DataFrame<Object> dataFrameBetweenDate(Date startDate, DataFrame<Object> dataFrame) {
        return dataFrameBetweenDate(dataFrame, startDate, null, "create_time");
    }
    public static DataFrame<Object> dataFrameBetweenDate(DataFrame<Object> dataFrame, Date endDate) {
        return dataFrameBetweenDate(dataFrame, null, endDate, "create_time");
    }
    public static DataFrame<Object> dataFrameBetweenDate(Date startDate, DataFrame<Object> dataFrame, Date endDate) {
        return dataFrameBetweenDate(dataFrame, startDate, endDate, "create_time");
    }

    public static void writeXls(DataFrame<Object> dataFrame, String file) throws IOException {
        Serialization.writeXls(dataFrame, new FileOutputStream(file));
    }

    public static void main(String[] args) throws ParseException, IOException {

        System.out.println(checkPattern("2020-1-22", "2021-03-22"));


    }
}
