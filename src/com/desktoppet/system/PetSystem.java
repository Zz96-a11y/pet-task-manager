package com.desktoppet.system;

import com.desktoppet.Main;
import com.desktoppet.model.Pet;
import com.desktoppet.view.InteractWindow;
import com.desktoppet.view.PetWindow;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

/**
 * 桌宠互动系统
 * 负责人：
 * 功能：
 * 连接Pet模型与PetWindow、InteractWindow视图
 * 管理抚摸、喂食、玩耍互动行为
 * 管理亲密度升级逻辑
 * 管理玩耍冷却时间
 * 管理待机动画切换
 * 管理互动面板窗口的打开和关闭
 */
public class PetSystem {

    //--------------数据设计-------------------
    private Pet pet;                    //桌宠实体
    private PetWindow petWindow;        //桌宠主窗口
    private InteractWindow interactWindow; //互动面板窗口
    private boolean soundEnabled;       //音效开关
    //--------------数据设计-------------------


    //--------------service-------------------
    //service:初始化桌宠互动系统
    /*
    负责人：
    功能：
        创建桌宠实体，初始名字为"小桌宠"
        创建桌宠主窗口并显示
        初始化音效开关为开启
        为桌宠主窗口的右键菜单项添加事件监听
        互动菜单项 → 打开互动面板窗口
        任务菜单项 → 由TaskSystem处理
        设置菜单项 → 由Main处理打开设置窗口
        启动待机动画定时器
    参数：void
    返回值：void
     */
    public PetSystem() {
        this.pet = new Pet("小桌宠");
        this.petWindow = new PetWindow(pet);
        this.soundEnabled = true;
        //.......

        petWindow.getInteractMenuItem().addActionListener(e -> openInteractWindow());
    }

    //service:打开互动面板
    /*
    负责人：
    功能：
        创建或显示互动面板窗口
        刷新亲密度显示
        为互动按钮（喂食/抚摸/玩耍）添加事件监听
        为关闭按钮添加事件监听
    参数：void
    返回值：void
     */
    public void openInteractWindow() {
        //.......
        if (interactWindow == null) {
            interactWindow = new InteractWindow(pet);
            interactWindow.getFeedBtn().addActionListener(e -> feedAction());
            interactWindow.getPlayBtn().addActionListener(e -> playAction());
            interactWindow.getPetBtn().addActionListener(e -> petAction());
            interactWindow.getCloseBtn().addActionListener(e -> closeInteractWindow());
        }
        //刷新亲密度
        refreshIntimacy();
        //显示窗口
        interactWindow.setVisible(true);
        //把这个窗口拿到屏幕最前面，变成当前激活窗口，盖在其他窗口之上
        interactWindow.toFront();
    }

    //service:关闭互动面板
    /*
    负责人：
    功能：
        隐藏互动面板窗口
        返回桌宠主界面
    参数：void
    返回值：void
     */
    public void closeInteractWindow() {
        //.......
        if (interactWindow != null) {
            interactWindow.setVisible(false);
        }
    }

    //service:抚摸互动
    /*
    负责人：
    功能：
        调用pet.pet()获取增加的亲密度
        在桌宠主窗口播放开心动画和爱心特效
        在互动面板显示+1提示
        刷新亲密度显示
        添加互动记录
        1.5秒后回到待机状态
    参数：void
    返回值：void
     */
    public void petAction() {
        //.......
        int levelBefore = pet.getLevel();
        pet.pet();
        petWindow.playHappy(levelBefore);//播放开心动画+飘字，播完自动回到待机
        refreshIntimacy();

        LocalTime now = LocalTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String timeStr = dateTimeFormatter.format(now);
        interactWindow.addRecord(timeStr + " 抚摸 +1");
    }

    //service:喂食互动
    /*
    负责人：
    功能：
        调用pet.feed()获取增加的亲密度
        播放进食动画
        显示+3提示
        刷新亲密度显示
        添加互动记录
        1.5秒后回到待机状态
    参数：void
    返回值：void
     */
    public void feedAction() {
        //.......
        int levelBefore = pet.getLevel();
        pet.feed();
        petWindow.playFeed(levelBefore);//播放进食动画+飘字，播完自动回到待机
        refreshIntimacy();

        LocalTime now = LocalTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String timeStr = dateTimeFormatter.format(now);
        interactWindow.addRecord(timeStr + " 喂食 +3");
    }

    //service:玩耍互动
    /*
    负责人：
    功能：
        调用pet.play()获取增加的亲密度
        如果返回-1表示冷却中，弹出提示"玩耍冷却中，请稍后再试"
        否则播放玩耍动画，显示+5提示，刷新亲密度
        启动冷却倒计时，每秒更新按钮文字
        冷却结束恢复玩耍按钮可用
        添加互动记录
    参数：void
    返回值：void
     */
    public void playAction() {
        //.......
        int levelBefore = pet.getLevel();
        int temp = pet.play();
        if (temp == -1) {
            JOptionPane.showMessageDialog(interactWindow, "玩耍冷却中，请稍后再试");
            return;
        }
        petWindow.playPlay(levelBefore);//播放跳舞动画+飘字，播完自动回到待机
        refreshIntimacy();

        LocalTime now = LocalTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String timeStr = dateTimeFormatter.format(now);
        interactWindow.addRecord(timeStr + " 玩耍 +5");

        //按钮置灰并显示30秒倒计时，结束后恢复
        interactWindow.startPlayCooldown(30);
    }

    //service:刷新亲密度显示
    /*
    负责人：
    功能：
        获取桌宠当前等级、亲密度、最大亲密度
        同时更新桌宠主窗口和互动面板的亲密度显示
    参数：void
    返回值：void
     */
    public void refreshIntimacy() {
        //.......
        int level = pet.getLevel();
        int intimacy = pet.getIntimacy();
        int maxIntimacy = pet.getMaxIntimacy();

        //更新主窗口
        petWindow.refreshIntimacy(level, intimacy, maxIntimacy);
        //更新互动面板
        if (interactWindow != null) {
            interactWindow.refreshIntimacy();
        }
    }

    //service:播放待机动画
    /*
    负责人：
    功能：
        调用pet.randomIdleAction()随机切换待机状态
        调用petWindow.playIdleAnimation()播放动画
        每隔3-5秒调用一次
    参数：void
    返回值：void
     */
    public void playIdleAnimation() {
        //.......
        pet.randomIdleAction();
        petWindow.playIdleAnimation();
    }

    //--------------------service---------------------------


    //--------------get/set-------------------
    public Pet getPet() {
        return pet;
    }

    public PetWindow getPetWindow() {
        return petWindow;
    }

    public InteractWindow getInteractWindow() {
        return interactWindow;
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }
    //--------------get/set-------------------
}
