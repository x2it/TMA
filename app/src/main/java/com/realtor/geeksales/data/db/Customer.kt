package com.realtor.geeksales.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** 意向等级：A=强烈意向，B=一般意向，C=弱意向，D=无效/拒接，U=未评级 */
enum class IntentLevel { A, B, C, D, U }

@Entity(
    tableName = "customers",
    indices = [Index(value = ["phoneNormalized"], unique = true)]
)
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    /** 原始号码字符串 */
    val phone: String,
    /** 归一化后用于去重与查询的号码（仅数字） */
    val phoneNormalized: String,
    /** 备用电话，逗号分隔 */
    val phone2: String? = null,
    val gender: String? = null,
    val age: Int? = null,
    val wechat: String? = null,
    val source: String? = null,   // 来源：端口/到访/转介绍/网络…
    /** 意向区域：如 "朝阳国贸｜通州副中心" */
    val areaPref: String? = null,
    /** 预算下限，单位 万元 */
    val budgetMinWan: Int? = null,
    /** 预算上限，单位 万元 */
    val budgetMaxWan: Int? = null,
    /** 房型偏好：如 两居/三居/叠拼 */
    val houseType: String? = null,
    /** 意向楼盘 */
    val targetProject: String? = null,
    val intentLevel: IntentLevel = IntentLevel.U,
    val note: String? = null,
    /** 下次跟进时间 (EpochMillis UTC) */
    val nextFollowAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    /** 是否已导入拨号队列 */
    val queued: Boolean = false,
    /** 队列中的顺序号，null = 不在队列 */
    val queueOrder: Int? = null,
    /** 已拨打次数 */
    val dialCount: Int = 0,
    /** 最近一次拨打时间 */
    val lastDialAt: Long? = null,
)
