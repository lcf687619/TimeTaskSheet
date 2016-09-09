package com.mission.schedule.entity;

/**
 * Created by lenovo on 2016/8/24.
 */
public class CLFriendScheduleTable {
    public static final String CLFriendScheduleTable = "CLFriendScheduleTable";
    public static final String fscID = "fscID";//INTEGER 好友表ID
    public static final String fscScheduleID = "fscScheduleID";// INTEGER 好友日程ID
    public static final String fscSenderID = "fscSenderID";// INTEGER 发送者ID
    public static final String fscGetterID = "fscGetterID";// INTEGER 接收者ID
    public static final String fscIsAlarm = "fscIsAlarm";// INTEGER 共4种：0 无闹钟 | 1 准时有闹钟 提前无闹钟 | 2 准时无闹钟 提前有闹钟 | 3 准时提前均有闹钟
    public static final String fscBeforeTime = "fscBeforeTime";// INTEGER 提前时间
    public static final String fscRepType = "fscRepType";// INTEGER 1每天，2每周，3每月，4阳历每年 5工作日 6农历每年
    public static final String fscPostPone = "fscPostPone";// INTEGER 0不顺延  1顺延
    public static final String fscOpenState = "fscOpenState";// INTEGER 0
    public static final String fscColorType = "fscColorType";// INTEGER 标签分类 和标签表对应
    public static final String fscDisplayTime = "fscDisplayTime";// INTEGER 0全天 1显示时间
    public static final String fscEndState = "fscEndState";// INTEGER 0未结束  1结束
    public static final String fscEndNumber = "fscEndNumber";// INTEGER 结束数量
    public static final String fscDownState = "fscDownState";// INTEGER 是否下行：0.未下行 | 1.下行
    public static final String fscDownNumber = "fscDownNumber";// INTEGER 下行数量
    public static final String fscIsImportant = "fscIsImportant";// INTEGER 0不重要 1重要
    public static final String fscAtype = "fscAtype";// INTEGER 附加信息类型 0 没有附加信息 | 1 附加链接| 2 附加图片 | 3 附加链接和图片
    public static final String fscRepInSTable = "fscRepInSTable";// INTEGER 是否生成子日程 0 生成 | 1 不生成
    public static final String fscPostState = "fscPostState";// INTEGER 修改类型：0.普通 | 1.撤销 | 2.完成 | 3.修改     0 未结束  1 已结束
    public static final String fscSourceType = "fscSourceType";// INTEGER 0 链接类型：0 普通 | 1 全链接（发现）| 2 ...
    public static final String fscMessagesNumber = "fscMessagesNumber";// INTEGER 留言数量
    public static final String fscRepIsPuse = "fscRepIsPuse";// INTEGER 重复是否暂停 1暂停 0不暂停
    public static final String fscRepID = "fscRepID";// INTEGER 重复ID
    public static final String fscRepLink = "fscRepLink";// INTEGER 是否与母计时脱钩 0 1关联，2是删除，3是结束
    public static final String fscRepStateOne = "fscRepStateOne";// INTEGER 标记子记事状态 0 普通 | 1 脱钩 | 2 删除 | 3 结束
    public static final String fscRepStateTwo = "fscRepStateTwo";// INTEGER 标记子记事状态 0 普通 | 1 脱钩 | 2 删除 | 3 结束
    public static final String fscUpdateState = "fscUpdateState";// INTEGER 同步更新状态  0不上传 1 新建 2修改 3删除
    public static final String fscContent = "fscContent";// VARCHAR(4000,0) 发送内容
    public static final String fscDate = "fscDate";// VARCHAR(50,0) 日期 yyyy-MM-dd
    public static final String fscTime = "fscTime";// VARCHAR(50,0)时间 HH:mm
    public static final String fscRingCode = "fscRingCode";// VARCHAR(100,0) 铃声编码
    public static final String fscRingDesc = "fscRingDesc";// VARCHAR(100,0) 铃声描述，铃声名称
    /**根据重复类型不同的参数
     * 每天
     * 每周 - 1、2、3...7
     * 每月 - 1、2、3...31
     * 每年 - 01-01、01-02、01-03...12-31
     */
    public static final String fscParameter = "fscParameter";// VARCHAR(100,0)
    public static final String fscParReamrk = "fscParReamrk";// VARCHAR(100,0) 存放农历日期转换成阳历日期，进行排序
    public static final String fscTags = "fscTags";// VARCHAR(100,0) 所在分类 ""
    public static final String fscSendName = "fscSendName";// VARCHAR(800,0) 发送者名称
    public static final String fscSourceDesc = "fscSourceDesc";// VARCHAR(500,0) 来源描述(链接)
    public static final String fscSourceSpare = "fscSourceSpare";// VARCHAR(500,0)来源备用(链接描述)
    public static final String fscRepDate = "fscRepDate" ;//VARCHAR(100,0)重复日期 yyyy-MM-dd
    public static final String fscRepNextCreateTime = "fscRepNextCreateTime";// VARCHAR(100,0)下一次已经生成子记事的时间    格式 - yyyy-mm-dd hh:mm
    public static final String fscRepLastCreateTime = "fscRepLastCreateTime";// VARCHAR(100,0) 上一次已经生成子记事的时间    格式 - yyyy-mm-dd hh:mm 无则为 @“”
    public static final String fscRepStartDate = "fscRepStartDate";// VARCHAR(100,0)重复起始日期
    public static final String fscRepInitialCreatedTime = "fscRepInitialCreatedTime";// VARCHAR(100,0) 母记事创建的时间 格式 - yyyy-mm-dd hh:mm
    public static final String fscRepDateOne = "fscRepDateOne";// VARCHAR(100,0) 操作子记事时间
    public static final String fscRepDateTwo = "fscRepDateTwo";// VARCHAR(100,0)操作子记事时间
    public static final String fscWebURL = "fscWebURL";// TEXT 网页链接
    public static final String fscImagePath = "fscImagePath";// VARCHAR(1000,0)图片路径
    public static final String fscCreateTime = "fscCreateTime";// datetime 创建日期
    public static final String fscUpdateTime = "fscUpdateTime";// datetime 更新时间
    public static final String fscRemark = "fscRemark";// VARCHAR(4000,0) NOT NULL DEFAULT ‘’,
    public static final String fscRemark1 = "fscRemark1";// VARCHAR(4000,0) NOT NULL DEFAULT ‘’,
    public static final String fscRemark2 = "fscRemark2";// VARCHAR(4000,0) NOT NULL DEFAULT ‘’,
    public static final String fscRemark3 = "fscRemark3";// VARCHAR(4000,0) NOT NULL DEFAULT ‘’,
}
