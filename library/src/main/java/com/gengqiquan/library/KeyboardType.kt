package com.gengqiquan.library;


enum class KeyboardType(val type: Int) {
    NONE(-1),
    NUMBER(0),
    NUMBER_DECIMAL(1),
    PHONE(2),
    Email(3),
    TEXT(4),
    VIN_CODE(10);

    companion object {
        fun from(findType: Int): KeyboardType = KeyboardType.values().first { it.type == findType }
    }
}