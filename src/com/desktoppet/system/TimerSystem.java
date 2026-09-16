package com.desktoppet.system;

import com.desktoppet.model.Task;
import com.desktoppet.view.StudyRoomWindow;
import com.desktoppet.view.TimerWindow;

import javax.swing.*;

/**
 * 专注计时系统
 * 负责人：
 * 功能：
 *     连接Task模型与TimerWindow视图
 *     管理专注计时的启动、暂停、继续、结束
 *     每秒更新计时显示
 *     计时结束后累加专注时长到任务
 *     管理计时器窗口的打开和关闭
 *     结束后可选择进入自习室（功能待定）
 */
public class TimerSystem {

    //--------------数据设计-------------------
    private TimerWindow timerWindow;        //计时器窗口
    private TaskSystem taskSystem;          //任务管理系统
    private StudyRoomWindow studyRoomWindow;//自习室窗口（待定）
    private String currentTaskId;           //当前计时任务ID
    private int currentSeconds;              //当前计时秒数
    private boolean isTiming;                //是否正在计时
    private boolean isPaused;               //是否暂停
    private Timer timer;                    //计时定时器
    //--------------数据设计-------------------


    //--------------service-------------------
    //service:初始化计时系统
    /*
    负责人：
    功能：
        关联任务管理系统
        初始化计时状态为未开始
        为开始/暂停/结束按钮添加事件监听
    参数：taskSystem 任务管理系统
    返回值：void
     */
    public TimerSystem(TaskSystem taskSystem){
        this.taskSystem = taskSystem;
        this.currentSeconds = 0;
        this.isTiming = false;
        this.isPaused = false;
    }

    //service:打开计时器窗口
    /*
    负责人：
    功能：
        设置当前计时任务ID
        获取任务信息（名称、累计专注时长）
        创建计时器窗口
        重置计时状态和显示
        显示计时器窗口
    参数：taskId 要计时的任务ID
    返回值：void
     */
    public void openTimerWindow(String taskId){
        this.currentTaskId = taskId;
        // 获取任务信息（名称、累计专注时长）
        Task task = taskSystem.getTaskById(taskId);
        if (task == null) {
            JOptionPane.showMessageDialog(null, "未找到该任务", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // 释放上一次的计时器窗口（如果有），避免重复窗口
        if (this.timerWindow != null) {
            this.timerWindow.dispose();
        }
        // 创建计时器窗口
        this.timerWindow = new TimerWindow(task, this, taskSystem);
        // 重置计时状态和显示
        this.currentSeconds = 0;
        this.isTiming = false;
        this.isPaused = false;
        if (this.timer != null) {
            this.timer.stop();
        }
        this.timerWindow.updateTimeDisplay(0);
        this.timerWindow.refreshTotalFocus();

    }

    //service:开始计时
    /*
    负责人：
    功能：
        设置isTiming为true，isPaused为false
        启动定时器，每秒触发一次
        定时器中每秒currentSeconds加1，调用timerWindow.updateTimeDisplay更新显示
        开始按钮置灰，暂停按钮可用
    参数：void
    返回值：void
     */
    public void startTiming(){
        this.isTiming = true;
        this.isPaused = false;
        this.currentSeconds = 0;
        // 启动定时器，每秒触发一次
        if (this.timer != null) {
            this.timer.stop();
        }
        this.timer = new Timer(1000, e -> {
            this.currentSeconds++;
            if (this.timerWindow != null) {
                this.timerWindow.updateTimeDisplay(this.currentSeconds);
            }
        });
        this.timer.start();
        // 开始按钮不可用，暂停按钮可用
        if (this.timerWindow != null) {
            this.timerWindow.getStartBtn().setEnabled(false);
            this.timerWindow.getPauseBtn().setEnabled(true);
            this.timerWindow.getPauseBtn().setText("暂停");
        }
    }

    //service:暂停/继续计时
    /*
    负责人：
    功能：
        如果正在计时且未暂停，调用pauseTiming暂停
        如果正在计时且已暂停，调用resumeTiming继续
    参数：void
    返回值：void
     */
    public void togglePause(){
        if (this.isTiming && !this.isPaused) {
            //暂停
            pauseTiming();
        } else if (this.isTiming && this.isPaused) {
            //继续
            resumeTiming();
        }
    }

    //service:暂停计时
    /*
    负责人：
    功能：
        设置isPaused为true
        停止定时器更新
        暂停按钮文字变为"继续"
    参数：void
    返回值：void
     */
    public void pauseTiming(){
        this.isPaused = true;
        if (this.timer != null) {
            this.timer.stop();
        }
        if (this.timerWindow != null) {
            this.timerWindow.getPauseBtn().setText("继续");
        }
    }

    //service:继续计时
    /*
    负责人：
    功能：
        设置isPaused为false
        恢复定时器
        暂停按钮文字变回"暂停"
    参数：void
    返回值：void
     */
    public void resumeTiming(){
        this.isPaused = false;
        if (this.timer != null) {
            this.timer.start();
        }
        if (this.timerWindow != null) {
            this.timerWindow.getPauseBtn().setText("暂停");
        }
    }

    //service:结束计时
    /*
    负责人：
    功能：
        停止定时器
        复位计时状态（isTiming、isPaused）

    参数：void
    返回值：void
     */
    public void stopTiming(){
        // 停止定时器
        if (this.timer != null) {
            this.timer.stop();
            this.timer = null;
        }
        this.isTiming = false;
        this.isPaused = false;

    }

    //service:打开自习室窗口
    /*
    负责人：
    功能：
        【功能待定】创建自习室窗口
        进入自习室专注模式
        显示自习室窗口
    参数：task 当前任务
    返回值：void
     */
    public void openStudyRoom(Task task){
        // 创建自习室窗口
        this.studyRoomWindow = new StudyRoomWindow();

        // 显示自习室窗口
        this.studyRoomWindow.setVisible(true);
        // 更新自习室窗口的欢迎信息
        studyRoomWindow.updateWelcomeFocusTime(task.getTotalFocusSeconds());
    }

    //--------------------service---------------------------


    //--------------get/set-------------------
    public boolean isTiming(){ return isTiming; }
    public boolean isPaused(){ return isPaused; }
    public int getCurrentSeconds(){ return currentSeconds; }
    public String getCurrentTaskId(){ return currentTaskId; }
    public TimerWindow getTimerWindow(){ return timerWindow; }
    //--------------get/set-------------------
}