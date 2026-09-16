package com.desktoppet.model;

import java.util.Random;

/**
 * 桌宠实体类
 * 负责人：
 * 功能：
 * 存储桌宠名字、等级、亲密度、当前状态等数据
 * 提供抚摸、喂食、玩耍等互动方法
 * 提供亲密度升级判断
 */
public class Pet {

    //--------------数据设计-------------------
    private String name;            //名字
    private int level;              //等级
    private int intimacy;           //当前亲密度
    private int maxIntimacy;        //当前等级最大亲密度
    private String state;           //当前状态（待机/开心/进食/玩耍/学习）
    private long playCooldownEnd;   //玩耍冷却结束时间戳

    //新增：
    public static final int maxLevel = 6;           //最高等级
    //--------------数据设计-------------------


    //--------------service-------------------
    //service:初始化桌宠
    /*
    负责人：
    功能：
        初始化桌宠名字
        初始等级为1
        初始亲密度为0
        初始状态为待机
    参数：name
    返回值：void
     */
    public Pet(String name) {
        this.name = name;
        this.level = 1;
        this.intimacy = 0;
        this.maxIntimacy = 30;
        this.state = "待机";
        //.......
    }

    //service:抚摸互动
    /*
    负责人：
    功能：
        桌宠播放开心反应动画
        亲密度+1
        弹出爱心特效和+1提示
        判断是否升级
    参数：void
    返回值：int 增加的亲密度
     */
    public int pet() {
        //.......
        this.state = "开心";
        this.intimacy += 1;
        checkLevelUp();
        return 1;
    }

    //service:喂食互动
    /*
    负责人：
    功能：
        桌宠播放进食动画
        亲密度+3
        弹出+3提示
        判断是否升级
    参数：void
    返回值：int 增加的亲密度
     */
    public int feed() {
        //.......
        this.state = "进食";
        this.intimacy += 3;
        System.out.println("当前亲密度+3");
        checkLevelUp();
        return 3;
    }

    //service:玩耍互动
    /*
    负责人：
    功能：
        判断是否在30秒冷却中
        冷却中返回-1，按钮置灰
        未冷却则播放玩耍动画，亲密度+5
        设置冷却结束时间
        判断是否升级
    参数：void
    返回值：int 增加的亲密度，冷却中返回-1
     */
    public int play() {
        //.......
        if (getPlayCooldownRemaining() > 0) {
            return -1;
        }
        this.state = "玩耍";
        this.intimacy += 5;
        long now = System.currentTimeMillis();
        this.playCooldownEnd = now + 30 * 1000L;
        checkLevelUp();
        return 5;
    }

    //service:判断是否升级
    /*
    负责人：
    功能：
        亲密度累计达到当前等级上限阈值时
        等级提升，播放升级特效
        更新等级显示和最大亲密度
        等级阈值如: Lv1->Lv2需30点, Lv2->Lv3需60点
    参数：void
    返回值：void
     */
    private void checkLevelUp() {
        //.......
        // 【调试】打印进入时的状态
        System.out.println(">>> 开始检查: Level=" + this.level + ", 当前经验="
                + this.intimacy + ", 当前上限=" + this.maxIntimacy);
        //已满等级不再处理
        if (level >= maxLevel) {
            this.maxIntimacy = 99999;
            return;
        }
        // 处理升级后经验值
        if (this.intimacy >= this.maxIntimacy) {
            // 1. 保存旧上限
            int oldMax = this.maxIntimacy;
            // 2. 提升等级
            this.level++;
            //3.更新亲密度
            switch (this.level) {
                case 1:
                    this.maxIntimacy = 30;
                    break;
                case 2:
                    this.maxIntimacy = 60;
                    break;
                case 3:
                    this.maxIntimacy = 90;
                    break;
                case 4:
                    this.maxIntimacy = 120;
                    break;
                case 5:
                    this.maxIntimacy = 150;
                    break;
                case 6:
                    this.maxIntimacy = 99999;
                    break;
                default:
                    this.maxIntimacy = 60;
                    break;
            }
            // 4.扣除升级消耗的经验值（LV1：31/30 -> LV2:1/60）
            this.intimacy -= oldMax;
            // 【调试】打印计算后的结果
            System.out.println(">>> 升级完成: 新Level=" + this.level + ", 剩余经验=" + this.intimacy + ", 新上限=" + this.maxIntimacy);
        }
        else {
            System.out.println(">>> 未满足升级条件");
        }
    }


    //service:获取玩耍剩余冷却秒数
    /*
    负责人：
    功能：
        计算当前时间与冷却结束时间的差值
        返回剩余秒数，冷却结束返回0
    参数：void
    返回值：int 剩余冷却秒数
     */
    public int getPlayCooldownRemaining() {
        //.......
        long now = System.currentTimeMillis();
        int diff = (int) ((this.playCooldownEnd - now) / 1000);
        return (diff >= 0) ? diff : 0;
    }

    //service:回到待机状态
    /*
    负责人：
    功能：
        将桌宠状态设置为待机
    参数：void
    返回值：void
     */
    public void toIdle() {
        this.state = "待机";
    }

    //service:随机切换待机动画
    /*
    负责人：
    功能：
        从待机、眨眼、呼吸、小幅移动中随机选择一个状态
        每隔3-5秒调用一次
    参数：void
    返回值：void
     */
    public void randomIdleAction() {
        //.......
        String[] idleStates = {"待机", "眨眼", "呼吸", "小幅移动"};
        Random random = new Random();
        int index = random.nextInt(idleStates.length);
        this.state = idleStates[index];
    }

    //--------------------service---------------------------


    //--------------get/set-------------------
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getIntimacy() {
        return intimacy;
    }

    public void setIntimacy(int intimacy) {
        this.intimacy = intimacy;
    }

    public int getMaxIntimacy() {
        return maxIntimacy;
    }

    public void setMaxIntimacy(int maxIntimacy) {
        this.maxIntimacy = maxIntimacy;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    //--------------get/set-------------------
}
