package com.desktoppet.system;

import com.desktoppet.model.Task;
import com.desktoppet.view.TaskEditDialog;
import com.desktoppet.view.TaskItemPanel;
import com.desktoppet.view.TaskWindow;
import com.google.gson.Gson;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * 任务管理系统
 * 负责人：
 * 功能：
 *     连接Task模型与TaskWindow、TaskEditDialog视图
 *     管理任务的增删改查
 *     管理任务数据的本地JSON持久化
 *     管理日期筛选（上一天/今天/下一天）
 *     管理任务完成状态切换
 *     管理任务界面窗口的打开和关闭
 */
public class TaskSystem {

    //--------------数据设计-------------------
    private ArrayList<Task> allTasks;       //所有任务列表
    private String currentDate;              //当前选中日期
    private TaskWindow taskWindow;           //任务界面窗口
    private TaskEditDialog taskEditDialog;   //任务编辑弹窗
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private TimerSystem timerSystem;
    //--------------数据设计-------------------


    //--------------service-------------------
    //service:初始化任务管理系统
    /*
    负责人：
    功能：
        初始化所有任务列表（从本地JSON文件加载）
        初始化当前选中日期为今天（LocalDate.now()动态获取）
        创建任务界面窗口
        创建任务编辑弹窗
        为日期导航按钮（上一天/今天/下一天）添加事件监听
        为新增任务按钮添加事件监听
        为关闭按钮添加事件监听
    参数：void
    返回值：void
     */
    public TaskSystem(){
        this.allTasks = new ArrayList<>();
        this.currentDate = LocalDate.now().format(DATE_FMT);
        this.taskWindow = new TaskWindow();
        this.taskEditDialog = new TaskEditDialog(taskWindow);
        loadTasks(); //初始化所有任务列表
        taskWindow.getPrevBtn().addActionListener(e -> previousDay());  //上一天
        taskWindow.getTodayBtn().addActionListener(e -> toToday());  //今天
        taskWindow.getNextBtn().addActionListener(e -> nextDay());  //明天
        taskWindow.getAddTaskBtn().addActionListener(e -> addTask());   //添加/修改任务
    }

    //service:打开任务界面
    /*
    负责人：
    功能：
        显示任务界面窗口
        刷新日期显示和任务列表
    参数：void
    返回值：void
     */
    public void openTaskWindow(){
        taskWindow.setVisible(true);//显示任务界面窗口
        LocalDate date = LocalDate.parse(currentDate, DATE_FMT);//计算星期
        String week = switch (date.getDayOfWeek()) {
            case MONDAY    -> "周一";
            case TUESDAY   -> "周二";
            case WEDNESDAY -> "周三";
            case THURSDAY  -> "周四";
            case FRIDAY    -> "周五";
            case SATURDAY  -> "周六";
            case SUNDAY    -> "周日";
        };
        taskWindow.updateDateLabel(currentDate, week);
        refreshTaskList();//刷新
    }

    //service:关闭任务界面
    /*
    负责人：
    功能：
        隐藏任务界面窗口
        返回桌宠主界面
    参数：void
    返回值：void
     */
    public void closeTaskWindow(){
        taskWindow.setVisible(false);

    }

