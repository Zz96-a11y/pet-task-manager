package com.desktoppet.view;

import com.desktoppet.model.Task;

import javax.swing.*;
import java.awt.*;

/**
 * 任务列表项组件
 * 负责人：gyjw
 * 功能：
 *     复选框（已完成绿色对勾，未完成空框）
 *     任务名称（未完成红色，已完成灰色加删除线）
 *     信息行（预计· 已完成/未完成 · 专注）
 *     操作按钮：开始、编辑、删除
 */
public class TaskItemPanel extends JPanel {

    //--------------组件设计-------------------
    private Task task;              //关联的任务对象
    private JCheckBox checkBox;     //完成状态复选框
    private JLabel nameLabel;       //任务名称标签
    private JLabel infoLabel;       //信息标签（预计/状态/专注）
    private JButton startBtn;       //开始按钮
    private JButton editBtn;        //编辑按钮
    private JButton deleteBtn;      //删除按钮
    //--------------组件设计-------------------

    //--------------配色设计-------------------
    private static final Color UNFINISHED_COLOR = new Color(0xE53935);   //未完成名称红色
    private static final Color FINISHED_COLOR   = new Color(0x999999);   //已完成名称灰色
    private static final Color INFO_COLOR       = new Color(0x999999);   //信息行灰色
    private static final Color LINE_COLOR       = new Color(0xF0F0F0);   //行分隔线
    private static final Color CHECK_GREEN      = new Color(0x4CAF50);   //绿色对勾
    private static final Color CHECK_GRAY       = new Color(0xBBBBBB);   //未完成空圈
    private static final Color BTN_BORDER       = new Color(0xDDDDDD);   //按钮边框
    //--------------配色设计-------------------


    //--------------service-------------------
    //service:初始化任务列表项
    /*
    负责人：gyjw
    功能：
        设置布局
        初始化复选框（自定义绿色对勾，根据完成状态设置选中）
        初始化任务名称标签（未完成红色，已完成灰色删除线）
        初始化信息标签（预计时长/状态/专注时长）
        初始化开始、编辑、删除三个操作按钮
        按钮业务监听由TaskSystem通过getter绑定
    参数：task 任务对象
    返回值：void
     */
    public TaskItemPanel(Task task){
        this.task = task;
        setLayout(new BorderLayout(6, 0));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, LINE_COLOR));
        setPreferredSize(new Dimension(0, 66));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));
        setAlignmentX(Component.LEFT_ALIGNMENT);

        //复选框（自定义绿色对勾）
        checkBox = new JCheckBox();
        checkBox.setOpaque(false);
        checkBox.setContentAreaFilled(false);
        checkBox.setFocusPainted(false);
        checkBox.setSelected(task.isFinished());
        checkBox.setIcon(new CheckIcon(false));
        checkBox.setSelectedIcon(new CheckIcon(true));
        checkBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        //中间：任务名称 + 信息行
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 4));
        nameLabel = new JLabel();
        nameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        infoLabel = new JLabel();
        infoLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        infoLabel.setForeground(INFO_COLOR);
        centerPanel.add(nameLabel);
        centerPanel.add(Box.createVerticalStrut(3));
        centerPanel.add(infoLabel);

        //右侧：操作按钮
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 15));
        btnPanel.setOpaque(false);
        startBtn = createActionBtn("开始", new Color(0x43A047));
        editBtn = createActionBtn("编辑", new Color(0x666666));
        deleteBtn = createActionBtn("删除", UNFINISHED_COLOR);
        btnPanel.add(startBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);

        add(checkBox, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.EAST);

        refresh();
    }

    //service:创建操作按钮
    /*
    负责人：gyjw
    功能：
        创建一个统一样式的小按钮（白底、细描边、指定前景色）
    参数：text 按钮文字, fg 前景色
    返回值：JButton 按钮
     */
    private JButton createActionBtn(String text, Color fg){
        JButton btn = new JButton(text);
        btn.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(BTN_BORDER));
        btn.setBackground(Color.WHITE);
        return btn;
    }

    //service:刷新任务项显示
    /*
    负责人：gyjw
    功能：
        更新复选框选中状态
        更新任务名称颜色和删除线
        更新信息标签内容
    参数：void
    返回值：void
     */
    public void refresh(){
        checkBox.setSelected(task.isFinished());
        String name = task.getName() == null ? "" : task.getName();
        if (task.isFinished()) {
            nameLabel.setText("<html><s>" + escapeHtml(name) + "</s></html>");
            nameLabel.setForeground(FINISHED_COLOR);
        } else {
            nameLabel.setText(name);
            nameLabel.setForeground(UNFINISHED_COLOR);
        }
        infoLabel.setText(buildInfo());
    }

    //service:构建信息行文字
    /*
    负责人：gyjw
    功能：
        根据任务数据生成"预计X分钟 · 状态 · 专注Y分钟"格式的信息行
    参数：void
    返回值：String 信息行文字
     */
    private String buildInfo(){
        String status = task.isFinished() ? "已完成" : "未完成";
        int focusMin = task.getTotalFocusSeconds() / 60;
        return "预计 " + task.getExpectedDuration() + "分钟 · " + status + " · 专注 " + focusMin + "分钟";
    }

    //service:HTML转义
    /*
    负责人：gyjw
    功能：
        转义任务名中的 & < >，防止破坏名称的HTML样式
    参数：s 原始文字
    返回值：String 转义后文字
     */
    private static String escapeHtml(String s){
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    //--------------------service---------------------------


    //--------------get/set-------------------
    public Task getTask(){ return task; }
    public JCheckBox getCheckBox(){ return checkBox; }
    public JButton getStartBtn(){ return startBtn; }
    public JButton getEditBtn(){ return editBtn; }
    public JButton getDeleteBtn(){ return deleteBtn; }
    //--------------get/set-------------------

    //--------------内部类：绿色对勾图标-------------------
    /*
    负责人：gyjw
    功能：
        自绘复选框图标：未完成=灰色空圆圈，已完成=绿色圆圈+白色对勾
     */
    private static class CheckIcon implements Icon {
        private final boolean selected;
        CheckIcon(boolean selected){ this.selected = selected; }
        @Override
        public int getIconWidth(){ return 18; }
        @Override
        public int getIconHeight(){ return 18; }
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y){
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (selected) {
                g2.setColor(CHECK_GREEN);
                g2.fillOval(x, y, 16, 16);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(x + 4, y + 8, x + 7, y + 11);
                g2.drawLine(x + 7, y + 11, x + 12, y + 5);
            } else {
                g2.setColor(CHECK_GRAY);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(x + 1, y + 1, 14, 14);
            }
            g2.dispose();
        }
    }
    //--------------内部类：绿色对勾图标-------------------
}