package com.desktoppet.model;

/**
 * 任务实体类
 * 负责人：
 * 功能：
 *     存储任务的ID、名称、描述、日期、优先级、预计时长、完成状态、累计专注时长
 *     提供完成状态切换、增加专注时长等方法
 */
public class Task {

    //--------------数据设计-------------------
    private String id;              //任务唯一ID
    private String name;            //任务名称
    private String description;     //任务描述
    private String date;            //任务日期（yyyy-MM-dd）
    private String priority;        //优先级（高/中/低）
    private int expectedDuration;   //预计时长（分钟）
    private boolean finished;       //是否已完成
    private int totalFocusSeconds;  //累计专注时长（秒）
    //--------------数据设计-------------------


    //--------------service-------------------
    //service:初始化任务
    /*
    负责人：
    功能：
        初始化任务ID、名称、描述、日期、优先级、预计时长
        初始状态为未完成
        初始累计专注时长为0
    参数：id, name, description, date, priority, expectedDuration
    返回值：void
     */
    public Task(String id, String name, String description, String date,
                String priority, int expectedDuration){
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.priority = priority;
        this.expectedDuration = expectedDuration;
        this.finished = false;
        this.totalFocusSeconds = 0;
    }

    //service:切换完成状态
    /*
    负责人：
    功能：
        如果当前为已完成，则改为未完成
        否则改为已完成
    参数：void
    返回值：void
     */
    public void toggleFinished(){
        this.finished = !this.finished;
    }

    //service:增加专注时长
    /*
    负责人：
    功能：
        将本次专注秒数累加到任务总专注时长
    参数：seconds 本次专注秒数
    返回值：void
     */
    public void addFocusSeconds(int seconds){
        this.totalFocusSeconds += seconds;
    }

    //--------------------service---------------------------


    //--------------get/set-------------------
    public String getId(){ return id; }
    public void setId(String id){ this.id = id; }
    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }
    public String getDescription(){ return description; }
    public void setDescription(String description){ this.description = description; }
    public String getDate(){ return date; }
    public void setDate(String date){ this.date = date; }
    public String getPriority(){ return priority; }
    public void setPriority(String priority){ this.priority = priority; }
    public int getExpectedDuration(){ return expectedDuration; }
    public void setExpectedDuration(int expectedDuration){ this.expectedDuration = expectedDuration; }
    public boolean isFinished(){ return finished; }
    public void setFinished(boolean finished){ this.finished = finished; }
    public int getTotalFocusSeconds(){ return totalFocusSeconds; }
    public void setTotalFocusSeconds(int totalFocusSeconds){ this.totalFocusSeconds = totalFocusSeconds; }
    //--------------get/set-------------------
}
