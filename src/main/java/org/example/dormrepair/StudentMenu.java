package org.example.dormrepair;

import java.util.Scanner;

import org.apache.ibatis.session.SqlSession;
import org.example.dormrepair.mapper.UserMapper;
import org.example.dormrepair.pojo.User;
import org.example.dormrepair.pojo.repairTable;
import org.example.dormrepair.pojo.studentDorm;
import org.example.dormrepair.service.RepairTableService;
import org.example.dormrepair.service.StudentDormService;
import java.util.List;

import org.example.dormrepair.service.UserService;
import org.example.dormrepair.util.MyBatisUtil;
import org.example.dormrepair.util.RegEx;

public class StudentMenu {

    private final String[] areas = {"西区","东区"};
    private final String[] buildings = {
            "1栋","2栋","3栋","4栋","5栋",
            "6栋","7栋","8栋","9栋","10栋",
            "11栋","12栋","13栋","14栋","15栋",
            "16栋","17栋"
    };
    private final String[] deviceType = {
            "电器类 开关跳闸", "电器类 插座", "电器类 调速器", "电器类 按钮开关", "电器类 光管",
            "电器类 风扇", "电器类 阳台灯", "电器类 厕所灯", "电器类 排气扇", "电器类 电线",
            "电器类 学生宿舍空调",//0~10  共11个
            "水件类 水龙头", "水件类 水管", "水件类 沐浴器",//11~13  3个
            "家具类 家具把手", "家具类 床", "家具类 书架", "家具类 衣柜", "家具类 椅子",
            "家具类 键盘托", "家具类 书桌", "家具类 床板", "家具类 蚊帐架",//14~22 9个
            "门窗类 门", "门窗类 锁", "门窗类 窗", "门窗类 玻璃门", "门窗类 窗把手",
            "门窗类 门把手", "门窗类 插销",//23~29  7个
            "土建类 天花板漏水", "土建类 墙体渗水", "土建类 瓷砖开裂",//30~32  3个
            "排水类 下水道", "排水类 厕所",//33~34  2个
            "消防类 消防设施",//35
            "其他类 其他",//36
            "宿舍空调 美的空调", "宿舍空调 小天鹅空调", "宿舍空调 TCL空调", "宿舍空调 格力空调",
            "宿舍空调 其它品牌",//37~41  5个

    };

    private final StudentDormService dormService = new StudentDormService();
    private final RepairTableService repairTableService = new RepairTableService();

