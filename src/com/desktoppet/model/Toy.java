package com.desktoppet.model;

/**
 * 玩具实体类
 * 负责人：
 * 功能：
 *     存储玩具的ID、名称、亲密度加成、冷却时间、描述
 *     提供玩耍方法，带冷却时间限制
 *     提供冷却查询方法
 */
public class Toy {

    //--------------数据设计-------------------
    private String id;              //玩具ID
    private String name;            //玩具名称
    private int intimacyBonus;      //玩耍增加的亲密度
    private int cooldownSeconds;    //冷却时间（秒）
    private String description;     //玩具描述
    private long lastUsedTime;      //上次使用时间
    //--------------数据设计-------------------


    //--------------service-------------------
    //service:初始化玩具
    /*
    负责人：
    功能：
        初始化玩具ID、名称、亲密度加成、冷却时间、描述
        初始上次使用时间为0
    参数：id, name, intimacyBonus, cooldownSeconds, description
    返回值：void
     */
    public Toy(String id, String name, int intimacyBonus, int cooldownSeconds, String description){
        this.id = id;
        this.name = name;
        this.intimacyBonus = intimacyBonus;
        this.cooldownSeconds = cooldownSeconds;
        this.description = description;
        this.lastUsedTime = 0;
    }

    //service:执行玩耍
    /*
    负责人：
    功能：
        判断是否在冷却中
        冷却中返回-1
        未冷却则记录使用时间，返回亲密度加成
    参数：void
    返回值：int 增加的亲密度，冷却中返回-1
     */
    public int play(){
        if (isOnCooldown()) {
            return -1;
        }
        this.lastUsedTime = System.currentTimeMillis();
        return intimacyBonus;
    }

    //service:获取剩余冷却秒数
    /*
    负责人：
    功能：
        计算当前时间与上次使用时间的差值
        返回剩余冷却秒数，冷却结束返回0
    参数：void
    返回值：int 剩余冷却秒数
     */
    public int getCooldownRemaining(){
        if (lastUsedTime <= 0) {
            return 0;
        }
        long elapsedSeconds = (System.currentTimeMillis() - lastUsedTime) / 1000;
        int remaining = cooldownSeconds - (int) elapsedSeconds;
        return remaining > 0 ? remaining : 0;
    }

    //service:判断是否在冷却中
    /*
    负责人：
    功能：
        调用getCooldownRemaining判断是否大于0
    参数：void
    返回值：boolean 是否冷却中
     */
    public boolean isOnCooldown(){
        return getCooldownRemaining() > 0;
    }

    //--------------------service---------------------------


    //--------------get/set-------------------
    public String getId(){ return id; }
    public void setId(String id){ this.id = id; }
    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }
    public int getIntimacyBonus(){ return intimacyBonus; }
    public void setIntimacyBonus(int intimacyBonus){ this.intimacyBonus = intimacyBonus; }
    public int getCooldownSeconds(){ return cooldownSeconds; }
    public void setCooldownSeconds(int cooldownSeconds){ this.cooldownSeconds = cooldownSeconds; }
    public String getDescription(){ return description; }
    public void setDescription(String description){ this.description = description; }
    //--------------get/set-------------------
}
