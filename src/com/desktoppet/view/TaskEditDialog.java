package com.desktoppet.view;

import com.desktoppet.model.Task;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

/**
 * 新增/编辑任务弹窗
 * 负责人：gyjw
 * 功能：
 *     从任务界面点击"+ 新增任务"或任务项编辑按钮打开
 *     顶部标题栏（新增任务 / 编辑任务）
 *     任务名称输入框（必填，带*号）
 *     任务描述文本域
 *     任务日期输入框 + 优先级下拉框（高/中/低）
 *     预计时长输入框（分钟）
 *     底部取消按钮+ 保存按钮
 *     编辑模式下表单回显原任务数据
 *     新增和修改共用同一弹窗
 */
public class TaskEditDialog extends JDialog {

    //--------------组件设计-------------------
    private JLabel titleLabel;          //标题标签（新增任务）
    private JTextField nameField;       //任务名称输入框
    private JTextArea descArea;         //任务描述文本域
    private JTextField dateField;       //任务日期输入框
    private JComboBox<String> priorityCombo; //优先级下拉框
    private JTextField durationField;   //预计时长输入框
    private JButton cancelBtn;          //取消按钮
    private JButton saveBtn;            //保存按钮
    private Task editingTask;           //正在编辑的任务（null为新增模式）
    private boolean confirmed;          //是否点击了保存
    //--------------组件设计-------------------

    //--------------常量设计-------------------
    private static final String[] PRIORITIES = {"高", "中", "低"};
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
    private static final Color TITLE_BAR_BG = new Color(0xF5F5F7);   //标题栏背景
    private static final Color LINE_COLOR   = new Color(0xE0E0E0);   //分隔线
    private static final Color FIELD_BORDER = new Color(0xCCCCCC);   //输入框边框
    private static final Color SAVE_GREEN   = new Color(0x4CAF50);   //保存按钮绿色
    //--------------常量设计-------------------


    //--------------service-------------------
    //service:初始化任务编辑弹窗
    /*
    负责人：gyjw
    功能：
        设置弹窗大小、位置、无边框、模态
        初始化顶部标题栏（可拖拽）
        初始化任务表单（名称/描述/日期/优先级/预计时长）
        初始化取消按钮和保存按钮
        为按钮添加事件监听：保存→校验→confirmed=true并关闭；取消→关闭
    参数：owner 父窗口
    返回值：void
     */
    public TaskEditDialog(JFrame owner){
        super(owner, "新增任务", true);
        this.confirmed = false;
        setUndecorated(true);
        setResizable(false);
        setSize(420, 470);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
        initListeners();
    }

