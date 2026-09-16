package com.desktoppet;

import com.desktoppet.system.PetSystem;
import com.desktoppet.system.TaskSystem;
import com.desktoppet.system.TimerSystem;
import com.desktoppet.view.SettingWindow;
import com.desktoppet.view.TeamWindow;
import com.desktoppet.system.MusicManager;
import javax.swing.*;

/**
 * 桌宠应用主入口
 * 负责人：
 * 功能：
 *     初始化所有系统（PetSystem、TaskSystem、TimerSystem）
 *     初始化设置窗口和团队介绍窗口
 *     启动桌宠主窗口
 */
public class Main {

    //--------------系统对象-------------------
    private static PetSystem petSystem;        //桌宠互动系统
    private static TaskSystem taskSystem;      //任务管理系统
    private static TimerSystem timerSystem;    //专注计时系统
    //--------------系统对象-------------------

    //--------------窗口对象-------------------
    private static SettingWindow settingWindow;    //设置界面窗口
    private static TeamWindow teamWindow;          //团队介绍窗口
    //--------------窗口对象-------------------


    //===========================主方法===============================
    public static void main(String[] args) {
        // 在事件调度线程中启动GUI
        SwingUtilities.invokeLater(() -> {
            initSystems();
            initWindows();
            initMenuListeners();
            initWindowListeners();
            // 启动桌宠主窗口
            petSystem.getPetWindow().setVisible(true);
        });
    }

    //===========================初始化所有系统===============================
    /*
    负责人：
    功能：
        创建PetSystem（桌宠互动系统，内含桌宠主窗口和互动面板窗口）
        创建TaskSystem（任务管理系统，内含任务界面窗口和任务编辑弹窗）
        创建TimerSystem（专注计时系统，关联TaskSystem）
        调用petSystem.refreshIntimacy初始化亲密度显示
        调用taskSystem.refreshTaskList初始化任务列表
    参数：void
    返回值：void
     */
    private static void initSystems(){
        petSystem = new PetSystem();
        taskSystem = new TaskSystem();
        timerSystem = new TimerSystem(taskSystem);
        taskSystem.setTimerSystem(timerSystem);//挂回任务系统，任务列表的"开始"按钮才能打开计时器
        //.......
        // ===== 新增：启动背景音乐 =====
        MusicManager.getInstance().play();
    }

    //===========================初始化所有窗口===============================
    /*
    负责人：
    功能：
        创建设置界面窗口（SettingWindow）
        创建团队介绍窗口（TeamWindow）
        初始状态均为不可见
    参数：void
    返回值：void
     */
    private static void initWindows(){
        settingWindow = new SettingWindow();
        teamWindow = new TeamWindow();
        //.......
    }

    //===========================初始化右键菜单监听===============================
    /*
    负责人：
    功能：
        为桌宠主窗口的右键菜单项添加事件监听：
        互动菜单项 → 调用petSystem.openInteractWindow()打开互动面板
        任务菜单项 → 调用taskSystem.openTaskWindow()打开任务界面
        设置菜单项 → 显示settingWindow设置界面
    参数：void
    返回值：void
     */
    private static void initMenuListeners(){
        // 任务菜单项 → 打开任务界面
        petSystem.getPetWindow().getTaskMenuItem().addActionListener(e -> {
            taskSystem.openTaskWindow();
        });

        // 设置菜单项 → 打开设置界面
        petSystem.getPetWindow().getSettingMenuItem().addActionListener(e -> {
            settingWindow.setVisible(true);
        });
    }

    //===========================初始化窗口按钮监听===============================
    /*
    负责人：
    功能：
        互动面板关闭按钮 → 调用petSystem.closeInteractWindow()
        任务界面关闭按钮 → 调用taskSystem.closeTaskWindow()
        设置界面返回按钮 → 关闭设置窗口
        设置界面退出程序按钮 → 调用settingWindow.exitProgram()
        设置界面团队介绍按钮 → 显示teamWindow
        团队介绍返回按钮 → 关闭团队介绍窗口，返回设置界面
        任务项开始按钮 → 调用timerSystem.openTimerWindow()
        任务项编辑按钮 → 调用taskSystem.editTask()
        任务项删除按钮 → 调用taskSystem.deleteTask()
        任务项复选框 → 调用taskSystem.toggleTaskFinished()
    参数：void
    返回值：void
     */
    private static void initWindowListeners(){
        // 互动面板关闭按钮
        if (petSystem.getInteractWindow() != null) {
            petSystem.getInteractWindow().getCloseBtn().addActionListener(e -> {
                petSystem.closeInteractWindow();
            });
        }

        // 任务界面关闭按钮
        taskSystem.getTaskWindow().getCloseBtn().addActionListener(e -> {
            taskSystem.closeTaskWindow();
        });

        // 设置界面返回按钮
        settingWindow.getBackBtn().addActionListener(e -> {
            settingWindow.setVisible(false);
        });

        // 设置界面退出程序按钮
        settingWindow.getExitBtn().addActionListener(e -> {
            settingWindow.exitProgram();
        });

        // 设置界面团队介绍按钮
        settingWindow.getTeamBtn().addActionListener(e -> {
            teamWindow.setVisible(true);
        });

        // 团队介绍返回按钮
        teamWindow.getBackBtn().addActionListener(e -> {
            teamWindow.setVisible(false);
        });
    }

    //===========================窗口切换方法===============================

    // 显示设置界面
    private static void showSettingWindow(){
        settingWindow.setVisible(true);
    }

    // 显示团队介绍
    private static void showTeamWindow(){
        teamWindow.setVisible(true);
    }

    //===========================窗口切换方法===============================
}