    //service:加载任务数据
    /*
    负责人：
    功能：
        从本地JSON文件（data/tasks.json）读取所有任务数据
        解析为Task对象列表
        文件不存在时初始化示例数据
    参数：void
    返回值：void
     */
    public void loadTasks(){
        File file = new File("data/tasks.json");//找到任务数据
        if (!file.exists()) {//文件不存在时自动创建
            try {
                if (file.getParentFile() != null) {
                    file.getParentFile().mkdirs();//创建data目录
                }
                try (Writer writer = new FileWriter(file, StandardCharsets.UTF_8)) {
                    writer.write("[]");//写入空任务列表
                }
                this.allTasks = new ArrayList<>();//初始化为空列表
                System.out.println("任务数据文件不存在，已自动创建 data/tasks.json");
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        try (Reader reader = new FileReader(file, StandardCharsets.UTF_8)) {//转换为可读取的字节流
            Task[] arr = new Gson().fromJson(reader, Task[].class);//读取tasks文件
            this.allTasks = (arr != null) ?
                    new ArrayList<>(Arrays.asList(arr)) : new ArrayList<>();//数据存储到列表
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //service:保存任务数据
    /*
    负责人：
    功能：
        将所有任务列表写入本地JSON文件
        任务增删改后调用
    参数：void
    返回值：void
     */
    public void saveTasks(){
        File file = new File("data/tasks.json"); //找到本都json文件
        if (file.getParentFile() != null && !file.getParentFile().exists()){
            file.getParentFile().mkdirs(); //确保父目录存在
        }
        Writer writer = null;
        try {
            writer = new FileWriter(file,StandardCharsets.UTF_8); //
            new Gson().toJson(allTasks,writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //service:获取当前日期的任务列表
    /*
    负责人：
    功能：
        遍历所有任务，筛选出日期等于当前选中日期的任务
        返回筛选后的任务列表
    参数：void
    返回值：ArrayList<Task> 当前日期的任务列表
     */
    public ArrayList<Task> getCurrentDateTasks(){
        ArrayList<Task> result = new ArrayList<>();
        for (Task task : allTasks){
            if (task.getDate().equals(currentDate)){
                result.add(task);
            }
        }
        return result;
    }

    //service:刷新任务列表显示
    /*
    负责人：
    功能：
        获取当前日期的任务列表
        调用taskWindow.renderTaskList渲染任务列表
        为每个任务项的操作按钮（复选框/开始/编辑/删除）绑定事件
    参数：void
    返回值：void
     */
    public void refreshTaskList(){
        ArrayList<Task> result = getCurrentDateTasks();
        taskWindow.renderTaskList(result);
        for (TaskItemPanel itemPanel : taskWindow.getTaskItems()){
            String taskId = itemPanel.getTask().getId();
            itemPanel.getCheckBox().addActionListener(e -> toggleTaskFinished(taskId));  //复选框

            //开始
            itemPanel.getStartBtn().addActionListener(e -> {
                if (timerSystem != null){
                    timerSystem.openTimerWindow(taskId);
                }
            });
            itemPanel.getEditBtn().addActionListener(e -> editTask(taskId));//编辑
            itemPanel.getDeleteBtn().addActionListener(e -> deleteTask(taskId));//删除
        }
    }

    //service:新增任务
    /*
    负责人：
    功能：
        打开任务编辑弹窗（新增模式）
        用户填写表单并点击保存后
        校验表单，生成唯一任务ID
        将任务数据添加到列表并保存到文件
        刷新任务列表显示
    参数：void
    返回值：void
     */
    public void addTask(){
        taskEditDialog.setTask(null); //进入新增模式
        taskEditDialog.setVisible(true); //显示弹窗
        if (!taskEditDialog.isConfirmed()){
            return;
        }
        String[] data = taskEditDialog.getFormData();//获取任务数据表
        String id = UUID.randomUUID().toString();
        Task task = new Task(id, data[0],data[1], data[2],
                data[3], Integer.parseInt(data[4]));//创建任务对象
        allTasks.add(task);//存入数据
        saveTasks();//保存到本地的json文件
        refreshTaskList();//更新数据
    }

    //service:编辑任务
    /*
    负责人：
    功能：
        打开任务编辑弹窗（编辑模式，回显原数据）
        用户修改表单并点击保存后
        校验表单，更新对应ID的任务数据
        保存到文件并刷新任务列表
    参数：taskId 要编辑的任务ID
    返回值：void
     */
    public void editTask(String taskId){
        Task task = getTaskById(taskId);//寻找选择的id对应的任务
        if (task == null) return; //找不到直接结束
        taskEditDialog.setTask(task);
        taskEditDialog.setVisible(true);
        if (!taskEditDialog.isConfirmed()) return; //点了取消返回
        String[] data =taskEditDialog.getFormData();
        task.setName(data[0]);
        task.setDescription(data[1]);
        task.setDate(data[2]);
        task.setPriority(data[3]);
        task.setExpectedDuration(Integer.parseInt(data[4]));
        saveTasks();
        refreshTaskList();
    }

    //service:删除任务
    /*
    负责人：
    功能：
        弹出确认对话框"确定删除该任务？"
        用户确认后从任务列表中移除该任务
        保存到文件并刷新任务列表
    参数：taskId 要删除的任务ID
    返回值：void
     */
    public void deleteTask(String taskId){
        Task task = getTaskById(taskId);
        if (task == null) return;  // 找不到直接返回
        int choice = JOptionPane.showConfirmDialog(
                taskWindow,                 // 父窗口，弹窗居中在任务窗口上方
                "确定删除该任务？",           // 提示文字
                "删除确认",                  // 弹窗标题
                JOptionPane.YES_NO_OPTION   // 按钮类型：是/否
        );
        if (choice == JOptionPane.YES_OPTION) {
            allTasks.remove(task);   // 从列表移除
            saveTasks();             // 保存到文件
            refreshTaskList();       // 刷新界面
        }
    }

    //service:切换任务完成状态
    /*
    负责人：
    功能：
        根据任务ID找到任务
        调用task.toggleFinished()切换状态
        保存到文件并刷新任务列表
    参数：taskId 任务ID
    返回值：void
     */
    public void toggleTaskFinished(String taskId){
        Task task = getTaskById(taskId);
        task.toggleFinished();
        saveTasks();
        refreshTaskList();
    }

    //service:切换到上一天
    /*
    负责人：
    功能：
        当前日期减一天（LocalDate.minusDays(1)）
        更新日期显示
        刷新任务列表
    参数：void
    返回值：void
     */
    public void previousDay(){
        // 字符串转 LocalDate，减一天，再转回字符串
        LocalDate date = LocalDate.parse(currentDate, DATE_FMT);
        date = date.minusDays(1);
        currentDate = date.format(DATE_FMT);
        // 计算星期
        String week = switch (date.getDayOfWeek()) {
            case MONDAY    -> "周一";
            case TUESDAY   -> "周二";
            case WEDNESDAY -> "周三";
            case THURSDAY  -> "周四";
            case FRIDAY    -> "周五";
            case SATURDAY  -> "周六";
            case SUNDAY    -> "周日";
        };// 更新日期标签显示
        taskWindow.updateDateLabel(currentDate, week);// 刷新任务列表（显示前一天的任务）
        refreshTaskList();
    }

    //service:切换到下一天
    /*
    负责人：
    功能：
        当前日期加一天（LocalDate.plusDays(1)）
        更新日期显示
        刷新任务列表
    参数：void
    返回值：void
     */
    public void nextDay(){
        // 字符串转 LocalDate，加一天，再转回字符串
        LocalDate date = LocalDate.parse(currentDate, DATE_FMT);
        date = date.plusDays(1);
        currentDate = date.format(DATE_FMT);
        // 计算星期
        String week = switch (date.getDayOfWeek()) {
            case MONDAY    -> "周一";
            case TUESDAY   -> "周二";
            case WEDNESDAY -> "周三";
            case THURSDAY  -> "周四";
            case FRIDAY    -> "周五";
            case SATURDAY  -> "周六";
            case SUNDAY    -> "周日";
        };// 更新日期标签显示
        taskWindow.updateDateLabel(currentDate, week);// 刷新任务列表（显示明天的任务）
        refreshTaskList();
    }

    //service:切换到今天
    /*
    负责人：
    功能：
        当前日期设置为今天（LocalDate.now()）
        更新日期显示
        刷新任务列表
    参数：void
    返回值：void
     */
    public void toToday(){
        LocalDate date = LocalDate.now();
        currentDate = date.format(DATE_FMT);
        // 计算星期
        String week = switch (date.getDayOfWeek()) {
            case MONDAY    -> "周一";
            case TUESDAY   -> "周二";
            case WEDNESDAY -> "周三";
            case THURSDAY  -> "周四";
            case FRIDAY    -> "周五";
            case SATURDAY  -> "周六";
            case SUNDAY    -> "周日";
        };// 更新日期标签显示
        taskWindow.updateDateLabel(currentDate, week);// 刷新任务列表（显示今天的任务）
        refreshTaskList();
    }

    //service:根据ID获取任务
    /*
    负责人：
    功能：
        遍历任务列表，找到ID匹配的任务
        找到返回任务对象，找不到返回null
    参数：taskId 任务ID
    返回值：Task 任务对象
     */
    public Task getTaskById(String taskId){
        for(Task task : allTasks){
            if(task.getId().equals(taskId)){
                return task;
            }
        }
        return null;
    }
    /**
     * 功能：自动找到今天正在计时的任务
     */
    public Task getCurrentTask() {
        // 1. 获取今天的日期字符串 ("yyyy-MM-dd")
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 2. 遍历所有任务
        for (Task task : allTasks) {
            // 筛选条件：日期是今天 且 状态是正在运行(!task.isFinished())
            if (task.getDate().equals(today) && !task.isFinished()) {
                return task;
            }
        }

        return null;
    }

    //--------------------service---------------------------


    //--------------get/set-------------------
    public ArrayList<Task> getAllTasks(){ return allTasks; }
    public String getCurrentDate(){ return currentDate; }
    public void setCurrentDate(String currentDate){ this.currentDate = currentDate; }
    public TaskWindow getTaskWindow(){ return taskWindow; }
    public TaskEditDialog getTaskEditDialog(){ return taskEditDialog; }
    public void setTimerSystem(TimerSystem timerSystem){this.timerSystem = timerSystem;}
    //--------------get/set-------------------
}