    //service:初始化界面
    /*
    负责人：gyjw
    功能：
        搭建弹窗整体布局：顶部标题栏、中部表单、底部按钮
    参数：void
    返回值：void
     */
    private void initUI(){
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        setContentPane(content);

        //顶部标题栏（可拖拽）
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(TITLE_BAR_BG);
        titleBar.setPreferredSize(new Dimension(0, 40));
        titleBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, LINE_COLOR));
        titleLabel = new JLabel("新增任务", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        titleBar.add(titleLabel, BorderLayout.CENTER);
        attachDrag(this, titleBar);
        content.add(titleBar, BorderLayout.NORTH);

        //表单区
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(16, 24, 10, 24));

        //任务名称（必填）
        form.add(createRow("任务名称 *", nameField = createTextField()));

        //任务描述
        form.add(Box.createVerticalStrut(8));
        form.add(createFormLabel("任务描述"));
        form.add(Box.createVerticalStrut(4));
        descArea = new JTextArea(3, 10);
        descArea.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(BorderFactory.createLineBorder(FIELD_BORDER));
        descScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        descScroll.setPreferredSize(new Dimension(0, 76));
        descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
        form.add(descScroll);

        //任务日期
        form.add(Box.createVerticalStrut(8));
        form.add(createRow("任务日期", dateField = createTextField()));

        //优先级
        form.add(Box.createVerticalStrut(6));
        priorityCombo = new JComboBox<>(PRIORITIES);
        priorityCombo.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        form.add(createRow("优先级", priorityCombo));

        //预计时长
        form.add(Box.createVerticalStrut(6));
        JPanel durRow = createRow("预计时长", durationField = createTextField());
        durRow.add(createFormLabel("分钟"), BorderLayout.EAST);
        form.add(durRow);

        content.add(form, BorderLayout.CENTER);

        //底部按钮
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        bottom.setBackground(new Color(0xFAFAFA));
        bottom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, LINE_COLOR));
        cancelBtn = createBottomBtn("取消", Color.WHITE, new Color(0x666666));
        saveBtn = createBottomBtn("保存", SAVE_GREEN, Color.WHITE);
        bottom.add(cancelBtn);
        bottom.add(saveBtn);
        content.add(bottom, BorderLayout.SOUTH);
    }

    //service:初始化事件监听
    /*
    负责人：gyjw
    功能：
        保存按钮：先校验，通过则置confirmed=true并关闭弹窗
        取消按钮：直接关闭弹窗（confirmed保持false）
    参数：void
    返回值：void
     */
    private void initListeners(){
        saveBtn.addActionListener(e -> {
            if (validateForm()){
                confirmed = true;
                dispose();
            }
        });
        cancelBtn.addActionListener(e -> dispose());
    }

    //service:创建统一样式输入框
    private JTextField createTextField(){
        JTextField tf = new JTextField();
        tf.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        return tf;
    }

    //service:创建表单行（左标签 + 右输入组件）
    private JPanel createRow(String labelText, JComponent field){
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        row.add(createFormLabel(labelText), BorderLayout.WEST);
        if (field != null){
            field.setPreferredSize(new Dimension(0, 30));
            row.add(field, BorderLayout.CENTER);
        }
        return row;
    }

    //service:创建表单标签
    private JLabel createFormLabel(String text){
        JLabel label = new JLabel(text);
        label.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        return label;
    }

    //service:创建底部按钮
    private JButton createBottomBtn(String text, Color bg, Color fg){
        JButton btn = new JButton(text);
        btn.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(78, 30));
        return btn;
    }

    //service:窗口拖拽
    /*
    负责人：gyjw
    功能：
        给指定区域绑定鼠标拖拽，实现无边框窗口的整体移动
    参数：w 要移动的窗口, area 拖拽区域
    返回值：void
     */
    private static void attachDrag(Window w, JComponent area){
        final Point[] offset = {new Point()};
        area.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){ offset[0] = e.getPoint(); }
        });
        area.addMouseMotionListener(new MouseMotionAdapter(){
            @Override
            public void mouseDragged(MouseEvent e){
                Point p = e.getLocationOnScreen();
                w.setLocation(p.x - offset[0].x, p.y - offset[0].y);
            }
        });
    }

    //service:设置要编辑的任务
    /*
    负责人：gyjw
    功能：
        task为null时为新增模式，标题显示"新增任务"，表单清空，日期默认今天，优先级默认"中"
        task不为null时为编辑模式，标题显示"编辑任务"，表单回显原任务数据
        每次调用都重置confirmed为false
    参数：task 要编辑的任务，null表示新增
    返回值：void
     */
    public void setTask(Task task){
        this.editingTask = task;
        this.confirmed = false;
        if (task == null) {
            titleLabel.setText("新增任务");
            nameField.setText("");
            descArea.setText("");
            dateField.setText(LocalDate.now().format(DATE_FMT));
            priorityCombo.setSelectedItem("中");
            durationField.setText("");
        } else {
            titleLabel.setText("编辑任务");
            nameField.setText(task.getName() == null ? "" : task.getName());
            descArea.setText(task.getDescription() == null ? "" : task.getDescription());
            dateField.setText(task.getDate() == null ? "" : task.getDate());
            priorityCombo.setSelectedItem(task.getPriority());
            durationField.setText(String.valueOf(task.getExpectedDuration()));
        }
        nameField.requestFocusInWindow();
    }

    //service:表单校验
    /*
    负责人：gyjw
    功能：
        校验任务名称非空，为空时弹出"请输入任务名称"提示，返回false
        校验日期格式是否有效（yyyy-MM-dd严格校验），无效时弹出"请选择有效日期"，返回false
        校验预计时长是否为正整数，无效时弹出"请输入有效的预计时长"，返回false
        校验通过返回true
    参数：void
    返回值：boolean 校验是否通过
     */
    public boolean validateForm(){
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入任务名称");
            return false;
        }
        if (!isValidDate(dateField.getText().trim())) {
            JOptionPane.showMessageDialog(this, "请选择有效日期");
            return false;
        }
        try {
            if (Integer.parseInt(durationField.getText().trim()) <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效的预计时长");
            return false;
        }
        return true;
    }

    //service:日期有效性校验
    /*
    负责人：gyjw
    功能：
        用yyyy-MM-dd严格解析器校验日期，如2026-02-30这类非法日期会拒绝
    参数：text 日期字符串
    返回值：boolean 是否有效
     */
    private boolean isValidDate(String text){
        if (text == null || text.trim().isEmpty()) return false;
        try {
            LocalDate.parse(text.trim(), DATE_FMT);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //service:获取表单数据
    /*
    负责人：gyjw
    功能：
        从表单中获取任务名称、描述、日期、优先级、预计时长
        封装为字符串数组返回（原始字符串，由调用方解析）
    参数：void
    返回值：String[] 表单数据 [名称, 描述, 日期, 优先级, 预计时长]
     */
    public String[] getFormData(){
        return new String[]{
            nameField.getText().trim(),
            descArea.getText(),
            dateField.getText().trim(),
            (String) priorityCombo.getSelectedItem(),
            durationField.getText().trim()
        };
    }

    //--------------------service---------------------------


    //--------------get/set-------------------
    public boolean isConfirmed(){ return confirmed; }
    public Task getEditingTask(){ return editingTask; }
    public JButton getCancelBtn(){ return cancelBtn; }
    public JButton getSaveBtn(){ return saveBtn; }
    //--------------get/set-------------------
}