    public void enter(Scanner sc,String stuUserId){
        // 第一次没绑定就自动绑定
        if (!dormService.hasDorm(stuUserId)){
            System.out.println("首次登录请先绑定宿舍");
            // 调用封装好的方法 → 绑定宿舍
            studentDorm dorm = inputDormInfo(sc,stuUserId);
            dormService.addDorm(dorm);
            System.out.println("宿舍绑定成功！");
        }

        // 学生菜单
        while (true){
            System.out.println("=====学生菜单=====");
            System.out.println("1.绑定/修改宿舍");
            System.out.println("2.创建报修单");
            System.out.println("3.查看我的报修记录");
            System.out.println("4.取消报修单");
            System.out.println("5.修改密码");
            System.out.println("6.评分维修人员");
            System.out.println("7.退出");
            System.out.print("请选择操作(输入1~7):");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice){
                case 1:
                    System.out.println("=====绑定/修改宿舍信息=====");
                    studentDorm newDorm = inputDormInfo(sc,stuUserId);
                    dormService.updateDorm(newDorm);
                    System.out.println("宿舍修改成功！");
                    break;

                case 2:
                    System.out.println("=====创建报修单=====");
                    repairTable rt = new repairTable();//建表
                    rt.setStuUserId(stuUserId);//绑定当前学生账号
                    System.out.println("-----选择故障设备类型-----");
                    int deviceNum;//选择的编号
                    for(int i = 0; i < deviceType.length; i++){
                        System.out.println((i+1)+"."+deviceType[i]);
                    }
                    System.out.println("请输入设备编号:");
                    while (true){
                        deviceNum = sc.nextInt();
                        if (deviceNum<1||deviceNum>deviceType.length) {
                            System.out.println("设备编号输入错误！请重新输入:");
                            continue;
                        }
                        break;
                    }
                    sc.nextLine();

                    rt.setDeviceType(deviceType[deviceNum-1]);
                    System.out.println("请输入故障具体描述:");
                    String description = sc.nextLine();
                    rt.setDescription(description);

                    //自动根据类型确定紧急程度  1普通，2紧急，3非常紧急
                    switch (deviceNum-1){
                        case 0,9,12,26,30,31,33,34,35:
                            rt.setUrgency(3);
                            break;
                        case 1,4,7,10,11,13,23,24,37,38,39,40,41:
                            rt.setUrgency(2);
                            break;
                        default:
                            rt.setUrgency(1);
                    }
                    System.out.println("请输入联系电话:");
                    String phonenum = sc.nextLine();
                    while (!RegEx.checkPhone(phonenum)){
                        System.out.println("手机号格式错误！" + "需要11位数字且以13~19开头");
                        System.out.println("重新输入联系电话：");
                        phonenum = sc.nextLine();
                    }
                    rt.setPhonenum(phonenum);

                    rt.setStatus(0);//处理状态 0待处理
                    rt.setRepairUserId(null);//还没有维修人员接
                    rt.setScore(null);//评分初始化
                    rt.setRepairId(null);//报修单id由数据库自动生成
                    rt.setDeviceType(deviceType[deviceNum-1]);//设备类型
                    rt.setCreateTime(null);//当前时间

                    boolean isSuccess = repairTableService.submitRepair(rt);
                    if (isSuccess) {
                        System.out.println("报修单创建成功！请等待管理员分配维修人员");
                        System.out.println("你的报修单ID："+rt.getRepairId());
                    }
                    else {
                        System.out.println("报修单创建失败!");
                    }
                    break;

                case 3:
                    System.out.println("\n===== 我的报修记录 =====");
                    List<repairTable> myRepairs = repairTableService.getMyRepairs(stuUserId);

                    if (myRepairs.isEmpty()) {
                        System.out.println("暂无报修记录！");
                        break;
                    }

                    for (repairTable rt2 : myRepairs) {
                        String statusStr = switch (rt2.getStatus()) {
                            case 0 -> "待处理";
                            case 1 -> "处理中";
                            case 2 -> "已完成";
                            case 3 -> "已取消";
                            default -> "未知状态";
                        };

                        String urgencyStr = switch (rt2.getUrgency()) {
                            case 1 -> "普通";
                            case 2 -> "紧急";
                            case 3 -> "非常紧急";
                            default -> "未知紧急程度";
                        };

                        System.out.println("----------------------------------------");
                        System.out.printf("报修ID：%d%n", rt2.getRepairId());
                        System.out.printf("设备类型：%s%n", rt2.getDeviceType());
                        System.out.printf("故障描述：%s%n", rt2.getDescription());
                        System.out.printf("紧急程度：%s | 联系电话：%s%n", urgencyStr, rt2.getPhonenum());
                        System.out.printf("当前状态：%s%n", statusStr);
                        System.out.printf("报修时间：%s%n", rt2.getCreateTime());
                        System.out.printf("最后更新：%s%n", rt2.getUpdateTime());
                        if (rt2.getRepairUserId() != null && !rt2.getRepairUserId().isEmpty()) {
                            System.out.printf("维修人员：%s%n", rt2.getRepairUserId());
                        }
                        else {
                            System.out.println("维修人员：暂无分配");
                        }

                        if (rt2.getStatus() == 2 && rt2.getScore() != null) {
                            System.out.printf("学生评分：%d分%n", rt2.getScore());
                        }
                        else {
                            System.out.println("学生评分：暂无评分(完成后可评价）");
                        }
                    }
                    System.out.println("----------------------------------------");
                    break;


                case 4:
                    // 取消报修
                    System.out.println("\n=====取消报修单=====");
                    List<repairTable> myRepairs2 = repairTableService.getMyRepairs(stuUserId);
                    if (myRepairs2.isEmpty()) {
                        System.out.println("暂无任何报修记录，无法取消");
                        break;
                    }
                    List<repairTable> cancelTableRepairs = myRepairs2.stream()
                            .filter(rt3 -> rt3.getStatus() == 0) // 仅显示待处理的报修单
                            .toList();//
                    if (cancelTableRepairs.isEmpty()) {
                        System.out.println("仅待处理状态的报修单可以取消，暂无符合条件的报修单！");
                        break;
                    }

                    System.out.println("可取消的报修单列表；");
                    for (int i = 0;i < cancelTableRepairs.size(); i++){
                        repairTable rt4 = cancelTableRepairs.get(i);
                        System.out.printf("%d.报修ID：%d |设备类型： %s | 故障描述：%s | 报修时间：%s%n",
                                (i+1), rt4.getRepairId(), rt4.getDeviceType(),
                                rt4.getDescription(), rt4.getCreateTime());
                    }
                    int cancelNum;

                    System.out.print("请输入要取消的报修单编号:");
                    cancelNum = sc.nextInt();
                    sc.nextLine();
                    if (cancelNum < 1||cancelNum>cancelTableRepairs.size()) {
                        System.out.println("输入错误！编号超出范围");
                        break;
                    }

                    repairTable toCancel = cancelTableRepairs.get(cancelNum-1);
                    boolean isCancelSuccess = repairTableService.cancelRepair(toCancel.getRepairId(),stuUserId);
                    if (isCancelSuccess) {
                        System.out.printf("报修ID【%d】取消成功！\n",toCancel.getRepairId());
                    }
                    else {
                        System.out.printf("报修ID【%d】取消失败！\n",toCancel.getRepairId());
                    }
                    break;

                case 5:
                    // 修改密码
                    System.out.println("\n=====修改密码=====");
                    System.out.println("请输入当前旧密码：");
                    String oldPwd = sc.nextLine();
                    try (SqlSession session = MyBatisUtil.getSqlSession()){
                        UserMapper mapper = session.getMapper(UserMapper.class);
                        User user = mapper.selectByUserId(stuUserId);
                        if (!user.getPassword().equals(oldPwd)) {
                            System.out.println("原密码错误！修改终止！");
                            break;
                        }
                    }
                    System.out.println("请输入新密码：");
                    String newPwd = sc.nextLine();
                    System.out.println("请再次输入新密码：");
                    String reNewPwd = sc.nextLine();
                    if (!RegEx.checkPassword(newPwd)){
                        System.out.println("密码需要在6~16位之间！");
                        break;
                    }
                    if (!newPwd.equals(reNewPwd)) {
                        System.out.println("两次输入的新密码不一致！修改终止！");
                        break;
                    }
                    if (oldPwd.equals(newPwd)) {
                        System.out.println("新密码不能与原密码相同！");
                        break;
                    }
                    boolean isUpdateSuccess = new UserService().updatePassword(stuUserId, newPwd);
                    if (isUpdateSuccess) {
                        System.out.println("密码修改成功！请重新登录！");
                        return;
                    }
                    else {
                        System.out.println("密码修改失败！");
                    }
                    break;
                case 6:
                    System.out.println("=====我的已完成报修单(可评分)=====");
                    List<repairTable> list = repairTableService.getMyRepairs(stuUserId);
                    List<repairTable> canScoreList = list.stream()
                            .filter(rt5 -> rt5.getStatus() == 2 && (rt5.getScore() == null || rt5.getScore() == 0)) // 已完成且未评分的报修单
                            .toList();
                    if (canScoreList.isEmpty()) {
                        System.out.println("暂无可评分的报修单！只有已完成且未评分的报修单才可以评分");
                        break;
                    }
                    System.out.println("可评分的报修单列表：");
                    for (int i = 0; i < canScoreList.size(); i++) {
                        repairTable rt6 = canScoreList.get(i);
                        System.out.printf("%d.报修ID：%d |设备类型： %s | 故障描述：%s | 报修时间：%s%n",
                                (i + 1), rt6.getRepairId(), rt6.getDeviceType(),
                                rt6.getDescription(), rt6.getCreateTime());
                    }
                    int scoreNum;
                    System.out.print("请输入要评分的报修单编号:");
                    scoreNum = sc.nextInt();
                    sc.nextLine();
                    if (scoreNum < 1 || scoreNum > canScoreList.size()) {
                        System.out.println("输入错误！编号超出范围");
                        break;
                    }
                    repairTable toScore = canScoreList.get(scoreNum - 1);
                    System.out.print("请输入评分（1-5分，1分最差，5分最好）:");
                    int score = sc.nextInt();
                    sc.nextLine();
                    if (score < 1 || score > 5) {
                        System.out.println("输入错误！评分必须在1-5之间");
                        break;
                    }
                    boolean isScoreSuccess = repairTableService.scoreRepair(toScore.getRepairId(), score);
                    if (isScoreSuccess) {
                        System.out.printf("报修ID【%d】评分成功！\n", toScore.getRepairId());
                    } else {
                        System.out.printf("报修ID【%d】评分失败！\n", toScore.getRepairId());
                    }
                    break;

                case 7:
                    System.out.println("退出登录");
                    return;

                default:
                    System.out.println("输入错误");
            }
        }
    }


    private studentDorm inputDormInfo(Scanner sc,String stuUseId) {
        // 选择区域
        System.out.println("=====选择宿舍所在区域=====");
        int areaNum;
        for (int i = 0; i < areas.length; i++){
            System.out.println((i+1)+"."+areas[i]);
        }
        System.out.print("请输入编号:");
        while (true) {
            areaNum = sc.nextInt();
            if (areaNum<1||areaNum>areas.length) {
                System.out.println("输入错误请重新输入");
                continue;
            }
            break;
        }
        sc.nextLine();
        String area = areas[areaNum-1];

        // 选择楼栋
        System.out.println("=====选择所在楼栋=====");
        int buildingIndex = (areaNum == 1) ? 17 : 15;

        for(int i = 0; i < buildingIndex; i++){
            System.out.println((i+1)+"."+buildings[i]);
        }
        System.out.print("请输入楼栋编号:");
        int buildingNum;
        while (true) {
            buildingNum = sc.nextInt();
            if (buildingNum<1||buildingNum>buildingIndex) {
                System.out.println("输入错误请重新输入");
                continue;
            }
            break;
        }
        sc.nextLine();
        String building = buildings[buildingNum-1];

        // 房间号
        System.out.println("=====输入房间号=====");
        String roomNumber;
        System.out.print("请输入房间号(如:101,305):");
        while (true){
            roomNumber = sc.nextLine();
            if (RegEx.checkRoomNumber(roomNumber)){
                break;
            }
            System.out.println("房间号格式错误！重新输入");
        }

        // 名字
        System.out.print("请输入名字:");
        String name = sc.nextLine();

        // 直接返回组装好的对象
        return new studentDorm(stuUseId, area, building, roomNumber, name);
    }

}