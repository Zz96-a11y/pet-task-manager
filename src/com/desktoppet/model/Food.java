package com.desktoppet.model;

/**
 * 食物实体类
 * 负责人：
 * 功能：
 *     存储食物的ID、名称、亲密度加成、描述
 *     提供喂食方法返回亲密度加成
 */
public class Food {

    //--------------数据设计-------------------
    private String id;              //食物ID
    private String name;            //食物名称
    private int intimacyBonus;      //喂食增加的亲密度
    private String description;     //食物描述
    //--------------数据设计-------------------


    //--------------service-------------------
    //service:初始化食物
    /*
    负责人：
    功能：
        初始化食物ID、名称、亲密度加成、描述
    参数：id, name, intimacyBonus, description
    返回值：void
     */
    public Food(String id, String name, int intimacyBonus, String description){
        this.id = id;
        this.name = name;
        this.intimacyBonus = intimacyBonus;
        this.description = description;
    }

    //service:执行喂食
    /*
    负责人：
    功能：
        返回该食物的亲密度加成
    参数：void
    返回值：int 增加的亲密度
     */
    public int eat(){
        return intimacyBonus;
    }

    //--------------------service---------------------------


    //--------------get/set-------------------
    public String getId(){ return id; }
    public void setId(String id){ this.id = id; }
    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }
    public int getIntimacyBonus(){ return intimacyBonus; }
    public void setIntimacyBonus(int intimacyBonus){ this.intimacyBonus = intimacyBonus; }
    public String getDescription(){ return description; }
    public void setDescription(String description){ this.description = description; }
    //--------------get/set-------------------
}
